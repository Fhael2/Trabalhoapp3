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

data class TaskFormUiState(
    val titulo: String = "",
    val descricao: String = "",
    val plantaId: Int = -1,
    val erro: String = "",
    val salvando: Boolean = false,
    val plantas: List<Plant> = listOf(),
    val carregou: Boolean = false
)

class TaskFormViewModel(
    private val taskRepository: CareTaskRepository,
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskFormUiState())
    val uiState: StateFlow<TaskFormUiState> = _uiState.asStateFlow()

    private fun atualizarEstado(
        titulo: String = _uiState.value.titulo,
        descricao: String = _uiState.value.descricao,
        plantaId: Int = _uiState.value.plantaId,
        erro: String = _uiState.value.erro,
        salvando: Boolean = _uiState.value.salvando,
        plantas: List<Plant> = _uiState.value.plantas,
        carregou: Boolean = _uiState.value.carregou
    ) {
        _uiState.value = TaskFormUiState(titulo, descricao, plantaId, erro, salvando, plantas, carregou)
    }

    init {
        viewModelScope.launch {
            plantRepository.buscarTodos().collect { lista ->
                atualizarEstado(plantas = lista, carregou = true)
            }
        }
    }

    fun onTituloChange(valor: String) {
        atualizarEstado(titulo = valor)
    }

    fun onDescricaoChange(valor: String) {
        atualizarEstado(descricao = valor)
    }

    fun escolherPlanta(id: Int) {
        atualizarEstado(plantaId = id)
    }

    fun salvar(aoSalvar: () -> Unit) {
        val estado = _uiState.value
        when {
            estado.titulo.isBlank() -> atualizarEstado(erro = "Dê um título pra tarefa")
            estado.plantaId == -1 -> atualizarEstado(erro = "Escolha uma planta")
            else -> {
                atualizarEstado(erro = "", salvando = true)
                val tarefa = CareTask(
                    title = estado.titulo,
                    description = estado.descricao,
                    plantId = estado.plantaId
                )
                viewModelScope.launch {
                    try {
                        taskRepository.inserir(tarefa)
                        aoSalvar()
                    } catch (e: Exception) {
                        Log.e("BrotoApp", "Erro ao inserir tarefa", e)
                        atualizarEstado(erro = "Erro ao salvar a tarefa", salvando = false)
                    }
                }
            }
        }
    }
}

class TaskFormViewModelFactory(
    private val taskRepository: CareTaskRepository,
    private val plantRepository: PlantRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskFormViewModel(taskRepository, plantRepository) as T
    }
}
