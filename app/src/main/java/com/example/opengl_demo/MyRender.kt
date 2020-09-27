package com.example.opengl_demo

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.opengl_demo.filter.CameraFilter
import com.example.opengl_demo.filter.ScreenFilter
import com.example.opengl_demo.util.CameraHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRender(myGLSufaceView: MyGLSurfaceView) : GLSurfaceView.Renderer {
    private lateinit var cameraHelper: CameraHelper
    private var mGLSurfaceView: MyGLSurfaceView = myGLSufaceView
    private lateinit var mTextureId: IntArray
    private lateinit var mSurfaceTexture: SurfaceTexture
    private lateinit var cameraFilter: CameraFilter
    private lateinit var screenFilter: ScreenFilter
    private var mMediaRecorder: MyMediaRecorder? = null

    private val TAG = "MyRender"
    private var tMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        cameraHelper = CameraHelper(mGLSurfaceView.context)
        mTextureId = IntArray(1)
        glGenTextures(mTextureId.size, mTextureId, 0)
        mSurfaceTexture = SurfaceTexture(mTextureId[0])
        cameraFilter = CameraFilter(mGLSurfaceView.context, R.raw.camera_vertex, R.raw.camera_fragment)
        screenFilter = ScreenFilter(mGLSurfaceView.context, R.raw.base_vertext, R.raw.base_fragment)
        mSurfaceTexture.setOnFrameAvailableListener {
            mGLSurfaceView.requestRender() //这里发起渲染请求
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cameraHelper.openCamera(width, height, mSurfaceTexture)
        if (mMediaRecorder == null) {
            var size = cameraHelper.getPreviewSize()
            if (size != null) {
                mMediaRecorder = MyMediaRecorder(size.width, size.height, EGL14.eglGetCurrentContext(), mGLSurfaceView.context)
                Log.i(TAG,size.toString())
            }
        }
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
        screenFilter.draw(textureId)
        mMediaRecorder?.encodeFrame(textureId, mSurfaceTexture.timestamp) //这里进行编码
    }

    fun startRecord() {
        mMediaRecorder?.startRecord()
    }

    fun stopRecord() {
        mMediaRecorder?.stopRecord()
    }
}