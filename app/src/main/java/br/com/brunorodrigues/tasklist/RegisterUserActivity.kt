package br.com.brunorodrigues.tasklist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import br.com.brunorodrigues.tasklist.databinding.ActivityRegisterUserBinding
import br.com.brunorodrigues.tasklist.extension.isEmailValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterUserBinding
    private lateinit var auth: FirebaseAuth
    private val email get() = binding.textInputEmail.text.toString()
    private val password get() = binding.textInputPassword.text.toString()
    private val passwordConfirmation get() = binding.textInputPasswordConfirmation.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setListener()
    }

    private fun setListener() = with(binding) {
        binding.imgBack.setOnClickListener { finish() }

        buttonSignup.setOnClickListener {
            validateFields()
        }

        textInputEmail.addTextChangedListener {
            textInputLayoutEmail.error = null
        }
        textInputPassword.addTextChangedListener {
            textInputLayoutPassword.error = null
        }
        textInputPasswordConfirmation.addTextChangedListener {
            textInputLayoutPasswordConfirmation.error = null
        }

    }

    private fun validateFields() = with(binding) {
        if (email.isBlank()) {
            textInputLayoutEmail.error = "E-mail é obrigatório"
            return
        }

        if (email.isEmailValid()) {
            textInputLayoutEmail.error = "E-mail inválido"
            return
        }

        if (password.isBlank()) {
            textInputLayoutPassword.error = "Senha é obrigatória"
            return
        }

        if (passwordConfirmation.isBlank()) {
            textInputLayoutPasswordConfirmation.error = "Senha é obrigatória"
            return
        }

        if (password.length < 6) {
            textInputLayoutPassword.error = "A senha precisa ter no minímo 6 caracteres"
            return
        }

        if (password != passwordConfirmation) {
            textInputLayoutPasswordConfirmation.error = "Senhas diferentes"
            return
        }

        createAccount()
    }

    private fun createAccount() {
        showLoading()
        auth.createUserWithEmailAndPassword(email, passwordConfirmation)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Conta criada",
                        Toast.LENGTH_SHORT,
                    ).show()
                    auth.signOut()
                    finish()
                    hideLoading()
                } else {
                    hideLoading()
                    Toast.makeText(
                        baseContext,
                        "Falha na autenticação",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun showLoading() = with(binding) {
        constraintFields.isVisible = false
        progressBar.isVisible = true
    }

    private fun hideLoading() = with(binding) {
        constraintFields.isVisible = true
        progressBar.isVisible = false
    }
}