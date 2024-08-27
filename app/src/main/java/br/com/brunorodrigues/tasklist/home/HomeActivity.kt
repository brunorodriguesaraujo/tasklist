package br.com.brunorodrigues.tasklist.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.brunorodrigues.tasklist.databinding.ActivityHomeBinding
import br.com.brunorodrigues.tasklist.model.TaskModel
import br.com.brunorodrigues.tasklist.signin.LoginActivity
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
        initAdapter()
        setViews()
        setListener()
    }

    private fun initFirebaseAuth() {
        auth = Firebase.auth
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapter() {
        val list = listOf(
            TaskModel(
                "1",
                "title",
                "alo",
                "24 de ago de 2024"
            ),
            TaskModel(
                "2",
                "title2",
                "aloo",
                "24 de ago de 2024"
            ),
            TaskModel(
                "2",
                "title2",
                "aloo",
                "25 de ago de 2024"
            ),
        )

        val homeAdapter = HomeAdapter(prepareItems(list))
        binding.rvTask.adapter = homeAdapter
        homeAdapter.notifyDataSetChanged()
    }

    private fun prepareItems(list: List<TaskModel>): List<Any> {
        return list.groupBy { it.date }.flatMap { (date, tasks) ->
            listOf(date) + tasks
        }
    }

    private fun setViews() {
    }

    private fun setListener() {
//        binding.toolbar.setClickButtonLogout { signOut() }
    }

    private fun signOut() {
        auth.signOut()
        finishAffinity()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}