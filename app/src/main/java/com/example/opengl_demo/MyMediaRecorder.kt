package com.example.opengl_demo

import android.content.Context
import android.media.*
import android.opengl.EGLContext
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface


@Suppress("DEPRECATION")
class MyMediaRecorder(private val width: Int, private val height: Int, share_context: EGLContext, context: Context) {
    private lateinit var mInputSurface: Surface
    private val TAG = "MyMediaRecorder"
    private lateinit var mediaCodec: MediaCodec
    private lateinit var mediaMuxer: MediaMuxer
    private lateinit var handler: Handler
    private lateinit var myEgl: MyEGL
    private var shareContext = share_context
    private var mContext = context
    private var path: String = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath +"/" + System.currentTimeMillis() + "test.mp4"
    private var isStart = false

    init {
        //getCodeInfo()  //test
    }

    fun startRecord() {
        Log.i(TAG, "startRecord:$path")
        if (isStart)
            return
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)

        //设置格式
        val videoFormat =
            MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        val bitrate = width * height * 25 * 0.07
        //设置码率 公式: pixel count * FPS * motion factor * 0.07  / 1000
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate.toInt())
        //设置帧率
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25)
        //设置颜色模式
        videoFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        //关键帧的间隔
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 20)
        mediaCodec.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        //创建输入的surface
        mInputSurface = mediaCodec.createInputSurface()

        //创建封装器
        try {
            mediaMuxer = MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //配置EGL环境
        val handlerThread = HandlerThread("MyRecorderThread")
        handlerThread.start()
        val looper = handlerThread.looper
        handler = Handler(looper)
        handler.post {
            myEgl = MyEGL(shareContext, mInputSurface, mContext, this.height, this.width)
            mediaCodec.start() //启动编码器
            isStart = true
        }
    }

    fun encodeFrame(textureId: Int, timestamp: Long) {
        if (isStart) {
            handler.post {
                myEgl.draw(textureId, timestamp) //先渲染数据
                getEncodeData(false)
            }
        }
    }

    fun stopRecord() {
        if (!isStart)
            return
        isStart = false
        handler.post {
            Log.i(TAG, "stopRecord:$path")
            getEncodeData(true) //手动停止
            mediaCodec.stop()
            mediaCodec.release()
            mediaMuxer.stop()
            mediaMuxer.release()
            mInputSurface.release()
            handler.looper.quitSafely()
        }
    }

    private fun getEncodeData(endOfStream: Boolean) {
        if (endOfStream) {
            mediaCodec.signalEndOfInputStream()
        }
        //获取编码数据
        val bufferInfo = MediaCodec.BufferInfo()
        var index = 0
        while (true) {
            when (val status = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)) {
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Log.i(TAG, "INFO_TRY_AGAIN_LATER")
                    if (!endOfStream) {
                        break
                    }
                }
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                    Log.i(TAG, "INFO_OUTPUT_BUFFERS_CHANGED")
                }
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    Log.i(TAG, "INFO_OUTPUT_FORMAT_CHANGED")
                    index = mediaMuxer.addTrack(mediaCodec.outputFormat)
                    mediaMuxer.start() //启动封装器
                }
                else -> {
                    //取有效的数据
                    val outputBuffer = mediaCodec.getOutputBuffer(status)
                    Log.i(TAG, bufferInfo.size.toString())
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        //配置信息丢弃
                        bufferInfo.size = 0
                    }
                    if (bufferInfo.size != 0) {
                        outputBuffer?.position(bufferInfo.offset)
                        outputBuffer?.limit(bufferInfo.offset + bufferInfo.size)
                        //写数据
                        if (outputBuffer != null) {
                            mediaMuxer.writeSampleData(index, outputBuffer, bufferInfo)
                        }
                    }
                    mediaCodec.releaseOutputBuffer(status, false)
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        //编码完毕
                        break
                    }
                } //end of when else
            } // end when
        } // end while
    }

    private fun getCodeInfo() {
        val list = MediaCodecList(MediaCodecList.REGULAR_CODECS) //REGULAR_CODECS参考api说明

        val codecs = list.codecInfos
        Log.d(TAG, "Decoders: ")
        for (codec in codecs) {
            if (codec.isEncoder) continue
            Log.d(TAG, codec.name)
        }
        Log.d(TAG, "Encoders: ")
        for (codec in codecs) {
            if (codec.isEncoder) Log.d(TAG, codec.name)
        }
    }
}