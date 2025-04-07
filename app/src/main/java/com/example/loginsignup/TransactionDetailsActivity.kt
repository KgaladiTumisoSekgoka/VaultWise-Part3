package com.example.loginsignup

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TransactionDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        // Get Data from Intent
        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val amount = intent.getStringExtra("amount")
        val iconResId = intent.getIntExtra("icon", R.drawable.ic_launcher_foreground)

        // Find Views
        val titleTextView = findViewById<TextView>(R.id.transactionTitle)
        val dateTextView = findViewById<TextView>(R.id.transactionDate)
        val amountTextView = findViewById<TextView>(R.id.transactionAmount)
        val iconImageView = findViewById<ImageView>(R.id.transactionIcon)

        // Set Data to Views
        titleTextView.text = title
        dateTextView.text = date
        amountTextView.text = amount
        iconImageView.setImageResource(iconResId)
    }
}
