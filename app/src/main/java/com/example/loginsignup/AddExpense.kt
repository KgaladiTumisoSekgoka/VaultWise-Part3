package com.example.loginsignup

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.loginsignup.data.AppDatabase
import com.example.loginsignup.data.Category
import com.example.loginsignup.data.Expense
import com.example.loginsignup.data.ExpenseDao
import com.example.loginsignup.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AddExpense : AppCompatActivity() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileNameDisplay: EditText
    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var db: AppDatabase // Your RoomDB class
    private lateinit var expenseDao: ExpenseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)

        db = AppDatabase.getDatabase(this) //  getDatabase(context) function in your RoomDB class
        expenseDao = db.expenseDao()


        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Ask for Camera Permission
        val cameraProviderResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val bitmap = it.data?.extras?.get("data") as? Bitmap
                if (bitmap != null) {
                    binding.imgCameraImage.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.imageButton20.setOnClickListener(){
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraProviderResult.launch(intent)
        }

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

        binding.button6.setOnClickListener {
            val title = binding.editTextText3.text.toString()
            val description = binding.editTextText4.text.toString()
            val amount = binding.editTextNumberDecimal.text.toString().toDoubleOrNull() ?: 0.0
            val date = binding.editTextDate.text.toString()
            val time = binding.editTextTime.text.toString()
            val fileName = binding.textView19.text.toString()
            val selectedCategory = binding.spinner2.selectedItem?.toString() ?: "Other"

            val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val userId = prefs.getInt("USER_ID", -1)

            if (userId != -1) {
                val db = AppDatabase.getDatabase(this)

                // ✅ Make sure this is running inside a coroutine properly
                lifecycleScope.launch(Dispatchers.IO) {
                    // Get or insert category
                    var categoryId = db.categoryDao().getCategoryIdByNameAndUserId(selectedCategory, userId)

                    if (categoryId == null) {
                        val newCategory = Category(category_name = selectedCategory, user_id = userId)
                        categoryId = db.categoryDao().insert(newCategory).toInt()
                    }

                    val expense = Expense(
                        title = title,
                        description = description,
                        amount = amount,
                        date = date,
                        startTime = time,
                        photoPath = null,
                        filePath = if (fileName.isEmpty()) null else fileName,
                        category_id = categoryId,
                        user_id = userId
                    )

                    db.expenseDao().insertExpense(expense)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddExpense, "Expense added", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AddExpense, Transactions::class.java))
                    }
                }
            } else {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            }
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
    }

    private fun getFileName(uri: Uri?): String {
        return uri?.path?.substringAfterLast("/") ?: "Unknown File"
    }
}
