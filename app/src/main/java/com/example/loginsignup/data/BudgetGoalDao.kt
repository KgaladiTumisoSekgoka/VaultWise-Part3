package com.example.loginsignup.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

//Dao
@Dao
interface BudgetGoalDao {

    // Insert a new BudgetGoal

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: BudgetGoal)

    // Get BudgetGoal by user_id
    @Query("SELECT * FROM budget_goals WHERE user_id = :userId AND month = :month LIMIT 1")
    suspend fun getGoalByUserAndMonth(userId: Int, month: String): BudgetGoal?

    @Query("SELECT * FROM budget_goals WHERE user_id = :userId")
    suspend fun getGoalsByUser(userId: Int): List<BudgetGoal>

    @Query("DELETE FROM budget_goals WHERE month = :month")
    suspend fun deleteGoalsByMonth(month: String)

    @Query("DELETE FROM budget_goals WHERE user_id = :userId")
    suspend fun deleteGoalsByUser(userId: Int)

    // Update an existing BudgetGoal

    @Update
    suspend fun updateGoal(goal: BudgetGoal)

    @Delete
    suspend fun deleteGoal(goal: BudgetGoal)
}
