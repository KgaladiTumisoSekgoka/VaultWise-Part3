package com.example.loginsignup.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, BudgetGoal::class, Expense::class, Category::class, Reward::class, Streak::class],
    version = 9)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun budgetGoalDao(): BudgetGoalDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun rewardDao(): RewardDao
    abstract fun streakDao(): StreakDao // ✅ Add this

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, // <- This is the class name
                    "budget_tracker_database"
                ).fallbackToDestructiveMigration() // handles version upgrades
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}