package com.example.loginsignup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

//Entity
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("category_id"), Index("user_id")]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val expense_id: Int = 0,
    @ColumnInfo(name = "date") val date: String, // Format: YYYY-MM-DD
    @ColumnInfo(name = "start_time") val startTime: String?,
    @ColumnInfo(name = "end_time") val endTime: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "photo_path") val photoPath: String?,
    @ColumnInfo(name = "category_id") val category_id: Int?,
    @ColumnInfo(name = "user_id") val user_id: Int
)
