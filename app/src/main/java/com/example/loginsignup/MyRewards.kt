package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignup.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyRewards : AppCompatActivity() {
    private lateinit var rewardRecyclerView: RecyclerView
    private lateinit var db: AppDatabase // Your RoomDB class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_rewards)

        rewardRecyclerView = findViewById(R.id.rewardRecyclerView)
        rewardRecyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getDatabase(applicationContext)
        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)

        CoroutineScope(Dispatchers.IO).launch {
            val rewards = db.rewardDao().getRewardsForUser(userId)

            withContext(Dispatchers.Main) {
                rewardRecyclerView.adapter = RewardAdapter(rewards)
            }
        }

        val goBackButton = findViewById<ImageButton>(R.id.imageButton30)
        goBackButton.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }
    }
}