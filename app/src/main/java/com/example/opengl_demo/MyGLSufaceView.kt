package com.example.opengl_demo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSufaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private fun initGL() {
        setEGLContextClientVersion(2)
        setRenderer(MyRender())
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}