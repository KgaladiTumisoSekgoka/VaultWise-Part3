package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.ExpenseDao
import com.example.loginsignup.data.ExpenseWithCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.ItemTouchHelper

class Transactions : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var expenseDao: ExpenseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactions)

        // ---- Navigation Buttons Setup ----
        val btnHome = findViewById<ImageButton>(R.id.imageButton13)
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }

        val btnTransact = findViewById<ImageButton>(R.id.imageButton14)
        btnTransact.setOnClickListener {
            // Optional: Show a toast instead of restarting the same screen
        }

        val btnFAB = findViewById<ImageButton>(R.id.imageButton17)
        btnFAB.setOnClickListener {
            startActivity(Intent(this, AddExpense::class.java))
        }

        val btnChart = findViewById<ImageButton>(R.id.imageButton15)
        btnChart.setOnClickListener {
            startActivity(Intent(this, Chart::class.java))
        }

        val btnMore = findViewById<ImageButton>(R.id.imageButton16)
        btnMore.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // ---- RecyclerView Setup ----
        recyclerView = findViewById(R.id.recyclerView) // Make sure your XML uses this ID
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // ---- Get Database DAO ----
        val db = AppDatabase.getDatabase(applicationContext)
        expenseDao = db.expenseDao()

        // ---- Get Logged-in User ID ----
        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)

        // ---- Load Expenses for User ----
        lifecycleScope.launch {
            val expenses = withContext(Dispatchers.IO) {
                expenseDao.getExpensesByUser(userId)
            }

            if (expenses.isNotEmpty()) {
                transactionAdapter = TransactionAdapter(
                    expenses,
                    onItemClick = { selectedExpense ->
                        val intent = Intent(this@Transactions, EditTransactionActivity::class.java).apply {
                            putExtra("title", selectedExpense.expense.title)
                            putExtra("description", selectedExpense.expense.description)
                            putExtra("amount", selectedExpense.expense.amount)
                            putExtra("date", selectedExpense.expense.date)
                            putExtra("startTime", selectedExpense.expense.startTime)
                            putExtra("category", selectedExpense.category.category_name)
                            putExtra("photoPath", selectedExpense.expense.photoPath)
                        }
                        startActivity(intent)
                    },
                    onEditClick = { selectedExpense ->
                        val intent = Intent(this@Transactions, EditTransactionActivity::class.java).apply {
                            putExtra("expenseId", selectedExpense.expense.expense_id)
                        }
                        startActivity(intent)
                    },
                    onDeleteClick = { selectedExpense ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                expenseDao.deleteExpenseById(selectedExpense.expense.expense_id)

                            }

                            // Refresh the list after deletion
                            val updatedExpenses = withContext(Dispatchers.IO) {
                                expenseDao.getExpensesByUser(userId)
                            }

                            transactionAdapter.updateData(updatedExpenses)

                        }
                    }

                )
                recyclerView.adapter = transactionAdapter

                // ---- Swipe-to-Delete Logic ----
                val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean = false

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        val swipedExpense = transactionAdapter.getExpenseAt(position)

                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                expenseDao.deleteExpenseById(swipedExpense.expense.expense_id)
                            }

                            val updatedExpenses = withContext(Dispatchers.IO) {
                                expenseDao.getExpensesByUser(userId)
                            }

                            transactionAdapter.updateData(updatedExpenses)
                        }
                    }
                })

                itemTouchHelper.attachToRecyclerView(recyclerView)
            }
            }
        }

    }



