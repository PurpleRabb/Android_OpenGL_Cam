package com.example.opengl_demo.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20.*

class CameraFilter(context: Context, vertexId: Int, fragmentId: Int) : BaseFilter(context, vertexId,
    fragmentId
) {

    override val TAG = "ScreenFilter"
    private lateinit var tMatrix : FloatArray

    fun setMatrix(t_matrix : FloatArray) {
        this.tMatrix = t_matrix
    }

    override fun draw(mTextureId: IntArray) {
        //super.draw(mTextureId)
        //Log.i(TAG,"draw")
        glViewport(0,0, mWidth, mHeight) //设置视窗大小
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
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId[0])
        glUniform1i(vTexture, 0)

        //绘制
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    }
}