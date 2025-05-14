package com.example.loginsignup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.Reward
import com.example.loginsignup.data.RewardDao
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
        insertDummyData()
        loadRewards()
    }

    // Insert dummy data into the database
    private fun insertDummyData() {
        lifecycleScope.launch {
            try {
                val dummyRewards = listOf(
                    Reward(
                        user_id = userId,
                        month = "May",
                        rewardTitle = "Step Master",
                        rewardDescription = "Awarded for walking 10,000 steps!",
                        dateEarned = System.currentTimeMillis(),
                        iconResId = R.drawable.step_master// Image for Wellness Warrior
                    ),
                    Reward(
                        user_id = userId,
                        month = "April",
                        rewardTitle = "Budget Boss",
                        rewardDescription = "Awarded for staying under budget this month!",
                        dateEarned = System.currentTimeMillis(),
                        iconResId = R.drawable.step_master // Image for Step Master
                    ),
                    Reward(
                        user_id = userId,
                        month = "March",
                        rewardTitle = "Wellness Warrior",
                        rewardDescription = "Awarded for maintaining a healthy lifestyle!",
                        dateEarned = System.currentTimeMillis(),
                        iconResId = R.drawable.budget_boss// Image for Wellness Warrior
                    )
                )

                // Insert the dummy data
                withContext(Dispatchers.IO) {
                    dummyRewards.forEach { reward ->
                        rewardDao.insertReward(reward)
                    }
                }
            } catch (e: Exception) {
                // Show error message in case of failure
                Toast.makeText(this@MyRewards, "Failed to insert dummy data", Toast.LENGTH_SHORT).show()
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

}
