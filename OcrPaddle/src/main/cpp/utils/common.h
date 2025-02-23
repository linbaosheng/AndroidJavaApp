#ifndef MYAPPLICATION_COMMON_H
#define MYAPPLICATION_COMMON_H

#include <map>
#include <vector>
#include <fstream>
#include "log.h"

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

#endif //MYAPPLICATION_COMMON_H
