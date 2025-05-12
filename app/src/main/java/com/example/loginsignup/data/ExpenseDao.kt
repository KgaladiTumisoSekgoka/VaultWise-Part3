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

    @Query("SELECT * FROM expenses WHERE expense_id = :expenseId LIMIT 1")
    suspend fun getExpenseById(expenseId: Int): Expense?

    @Query("SELECT SUM(amount) FROM expenses WHERE user_id = :userId")
    suspend fun getTotalSpentByUser(userId: Int): Double?

    @Query("SELECT SUM(amount) FROM expenses WHERE user_id = :userId AND date LIKE :datePrefix || '%'")
    fun getTotalExpensesForMonth(userId: Int, datePrefix: String): Double?

    @Query("""
    SELECT categories.category_name, SUM(expenses.amount) as total 
    FROM expenses 
    INNER JOIN categories ON expenses.category_id = categories.category_id 
    WHERE expenses.user_id = :userId 
    GROUP BY categories.category_id
""")
    suspend fun getTotalExpensesByCategory(userId: Int): List<CategoryExpenseTotal>
    data class CategoryExpenseTotal(
        val category_name: String,
        val total: Double
    )

    @Query("""
    SELECT c.category_name, 
           SUM(e.amount) AS total,
           bg.min_goal,
           bg.max_goal
    FROM categories c
    LEFT JOIN expenses e 
        ON c.category_id = e.category_id 
        AND e.user_id = :userId
        AND e.date LIKE :month || '%'
    LEFT JOIN budget_goals bg 
        ON bg.user_id = :userId AND bg.month = :month
    GROUP BY c.category_id
""")
    suspend fun getCategoryExpensesWithGoals(userId: Int, month: String): List<CategoryExpenseWithGoals>


    data class CategoryExpenseWithGoals(
        val category_name: String,
        val total: Double?,
        val min_goal: Double?,
        val max_goal: Double?
    )

    @Update
    fun updateExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE expense_id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Int)
}

