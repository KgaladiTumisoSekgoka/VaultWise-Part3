package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.BudgetGoalDao
import com.example.loginsignup.data.ExpenseDao

import kotlinx.coroutines.launch

import java.util.ArrayList

class Chart : AppCompatActivity() {

    private lateinit var anyChartView: AnyChartView
    private lateinit var db: AppDatabase // Your RoomDB class
    private lateinit var budgetDao: BudgetGoalDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)

        db = AppDatabase.getDatabase(this)
        budgetDao = db.budgetGoalDao()

        // ✅ Retrieve userId like you do in HomeScreen
        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)
        Log.d("ChartActivity", "User ID: $userId")

        val expenseDao = db.expenseDao()

        lifecycleScope.launch {
            // Fetch budget goals for the user
            val budgetGoal = budgetDao.getCurrentGoalByUserId(userId)  // Fetch the goal for the user
            val minGoal = budgetGoal?.minGoal ?: 0.0  // Use 0.0 if no goal found
            val maxGoal = budgetGoal?.maxGoal ?: 0.0

            // Fetch expenses by category
            val expensesByCategory = expenseDao.getTotalExpensesByCategory(userId)
            if (expensesByCategory.isEmpty()) {
                Log.d("ChartActivity", "No expenses found for the user.")
            } else {
                setupBarChart(expensesByCategory, minGoal, maxGoal)
            }
        }

        anyChartView = findViewById(R.id.anyChartView)
    }

    private fun setupBarChart(data: List<ExpenseDao.CategoryExpenseTotal>, minGoal: Double, maxGoal: Double) {
        // Create a Cartesian chart (which allows both bar and line chart features)
        val cartesian = AnyChart.cartesian()

        // Create the bar chart for categories
        val dataEntries = data.map {
            ValueDataEntry(it.category_name, it.total)
        }

        val barSeries = cartesian.column(dataEntries)
        barSeries.name("Amount Spent")

        // Add the min and max goal lines (as line charts)
        val minGoalLineData = listOf(
            ValueDataEntry("Min Goal", minGoal),
            ValueDataEntry("Min Goal", minGoal)
        )
        val minGoalLine = cartesian.line(minGoalLineData)
        minGoalLine.name("Min Goal").color("#FF0000")  // Red line for Min Goal

        val maxGoalLineData = listOf(
            ValueDataEntry("Max Goal", maxGoal),
            ValueDataEntry("Max Goal", maxGoal)
        )
        val maxGoalLine = cartesian.line(maxGoalLineData)
        maxGoalLine.name("Max Goal").color("#00FF00")  // Green line for Max Goal

        // Set titles and labels
        cartesian.title("Amount Spent by Category")
        cartesian.xAxis(0).title("Category")
        cartesian.yAxis(0).title("Amount Spent")
        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")  // Format the amount

        // Set up the chart view
        anyChartView.setChart(cartesian)
    }
}







