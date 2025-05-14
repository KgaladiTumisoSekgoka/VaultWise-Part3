package com.example.loginsignup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.BudgetGoalDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaxandMinGoalView : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var budgetGoalDao: BudgetGoalDao
    private lateinit var chartView: AnyChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maxand_min_goal_view)

        db = AppDatabase.getDatabase(applicationContext)
        budgetGoalDao = db.budgetGoalDao()
        chartView = findViewById(R.id.anyChartView)

        loadChartData() // Call moved function

        val btnHome = findViewById<ImageButton>(R.id.imageButton9)
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }

        val backBtn = findViewById<ImageButton>(R.id.imageButton31)
        backBtn.setOnClickListener {
            startActivity(Intent(this, Chart::class.java))
        }

        val btnTransact = findViewById<ImageButton>(R.id.imageButton10)
        btnTransact.setOnClickListener {
            startActivity(Intent(this, Transactions::class.java))
        }

        val btnFAB = findViewById<ImageButton>(R.id.imageButton8)
        btnFAB.setOnClickListener {
            startActivity(Intent(this, AddExpense::class.java))
        }

        val btnChart = findViewById<ImageButton>(R.id.imageButton11)
        btnChart.setOnClickListener {
            startActivity(Intent(this, Chart::class.java))
        }

        val btnMore = findViewById<ImageButton>(R.id.imageButton12)
        btnMore.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun loadChartData() {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", -1)


        if (userId == -1) {
            Log.e("MaxMinGraph", "User ID not found")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val goals = budgetGoalDao.getGoalsByUser(userId)
            if (goals.isEmpty()) {
                Log.d("MaxMinGraph", "No goals found")
                return@launch
            }

            val data = mutableListOf<ValueDataEntry>()
            goals.forEach { goal ->
                data.add(ValueDataEntry("${goal.month} Min", goal.minGoal))
                data.add(ValueDataEntry("${goal.month} Max", goal.maxGoal))
            }

            val column = AnyChart.column()
            column.data(data as MutableList<DataEntry>)

            withContext(Dispatchers.Main) {
                chartView.setChart(column)
            }
        }
    }
}
