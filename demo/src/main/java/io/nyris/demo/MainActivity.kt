package io.nyris.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.nyris.ui.NyrisSearcher

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun click(view: View) {
        NyrisSearcher(this, apiKey = "YOUR_API_KEY", isDebug = true)
            //.host("")
            .dialogErrorTitle("Error Dialog")
            .positiveButtonText("My OK")
            .captureLabelText("My Capture")
            .noOfferFoundErrorText("No offers found :(")
            .cameraPermissionDeniedErrorMessage("You need to accept the camera permission!")
            .limit(20)
            .start()
    }
}
