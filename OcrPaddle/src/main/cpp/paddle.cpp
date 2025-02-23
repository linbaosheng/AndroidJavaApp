#include <jni.h>
#include "log.h"
#include <string>
#include "common.h"
#include "jni_tools.h"
#include "paddle_api.h"
#include "opencv2/opencv.hpp"
#include <android/bitmap.h>
#include "det_process.h"
#include "cls_process.h"
#include "rec_process.h"

using namespace cv;
#define CLS_NB "ch_ppocr_mobile_v2.0_cls_slim_opt.nb"
#define DET_NB "ch_ppocr_mobile_v2.0_det_slim_opt.nb"
#define REC_NB "ch_ppocr_mobile_v2.0_rec_slim_opt.nb"
//
std::shared_ptr<ClsPredictor> clsPredictor_;
std::shared_ptr<DetPredictor> detPredictor_;
std::shared_ptr<RecPredictor> recPredictor_;

std::map<std::string, double> Config_;
/*std::shared_ptr<paddle::lite_api::PaddlePredictor> createPredictor(const std::string& recModelPath){
    paddle::lite_api::MobileConfig config;
    config.set_model_from_file(recModelPath);
    config.set_threads(2);
    config.set_power_mode(paddle::lite_api::PowerMode::LITE_POWER_HIGH);

    // 生成模型对象
   return paddle::lite_api::CreatePaddlePredictor<paddle::lite_api::MobileConfig>(
                    config);
}*/
cv::Mat GetRotateCropImage(cv::Mat srcimage, std::vector<std::vector<int>> box) {
    cv::Mat image;
    srcimage.copyTo(image);
    std::vector<std::vector<int>> points = box;

    int x_collect[4] = {box[0][0], box[1][0], box[2][0], box[3][0]};
    int y_collect[4] = {box[0][1], box[1][1], box[2][1], box[3][1]};
    int left = int(*std::min_element(x_collect, x_collect + 4));
    int right = int(*std::max_element(x_collect, x_collect + 4));
    int top = int(*std::min_element(y_collect, y_collect + 4));
    int bottom = int(*std::max_element(y_collect, y_collect + 4));

    cv::Mat img_crop;
    image(cv::Rect(left, top, right - left, bottom - top)).copyTo(img_crop);

    for (int i = 0; i < points.size(); i++) {
        points[i][0] -= left;
        points[i][1] -= top;
    }

    int img_crop_width =
            static_cast<int>(sqrt(pow(points[0][0] - points[1][0], 2) +
                                  pow(points[0][1] - points[1][1], 2)));
    int img_crop_height =
            static_cast<int>(sqrt(pow(points[0][0] - points[3][0], 2) +
                                  pow(points[0][1] - points[3][1], 2)));

    cv::Point2f pts_std[4];
    pts_std[0] = cv::Point2f(0., 0.);
    pts_std[1] = cv::Point2f(img_crop_width, 0.);
    pts_std[2] = cv::Point2f(img_crop_width, img_crop_height);
    pts_std[3] = cv::Point2f(0.f, img_crop_height);

    cv::Point2f pointsf[4];
    pointsf[0] = cv::Point2f(points[0][0], points[0][1]);
    pointsf[1] = cv::Point2f(points[1][0], points[1][1]);
    pointsf[2] = cv::Point2f(points[2][0], points[2][1]);
    pointsf[3] = cv::Point2f(points[3][0], points[3][1]);

    cv::Mat M = cv::getPerspectiveTransform(pointsf, pts_std);

    cv::Mat dst_img;
    cv::warpPerspective(img_crop, dst_img, M,
                        cv::Size(img_crop_width, img_crop_height),
                        cv::BORDER_REPLICATE);

    const float ratio = 1.5;
    if (static_cast<float>(dst_img.rows) >=
        static_cast<float>(dst_img.cols) * ratio) {
        cv::Mat srcCopy = cv::Mat(dst_img.rows, dst_img.cols, dst_img.depth());
        cv::transpose(dst_img, srcCopy);
        cv::flip(srcCopy, srcCopy, 0);
        return srcCopy;
    } else {
        return dst_img;
    }
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_aml_ocr_paddle_jni_Native_paddleOcrTest(JNIEnv *env, jclass clazz, jstring model_path,
                                                 jstring img_path, jstring key_path, jstring config_path) {
    LOGI("paddle ocr jni test start");
    // 测试图片路径
    std::string imgDemoPath = jstring_to_cpp_string(env, img_path);

    std::string modelPath = jstring_to_cpp_string(env, model_path);

    std::string dict_path = jstring_to_cpp_string(env, key_path);
    std::string configPath = jstring_to_cpp_string(env, config_path);

    LOGI("imgDemoPath: %s", imgDemoPath.c_str());

    //    std::string clsModelPath = jstring_to_cpp_string(env, jClsModelPath);
//    std::string recModelPath = jstring_to_cpp_string(env, jRecModelPath);
//    std::string configPath = jstring_to_cpp_string(env, jConfigPath);
//    std::string labelPath = jstring_to_cpp_string(env, jLabelPath);

    // 获取图片信息

    std::string recModelPath = modelPath + "/" + REC_NB;
    std::string detModelPath = modelPath + "/" + DET_NB;
    std::string clsModelPath = modelPath + "/" + CLS_NB;

   // 创建文本检测封装类
    detPredictor_.reset(
            new DetPredictor(detModelPath, 2, "LITE_POWER_HIGH"));
    clsPredictor_.reset(
            new ClsPredictor(clsModelPath, 2, "LITE_POWER_HIGH"));

    recPredictor_.reset(
            new RecPredictor(recModelPath, 2, "LITE_POWER_HIGH"));

//    // 创建模型对象
//    paddle::lite_api::MobileConfig config;
//    config.set_model_from_file(recModelPath);
//    config.set_threads(2);
//    config.set_power_mode(paddle::lite_api::PowerMode::LITE_POWER_HIGH);
//    // 生成模型对象
//    std::shared_ptr<paddle::lite_api::PaddlePredictor> recPredictor_ =
//            paddle::lite_api::CreatePaddlePredictor<paddle::lite_api::MobileConfig>(
//                    config);


    // 加载字典
    Config_ = LoadConfigTxt(configPath);

    std::vector<std::string> charactor_dict_ = ReadDict(dict_path);

    charactor_dict_.insert(charactor_dict_.begin(), "#"); // blank char for ctc
    charactor_dict_.push_back(" ");

    // 数据转换
    LOGI("paddle ocr start 1");
    // 读取固定图片 2. 预处理（保持与检测模型匹配的尺寸） 什么意思，这个960是怎么来的，实际项目中我要怎么设置值
    cv::Mat origin_img = cv::imread(imgDemoPath, IMREAD_UNCHANGED);
    if (origin_img.empty()) {
        LOGI("Error: Could not read image!");
        return false;
    }
    LOGI("origin_img channels: %d", origin_img.channels());
    cv::Mat bgrImage;
    if (origin_img.channels() == 4){
        cv::cvtColor(origin_img, bgrImage, cv::COLOR_RGBA2BGR);
    }else{
        origin_img.copyTo(bgrImage);
    }
    LOGI("bgrImage channels: %d", bgrImage.channels());
    // 执行ocr
// 2. 检测阶段预处理

    // 这里的宽高会影响后续的推理速度，和硬件有关系，太大，内存占用高
    int height = 448;
    int width = 448;

    // 尺寸归一化：适配模型输入
    cv::Mat bgrImage_resize;
    cv::resize(bgrImage, bgrImage_resize, cv::Size(width, height));

    // 创建深拷贝：保留原始预处理结果
    cv::Mat srcImg;
    bgrImage_resize.copyTo(srcImg);
//    LOGI("srcimg channels: %d", srcimg.channels());
//    LOGI("srcimg shape: %d x %d x %d", srcimg.rows, srcimg.cols, srcimg.channels());

    LOGI("paddle ocr start 2");
    // 文本检测 方向检测 识别文字 其中方向检测可以暂时不用关心
    auto text_boxes = detPredictor_->Predict(srcImg, Config_, nullptr, nullptr, nullptr);
    LOGI("paddle ocr start 3");
//    LOGD("debug===text_boxes: %d", text_boxes.size());
    std::vector<float> mean = {0.5f, 0.5f, 0.5f};
    std::vector<float> scale = {1 / 0.5f, 1 / 0.5f, 1 / 0.5f};

    cv::Mat img;
    bgrImage_resize.copyTo(img);
    cv::Mat crop_img;

    std::vector<std::string> rec_text;
    std::vector<float> rec_text_score;
//    LOGD("debug===boxes: %d", text_boxes.size());
    LOGI("paddle ocr start 4");
    int use_direction_classify = int(Config_["use_direction_classify"]);
    for (int i = text_boxes.size() - 1; i >= 0; i--) {
        // 使用 GetRotateCropImage 获取该框中的图像区域，并进行旋转裁剪
        crop_img = GetRotateCropImage(img, text_boxes[i]);
//        if (use_direction_classify >= 1) {
//            // 方向分类
//            crop_img = clsPredictor_->Predict(crop_img, nullptr, nullptr, nullptr, 0.9);
//        }
        // 识别阶段
        auto res = recPredictor_->Predict(crop_img, nullptr, nullptr, nullptr,
                                          charactor_dict_);
        rec_text.push_back(res.first);
        rec_text_score.push_back(res.second);
    }
    LOGI("paddle ocr start 5");
    // 5. 遍历所有检测框进行识别
    printVector(rec_text);
    // 获取返回结果
    LOGI("paddle ocr jni test end");
    return 0;
}