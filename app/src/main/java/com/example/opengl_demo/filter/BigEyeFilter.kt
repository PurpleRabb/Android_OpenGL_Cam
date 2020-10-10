package com.example.opengl_demo.filter

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import com.example.opengl_demo.face.Face
import com.example.opengl_demo.util.TextureHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class BigEyeFilter(context: Context, vertexId: Int, fragmentId: Int) :
    BaseFilter(context, vertexId, fragmentId) {

    private var mFrameBuffers: IntArray? = null
    private lateinit var mFrameBufferTextures: IntArray

    private var left_eye = 0
    private var right_eye = 0

    private var left: FloatBuffer? = null
    private var right: FloatBuffer? = null
    private var mFace: Face? = null

    init {
        left_eye = glGetUniformLocation(mProgramId, "left_eye")
        right_eye = glGetUniformLocation(mProgramId, "right_eye")

        left = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        right = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
    }

    override fun onReady(width: Int, height: Int) {
        super.onReady(width, height)

        if (mFrameBuffers != null) {
            releaseFBO()
        }

        mFrameBuffers = IntArray(1) //创建FBO
        glGenFramebuffers(mFrameBuffers!!.size, mFrameBuffers, 0)

        mFrameBufferTextures = IntArray(1)
        TextureHelper.genTextures(mFrameBufferTextures)

        glBindTexture(GL_TEXTURE_2D, mFrameBufferTextures[0]) //绑定
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            width,
            height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            null
        )

        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers!![0])
        glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D,
            mFrameBufferTextures[0],
            0
        )

        glBindTexture(GL_TEXTURE_2D, 0) //解绑，与绑定配对
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    override fun draw(mTextureId: Int): Int {
        if (mFace == null) {
            return mTextureId
        }

        glViewport(0, 0, mWidth, mHeight) //设置视窗大小
        //绑定 FBO（否则会渲染到屏幕）
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers!![0])

        glUseProgram(mProgramId) //告诉OpenGL 使用前面创建的程序

        //画画
        //顶点坐标赋值
        mVertextBuffer.position(0) //从缓冲区的第一个位置开始读取顶点坐标信息

        //传值
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, mVertextBuffer)
        //激活
        glEnableVertexAttribArray(vPosition)

        //纹理坐标
        mTextureBuffer.position(0)
        //指定索引处的顶点属性数组的位置和 数据格式，方便渲染时来使用
        // 索引值
        //数据类型
        //是否需要归一化处理。
        //步长。0：数据是精密排列
        //指定索引处的顶点属性数组的位置和 数据格式，方便渲染时来使用
        // 每个顶点属性的分量（组件）数 , 必须是 1，2，3或4。（比如顶点vec2( x, y), vec3(x,y,z),颜色vec4（r,g,b,a））
        //步长。0：数据是精密排列
        //缓冲区，告诉opengl到哪里去拿数据
        glVertexAttribPointer(vTexturePos, 2, GL_FLOAT, false, 0, mTextureBuffer)
        glEnableVertexAttribArray(vTexturePos)

        val landmarks = mFace!!.landmarks
        //左眼
        var x = landmarks[2] / mFace!!.imgWidth
        var y = landmarks[3] / mFace!!.imgHeight
        left!!.clear()
        left!!.put(x)
        left!!.put(y)
        left!!.position(0)
        glUniform2fv(left_eye, 1, left)


        //右眼
        x = landmarks[4] / mFace!!.imgWidth
        y = landmarks[5] / mFace!!.imgHeight
        right!!.clear()
        right!!.put(x)
        right!!.put(y)
        right!!.position(0)
        glUniform2fv(right_eye, 1, right)

        //vTexture
        //激活图层

        //vTexture
        //激活图层
        glActiveTexture(GL_TEXTURE0)
        //绑定纹理
        //绑定纹理
        glBindTexture(GL_TEXTURE_2D, mTextureId)
        glUniform1i(vTexture, 0)

        //通知 opengl 绘制

        //通知 opengl 绘制
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

        glBindTexture(GL_TEXTURE_2D, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        return mFrameBufferTextures[0]
    }

    private fun releaseFBO() {
        if (null != mFrameBufferTextures) {
            glDeleteTextures(1, mFrameBufferTextures, 0)
        }
        if (null != mFrameBuffers) {
            glDeleteFramebuffers(1, mFrameBuffers, 0)
        }
    }

    fun setFace(face: Face?) {
        if (face != null) {
            mFace = face
        }
    }
}