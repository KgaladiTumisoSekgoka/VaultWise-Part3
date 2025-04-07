package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry

import java.util.ArrayList

class Chart : AppCompatActivity() {

    private lateinit var anyChartView: AnyChartView
    private val months = arrayOf("January", "February", "March", "April")
    private val salary = intArrayOf(12000, 18922, 15410, 10000) // Add a value for April

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)

        anyChartView = findViewById(R.id.anyChartView)

        setupChartView()

        // Find button by ID
        val btnHome = findViewById<ImageButton>(R.id.imageButton9)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
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

    private fun setupChartView() {
        val pie = AnyChart.pie()

        val dataEntries: MutableList<DataEntry> = ArrayList()

        for (i in months.indices) {
            dataEntries.add(ValueDataEntry(months[i], salary[i]))
        }

        pie.data(dataEntries)
        pie.title("Salary")

        anyChartView.setChart(pie)
    }
}
