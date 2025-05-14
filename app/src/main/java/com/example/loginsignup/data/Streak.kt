package com.example.loginsignup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

//Entity
@Entity(
    tableName = "streaks",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["user_id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("user_id")]
)
data class Streak(
    @PrimaryKey(autoGenerate = true) val streak_id: Int = 0,
    @ColumnInfo(name = "user_id") val user_id: Int,
    @ColumnInfo(name = "last_logged_date") val lastLoggedDate: String,
    @ColumnInfo(name = "current_streak") val currentStreak: Int
)
