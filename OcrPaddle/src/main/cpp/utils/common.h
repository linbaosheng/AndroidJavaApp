#ifndef MYAPPLICATION_COMMON_H
#define MYAPPLICATION_COMMON_H

#include <map>
#include <vector>
#include <fstream>
#include "log.h"
#include "opencv2/opencv.hpp"
std::vector<std::string> split(const std::string &str,
                               const std::string &delim) {
    std::vector<std::string> res;
    if ("" == str)
        return res;
    char *strs = new char[str.length() + 1];
    std::strcpy(strs, str.c_str());

    char *d = new char[delim.length() + 1];
    std::strcpy(d, delim.c_str());

    char *p = std::strtok(strs, d);
    while (p) {
        std::string s = p;
        res.push_back(s);
        p = std::strtok(NULL, d);
    }

    return res;
}

std::vector<std::string> ReadDict(const std::string &path) {
    std::ifstream in(path);
    std::string filename;
    std::string line;
    std::vector<std::string> m_vec;
    if (in) {
        while (getline(in, line)) {
            m_vec.push_back(line);
        }
    } else {
        LOGI("no such file");
    }
    return m_vec;
}

std::map<std::string, double> LoadConfigTxt(const std::string &config_path) {
    auto config = ReadDict(config_path);

    std::map<std::string, double> dict;
    for (int i = 0; i < config.size(); i++) {
        std::vector<std::string> res = split(config[i], " ");
        dict[res[0]] = stod(res[1]);
    }
    return dict;
}

void printVector(const std::vector<std::string> &vec) {
    LOGI("printVector start");
    for (const auto &text: vec) {
        LOGI("      text: %s", text.c_str());
    }
    LOGI("printVector end");
}

/**
 * 判断图像是否为纯色
 *
 * @param image 输入图像
 * @param threshold 颜色差异阈值（默认 10）
 * @return true 如果图像是纯色，否则 false
 */
inline static bool isSolidColor(const cv::Mat& image, double threshold = 10) {
    if (image.empty()) {
        std::cerr << "Error: Image is empty!" << std::endl;
        return false;
    }
    // 创建一个纯色3通道的图像
    //    cv::Mat solidColorImage = cv::Mat(100, 100, CV_8UC3, cv::Scalar(255, 0, 0)); // 纯蓝色图像

    // 对于大图像，可以先缩小图像尺寸，再判断纯色
//    cv::Mat resizedImage;
//    cv::resize(image, resizedImage, cv::Size(64, 64));

    // 计算图像的平均颜色值 cv::mean 返回图像每个通道的平均值（BGR 顺序）。
    cv::Scalar meanColor = cv::mean(image);

    // 计算每个像素与平均颜色值的差异 cv::absdiff 计算每个像素与平均颜色值的绝对差异。
    cv::Mat diff;
    cv::absdiff(image, meanColor, diff);

    // 将差异图像转换为灰度图 将差异图像转换为灰度图，便于计算最大值
    cv::Mat grayDiff;
    cv::cvtColor(diff, grayDiff, cv::COLOR_BGR2GRAY);

    // 计算差异的最大值 cv::minMaxLoc 返回灰度图像中的最小值和最大值。
    double maxDiff;
    cv::minMaxLoc(grayDiff, nullptr, &maxDiff);

    // 判断是否为纯色 如果最大差异小于阈值，则认为图像是纯色。
    return maxDiff <= threshold;
}
#endif //MYAPPLICATION_COMMON_H
