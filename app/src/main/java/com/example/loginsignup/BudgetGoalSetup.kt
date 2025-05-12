package com.example.loginsignup

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.BudgetGoal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetGoalSetup : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var userId: Int = -1
    private lateinit var monthSpinner: Spinner
    private lateinit var minGoalEditText: EditText
    private lateinit var maxGoalEditText: EditText

    private val months = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget_goal_setup)

        db = AppDatabase.getDatabase(applicationContext)

        // UI Elements
        val btnSubmit = findViewById<Button>(R.id.button7)
        val btnReset = findViewById<Button>(R.id.button8)
        val btnBack = findViewById<ImageButton>(R.id.imageButton18)
        progressBar = findViewById(R.id.budgetProgressBar)
        progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
        progressText = findViewById(R.id.budgetProgressText)

        minGoalEditText = findViewById(R.id.editTextNumberDecimal2)
        maxGoalEditText = findViewById(R.id.editTextNumber)

        monthSpinner = findViewById(R.id.spinner_month)

        monthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)

        btnBack.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
            finish()
        }

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("USER_ID", -1)

        val savedMonth = sharedPref.getString("budget_month", "January") ?: "January"
        val monthIndex = months.indexOf(savedMonth)
        if (monthIndex >= 0) monthSpinner.setSelection(monthIndex)

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedMonth = months[position]
                sharedPref.edit().putString("budget_month", selectedMonth).apply()
                val currentYear = java.time.LocalDate.now().year
                val monthNumber = position + 1
                val formattedMonth = monthNumber.toString().padStart(2, '0')
                val datePrefix = "$currentYear/$formattedMonth"

                sharedPref.edit().putString("budget_month", selectedMonth).apply()
                loadBudgetData(selectedMonth, datePrefix)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSubmit.setOnClickListener {
            val minGoal = minGoalEditText.text.toString().toDoubleOrNull()
            val maxGoal = maxGoalEditText.text.toString().toDoubleOrNull()
            val selectedMonth = monthSpinner.selectedItem.toString()

            if (minGoal == null || maxGoal == null ) {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val dao = db.budgetGoalDao()
                val existingGoal = dao.getBudgetByUserAndMonth(userId, selectedMonth)

                if (existingGoal != null) {
                    val updatedGoal = existingGoal.copy(
                        minGoal = minGoal,
                        maxGoal = maxGoal
                        //remainingBudget = budgetAmount
                    )
                    dao.updateGoal(updatedGoal)
                } else {
                    val newGoal = BudgetGoal(
                        user_id = userId,
                        month = selectedMonth,
                        minGoal = minGoal,
                        maxGoal = maxGoal
                    )
                    dao.insertGoal(newGoal)
                }
                // Switch back to the main thread to show toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BudgetGoalSetup, "Budget has been updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnReset.setOnClickListener {
            minGoalEditText.text.clear()
            maxGoalEditText.text.clear()
            monthSpinner.setSelection(0)
            progressBar.progress = 0
            progressText.text = ""
            Toast.makeText(this, "Budget has been cleared successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBudgetData(month: String, datePrefix: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val goalDao = db.budgetGoalDao()
            val expenseDao = db.expenseDao()
            val goal = goalDao.getBudgetByUserAndMonth(userId, month)
            //val datePrefix = month  // month is already like "2025/04"
            val totalExpenses = expenseDao.getTotalExpensesForMonth(userId, datePrefix) ?: 0.0


            runOnUiThread {
                if (goal != null) {
                    minGoalEditText.setText(goal.minGoal.toString())
                    maxGoalEditText.setText(goal.maxGoal.toString())

                    updateProgressBar(goal.minGoal, goal.maxGoal, totalExpenses)
                } else {
                    minGoalEditText.setText("")
                    maxGoalEditText.setText("")
                    progressBar.progress = 0
                    progressText.text = ""
                }
            }
        }
    }

    private fun updateProgressBar(min: Double, max: Double, current: Double) {
        if (max > min) {
            val progress = (current / max * 100).toInt().coerceIn(0, 100)
            progressBar.progress = progress
            progressText.text = "Progress: $progress%\nSpent: R$current / R$max"

            val colorRes = when {
                progress < 50 -> R.color.blue       // Safe zone
                progress in 50..79 -> R.color.orange // Warning zone
                else -> R.color.red                 // Danger zone
            }

            progressBar.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, colorRes))
        } else {
            progressBar.progress = 0
            progressText.text = "Invalid goal range"
            progressBar.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray))
        }
    }
}
