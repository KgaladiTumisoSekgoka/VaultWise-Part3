package com.example.loginsignup.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, BudgetGoal::class, Expense::class, Category::class],
    version = 1)

abstract class AppDatabase : RoomDatabase() {
    abstract fun UserDao(): UserDao
    abstract fun BudgetGoalDao(): BudgetGoalDao
    abstract fun ExpenseDao(): ExpenseDao
    abstract fun CategoryDao(): CategoryDao

}