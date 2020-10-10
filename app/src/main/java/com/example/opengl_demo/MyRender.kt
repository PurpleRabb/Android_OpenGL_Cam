package com.example.opengl_demo

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.os.Environment
import android.util.Log
import com.example.opengl_demo.face.FaceTrack
import com.example.opengl_demo.filter.BigEyeFilter
import com.example.opengl_demo.filter.CameraFilter
import com.example.opengl_demo.filter.ScreenFilter
import com.example.opengl_demo.util.CameraHelper
import com.example.opengl_demo.util.FileUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRender(myGLSufaceView: MyGLSurfaceView) : GLSurfaceView.Renderer {
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private lateinit var cameraHelper: CameraHelper
    private var mGLSurfaceView: MyGLSurfaceView = myGLSufaceView
    private lateinit var mTextureId: IntArray
    private lateinit var mSurfaceTexture: SurfaceTexture
    private lateinit var cameraFilter: CameraFilter
    private lateinit var screenFilter: ScreenFilter
    private var mBigEyeFilter: BigEyeFilter? = null
    private var mMediaRecorder: MyMediaRecorder? = null
    private lateinit var mFaceTrack: FaceTrack

    private val TAG = "MyRender"
    private var tMatrix = FloatArray(16)

    init {
        //拷贝模型文件到sd卡
        FileUtil.copyFilesFassets(
            mGLSurfaceView.context, "lbpcascade_frontalface.xml",
            mGLSurfaceView.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
                    + "/lbpcascade_frontalface.xml"
        )
        FileUtil.copyFilesFassets(
            mGLSurfaceView.context, "seeta_fa_v1.1.bin",
            mGLSurfaceView.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
                    + "/seeta_fa_v1.1.bin"
        )
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        cameraHelper = CameraHelper(mGLSurfaceView.context)
        mTextureId = IntArray(1)
        glGenTextures(mTextureId.size, mTextureId, 0)
        mSurfaceTexture = SurfaceTexture(mTextureId[0])
        cameraFilter = CameraFilter(
            mGLSurfaceView.context,
            R.raw.camera_vertex,
            R.raw.camera_fragment
        )
        screenFilter = ScreenFilter(mGLSurfaceView.context, R.raw.base_vertext, R.raw.base_fragment)
        mSurfaceTexture.setOnFrameAvailableListener {
            mGLSurfaceView.requestRender() //这里发起渲染请求
        }
        cameraHelper.setOnPreviewListener(object : CameraHelper.OnPreviewListener {
            override fun onPreviewFrame(data: ByteArray?, len: Int) {
                mFaceTrack.detector(data)
            }
        })
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mWidth = width
        mHeight = height
        cameraHelper.openCamera(width, height, mSurfaceTexture)
        if (mMediaRecorder == null) {
            var size = cameraHelper.getPreviewSize()
            if (size != null) {
                mMediaRecorder = MyMediaRecorder(
                    size.width,
                    size.height,
                    EGL14.eglGetCurrentContext(),
                    mGLSurfaceView.context
                )
                Log.i(TAG, size.toString())
            }
        }

        //创建人脸检测跟踪器
        mFaceTrack = FaceTrack(
            mGLSurfaceView.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + "/lbpcascade_frontalface.xml",
            mGLSurfaceView.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + "/seeta_fa_v1.1.bin",
            cameraHelper
        )
        Log.i(
            TAG,
            mGLSurfaceView.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
        )
        mFaceTrack.startTrack() //启动跟踪器

        cameraFilter.onReady(width, height)
        screenFilter.onReady(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClearColor(50f, 50f, 100f, 0f)
        glClear(GL_COLOR_BUFFER_BIT)

        mSurfaceTexture.updateTexImage()
        mSurfaceTexture.getTransformMatrix(tMatrix)
        cameraFilter.setMatrix(tMatrix)
        var textureId = cameraFilter.draw(mTextureId[0])
        if (null != mBigEyeFilter) {
            mBigEyeFilter!!.setFace(mFaceTrack.getFace())
            textureId = mBigEyeFilter!!.draw(textureId)
        }
        screenFilter.draw(textureId)
        mMediaRecorder?.encodeFrame(textureId, mSurfaceTexture.timestamp) //这里进行编码
    }

    fun startRecord() {
        mMediaRecorder?.startRecord()
    }

    fun stopRecord() {
        mMediaRecorder?.stopRecord()
    }

    fun enableBigEye(checked: Boolean) {
        mGLSurfaceView.queueEvent { //放在GLThread中执行
            if (checked) {
                mBigEyeFilter = BigEyeFilter(
                    mGLSurfaceView.context,
                    R.raw.base_vertext,
                    R.raw.bigeye_fragment
                )
                mBigEyeFilter!!.onReady(mWidth, mHeight)
            } else {
                mBigEyeFilter!!.release()
            }
        }
    }
}