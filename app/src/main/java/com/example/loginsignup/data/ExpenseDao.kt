package com.example.loginsignup.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE user_id = :userId")
    suspend fun getExpensesByUser(userId: Int): List<Expense>

    @Delete
    suspend fun deleteExpense(expense: Expense)
}

