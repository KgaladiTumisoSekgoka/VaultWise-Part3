package com.example.loginsignup

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.example.loginsignup.data.Reward
import com.example.loginsignup.data.Streak
import com.example.loginsignup.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddExpense : AppCompatActivity() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileNameDisplay: EditText
    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var db: AppDatabase // Your RoomDB class
    private lateinit var expenseDao: ExpenseDao
    private var capturedPhotoPath: String? = null
    lateinit var editTextDate: TextView
    lateinit var btnShowDatePicker: Button
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)

        Log.d("AddExpense", "onCreate called")

        db = AppDatabase.getDatabase(this) //  getDatabase(context) function in your RoomDB class
        Log.d("AddExpense", "Database initialized")
        expenseDao = db.expenseDao()
        Log.d("AddExpense", "ExpenseDao initialized")

        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Ask for Camera Permission
        // ✅ Setup camera result
        val cameraProviderResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            Log.d("AddExpense", "Camera result received")
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                Log.d("AddExpense", "Bitmap received from camera")
                val bitmap = it.data?.extras?.get("data") as? Bitmap
                if (bitmap != null) {
                    binding.imgCameraImage.setImageBitmap(bitmap)
                    capturedPhotoPath = saveImageToInternalStorage(bitmap) // ✅ Save path
                    Log.d("AddExpense", "Image saved at path: $capturedPhotoPath")
                } else {
                    Log.e("AddExpense", "Failed to load image from camera")
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ✅ Launch camera
        binding.imageButton20.setOnClickListener {
            Log.d("AddExpense", "Camera button clicked")
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraProviderResult.launch(intent)
        }

        // ✅ File picker
        val btnAttach = findViewById<ImageButton>(R.id.imageButton23)
        fileNameDisplay = findViewById(R.id.textView19)
        filePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.d("AddExpense", "File picker result received")
            if (result.resultCode == RESULT_OK && result.data != null) {
                val fileUri: Uri? = result.data?.data
                val fileName = getFileName(fileUri)
                fileNameDisplay.setText(fileName)
            }
        }

        btnAttach.setOnClickListener {
            Log.d("AddExpense", "Attach file button clicked")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            filePickerLauncher.launch(intent)
        }

        binding.button6.setOnClickListener {
            Log.d("AddExpense", "Submit button clicked")
            val title = binding.editTextText3.text.toString()
            val description = binding.editTextText4.text.toString()
            val amount = binding.editTextNumberDecimal.text.toString().toDoubleOrNull() ?: 0.0
            val date = binding.editTextDate.text.toString()
            val time = binding.editTextTime.text.toString()
            val fileName = binding.textView19.text.toString()
            val selectedCategory = binding.spinner2.selectedItem?.toString() ?: "Other"
            val customCategoryText = findViewById<EditText>(R.id.editTextCustomCategory).text.toString().trim()
            Log.d("AddExpense", "Collected input: title=$title, amount=$amount, date=$date, time=$time")

            if (date.isBlank()) {
                Log.w("AddExpense", "Date is blank")
                Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (time.isBlank()) {
                Log.w("AddExpense", "Time is blank")
                Toast.makeText(this, "Please pick a time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val userId = prefs.getInt("USER_ID", -1)

            if (userId == -1) {
                Log.e("AddExpense", "User not logged in")
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val username = prefs.getString("username", "User")
            val homeIntent = Intent(this, HomeScreen::class.java)
            homeIntent.putExtra("username", username)
            startActivity(homeIntent)

            // Insert the expense asynchronously
            lifecycleScope.launch(Dispatchers.IO) {
                Log.d("AddExpense", "Starting database insertion on IO thread")
                val categoryDao = db.categoryDao()
                var categoryId: Int? = null

                // Handle custom category logic
                if (customCategoryText.isNotEmpty()) {
                    Log.d("AddExpense", "Custom category provided: $customCategoryText")
                    val existing = categoryDao.getCategoryIdByNameAndUserId(customCategoryText, userId)
                    categoryId = existing ?: categoryDao.insert(
                        Category(category_name = customCategoryText, user_id = userId)
                    ).toInt()
                    Log.d("AddExpense", "Category ID resolved for custom category: $categoryId")
                } else {
                    Log.d("AddExpense", "Using selected category: $selectedCategory")
                    categoryId = categoryDao.getCategoryIdByNameAndUserId(selectedCategory, userId)
                    if (categoryId == null) {
                        Log.d("AddExpense", "Selected category not found, inserting new")
                        categoryId = categoryDao.insert(
                            Category(category_name = selectedCategory, user_id = userId)
                        ).toInt()
                    }
                    Log.d("AddExpense", "Category ID resolved for selected category: $categoryId")
                }

                // Create Expense object
                val expense = Expense(
                    title = title,
                    description = description,
                    amount = amount,
                    date = date,
                    startTime = time,
                    photoPath = capturedPhotoPath,
                    filePath = if (fileName.isEmpty()) null else fileName,
                    category_id = categoryId,
                    user_id = userId
                )
                Log.d("AddExpense", "Created Expense object: $expense")
                // Insert the expense into the database
                expenseDao.insertExpense(expense)
                Log.d("AddExpense", "Expense inserted into database")

                // After inserting the expense
                val dateStrings = expenseDao.getLoggedDatesForUser(userId)
                val dates = dateStrings.mapNotNull {
                    try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)
                    } catch (e: Exception) {
                        Log.e("AddExpense", "Date parse failed for string: $it", e)
                        null
                    }
                }.sorted()
                Log.d("AddExpense", "Parsed and sorted dates: $dates")

                var streak = 1
                for (i in 1 until dates.size) {
                    val diff = (dates[i].time - dates[i - 1].time) / (1000 * 60 * 60 * 24)
                    if (diff == 1L) {
                        streak++
                        Log.d("AddExpense", "Streak incremented: $streak")
                        if (streak == 7) break
                    } else if (diff > 1L) {
                        streak = 1
                        Log.d("AddExpense", "Streak reset due to gap")
                    }
                }

                // Award badge if streak is 7
                if (streak >= 7) {
                    Log.d("AddExpense", "7-day streak detected")
                    val rewardDao = db.rewardDao()
                    val alreadyAwarded = rewardDao.getRewardByTitle(userId, "Step Master")
                    if (alreadyAwarded == null) {
                        Log.d("AddExpense", "Awarding Step Master badge")
                        val reward = Reward(
                            user_id = userId,
                            month = SimpleDateFormat("MMMM", Locale.getDefault()).format(System.currentTimeMillis()),
                            rewardTitle = "Step Master",
                            rewardDescription = "Logged expenses for 7 days in a row!",
                            iconResId = R.drawable.step_master
                        )
                        rewardDao.insertReward(reward)
                        Log.d("AddExpense", "Step Master already awarded")
                    }
                }

                val wellnessCategories = listOf("Health", "Fitness", "Medical")
                val usedWellnessCategory = customCategoryText.ifEmpty { selectedCategory }

                if (wellnessCategories.any { it.equals(usedWellnessCategory, ignoreCase = true) }) {
                    Log.d("AddExpense", "Wellness category used: $usedWellnessCategory")
                    val rewardDao = db.rewardDao()
                    val alreadyAwarded = rewardDao.getRewardByTitle(userId, "Wellness Warrior")
                    if (alreadyAwarded == null) {
                        Log.d("AddExpense", "Awarding Wellness Warrior badge")
                        val reward = Reward(
                            user_id = userId,
                            month = getCurrentMonth(),
                            rewardTitle = "Wellness Warrior",
                            rewardDescription = "You’ve taken steps toward your health. Keep it up!",
                            iconResId = R.drawable.wellness_badge
                        )
                        rewardDao.insertReward(reward)
                    }else {
                        Log.d("AddExpense", "Wellness Warrior already awarded")
                    }
                }

                val streakDao = db.streakDao()
                val today = getTodayDate()
                val yesterday = getYesterdayDate()
                val existingStreak = streakDao.getStreak(userId)
                Log.d("AddExpense", "Existing streak: $existingStreak")
                val updatedStreak = when {
                    existingStreak == null -> {
                        Log.d("AddExpense", "No existing streak found. Creating new.")
                        Streak(user_id = userId, lastLoggedDate = today, currentStreak = 1)
                    }
                    existingStreak.lastLoggedDate == today -> {
                        Log.d("AddExpense", "Already logged today. Keeping streak unchanged.")
                        existingStreak
                    }
                    existingStreak.lastLoggedDate == yesterday -> {
                        Log.d("AddExpense", "Continuing streak from yesterday")
                        Streak(user_id = userId, lastLoggedDate = today, currentStreak = existingStreak.currentStreak + 1)
                    }
                    else -> {
                        Log.d("AddExpense", "Streak broken. Resetting.")
                        Streak(user_id = userId, lastLoggedDate = today, currentStreak = 1)
                    }
                }

                streakDao.insertOrUpdateStreak(updatedStreak)
                Log.d("AddExpense", "Updated streak inserted: $updatedStreak")

                if (updatedStreak.currentStreak == 7) {
                    val rewardDao = db.rewardDao()
                    val alreadyAwarded = rewardDao.getRewardByTitle(userId, "7-Day Streak")
                    if (alreadyAwarded == null) {
                        Log.d("AddExpense", "Awarding 7-Day Streak reward")
                        val reward = Reward(
                            user_id = userId,
                            month = getCurrentMonth(),
                            rewardTitle = "7-Day Streak",
                            rewardDescription = "🔥 Logged expenses 7 days in a row!",
                            iconResId = R.drawable.fire // make sure this icon exists
                        )
                        rewardDao.insertReward(reward)
                    }else {
                        Log.d("AddExpense", "7-Day Streak already awarded")
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddExpense, "Expense added", Toast.LENGTH_SHORT).show()
                    Log.d("AddExpense", "Switching to Transactions activity")
                    startActivity(Intent(this@AddExpense, Transactions::class.java))
                }
            }
        }

        val btnHome = findViewById<ImageButton>(R.id.imageButton19)
        btnHome.setOnClickListener {
            Log.d("AddExpense", "Home button clicked")
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
                Log.d("AddExpense", "Spinner item selected: $selectedCategory")
                Toast.makeText(applicationContext, "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("AddExpense", "Nothing selected in spinner")
            }
        }
        val btnPickTime = findViewById<Button>(R.id.btnPickTime)
        val editTextTime = findViewById<TextView>(R.id.editTextTime)
        btnPickTime.setOnClickListener{
            Log.d("AddExpense", "Pick Time button clicked")
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                editTextTime.text = SimpleDateFormat("HH:mm").format(cal.time)
                Log.d("AddExpense", "Time picked: ${editTextTime.text}")
            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()
        }

        editTextDate = findViewById<TextView>(R.id.editTextDate)
        btnShowDatePicker = findViewById<Button>(R.id.btnShowDatePicker)
        btnShowDatePicker.setOnClickListener{
            Log.d("AddExpense", "Show Date Picker button clicked")
            showDatePicker()
        }
    }

    // ✅ Save image to internal storage
    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        Log.d("AddExpense", "Saving image to internal storage")
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, filename)
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
        Log.d("AddExpense", "Image saved as $filename")
        return file.absolutePath
    }

    private fun showDatePicker(){
        Log.d("AddExpense", "Launching date picker dialog")
        val datePickerDialog = DatePickerDialog(this,{DatePicker, year:Int, monthOfYear:Int, dayofMonth:Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year,monthOfYear,dayofMonth)
            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            editTextDate.text = formattedDate
            Log.d("AddExpense", "Date selected: $formattedDate")
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun getCurrentMonth(): String {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        return monthFormat.format(calendar.time)

    }

    // ✅ Get file name
    private fun getFileName(uri: Uri?): String {
        Log.d("AddExpense", "Getting file name from URI")
        if (uri == null) return "Unknown File"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {

                return it.getString(nameIndex)
            }
        }
        return uri.lastPathSegment ?: "Unknown File"
    }
}
