package com.example.caid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.splashactivity.*

class SplashActivity : AppCompatActivity() {
    //Loading time of the splashscreen
    private val splashTimer:Long = 1000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashactivity)
        //After the splashscreen has been displayed for the set time the main activity is started
        Handler().postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }, splashTimer)
    }
}