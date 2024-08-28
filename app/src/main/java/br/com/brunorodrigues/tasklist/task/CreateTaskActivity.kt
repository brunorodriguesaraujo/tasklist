package br.com.brunorodrigues.tasklist.task

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.brunorodrigues.tasklist.R
import br.com.brunorodrigues.tasklist.commons.extension.formatterDateBrazilian
import br.com.brunorodrigues.tasklist.databinding.ActivityCreateTaskBinding
import java.util.Calendar

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        setListener()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.title = getString(R.string.create_task)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setListener() {
        binding.run {
            linearLayout.setOnClickListener { showDatePickerDialog() }
            etDate.setOnClickListener { showDatePickerDialog() }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etDate.setText(selectedDate.formatterDateBrazilian())
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}