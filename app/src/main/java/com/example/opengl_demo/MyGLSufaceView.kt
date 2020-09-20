package com.example.opengl_demo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import kotlinx.android.synthetic.main.activity_main.*

class MyGLSufaceView : GLSurfaceView {

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initGL()
    }

    private fun initGL() {
        setEGLContextClientVersion(2)

        //先设置渲染器，再设置渲染模式
        setRenderer(MyRender(this))
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}