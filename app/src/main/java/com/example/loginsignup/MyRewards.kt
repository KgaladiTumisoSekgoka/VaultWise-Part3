package com.example.loginsignup

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.Reward
import com.example.loginsignup.data.RewardDao
import com.example.loginsignup.data.Streak
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MyRewards : AppCompatActivity() {

    private lateinit var rewardDao: RewardDao
    private lateinit var rewardRecyclerView: RecyclerView
    private lateinit var adapter: RewardAdapter
    private var userId: Int = 1 // Example user ID (replace this with actual user ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_rewards)

        // Set up the RecyclerView
        rewardRecyclerView = findViewById(R.id.rewardRecyclerView)
        rewardRecyclerView.layoutManager = GridLayoutManager(this, 2) // Use GridLayout or LinearLayout

        rewardDao = AppDatabase.getDatabase(this).rewardDao()

        // Insert dummy data and then load rewards
        //insertDummyDataIfNeeded()
        loadRewards()
        checkAndAwardStepMaster()

        val streakCard = findViewById<FrameLayout>(R.id.streakCardContainer)
        val inflater = layoutInflater
        val streakView = inflater.inflate(R.layout.reward_streak_card, streakCard, false)
        streakCard.addView(streakView)

        // Show streak card when user logs any activity
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dates = db.expenseDao().getLoggedDatesForUser(userId)
            if (dates.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    streakCard.visibility = View.VISIBLE
                }
            }
        }

    }

    // Load rewards from the database
    private fun loadRewards() {
        lifecycleScope.launch {
            try {
                val rewards = withContext(Dispatchers.IO) {
                    rewardDao.getRewardsForUser(userId)
                }

                if (rewards.isEmpty()) {
                    Toast.makeText(this@MyRewards, "No rewards found.", Toast.LENGTH_SHORT).show()
                }

                adapter = RewardAdapter(rewards)
                rewardRecyclerView.adapter = adapter

            } catch (e: Exception) {
                Toast.makeText(this@MyRewards, "Failed to load rewards", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndAwardStepMaster() {
        lifecycleScope.launch {
            try {
                val expenseDao = AppDatabase.getDatabase(this@MyRewards).expenseDao()
                val rewardDao = AppDatabase.getDatabase(this@MyRewards).rewardDao()

                val dateStrings = withContext(Dispatchers.IO) {
                    expenseDao.getLoggedDatesForUser(userId)
                }

                val dates = dateStrings.mapNotNull {
                    try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)
                    } catch (e: Exception) {
                        null
                    }
                }.sorted()

                var streak = 1
                for (i in 1 until dates.size) {
                    val diff = (dates[i].time - dates[i - 1].time) / (1000 * 60 * 60 * 24)
                    if (diff == 1L) {
                        streak++
                        if (streak == 7) break
                    } else if (diff > 1L) {
                        streak = 1
                    }
                }

                if (streak >= 7) {
                    val alreadyAwarded = withContext(Dispatchers.IO) {
                        rewardDao.getRewardByTitle(userId, "Step Master")
                    }

                    if (alreadyAwarded == null) {
                        val reward = Reward(
                            user_id = userId,
                            month = SimpleDateFormat("MMMM", Locale.getDefault()).format(System.currentTimeMillis()),
                            rewardTitle = "Step Master",
                            rewardDescription = "Logged expenses for 7 days in a row!",
                            dateEarned = System.currentTimeMillis(),
                            iconResId = R.drawable.step_master
                        )

                        withContext(Dispatchers.IO) {
                            rewardDao.insertReward(reward)
                        }
                        withContext(Dispatchers.Main) {
                            loadRewards() // ✅ Refresh UI
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MyRewards, "Failed to check Step Master badge", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
