package br.com.brunorodrigues.tasklist.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskModel(
    val id: String = "",
    val title: String,
    val date: String
) : Parcelable