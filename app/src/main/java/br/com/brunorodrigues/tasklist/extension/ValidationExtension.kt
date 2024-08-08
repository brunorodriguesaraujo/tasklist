package br.com.brunorodrigues.tasklist.extension

import android.util.Patterns

fun String.isEmailValid() = !Patterns.EMAIL_ADDRESS.matcher(this).matches()