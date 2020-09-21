package com.example.opengl_demo.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20.*
import android.util.Log
import com.example.opengl_demo.R
import com.example.opengl_demo.util.TextResouceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL11Ext

class ScreenFilter {
    private var _width : Int = 0
    private var _height : Int = 0
    val TAG = "ScreenFilter"
    private var mProgramId : Int = 0
    private var vPosition : Int = 0
    private var vTexturePos : Int = 0
    private lateinit var mVertextBuffer : FloatBuffer
    private lateinit var mTextureBuffer : FloatBuffer
    private var vMatrix : Int = 0
    private var vTexture : Int = 0

    constructor(context: Context) {
        var vertextSrc = TextResouceReader.readTextFileFromResource(context, R.raw.camera_vertex)
        var fragmentSrc = TextResouceReader.readTextFileFromResource(context, R.raw.camera_fragment)

        var vShaderId = glCreateShader(GL_VERTEX_SHADER) //创建顶点着色器ID
        glShaderSource(vShaderId, vertextSrc) //绑定着色器源码
        glCompileShader(vShaderId) //编译着色器源码
        var status = IntArray(1)
        glGetShaderiv(vShaderId, GL_COMPILE_STATUS, status, 0)
        if (status[0] != GL_TRUE) {
            //编译失败
            Log.i(TAG, "complie vertext error")
        }

        var fShaderId = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fShaderId, fragmentSrc)
        glCompileShader(fShaderId)
        glGetShaderiv(vShaderId, GL_COMPILE_STATUS, status, 0)
        if (status[0] != GL_TRUE) {
            //编译失败
            Log.i(TAG, "complie fragment error")
        }

        /*编译shader*/
        mProgramId = glCreateProgram()
        glAttachShader(mProgramId, vShaderId)
        glAttachShader(mProgramId, fShaderId)
        glLinkProgram(mProgramId)
        glGetShaderiv(vShaderId, GL_LINK_STATUS, status, 0)
        if (status[0] != GL_TRUE) {
            //编译失败
            Log.i(TAG, "complie link error")
        }

        glDeleteShader(vShaderId)
        glDeleteShader(fShaderId)

        //获取shader变量赋值
        vPosition = glGetAttribLocation(mProgramId, "vPosition")
        vTexturePos = glGetAttribLocation(mProgramId, "vTexturePos")
        vMatrix = glGetUniformLocation(mProgramId, "vMatrix")
        vTexture = glGetUniformLocation(mProgramId, "vTexture")

        //构建顶点坐标, 4个顶点 * (x,y) * float
        mVertextBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        var v = floatArrayOf( //注意opengl的世界坐标系,每次根据前两个点画三角形
            -1.0f, -1.0f,
             1.0f, -1.0f,
            -1.0f,  1.0f,
             1.0f,  1.0f
        )
        mVertextBuffer.put(v)

        mTextureBuffer =
            ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        var t = floatArrayOf( //根据Android屏幕的坐标系画点,左下角开始
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
        )
        mTextureBuffer.put(t)

    }

    fun setSize(width: Int, height: Int) {
        this._width = width
        this._height = height
    }

    fun draw(mTextureId: IntArray, t_matrix: FloatArray) {
        //Log.i(TAG,"draw")
        glViewport(0,0, _width, _height) //设置视窗大小
        glUseProgram(mProgramId)

        mVertextBuffer.position(0) //每次调用put加入点后position都会加1，因此加入点后在绘图时候将position重置为0
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, mVertextBuffer) //赋值
        glEnableVertexAttribArray(vPosition) //激活

        mTextureBuffer.position(0) //每次调用put加入点后position都会加1，因此加入点后在绘图时候将position重置为0
        glVertexAttribPointer(vTexturePos, 2, GL_FLOAT, false, 0, mTextureBuffer) //赋值
        glEnableVertexAttribArray(vTexturePos) //激活

        //变换矩阵赋值
        glUniformMatrix4fv(vMatrix, 1, false, t_matrix, 0)

        glActiveTexture(GL_TEXTURE0) //指定纹理
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId[0])
        glUniform1i(vTexture, 0)

        //绘制
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

    }
}