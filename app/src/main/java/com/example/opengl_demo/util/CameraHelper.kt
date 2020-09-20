package com.example.opengl_demo.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import androidx.core.app.ActivityCompat


class CameraHelper(baseContext: Context) {
    private lateinit var mBackgroundHandler: Handler
    private lateinit var mBackgroundThread: HandlerThread
    private lateinit var cameraManager: CameraManager
    lateinit var surfacePreview : Surface
    lateinit var cameraDevice: CameraDevice
    lateinit var captureSession: CameraCaptureSession
    private lateinit var baseContext : Context

    val TAG = "CameraHelper"
    init {
        this.baseContext = baseContext
        startBackgroundThread()
    }

    public fun openCamera() {
        cameraManager = baseContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        if (ActivityCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        cameraManager.openCamera(cameraManager.cameraIdList[0], cameraStateCallBack, mBackgroundHandler)
    }

    fun setSurface(surface: Surface) {
        surfacePreview = surface
    }

    var cameraStateCallBack : CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            cameraDevice.createCaptureSession(listOf(surfacePreview), object :
                CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    //这里设置预览
                    var requestBuilder =
                        cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    requestBuilder.addTarget(surfacePreview)
                    captureSession.setRepeatingRequest(requestBuilder.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {

                }

            }, null)
        }

        override fun onDisconnected(camera: CameraDevice) {

        }

        override fun onError(camera: CameraDevice, error: Int) {

        }
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread.start()
        mBackgroundHandler = Handler(mBackgroundThread.getLooper())
    }
}