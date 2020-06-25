package com.ml.facedetectionapp

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.ml.facedetectionapp.Helper.RectOverlay
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var alertDialog: android.app.AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alertDialog=SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Processing....")
            .setCancelable(false)
            .build()

        detect_btn.setOnClickListener {
            camera_view.start()
            camera_view.captureImage()
            graphic_overlay.clear()
        }

        camera_view.addCameraKitListener(object : CameraKitEventListener{
            override fun onVideo(p0: CameraKitVideo?) {
                
            }

            override fun onEvent(p0: CameraKitEvent?) {

            }

            override fun onImage(p0: CameraKitImage?) {
                alertDialog?.show()
                var bitmap: Bitmap? = p0?.bitmap
                bitmap=Bitmap.createScaledBitmap(bitmap, camera_view.width, camera_view.height, false)
                camera_view.stop()

                detect(bitmap)
            }

            override fun onError(p0: CameraKitError?) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        camera_view.stop()
    }

    override fun onResume() {
        super.onResume()
        camera_view.start()
    }

    fun detect(bitmap: Bitmap){
        val visionImage:FirebaseVisionImage= FirebaseVisionImage.fromBitmap(bitmap)
        val options:FirebaseVisionFaceDetectorOptions=FirebaseVisionFaceDetectorOptions.Builder().build()
        val detector:FirebaseVisionFaceDetector=FirebaseVision.getInstance()
            .getVisionFaceDetector(options)

        detector.detectInImage(visionImage).addOnSuccessListener {
            getResults(it)
        }.addOnFailureListener{
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getResults(faces:List<FirebaseVisionFace>){
        var counter=0
        for (face:FirebaseVisionFace in faces){
            val rect:Rect=face.boundingBox
            val rectOverlay:RectOverlay= RectOverlay(graphic_overlay, rect)

            graphic_overlay.add(rectOverlay)

            counter+=1
        }
        alertDialog?.dismiss()
    }
}
