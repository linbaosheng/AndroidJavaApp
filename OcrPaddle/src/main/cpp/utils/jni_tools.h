//
// Created by Administrator on 2025/2/22.
//

#ifndef MYAPPLICATION_JNI_TOOLS_H
#define MYAPPLICATION_JNI_TOOLS_H
#include "jni.h"
#include <string>

inline std::string jstring_to_cpp_string(JNIEnv *env, jstring jstr) {
    // In java, a unicode char will be encoded using 2 bytes (utf16).
    // so jstring will contain characters utf16. std::string in c++ is
    // essentially a string of bytes, not characters, so if we want to
    // pass jstring from JNI to c++, we have convert utf16 to bytes.
    if (!jstr) {
        return "";
    }
    const jclass stringClass = env->GetObjectClass(jstr);
    const jmethodID getBytes =
            env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray)env->CallObjectMethod(
            jstr, getBytes, env->NewStringUTF("UTF-8"));

    size_t length = (size_t)env->GetArrayLength(stringJbytes);
    jbyte *pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string(reinterpret_cast<char *>(pBytes), length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

#endif //MYAPPLICATION_JNI_TOOLS_H
