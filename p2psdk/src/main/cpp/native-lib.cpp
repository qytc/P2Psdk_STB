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

bool checkHD(string dir_name,string device) {
    // check the parameter
    if (dir_name.empty()) {
        LOGE("dir_name is null !");
        return false;
    }
    DIR *dir = opendir(dir_name.c_str());
    // check is dir ?
    if (NULL == dir) {
        LOGE("Can not open dir. Check path or permission!");
        return false;
    }
    struct dirent *file;
    // read all the files in dir
    while ((file = readdir(dir)) != NULL) {
        // skip "." and ".."
        if (strcmp(file->d_name, ".") == 0 || strcmp(file->d_name, "..") == 0) {
            continue;
        }
        if (file->d_type == DT_DIR) {
            string filePath = dir_name + "/" + file->d_name;
            checkHD(filePath,device); // 递归执行
        } else {
            auto path=dir_name + "/" + file->d_name+"/uevent";

            ifstream in(path, ios::in);
            istreambuf_iterator<char> beg(in), end;
            string strdata(beg, end);
            in.close();
            if(strdata==""){
                LOGD("camera file is empty");
                return false;
            }
            string line= strdata.substr(strdata.find("PRODUCT=")+8);
            line=line.substr(0,line.find("TYPE=")-1);
            LOGD("%s",line.c_str());
            if(device.find(line) != std::string::npos){
                LOGD("checkHD success");
                return true;
            }
        }
    }
    closedir(dir);
    LOGD("closedir");
    return false;
}

extern "C" JNIEXPORT bool JNICALL
Java_io_qytc_p2psdk_utils_Check_supportHD(
        JNIEnv *env, jclass clazz) {

    ifstream in("/sdcard/camera.txt", ios::in);
    istreambuf_iterator<char> beg(in), end;
    string camera(beg, end);
    if(camera == ""){
        return false;
    }
    in.close();

    std::string dirPath="/sys/bus/usb/devices";
    return checkHD(dirPath,camera);
}
