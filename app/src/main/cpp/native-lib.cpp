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

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_opengl_1demo_face_FaceTrack_native_1create(JNIEnv *env, jobject thiz,
                                                            jstring model, jstring seeta) {
    // TODO: implement native_create()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_opengl_1demo_face_FaceTrack_native_1start(JNIEnv *env, jobject thiz, jlong self) {
    // TODO: implement native_start()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_opengl_1demo_face_FaceTrack_native_1stop(JNIEnv *env, jobject thiz, jlong self) {
    // TODO: implement native_stop()
}extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_opengl_1demo_face_FaceTrack_native_1detector(JNIEnv *env, jobject thiz, jlong self,
                                                              jbyteArray data, jobject camera_id,
                                                              jobject width, jobject height) {
    // TODO: implement native_detector()
}