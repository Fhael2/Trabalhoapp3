package com.example.brotoapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarefas")
data class CareTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val plantId: Int,
    val done: Boolean = false
) {

    fun comFeita(feita: Boolean) = CareTask(id, title, description, plantId, feita)
}
