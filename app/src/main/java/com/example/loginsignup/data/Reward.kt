package com.example.loginsignup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

//Entity
@Entity(
    tableName = "rewards",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["user_id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("user_id")]
)
data class Reward(
    @PrimaryKey(autoGenerate = true) val reward_id: Int = 0,
    @ColumnInfo(name = "user_id") val user_id: Int,
    @ColumnInfo(name = "month") val month: String,
    @ColumnInfo(name = "rewardTitle") val rewardTitle: String,
    @ColumnInfo(name = "rewardDescription") val rewardDescription: String,
    @ColumnInfo(name = "dateEarned") val dateEarned: Long = System.currentTimeMillis()
)