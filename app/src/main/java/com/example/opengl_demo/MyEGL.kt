package com.example.opengl_demo

import android.opengl.EGL14.*
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLExt

class MyEGL(base_context: EGLContext) {

    private lateinit var mEGLDisplay : EGLDisplay
    private var share_context = base_context
    private lateinit var mContext : EGLContext

    init {
        creatEGL() //创建EGL环境
    }

    private fun creatEGL() {
        mEGLDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY) //获取显示器
        var version = IntArray(2)
        eglInitialize(mEGLDisplay, version, 0, version , 1)
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
        eglChooseConfig(mEGLDisplay, attribute_list, 0, eglConfigs, 0,
            eglConfigs.size, num_config, 0)
        var config = eglConfigs[0] //获得config

        //创建上下文
        var ctx_contrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE)
        mContext = eglCreateContext(mEGLDisplay, config, share_context, ctx_contrib_list, 0)

    }
}