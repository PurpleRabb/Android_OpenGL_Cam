package com.example.opengl_demo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    init {
        initGL()
    }

    private fun initGL() {
        setEGLContextClientVersion(2)
        //先设置渲染器，再设置渲染模式
        setRenderer(MyRender(this))
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}