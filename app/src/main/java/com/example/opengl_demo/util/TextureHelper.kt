package com.example.opengl_demo.util

import android.opengl.GLES20
import android.opengl.GLES20.*

class TextureHelper {
    companion object {
        fun genTextures(textureId : IntArray) {
            for (tid in textureId) {
                glBindTexture(GL_TEXTURE_2D, tid) //绑定
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)

                glBindTexture(GL_TEXTURE_2D, 0) //解绑
            }
        }
    }
}