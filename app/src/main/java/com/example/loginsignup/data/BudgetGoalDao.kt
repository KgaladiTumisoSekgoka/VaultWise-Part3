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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: BudgetGoal)

    @Query("SELECT * FROM budget_goals WHERE user_id = :userId")
    suspend fun getGoalsByUser(userId: Int): List<BudgetGoal>

    @Update
    suspend fun updateGoal(goal: BudgetGoal)

    @Delete
    suspend fun deleteGoal(goal: BudgetGoal)
}
