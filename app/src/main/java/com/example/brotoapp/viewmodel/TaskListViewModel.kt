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

data class TaskListUiState(
    val tarefas: List<CareTask> = listOf(),
    val plantas: List<Plant> = listOf(),
    val pendentes: Int = 0,
    val concluidas: Int = 0,
    val carregou: Boolean = false
) {

    fun nomeDaPlanta(plantId: Int): String {
        var nome = "-"
        for (planta in plantas) {
            if (planta.id == plantId) {
                nome = planta.name
            }
        }
        return nome
    }
}

class TaskListViewModel(
    private val taskRepository: CareTaskRepository,
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    private fun atualizarEstado(
        tarefas: List<CareTask> = _uiState.value.tarefas,
        plantas: List<Plant> = _uiState.value.plantas,
        pendentes: Int = _uiState.value.pendentes,
        concluidas: Int = _uiState.value.concluidas,
        carregou: Boolean = _uiState.value.carregou
    ) {
        _uiState.value = TaskListUiState(tarefas, plantas, pendentes, concluidas, carregou)
    }

    init {
        viewModelScope.launch {
            taskRepository.buscarTodos().collect { lista ->
                atualizarEstado(
                    tarefas = lista,
                    pendentes = contarPendentes(lista),
                    concluidas = contarConcluidas(lista),
                    carregou = true
                )
            }
        }
        viewModelScope.launch {
            plantRepository.buscarTodos().collect { lista ->
                atualizarEstado(plantas = lista)
            }
        }
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

    fun remover(tarefa: CareTask) {
        viewModelScope.launch {
            try {
                taskRepository.deletar(tarefa)
            } catch (e: Exception) {
                Log.e("BrotoApp", "Erro ao remover tarefa", e)
            }
        }
    }
}

class TaskListViewModelFactory(
    private val taskRepository: CareTaskRepository,
    private val plantRepository: PlantRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskListViewModel(taskRepository, plantRepository) as T
    }
}
