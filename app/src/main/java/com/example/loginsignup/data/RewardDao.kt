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

    @Query("SELECT * FROM rewards WHERE user_id = :userId AND rewardTitle = :title LIMIT 1")
    fun getRewardByTitle(userId: Int, title: String): Reward?

    @Query("SELECT EXISTS(SELECT 1 FROM rewards WHERE user_id = :userId AND month = :month) LIMIT 1")
    suspend fun hasReceivedBudgetReward(userId: Int, month: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rewards: List<Reward>)

    @Update
    suspend fun updateReward(reward: Reward)

    @Delete
    suspend fun deleteReward(reward: Reward)


}
