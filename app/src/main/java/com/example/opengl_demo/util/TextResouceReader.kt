package com.example.opengl_demo.util

import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class TextResouceReader {
    private val TAG = "TextResourceReader"

    companion object {
        fun readTextFileFromResource(context: Context, resourceId: Int): String {
            val body = StringBuilder()
            try {
                val inputStream = context.resources.openRawResource(resourceId)
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferReader = BufferedReader(inputStreamReader)

                bufferReader.lineSequence().forEach {
                    body.append(it + '\n')
                }
            } catch (e: IOException) {
                throw RuntimeException("Could not open resource: $resourceId", e)
            } catch (e: Resources.NotFoundException) {
                throw java.lang.RuntimeException("Resource not found: $resourceId", e)
            }
            return body.toString()
        }

    }
}