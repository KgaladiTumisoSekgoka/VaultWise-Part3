package com.example.loginsignup

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class HelpandSupport : AppCompatActivity() {

    private var funnySound: MediaPlayer? = null
    private var bmwSound: MediaPlayer? = null

    private lateinit var bmwGif: ImageView
    private lateinit var submitButton: Button
    private lateinit var inputDescription: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_helpand_support)

        val backButton = findViewById<ImageButton>(R.id.backButton2)
        backButton.setOnClickListener { finish() }

        submitButton = findViewById(R.id.submitButton)
        inputDescription = findViewById(R.id.inputDescription)
        bmwGif = findViewById(R.id.bmwGif)

        // Initialize MediaPlayers
        funnySound = MediaPlayer.create(this, R.raw.funny_sound)
        bmwSound = MediaPlayer.create(this, R.raw.bmw_sound)

        submitButton.setOnClickListener {
            val problem = inputDescription.text.toString().trim()

            if (problem.isEmpty()) {
                inputDescription.error = "Please enter a description"
                return@setOnClickListener
            }

            if (problem.contains("bmw", ignoreCase = true)) {
                // Show GIF and play BMW sound
                bmwGif.visibility = ImageView.VISIBLE
                Glide.with(this).asGif().load(R.drawable.bmw_gif).into(bmwGif)
                bmwSound?.start()

                // Hide GIF after 5 seconds
                bmwGif.postDelayed({ bmwGif.visibility = ImageView.GONE }, 5000)
            } else {
                // Play funny sound
                funnySound?.start()
                bmwGif.visibility = ImageView.GONE
            }

            Toast.makeText(this, "Support request sent! We'll get back to you soon.", Toast.LENGTH_LONG).show()
            inputDescription.text.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        funnySound?.release()
        bmwSound?.release()
    }
}