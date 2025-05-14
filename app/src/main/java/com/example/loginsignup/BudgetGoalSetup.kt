package com.example.loginsignup

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieDrawable
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.BudgetGoal
import com.example.loginsignup.data.Reward
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

            if (minGoal > maxGoal) {
                Toast.makeText(this, "Min goal cannot be greater than Max goal", Toast.LENGTH_SHORT).show()
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
                    // Refresh the UI (clear the fields and reset the progress bar)
                    refreshPage()
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


    private fun refreshPage() {
        minGoalEditText.text.clear()  // Clear the minGoal input field
        maxGoalEditText.text.clear()  // Clear the maxGoal input field
        monthSpinner.setSelection(0)  // Reset month spinner to default selection
        progressBar.progress = 0  // Reset progress bar to 0
        progressText.text = ""  // Clear the progress text
    }
    private fun loadBudgetData(month: String, datePrefix: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val goalDao = db.budgetGoalDao()
            val expenseDao = db.expenseDao()
            val goal = goalDao.getBudgetByUserAndMonth(userId, month)
            val totalExpenses = expenseDao.getTotalExpensesForMonth(userId, datePrefix) ?: 0.0

            withContext(Dispatchers.Main) {
                if (goal != null) {
                    minGoalEditText.setText(goal.minGoal.toString())
                    maxGoalEditText.setText(goal.maxGoal.toString())

                    updateProgressBar(goal.minGoal, goal.maxGoal, totalExpenses)

                    val lottieView = findViewById<com.airbnb.lottie.LottieAnimationView>(R.id.lottieAnimationView)
                    val lottieCard = findViewById<androidx.cardview.widget.CardView>(R.id.lottieCard)

                    val warningCard = findViewById<androidx.cardview.widget.CardView>(R.id.lottieCard2)
                    val warningLottieView = findViewById<com.airbnb.lottie.LottieAnimationView>(R.id.lottieAnimationView2)
                    val warningMessage = findViewById<TextView>(R.id.warningMessage)

                    // Calculate percentage spent
                    val percentageSpent = (totalExpenses / goal.maxGoal) * 100

                    // Show warning animation if 90% or more of the max goal is reached (but still under 100%)
                    if (percentageSpent in 90.0..100.0) {
                        warningCard.visibility = View.VISIBLE  // Show warning card
                        warningLottieView.repeatCount = 2  // Repeat the animation 3 times (since the count starts from 0)
                        warningLottieView.repeatMode = LottieDrawable.RESTART  // Restart animation on each repeat
                        warningLottieView.playAnimation() // Play warning animation
                        warningMessage.text = "⚠️ You’ve spent 90% or more of your budget!" // Display warning message
                    } else {
                        warningCard.visibility = View.GONE  // Hide warning card if less than 90%
                    }

                    // 🎉 If within budget range (1% to 89%), show animation and congrats message
                    if (percentageSpent in 1.0..89.9) {
                        lottieCard.visibility = View.VISIBLE
                        lottieView.visibility = View.VISIBLE

                        lottieView.repeatCount = 2
                        lottieView.repeatMode = LottieDrawable.RESTART
                        lottieView.playAnimation()

                        lottieView.addAnimatorListener(object : android.animation.AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: android.animation.Animator) {
                                lottieCard.visibility = View.GONE
                            }
                        })

                        Toast.makeText(this@BudgetGoalSetup, "🎉 Congrats! You’re doing great and staying within budget!", Toast.LENGTH_LONG).show()
                    } else {
                        lottieCard.visibility = View.GONE
                        lottieView.visibility = View.GONE
                    }
                } else {
                    minGoalEditText.setText("")
                    maxGoalEditText.setText("")
                    progressBar.progress = 0
                    progressText.text = ""

                    findViewById<androidx.cardview.widget.CardView>(R.id.lottieCard).visibility = View.GONE
                    findViewById<com.airbnb.lottie.LottieAnimationView>(R.id.lottieAnimationView).visibility = View.GONE
                    findViewById<androidx.cardview.widget.CardView>(R.id.lottieCard2).visibility = View.GONE // Hide warning card if no goal
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
