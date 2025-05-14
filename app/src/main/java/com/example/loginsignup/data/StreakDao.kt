package com.example.loginsignup.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE user_id = :userId")
    suspend fun getStreak(userId: Int): Streak?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: Streak)
}
