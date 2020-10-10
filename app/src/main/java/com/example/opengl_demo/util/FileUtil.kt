package com.example.opengl_demo.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class FileUtil {
    companion object {
        fun copyFilesFassets(context: Context, oldPath: String, newPath: String) {
            try {
                val fileNames: Array<String> =
                    context.getAssets().list(oldPath) as Array<String> //获取assets目录下的所有文件及目录名
                if (fileNames.size > 0) { //如果是目录
                    val file = File(newPath)
                    file.mkdirs() //如果文件夹不存在，则递归
                    for (fileName in fileNames) {
                        copyFilesFassets(context, "$oldPath/$fileName", "$newPath/$fileName")
                    }
                } else { //如果是文件
                    val inputStream: InputStream = context.getAssets().open(oldPath)
                    val fos = FileOutputStream(File(newPath))
                    val buffer = ByteArray(1024)
                    var byteCount = 0
                    while (inputStream.read(buffer)
                            .also { byteCount = it } != -1
                    ) { //循环从输入流读取 buffer字节
                        fos.write(buffer, 0, byteCount) //将读取的输入流写入到输出流
                    }
                    fos.flush() //刷新缓冲区
                    inputStream.close()
                    fos.close()
                }
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
    }
}