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
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, // <- This should match the class name
                    "budget_tracker_database"
                ).fallbackToDestructiveMigration() // Optional: handles version upgrades
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}