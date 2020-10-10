package com.example.opengl_demo.face

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import com.example.opengl_demo.util.CameraHelper

class FaceTrack {
    private val TAG = "FaceTrack"

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    private var mCameraHelper: CameraHelper? = null

    private var mHandler: Handler? = null
    private var mHandlerThread: HandlerThread? = null

    private var self: Long = 0

    //结果
    private var mFace: Face? = null

    /**
     *
     * @param face_model 人脸检测的模型文件路径
     * @param seeta 中科院人脸关键点检测的模型文件路径
     * @param cameraHelper
     */
    fun FaceTrack(face_model: String, seeta: String, cameraHelper: CameraHelper?) {
        mCameraHelper = cameraHelper
        self = native_create(face_model, seeta)
        mHandlerThread = HandlerThread("FaceTrack")
        mHandlerThread!!.start()
        mHandler = object : Handler(mHandlerThread!!.looper) {
            override fun handleMessage(msg: Message) {
                //子线程 耗时再久 也不会对其他地方 (如：opengl绘制线程) 产生影响
                synchronized(this) {

                    //定位 线程中检测
                    mFace = native_detector(
                        self, msg.obj as ByteArray, mCameraHelper?.getCameraID(),
                        mCameraHelper?.getWidth(), mCameraHelper?.getHeight()
                    )
                    if (mFace != null) Log.e(TAG, mFace.toString())
                }
            }
        }
    }

    fun startTrack() {
        native_start(self)
    }

    fun stopTrack() {
        synchronized(this) {
            mHandlerThread!!.quitSafely()
            mHandler!!.removeCallbacksAndMessages(null)
            native_stop(self)
            self = 0
        }
    }


    fun detector(data: ByteArray?) {
        //把积压的 11号任务移除掉
        mHandler!!.removeMessages(11)
        //加入新的11号任务
        val message = mHandler!!.obtainMessage(11)
        message.obj = data
        mHandler!!.sendMessage(message)
    }

    fun getFace(): Face? {
        return mFace
    }


    private external fun native_create(model: String, seeta: String): Long

    private external fun native_start(self: Long)

    private external fun native_stop(self: Long)

    private external fun native_detector(
        self: Long, data: ByteArray, cameraId: Int?, width: Int?,
        height: Int?
    ): Face


}