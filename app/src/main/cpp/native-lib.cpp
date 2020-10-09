#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <iostream>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_opengl_1demo_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = cv::getVersionString();
    return env->NewStringUTF(hello.c_str());
}
