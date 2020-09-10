package com.example.caid
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import kotlinx.android.synthetic.main.activity_main.*

/**
 Parts of the code taken from the Codelab: Getting started with CameraX
 Available at: https://codelabs.developers.google.com/codelabs/camerax-getting-started/#0
 **/

private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

private lateinit var viewFinder: TextureView
private val getScreenWidth = Resources.getSystem().displayMetrics.widthPixels //Gets the width of the phone (px)
private val getScreenHeight = Resources.getSystem().displayMetrics.heightPixels //Gets the height of the phone (px)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewFinder = findViewById(R.id.view_finder)


        // Request camera permissions
        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // As the viewfinder is updated the layout is updated
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //TODO: Implement detection of colors
        //Creates bitmap
        val bmp = viewFinder.getBitmap(getScreenWidth, getScreenHeight)
        //Fetches pixel from pressed area on bitmap
        val pixel = bmp.getPixel(event.x.toInt(), event.y.toInt())

        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)

        val floatArray = floatArrayOf(1F, 2F, 3F)

        val setColor = Color.rgb(red, green, blue)

        Color.RGBToHSV(red,green,blue, floatArray)

        val hue = floatArray[0]

        val saturation = floatArray[1]

        val value = floatArray[2]

        //Ranges for different colors. Uses H, S and V in the HSV-model

        //Grey
        if  ((hue in 0.0..360.0) and ((saturation in 0.0..0.11) and ((value in 0.151..1.0)))) {
            colorText.text = "Grey"
        }
        //Black
        if ((hue in 0.0..360.0) and ((value in 0.0..0.10))) {
            colorText.text = "Black"
        }
        //White
        if ((hue in 0.0..360.0) and ((value in 0.8..1.0))) {
            colorText.text = "White"
        }
        //Red
        if ((hue in 1.01..13.0) and ((saturation in 0.511..1.0) and ((value in 0.151..1.0)))) {
            colorText.text = "Red"
        }
        //Orange
        if ((hue in 21.0..40.0) and ((saturation in 0.611..1.0) and ((value in 0.451..1.0)))) {
            colorText.text = "Orange"
        }
        //Brown
        if((hue in 22.0..32.0) and ((value in 0.151..1.0))) {
            colorText.text = "Brown"
        }
        //Yellow
        if ((hue in 40.0..75.0) and ((saturation in 0.511..1.0) and ((value in 0.151..1.0)))) {
            colorText.text = "Yellow"
        }
        //Green
        if ((hue in 75.0..150.0) and ((saturation in 0.111..1.0) and ((value in 0.151..1.0)))) {
            colorText.text = "Green"
        }
        //Blue
        if ((hue in 150.0..250.0) and ((saturation in 0.111..1.0) and ((value in 0.151..1.0)))) {
            colorText.text = "Blue"
        }
        //Purple
        if ((hue in 250.0..275.0) and ((saturation in 0.111..1.0) and ((value in 0.151..1.0)))) {
            colorText.text = "Purple"
        }
        //Magenta
        if ((hue in 275.0..324.0) and ((saturation in 0.111..1.0) and ((value in 0.151..1.0)))) {
            colorText.text = "Magenta"
        }
        //Pink
        if ((hue in 324.0..360.0) and ((saturation in 0.111..1.0) and ((value in 0.151..1.0)))) {
            colorText.text = "Pink"
        }


        buttonDisplay.setBackgroundColor(setColor)
        bmp.recycle() //Recycle for reusage and memory managment
        return super.onTouchEvent(event)
    }

    //Function for starting the camera. Is initiated in onCreate
    private fun startCamera() {
        // Creates configuration object and sets the target resolution
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(getScreenWidth, getScreenHeight))
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        //Code for the flashbutton(s).
        flashOff.setOnClickListener{
            preview.enableTorch(true)
            flashOff.visibility = View.INVISIBLE
            flashOn.visibility = View.VISIBLE
        }

        flashOn.setOnClickListener{
            preview.enableTorch(false)
            flashOn.visibility = View.INVISIBLE
            flashOff.visibility = View.VISIBLE
        }


        // as the viewfinder changes the layout is updated.
        preview.setOnPreviewOutputUpdateListener {

            //Updates the surfacetexture (viewfinder) by removing and re-adding it.
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        //Binds it to its own lifecycle
        CameraX.bindToLifecycle(this, preview)
    }

    //Implements viewfinder transformations.
    private fun updateTransform() {
        // TODO: Implement camera viewfinder transformations
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */

    //Checks if the user has agreed to the application gaining access to the camera.
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post {startCamera()} //Starts the startCamera function, i.e. the camera, if permission is granted
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT //Displays toast if access is not granted.
                ).show()
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    //Checks if all permissions in the androidmanifest have been granted.
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}
