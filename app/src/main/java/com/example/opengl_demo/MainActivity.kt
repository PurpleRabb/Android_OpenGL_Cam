package com.example.opengl_demo

import android.graphics.SurfaceTexture
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import com.example.opengl_demo.util.CameraHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var myRender : MyRender
    private lateinit var cameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myRender = MyRender()
        cameraHelper = CameraHelper(baseContext)
        m_GLSurfaceView.setRenderer(myRender)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                cameraHelper.setSurface(Surface(surface))
                cameraHelper.openCamera()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

            }

        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
