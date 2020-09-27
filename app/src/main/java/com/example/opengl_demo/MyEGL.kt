package com.example.opengl_demo

import android.content.Context
import android.opengl.*
import android.opengl.EGL14.*
import android.view.Surface
import com.example.opengl_demo.filter.ScreenFilter
import java.sql.Timestamp

class MyEGL(
    base_context: EGLContext,
    mMediaCodecSurface: Surface,
    context: Context,
    width: Int,
    height: Int
) {

    private lateinit var mEGLDisplay: EGLDisplay
    private var share_context = base_context
    private lateinit var mContext: EGLContext
    private lateinit var mConfig: EGLConfig
    private lateinit var eglSurface: EGLSurface
    private var screenFilter: ScreenFilter

    init {
        // 1.创建EGL环境
        creatEGL()
        // 2.通过MediaCodec的Surface创建窗口
        var contrib_list = intArrayOf(EGL_NONE)
        eglSurface =
            eglCreateWindowSurface(mEGLDisplay, mConfig, mMediaCodecSurface, contrib_list, 0)

        // 3.绑定两个Surface
        eglMakeCurrent(mEGLDisplay, eglSurface, eglSurface, mContext)

        // 4.渲染
        screenFilter = ScreenFilter(context, R.raw.base_vertext, R.raw.base_fragment)
        screenFilter.onReady(width, height)
    }

    fun draw(textureId: Int, timestamp: Long) {
        screenFilter.draw(textureId)
        EGLExt.eglPresentationTimeANDROID(mEGLDisplay, eglSurface, timestamp)
        eglSwapBuffers(mEGLDisplay, eglSurface)
    }

    private fun creatEGL() {
        mEGLDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY) //获取显示器
        var version = IntArray(2)
        eglInitialize(mEGLDisplay, version, 0, version, 1)
        var attribute_list = intArrayOf(
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGLExt.EGL_RECORDABLE_ANDROID, 1,
            EGL_NONE
        )

        var eglConfigs = arrayOfNulls<EGLConfig>(1)
        var num_config = IntArray(1)
        eglChooseConfig(
            mEGLDisplay, attribute_list, 0, eglConfigs, 0,
            eglConfigs.size, num_config, 0
        )
        mConfig = eglConfigs[0]!! //获得config

        //创建上下文
        var ctx_contrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE)
        mContext = eglCreateContext(mEGLDisplay, mConfig, share_context, ctx_contrib_list, 0)

    }
}