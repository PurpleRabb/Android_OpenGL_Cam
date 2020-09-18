package com.example.opengl_demo

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRender : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        gl?.glShadeModel(GL10.GL_SMOOTH) //阴影平滑
        gl?.glClearColor(1f, 0f, 0f, 0f) //设置清除屏幕时所用的颜色

        //将深度缓存设想为屏幕后面的层。深度缓存不断的对物体进入屏幕内部有多深进行跟踪
        gl?.glClearDepthf(1.0f)
        gl?.glEnable(GL10.GL_DEPTH_TEST)
        gl?.glDepthFunc(GL10.GL_LEQUAL)

        //透视修正
        gl?.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置输出屏幕的大小
        gl?.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //清除屏幕和深度缓存
        gl?.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        //充值当前模型观察矩阵
        gl?.glLoadIdentity()
    }
}