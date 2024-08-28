package br.com.brunorodrigues.tasklist.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import br.com.brunorodrigues.tasklist.R
import br.com.brunorodrigues.tasklist.databinding.ActivityHomeBinding
import br.com.brunorodrigues.tasklist.model.TaskModel
import br.com.brunorodrigues.tasklist.signin.LoginActivity
import br.com.brunorodrigues.tasklist.task.CreateTaskActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFirebaseAuth()
        setToolbar()
        initAdapter()
        setListener()
    }

    private fun initFirebaseAuth() {
        auth = Firebase.auth
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

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapter() {
        val list = listOf(
            TaskModel(
                "1",
                "title",
                "24 de ago de 2024"
            ),
            TaskModel(
                "2",
                "title2",
                "24 de ago de 2024"
            ),
            TaskModel(
                "3",
                "title3",
                "25 de ago de 2024"
            ),
        )

        val homeAdapter = HomeAdapter(prepareItems(list))
        binding.rvTask.adapter = homeAdapter
        homeAdapter.notifyDataSetChanged()
    }

    private fun prepareItems(list: List<TaskModel>): List<Any> {
        return list.reversed().groupBy {
            it.date
        }.flatMap { (date, tasks) ->
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