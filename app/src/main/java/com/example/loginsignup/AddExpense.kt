package com.example.loginsignup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class AddExpense : AppCompatActivity() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileNameDisplay: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)

        val btnAttach = findViewById<ImageButton>(R.id.imageButton23)
        fileNameDisplay = findViewById(R.id.textView19)

        filePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val fileUri: Uri? = result.data?.data
                    val fileName = getFileName(fileUri)
                    fileNameDisplay.setText(fileName)
                }
            }
        )

        btnAttach.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            filePickerLauncher.launch(intent)
        }

        val btnSubmit = findViewById<Button>(R.id.button6)
        btnSubmit.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
        val btnCustomCategory = findViewById<Button>(R.id.button5)
        btnCustomCategory.setOnClickListener {
            val intent = Intent(this, SelectCategory::class.java)
            startActivity(intent)
        }

        val btnHome = findViewById<ImageButton>(R.id.imageButton19)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }

        // **Spinner Setup**
        val spinner: Spinner = findViewById(R.id.spinner2)
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this, R.array.expense_categories, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                Toast.makeText(applicationContext, "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // **AutoCompleteTextView Setup**
        /*val categories = arrayOf("Food", "Transport", "Entertainment", "Shopping", "Other")
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteCategory)
        val autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        autoCompleteTextView.setAdapter(autoCompleteAdapter)

        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedCategory = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
        }*/
    }

    private fun getFileName(uri: Uri?): String {
        return uri?.path?.substringAfterLast("/") ?: "Unknown File"
    }
}
