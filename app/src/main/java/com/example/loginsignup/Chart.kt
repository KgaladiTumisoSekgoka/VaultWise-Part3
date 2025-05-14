package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.ExpenseDao
import android.view.View
import android.widget.AdapterView


import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Chart : AppCompatActivity() {

    private lateinit var anyChartView: AnyChartView
    private val categories = arrayOf("Food", "Transport", "Entertainment", "Utilities", "Other")
    private val expenses = intArrayOf(2500, 1800, 1200, 800, 700)
    private lateinit var db: AppDatabase // Your RoomDB class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)

        db = AppDatabase.getDatabase(this)

        // ✅ Retrieve userId like you do in HomeScreen
        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)
        Log.d("ChartActivity", "User ID: $userId")

        val expenseDao = db.expenseDao()

        lifecycleScope.launch {
            val expensesByCategory = expenseDao.getTotalExpensesByCategory(userId)
            if (expensesByCategory.isEmpty()) {
                Log.d("ChartActivity", "No expenses found for the user.")
            } else {
                setupChartView(expensesByCategory)
            }
        }
        val spinner: Spinner = findViewById(R.id.spinnerPeriod)
        val periodOptions = listOf("Today", "This Month", "This Quarter", "This Half-Year", "This Year")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periodOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val datePattern = when (periodOptions[position]) {
                    "Today" -> SimpleDateFormat("yyyy-MM-dd").format(Date())
                    "This Month" -> SimpleDateFormat("yyyy-MM").format(Date())
                    "This Quarter" -> getQuarterDatePrefix()
                    "This Half-Year" -> getHalfYearDatePrefix()
                    "This Year" -> SimpleDateFormat("yyyy").format(Date())
                    else -> ""
                }

                lifecycleScope.launch {
                    val expensesByCategory = if (periodOptions[position] == "This Quarter" || periodOptions[position] == "This Half-Year") {
                        getCustomPeriodExpenses(userId, datePattern)
                    } else {
                        db.expenseDao().getExpensesByCategoryForPeriod(userId, datePattern)
                    }
                    setupChartView(expensesByCategory)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        anyChartView = findViewById(R.id.anyChartView)
        //setupChartView()

        val btnHome = findViewById<ImageButton>(R.id.imageButton9)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        val btnViewMinandMaxBudgetGoal = findViewById<Button>(R.id.button5)
        btnViewMinandMaxBudgetGoal.setOnClickListener {
            val intent = Intent(this, MaxandMinGoalView::class.java)
            startActivity(intent)
        }

        val btnTransact = findViewById<ImageButton>(R.id.imageButton10)
        btnTransact.setOnClickListener {
            val intent = Intent(this, Transactions::class.java)
            startActivity(intent)
        }

        val btnFAB = findViewById<ImageButton>(R.id.imageButton8)
        btnFAB.setOnClickListener {
            val intent = Intent(this, AddExpense::class.java)
            startActivity(intent)
        }

        val btnChart = findViewById<ImageButton>(R.id.imageButton11)
        btnChart.setOnClickListener {
            val intent = Intent(this, Chart::class.java)
            startActivity(intent)
        }

        val btnMore = findViewById<ImageButton>(R.id.imageButton12)
        btnMore.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)

        }
    }
    private fun setupChartView(data: List<ExpenseDao.CategoryExpenseTotal>) {
        val pie = AnyChart.pie()
        val dataEntries = data.map {
            ValueDataEntry(it.category_name, it.total)
        }
        if (data.isEmpty()) {
            pie.title("No expense data found for selected period")
            Log.d("ChartActivity", "No expense data found for selected period.")
            return
        }
        pie.data(dataEntries)
        pie.title("Expenses by Category")
        pie.labels().position("outside")
        pie.legend().title().enabled(true)
        pie.legend().title().text("Categories")
        pie.legend().position("center-bottom")
        pie.legend().itemsLayout("horizontal")
        pie.legend().align("center")
        pie.palette(arrayOf("#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0"))

        anyChartView.setChart(pie)
    }
    fun getQuarterDatePrefix(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val startMonth = when (month) {
            in 1..3 -> "01"
            in 4..6 -> "04"
            in 7..9 -> "07"
            else -> "10"
        }
        return "$year-$startMonth" // We can use this in range queries
    }

    fun getHalfYearDatePrefix(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val startMonth = if (month <= 6) "01" else "07"
        return "$year-$startMonth"
    }

    suspend fun getCustomPeriodExpenses(userId: Int, startPrefix: String): List<ExpenseDao.CategoryExpenseTotal> {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        cal.time = sdf.parse(startPrefix) ?: Date()

        val start = sdf.format(cal.time)
        cal.add(Calendar.MONTH, if (startPrefix.endsWith("01") || startPrefix.endsWith("07")) 6 else 3)
        val end = sdf.format(cal.time)

        val allDates = db.expenseDao().getLoggedDatesForUser(userId)
        val filtered = allDates.filter { it.substring(0, 7) >= start && it.substring(0, 7) < end }

        val result = mutableMapOf<String, Double>()
        for (date in filtered) {
            val partial = db.expenseDao().getExpensesByCategoryForPeriod(userId, date.substring(0, 7))
            for (item in partial) {
                result[item.category_name] = (result[item.category_name] ?: 0.0) + item.total
            }
        }

        return result.map { ExpenseDao.CategoryExpenseTotal(it.key, it.value) }
    }

}