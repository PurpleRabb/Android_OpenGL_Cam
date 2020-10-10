package com.example.opengl_demo.face

import java.util.*

class Face {
    //每两个 保存 一个点 x+y
    //0、1 : 保存人脸的 x与y
    // 后面的 保存人脸关键点坐标 有序的
    lateinit var landmarks: FloatArray

    // 保存人脸框的宽、高
    var width = 0
    var height = 0

    //送去检测图片的宽、高
    var imgWidth = 0
    var imgHeight = 0

    fun Face(width: Int, height: Int, imgWidth: Int, imgHeight: Int, landmarks: FloatArray) {
        this.width = width
        this.height = height
        this.imgWidth = imgWidth
        this.imgHeight = imgHeight
        this.landmarks = landmarks
    }

    override fun toString(): String {
        return "Face{" +
                "landmarks=" + Arrays.toString(landmarks) +
                ", width=" + width +
                ", height=" + height +
                ", imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                '}'
    }
}