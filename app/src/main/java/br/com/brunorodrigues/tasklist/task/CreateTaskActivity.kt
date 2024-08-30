package br.com.brunorodrigues.tasklist.task

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.brunorodrigues.tasklist.R
import br.com.brunorodrigues.tasklist.commons.extension.TIME_ZONE_AMERICA_SP
import br.com.brunorodrigues.tasklist.commons.extension.formatterDateBrazilian
import br.com.brunorodrigues.tasklist.commons.utils.Constants
import br.com.brunorodrigues.tasklist.databinding.ActivityCreateTaskBinding
import br.com.brunorodrigues.tasklist.model.TaskModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.TimeZone

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTaskBinding
    private lateinit var db: FirebaseFirestore
    private val title get() = binding.etTask.text.toString()
    private var selectedDate = ""
    private var taskModel: TaskModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getTask()
        initFireStore()
        setToolbar()
        setListener()
    }

    private fun getTask() {
        taskModel = intent.extras?.getParcelable(Constants.TASK)
        if (taskModel != null) {
            binding.etTask.setText(taskModel?.title)
            binding.etDate.setText(taskModel?.date)
            binding.buttonSave.text = getString(R.string.edit)
        }
    }

    private fun initFireStore() {
        db = Firebase.firestore
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (taskModel != null) {
            menuInflater.inflate(R.menu.menu_create_task, menu)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                deleteTask()
                true
            }

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteTask() {
        db.collection(Constants.DB_NAME)
            .document(taskModel?.id ?: "")
            .delete()
            .addOnSuccessListener {
                Log.d(Constants.TAG, "added with SUCCESS")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(Constants.TAG, "Error adding", e)
            }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.title =
            if (taskModel != null) "Editar Tarefa" else getString(R.string.create_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_24)
    }

    private fun setListener() {
        binding.run {
            linearLayout.setOnClickListener { showDatePickerDialog() }
            buttonSave.setOnClickListener {
                if (etDate.text.isNotBlank() && title.isNotBlank()) {
                    if (taskModel != null) updateTask()
                    else saveTaskDatabase()
                } else {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.fill_fields),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE_AMERICA_SP))
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate =
                    "$selectedDay/${selectedMonth + 1}/$selectedYear".formatterDateBrazilian()
                binding.etDate.setText(selectedDate)
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun saveTaskDatabase() {
        val taskModel = TaskModel(
            title = title,
            date = selectedDate
        )

        db.collection(Constants.DB_NAME)
            .add(taskModel)
            .addOnSuccessListener { _ ->
                Log.d(Constants.TAG, "added with SUCCESS")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(Constants.TAG, "Error adding", e)
            }
    }

    private fun updateTask() {
        val date = selectedDate.ifEmpty { taskModel?.date }
        val map = mapOf(
            "title" to title,
            "date" to date
        )
        db.collection(Constants.DB_NAME)
            .document(taskModel?.id ?: "")
            .update(map)
            .addOnSuccessListener { _ ->
                Log.d(Constants.TAG, "update with SUCCESS")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(Constants.TAG, "Error update", e)
            }
    }

}