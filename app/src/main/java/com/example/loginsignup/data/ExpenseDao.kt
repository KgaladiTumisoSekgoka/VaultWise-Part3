package com.example.loginsignup.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Query("""
        SELECT expenses.*, categories.category_name 
        FROM expenses 
        LEFT JOIN categories ON expenses.category_id = categories.category_id
        WHERE expenses.user_id = :userId AND expenses.date BETWEEN :startDate AND :endDate
    """)
    fun getExpensesWithCategory(userId: Int, startDate: String, endDate: String): List<ExpenseWithCategory>

    @Query("""
        SELECT categories.category_name, SUM(expenses.amount) AS total_spent 
        FROM expenses 
        LEFT JOIN categories ON expenses.category_id = categories.category_id 
        WHERE expenses.user_id = :userId AND expenses.date BETWEEN :startDate AND :endDate 
        GROUP BY categories.category_name
    """)
    fun getTotalSpentPerCategory(userId: Int, startDate: String, endDate: String): List<CategoryTotal>
}
