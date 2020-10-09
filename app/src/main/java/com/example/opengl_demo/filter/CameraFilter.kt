package com.example.opengl_demo.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20.*
import com.example.opengl_demo.util.TextureHelper
import javax.microedition.khronos.opengles.GL11Ext

class CameraFilter(context: Context, vertexId: Int, fragmentId: Int) : BaseFilter(context, vertexId,
    fragmentId
) {

    private lateinit var mFrameBuffers: IntArray
    private lateinit var mFrameBufferTextures: IntArray
    override val TAG = "ScreenFilter"
    private lateinit var tMatrix : FloatArray

    fun setMatrix(t_matrix : FloatArray) {
        this.tMatrix = t_matrix
    }

    override fun draw(mTextureId: Int): Int {
        //super.draw(mTextureId)
        //Log.i(TAG,"draw")
        glViewport(0,0, mWidth, mHeight) //设置视窗大小

        //这里画到FBO上
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers[0])

        glUseProgram(mProgramId)

        mVertextBuffer.position(0) //每次调用put加入点后position都会加1，因此加入点后在绘图时候将position重置为0
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, mVertextBuffer) //赋值
        glEnableVertexAttribArray(vPosition) //激活

        mTextureBuffer.position(0) //每次调用put加入点后position都会加1，因此加入点后在绘图时候将position重置为0
        glVertexAttribPointer(vTexturePos, 2, GL_FLOAT, false, 0, mTextureBuffer) //赋值
        glEnableVertexAttribArray(vTexturePos) //激活

        //变换矩阵赋值
        glUniformMatrix4fv(vMatrix, 1, false, tMatrix, 0)

        glActiveTexture(GL_TEXTURE0) //指定纹理
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId)
        //glBindTexture(GL_TEXTURE_2D, mTextureId)
        glUniform1i(vTexture, 0)

        //绘制
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

        glBindTexture(GL_TEXTURE_2D, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        return mFrameBufferTextures[0]
    }

    override fun onReady(width: Int, height: Int) {
        super.onReady(width, height)

        mFrameBuffers = IntArray(1) //创建FBO
        glGenFramebuffers(mFrameBuffers.size, mFrameBuffers, 0)

        mFrameBufferTextures = IntArray(1)
        TextureHelper.genTextures(mFrameBufferTextures)

        glBindTexture(GL_TEXTURE_2D, mFrameBufferTextures[0]) //绑定
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height,0, GL_RGBA, GL_UNSIGNED_BYTE, null)

        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers[0])
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mFrameBufferTextures[0], 0)

        glBindTexture(GL_TEXTURE_2D, 0) //解绑，与绑定配对
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    override fun release() {
        super.release()
        if (mTextureBuffer != null) {
            glDeleteTextures(1,mFrameBufferTextures, 0)
        }

        if (mFrameBufferTextures != null) {
            glDeleteFramebuffers(1, mFrameBuffers, 0)
        }
    }
}