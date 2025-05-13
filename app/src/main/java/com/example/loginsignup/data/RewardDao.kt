package com.example.loginsignup.data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface RewardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: Reward)

    @Query("SELECT * FROM rewards WHERE user_id = :userId")
    suspend fun getRewardsForUser(userId: Int): List<Reward>

}
