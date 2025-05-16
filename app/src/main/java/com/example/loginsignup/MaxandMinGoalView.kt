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
import com.anychart.enums.Anchor
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.BudgetGoalDao
import com.example.loginsignup.data.ExpenseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaxandMinGoalView : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var budgetGoalDao: BudgetGoalDao
    private lateinit var expenseDao: ExpenseDao
    private lateinit var chartView: AnyChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maxand_min_goal_view)

        db = AppDatabase.getDatabase(applicationContext)
        budgetGoalDao = db.budgetGoalDao()
        expenseDao = db.expenseDao()
        chartView = findViewById(R.id.anyChartView)

        loadChartData()

        findViewById<ImageButton>(R.id.imageButton9).setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton31).setOnClickListener {
            startActivity(Intent(this, Chart::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton10).setOnClickListener {
            startActivity(Intent(this, Transactions::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton8).setOnClickListener {
            startActivity(Intent(this, AddExpense::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton11).setOnClickListener {
            startActivity(Intent(this, Chart::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton12).setOnClickListener {
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
            val expensesByCategory = expenseDao.getTotalExpensesByCategory(userId)

            if (goals.isEmpty()) {
                Log.d("MaxMinGraph", "No goals found")
                return@launch
            }

            val dataEntries = mutableListOf<DataEntry>()

            // Add min and max goals
            goals.forEach { goal ->
                dataEntries.add(ValueDataEntry("${goal.month} Min Goal", goal.minGoal))
                dataEntries.add(ValueDataEntry("${goal.month} Max Goal", goal.maxGoal))
            }

            // Add total expenses per category
            expensesByCategory.forEach { categoryTotal ->
                dataEntries.add(ValueDataEntry("${categoryTotal.category_name} Spent", categoryTotal.total))
            }

            val column = AnyChart.column()
            column.data(dataEntries)

            column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("R{%Value}")

            column.animation(true)
            column.title("Goals vs Expenses by Category")

            column.yScale().minimum(0.0)
            column.yAxis(0).labels().format("R{%Value}")

            column.tooltip().positionMode(TooltipPositionMode.POINT)

            withContext(Dispatchers.Main) {
                chartView.setChart(column)
            }
        }
    }
}
