#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <iostream>
#include <opencv2/imgproc/types_c.h>
#include "FaceTrack.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_opengl_1demo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = cv::getVersionString();
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_opengl_1demo_face_FaceTrack_native_1create(JNIEnv *env, jobject thiz,
                                                            jstring _model, jstring _seeta) {
    const char *model = env->GetStringUTFChars(_model, 0);
    const char *seeta = env->GetStringUTFChars(_seeta, 0);

    FaceTrack *faceTrack = new FaceTrack(model, seeta);

    env->ReleaseStringUTFChars(_model, model);
    env->ReleaseStringUTFChars(_seeta, seeta);
    return reinterpret_cast<jlong>(faceTrack);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_opengl_1demo_face_FaceTrack_native_1start(JNIEnv *env, jobject thiz, jlong self) {
    if (self) {
        FaceTrack *faceTrack = reinterpret_cast<FaceTrack *>(self);
        faceTrack->startTracking();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_opengl_1demo_face_FaceTrack_native_1stop(JNIEnv *env, jobject thiz, jlong self) {
    if (self) {
        FaceTrack *faceTrack = reinterpret_cast<FaceTrack *>(self);
        faceTrack->stopTracking();
        delete faceTrack;
    }
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_opengl_1demo_face_FaceTrack_native_1detector(JNIEnv *env, jobject thiz, jlong self,
                                                              jbyteArray _data, jint camera_id,
                                                              jint width, jint height) {
    if (self == 0) {
        return NULL;
    }
    jbyte *data = env->GetByteArrayElements(_data, 0);
    FaceTrack *faceTrack = reinterpret_cast<FaceTrack *>(self);
    Mat src(height + height / 2, width, CV_8UC1, data);
    //imwrite("/sdcard/camera.jpg", src);//摄像头原始图像
    cvtColor(src, src, CV_YUV2RGBA_NV21);

    if (camera_id == 1) {
        //前摄
        rotate(src, src, ROTATE_90_COUNTERCLOCKWISE);//逆时针90度
        flip(src, src, 1);//y 轴 翻转
    } else {
        //后摄
        rotate(src, src, ROTATE_90_CLOCKWISE);
    }
    //灰度化
    cvtColor(src, src, COLOR_RGBA2GRAY);

    //均衡化处理
    equalizeHist(src, src);

    vector<Rect2f> rects;
    faceTrack->detector(src, rects);
    env->ReleaseByteArrayElements(_data, data, 0);

    int imgWidth = src.cols;
    int imgHeight = src.rows;
    int ret = rects.size();
    if (ret) {
        //将结果填充到java空间的类
        jclass clazz = env->FindClass("com/example/opengl_demo/face/Face");
        jmethodID construct = env->GetMethodID(clazz, "<init>", "(IIII[F)V");

        int size = ret * 2;
        jfloatArray floatArray = env->NewFloatArray(size);

        for (int i = 0, j = 0; i < size; ++j) {
            //把rects里的数据成对的放入floatArray
            float f[2] = {rects[j].x, rects[j].y};
            env->SetFloatArrayRegion(floatArray, i, 2, f);
            i += 2;
        }
        Rect2f faceRect = rects[0];
        int faceWidth = faceRect.width;
        int faceHeight = faceRect.height;

        jobject face = env->NewObject(clazz, construct, faceWidth, faceHeight, imgWidth, imgHeight,
                                      floatArray);
        //画人脸矩形框
        //rectangle(src, faceRect, Scalar(255, 255, 255));
        //for (int i = 1; i < ret; ++i) {
        //    circle(src, Point2f(rects[i].x, rects[i].y), 5, Scalar(0, 255, 0));
        //}

        //imwrite("/sdcard/face.jpg", src);//画了人脸框的图像
        return face;
    }
    src.release();
    return NULL;

}