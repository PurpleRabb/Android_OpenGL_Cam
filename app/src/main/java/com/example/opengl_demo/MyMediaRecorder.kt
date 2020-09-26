package com.example.opengl_demo

import android.media.*
import android.opengl.EGLContext
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface


class MyMediaRecorder(width: Int, height: Int, share_context : EGLContext) {
    private var mInputSurface: Surface
    private val TAG = "MyMediaRecorder"
    private lateinit var mediaCodec: MediaCodec
    private lateinit var mediaMuxer : MediaMuxer
    private lateinit var handler : Handler
    private lateinit var myEgl : MyEGL
    private var share_context = share_context
    private var path : String = Environment.getExternalStorageDirectory().getPath() + "/test111.mp4"

    init {
        getCodeInfo()  //test
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)

        //设置格式
        var videoFormat =
            MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        var bitrate = width * height * 25 * 0.07
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
        } catch (e:Exception) {
            e.printStackTrace()
        }

        //配置EGL环境
        var handlerThread = HandlerThread("MyRecorderThread")
        handlerThread.start()
        var looper = handlerThread.looper
        handler = Handler(looper)
        handler.post(Runnable {
            myEgl = MyEGL(share_context)
        })
    }

    fun startRecord() {
        Log.i(TAG, "startRecord")
    }

    fun getCodeInfo() {
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