package com.example.loginsignup.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

//Dao
@Dao
interface ExpenseDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense) : Long

    @Transaction
    @Query("SELECT * FROM expenses WHERE user_id = :userId")
    fun getExpensesByUser(userId: Int): List<ExpenseWithCategory>

    @Query("SELECT * FROM expenses INNER JOIN categories ON expenses.category_id = categories.category_id")
    fun getAllExpensesWithCategory(): List<ExpenseWithCategory>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE expense_id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Int)
}

