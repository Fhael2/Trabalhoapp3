package com.example.brotoapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brotoapp.data.local.CareTask
import com.example.brotoapp.data.local.Plant
import com.example.brotoapp.data.repository.CareTaskRepository
import com.example.brotoapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val tarefa: CareTask? = null,
    val planta: Plant? = null
)

class TaskDetailViewModel(
    private val taskRepository: CareTaskRepository,
    private val plantRepository: PlantRepository,
    private val id: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private var tarefaAtual: CareTask? = null
    private var plantas: List<Plant> = listOf()

    init {
        viewModelScope.launch {
            taskRepository.buscarPorId(id).collect { tarefa ->
                tarefaAtual = tarefa
                montarEstado()
            }
        }
        viewModelScope.launch {
            plantRepository.buscarTodos().collect { lista ->
                plantas = lista
                montarEstado()
            }
        }
    }

    private fun montarEstado() {
        val tarefa = tarefaAtual
        var planta: Plant? = null
        if (tarefa != null) {
            for (p in plantas) {
                if (p.id == tarefa.plantId) {
                    planta = p
                }
            }
        }
        _uiState.value = TaskDetailUiState(tarefa = tarefa, planta = planta)
    }

    fun alternar(tarefa: CareTask) {
        viewModelScope.launch {
            try {
                taskRepository.atualizar(tarefa.comFeita(!tarefa.done))
            } catch (e: Exception) {
                Log.e("BrotoApp", "Erro ao atualizar tarefa", e)
            }
        }
    }
}

class TaskDetailViewModelFactory(
    private val taskRepository: CareTaskRepository,
    private val plantRepository: PlantRepository,
    private val id: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskDetailViewModel(taskRepository, plantRepository, id) as T
    }
}
