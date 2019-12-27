#include <jni.h>
#include <string>
#include <dirent.h>
#include <vector>
#include <android/log.h>
#include <iostream>
#include <sstream>
#include <fstream>
using  namespace std;

#ifndef LOG_TAG
#define LOG_TAG "GST"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG ,__VA_ARGS__) // 定义LOGF类型
#endif

//字符串分割到数组
void Split(const string &src, const string &separator, vector<string> &dest) {
    string str = src;
    string substring;
    string::size_type start = 0, index;
    dest.clear();
    index = str.find_first_of(separator, start);
    do {
        if (index != string::npos) {
            substring = str.substr(start, index - start);
            dest.push_back(substring);
            start = index + separator.size();
            index = str.find(separator, start);
            if (start == string::npos) break;
        }
    } while (index != string::npos);

    //the last part
    substring = str.substr(start);
    dest.push_back(substring);
}

#define LOG_TAG  "qytc-lib"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

string execCmd(const char *cmd) {
    //执行busybox lsusb获取usb设备
    FILE *fp = popen(cmd, "r");
    string usb = string();
    char buff1[10000];

    while (fgets(buff1, sizeof(buff1) - 1, fp) != NULL) {
        string s = string(buff1);
        usb += (s.substr(0, s.length() - 1) + ",");
    }
    pclose(fp);
    return usb;
}

bool compare(FILE *file, string usbinfo) {
    char buff2[100000];
    fgets(buff2, sizeof(buff2) - 1, file);
    fclose(file);
    //循环遍历判断
    vector<string> support;
    Split(buff2, ",", support);
    for (int i = 0; i < support.size(); i++) {
        string sd = support[i];
        sd = sd.substr(0, sd.length() - 1);
        if (usbinfo.find(sd.c_str()) != string::npos) {
            return true;
        }
    }
    return false;
}



extern "C" JNIEXPORT bool JNICALL
Java_io_qytc_p2psdk_utils_Check_supportHD(
        JNIEnv *env, jclass clazz) {
    //加载支持的摄像头
    FILE *file;
    file = fopen("/data/data/io.qytc.vc/camera", "re");
    if (file == nullptr) {
        return static_cast<jboolean>(false);
    }

    string devices = execCmd("cat /proc/bus/input/devices | grep Vendor");
    bool result = compare(file, devices);
    if(result){
        return static_cast<jboolean>(true);
    } else{
        string lsusb = execCmd("busybox lsusb");
        result = compare(file, lsusb);
        return static_cast<jboolean>(result);
    }

}
