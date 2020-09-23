package com.example.opengl_demo.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20.*
import android.util.Log
import com.example.opengl_demo.util.TextResouceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class BaseFilter(context: Context, vertexId: Int, fragmentId : Int) {
    open val TAG = "BaseFilter"
    var mProgramId : Int = 0
    var vPosition : Int = 0
    var vTexturePos : Int = 0
    var mVertextBuffer : FloatBuffer
    var mTextureBuffer : FloatBuffer
    var vMatrix : Int = 0
    var vTexture : Int = 0
    var mWidth : Int = 0
    var mHeight : Int = 0

    init {
        val vertextSrc = TextResouceReader.readTextFileFromResource(context, vertexId)
        val fragmentSrc = TextResouceReader.readTextFileFromResource(context, fragmentId)
        val vShaderId = glCreateShader(GL_VERTEX_SHADER) //创建顶点着色器ID
        glShaderSource(vShaderId, vertextSrc)
        glCompileShader(vShaderId)
        val status = IntArray(1)
        glGetShaderiv(vShaderId, GL_COMPILE_STATUS, status, 0)
        if (status[0] != GL_TRUE) {
            //编译失败
            Log.i(TAG, "complie vertext error")
        }
        val fShaderId = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fShaderId, fragmentSrc) //绑定着色器源码
        glCompileShader(fShaderId) //编译着色器源码
        glGetShaderiv(vShaderId, GL_COMPILE_STATUS, status, 0)
        if (status[0] != GL_TRUE) {
            //编译失败
            Log.i(TAG, "complie fragment error")
        }
        mProgramId = glCreateProgram()
        glAttachShader(mProgramId, vShaderId)
        glAttachShader(mProgramId, fShaderId)
        glLinkProgram(mProgramId)
        glGetShaderiv(vShaderId, GL_LINK_STATUS, status, 0)
        if (status[0] != GL_TRUE) {
            //编译失败
            Log.i(TAG, "link error")
        }
        glDeleteShader(vShaderId)
        glDeleteShader(fShaderId)
        vPosition = glGetAttribLocation(mProgramId, "vPosition")  //获取shader变量赋值
        vTexturePos = glGetAttribLocation(mProgramId, "vTexturePos")
        vMatrix = glGetUniformLocation(mProgramId, "vMatrix")
        vTexture = glGetUniformLocation(mProgramId, "vTexture")
        //构建顶点坐标, 4个顶点 * (x,y) * float
        mVertextBuffer =
            ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        val v = floatArrayOf( //注意opengl的世界坐标系,每次根据前两个点画三角形
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
        )
        mVertextBuffer.put(v)
        mTextureBuffer =
            ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        val t = floatArrayOf( //根据Android屏幕的坐标系画点,左下角开始
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
        )
        mTextureBuffer.put(t)
    }

    open fun draw(mTextureId: Int) : Int {
        glViewport(0, 0, mWidth, mHeight) //设置视窗大小
        glUseProgram(mProgramId)

        mVertextBuffer.position(0) //每次调用put加入点后position都会加1，因此加入点后在绘图时候将position重置为0
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, mVertextBuffer) //赋值
        glEnableVertexAttribArray(vPosition) //激活

        mTextureBuffer.position(0) //每次调用put加入点后position都会加1，因此加入点后在绘图时候将position重置为0
        glVertexAttribPointer(vTexturePos, 2, GL_FLOAT, false, 0, mTextureBuffer) //赋值
        glEnableVertexAttribArray(vTexturePos) //激活

        //变换矩阵赋值
        //glUniformMatrix4fv(vMatrix, 1, false, t_matrix, 0)

        glActiveTexture(GL_TEXTURE0) //指定纹理
        glBindTexture(GL_TEXTURE_2D, mTextureId)
        glUniform1i(vTexture, 0)

        //绘制
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

        return mTextureId
    }

    open fun onReady(width: Int, height: Int) {
        this.mWidth = width
        this.mHeight = height
    }

    open fun release() {
        glDeleteProgram(mProgramId)
    }
}