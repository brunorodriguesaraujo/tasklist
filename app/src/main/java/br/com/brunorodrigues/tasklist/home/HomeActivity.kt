package br.com.brunorodrigues.tasklist.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import br.com.brunorodrigues.tasklist.R
import br.com.brunorodrigues.tasklist.commons.extension.LOCALE_BRAZIL
import br.com.brunorodrigues.tasklist.commons.extension.PATTERN
import br.com.brunorodrigues.tasklist.commons.extension.showToast
import br.com.brunorodrigues.tasklist.commons.utils.Constants
import br.com.brunorodrigues.tasklist.databinding.ActivityHomeBinding
import br.com.brunorodrigues.tasklist.model.TaskModel
import br.com.brunorodrigues.tasklist.signin.LoginActivity
import br.com.brunorodrigues.tasklist.task.CreateTaskActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val list: MutableList<TaskModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoad()
        initFirebase()
        setToolbar()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        getTasks()
    }

    private fun initFirebase() {
        auth = Firebase.auth
        db = Firebase.firestore
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.title = getString(R.string.task_list_label)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                signOut()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoad() = with(binding) {
        progressBar.isVisible = true
        rvTask.isVisible = false
    }

    private fun hideLoad() = with(binding) {
        progressBar.isVisible = false
        rvTask.isVisible = true
    }

    private fun getTasks() {
        list.clear()
        db.collection(Constants.DB_NAME)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    list.add(
                        TaskModel(
                            id = document.id,
                            title = document.data["title"].toString(),
                            date = document.data["date"].toString(),
                            document.data["isChecked"].toString().toBoolean()
                        )
                    )
                    Log.d(Constants.TAG, "${document.id} => ${document.data}")
                }
                if (list.isEmpty()) renderEmpty(true)
                else {
                    renderEmpty(false)
                    initAdapter()
                    hideLoad()
                }
            }
            .addOnFailureListener { exception ->
                hideLoad()
                Log.w(Constants.TAG, "Error getting documents.", exception)
                showToast(this, getString(R.string.authentication_failed))
            }
    }

    private fun renderEmpty(isVisible: Boolean) = with(binding) {
        ivEmpty.isVisible = isVisible
        rvTask.isVisible = !isVisible
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapter() {
        val homeAdapter = HomeAdapter(prepareItems(list), { task, isChecked ->
            updateTask(task, isChecked)
        }) {
            startActivity(Intent(this, CreateTaskActivity::class.java).apply {
                putExtra(Constants.TASK, it)
            })
        }
        binding.rvTask.adapter = homeAdapter
        homeAdapter.notifyDataSetChanged()
    }

    private fun updateTask(taskModel: TaskModel, isChecked: Boolean) {
        val map = mapOf(
            "title" to taskModel.title,
            "date" to taskModel.date,
            "isChecked" to isChecked
        )
        db.collection(Constants.DB_NAME)
            .document(taskModel.id)
            .update(map)
            .addOnSuccessListener { _ ->
                Log.d(Constants.TAG, "update with SUCCESS")
            }
            .addOnFailureListener { e ->
                Log.w(Constants.TAG, "Error update", e)
            }
    }

    private fun prepareItems(list: List<TaskModel>): List<Any> {
        val dateFormat = SimpleDateFormat(PATTERN, LOCALE_BRAZIL)

        return list
            .groupBy {
                it.date
            }
            .toSortedMap { date1, date2 ->
                val parsedDate1 = dateFormat.parse(date1)
                val parsedDate2 = dateFormat.parse(date2)
                parsedDate2?.compareTo(parsedDate1) ?: throw Exception("Compare Date Exception")
            }
            .flatMap { (date, tasks) ->
                listOf(date) + tasks
            }
    }

    private fun setListener() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
        }
    }

    private fun signOut() {
        auth.signOut()
        finishAffinity()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}