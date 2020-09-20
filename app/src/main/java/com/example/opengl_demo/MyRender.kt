package com.example.opengl_demo

import android.graphics.SurfaceTexture
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Surface
import com.example.opengl_demo.filter.ScreenFilter
import com.example.opengl_demo.util.CameraHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRender(myGLSufaceView: MyGLSufaceView) : GLSurfaceView.Renderer {
    private lateinit var cameraHelper: CameraHelper
    private var mGLSurfaceView: MyGLSufaceView = myGLSufaceView
    private lateinit var mTextureId: IntArray
    private lateinit var mSurfaceTexture: SurfaceTexture
    private lateinit var mScreenFilter: ScreenFilter

    private var t_matrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        cameraHelper = CameraHelper(mGLSurfaceView.context)
        mTextureId = IntArray(1)
        glGenTextures(mTextureId.size, mTextureId, 0)
        mSurfaceTexture = SurfaceTexture(mTextureId[0])
        mScreenFilter = ScreenFilter(mGLSurfaceView.context)
        mSurfaceTexture.setOnFrameAvailableListener {
            mGLSurfaceView.requestRender() //这里发起渲染请求}
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cameraHelper.setSurface(Surface(mSurfaceTexture))
        cameraHelper.openCamera()
        mScreenFilter.setSize(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClearColor(255f, 0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT)

        mSurfaceTexture.updateTexImage()
        mSurfaceTexture.getTransformMatrix(t_matrix)
        mScreenFilter.draw(mTextureId, t_matrix)
    }
}