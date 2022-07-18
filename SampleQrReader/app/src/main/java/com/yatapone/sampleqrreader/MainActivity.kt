package com.yatapone.sampleqrreader

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.camera.CameraSettings
import com.yatapone.sampleqrreader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUEST_CODE = 999
    }

    private lateinit var binding: ActivityMainBinding
    private var permissionRejected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.qrView.barcodeView.decoderFactory = DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
        val cameraSettings = binding.qrView.barcodeView.cameraSettings

        /* enable settings below if needed */
//        cameraSettings.isScanInverted = true
//        cameraSettings.isBarcodeSceneModeEnabled = true
//        cameraSettings.isMeteringEnabled = true
//        cameraSettings.isAutoFocusEnabled = true
//        cameraSettings.isContinuousFocusEnabled = true
//        cameraSettings.isExposureEnabled = true
//        cameraSettings.isAutoTorchEnabled = true
        cameraSettings.focusMode = CameraSettings.FocusMode.AUTO
//        cameraSettings.focusMode = CameraSettings.FocusMode.MACRO
//        cameraSettings.focusMode = CameraSettings.FocusMode.CONTINUOUS
//        cameraSettings.focusMode = CameraSettings.FocusMode.INFINITY

        Log.d(TAG, "cameraSettings.isScanInverted=${cameraSettings.isScanInverted}")
        Log.d(TAG, "cameraSettings.isBarcodeSceneModeEnabled=${cameraSettings.isBarcodeSceneModeEnabled}")
        Log.d(TAG, "cameraSettings.isMeteringEnabled=${cameraSettings.isMeteringEnabled}")
        Log.d(TAG, "cameraSettings.isAutoFocusEnabled=${cameraSettings.isAutoFocusEnabled}")
        Log.d(TAG, "cameraSettings.isContinuousFocusEnabled=${cameraSettings.isContinuousFocusEnabled}")
        Log.d(TAG, "cameraSettings.isExposureEnabled=${cameraSettings.isExposureEnabled}")
        Log.d(TAG, "cameraSettings.isAutoTorchEnabled=${cameraSettings.isAutoTorchEnabled}")
        Log.d(TAG, "cameraSettings.focusMode=${cameraSettings.focusMode}")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: permissionRejected=$permissionRejected")

        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onResume: permission.CAMERA is granted.")
            binding.qrView.resume()
            qrReadStart()
        } else {
            Log.d(TAG, "onResume: permission.CAMERA is required.")
            if (!permissionRejected) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Camera permission is required")
                    .setMessage("Camera permission is required. Restart app and grant camera permission.")
                    .setPositiveButton("OK") { _, _ ->
                        finish()
                    }
                    .show()
            }
        }
    }

    private fun qrReadStart() {
        Log.d(TAG, "qrReadStart: ")
        binding.qrView.decodeSingle(object : BarcodeCallback {
            override fun barcodeResult(barcodeResult: BarcodeResult) {
                Log.d(TAG, "qrReadStart: barcodeResult.text=${barcodeResult.text}")
                binding.qrView.pause()

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("QR read result")
                    .setMessage(barcodeResult.text)
                    .setPositiveButton("OK") { _, _ ->
                        // restart
                        binding.qrView.resume()
                        qrReadStart()
                    }
                    .show()
            }

            override fun possibleResultPoints(list: List<ResultPoint>) {}
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: requestCode=$requestCode, permissions=${permissions[0]}, grantResults=${grantResults[0]}")
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            permissionRejected = true
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")

        binding.qrView.pause()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
    }
}