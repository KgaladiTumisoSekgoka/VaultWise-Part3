package com.example.loginsignup
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Transactions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactions)
        // Find button by ID
        val btnHome = findViewById<ImageButton>(R.id.imageButton13)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
        val btnTransact = findViewById<ImageButton>(R.id.imageButton14)
        btnTransact.setOnClickListener {
            val intent = Intent(this, Transactions::class.java)
            startActivity(intent)
        }
        val btnFAB = findViewById<ImageButton>(R.id.imageButton17)
        btnFAB.setOnClickListener {
            val intent = Intent(this, AddExpense::class.java)
            startActivity(intent)
        }
        val btnChart = findViewById<ImageButton>(R.id.imageButton15)
        btnChart.setOnClickListener {
            val intent = Intent(this, Chart::class.java)
            startActivity(intent)
        }
        val btnMore = findViewById<ImageButton>(R.id.imageButton16)
        btnMore.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //RecyclerView Setup
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)

        // Sample Transactions
        val transactionList = listOf(
            TransactionModel("Transport", "March 30, 2025", "-R50", R.drawable.blue_and_white_flat_illustrative_banking_finance_logo_removebg_preview),
            TransactionModel("Salary", "March 28, 2025", "+R5000", R.drawable.blue_and_white_flat_illustrative_banking_finance_logo_removebg_preview),
            TransactionModel("Groceries", "March 25, 2025", "-R200", R.drawable.blue_and_white_flat_illustrative_banking_finance_logo_removebg_preview),
            TransactionModel("Netflix", "March 20, 2025", "-R150", R.drawable.blue_and_white_flat_illustrative_banking_finance_logo_removebg_preview)
        )

        //
        val adapter = TransactionAdapter(transactionList)
        recyclerview.adapter = adapter
    }

}