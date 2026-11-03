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

data class PlantDetailUiState(
    val planta: Plant? = null,
    val tarefas: List<CareTask> = listOf()
)

class PlantDetailViewModel(
    private val plantRepository: PlantRepository,
    private val taskRepository: CareTaskRepository,
    private val id: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantDetailUiState())
    val uiState: StateFlow<PlantDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            plantRepository.buscarPorId(id).collect { planta ->
                _uiState.value = PlantDetailUiState(
                    planta = planta,
                    tarefas = _uiState.value.tarefas
                )
            }
        }
        viewModelScope.launch {
            taskRepository.buscarPorPlanta(id).collect { lista ->
                _uiState.value = PlantDetailUiState(
                    planta = _uiState.value.planta,
                    tarefas = lista
                )
            }
        }
    }

    fun aumentarFrequencia() {
        val planta = _uiState.value.planta
        if (planta != null) {
            atualizarPlanta(planta.comFrequencia(planta.wateringFrequencyDays + 1), "Erro ao mudar frequência")
        }
    }

    fun diminuirFrequencia() {
        val planta = _uiState.value.planta
        if (planta != null && planta.wateringFrequencyDays > 1) {
            atualizarPlanta(planta.comFrequencia(planta.wateringFrequencyDays - 1), "Erro ao mudar frequência")
        }
    }

    fun regar() {
        val planta = _uiState.value.planta
        if (planta != null) {
            atualizarPlanta(planta.comDiasDesdeRega(0), "Erro ao regar planta")
        }
    }

    fun alternarTarefa(tarefa: CareTask) {
        viewModelScope.launch {
            try {
                taskRepository.atualizar(tarefa.comFeita(!tarefa.done))
            } catch (e: Exception) {
                Log.e("BrotoApp", "Erro ao atualizar tarefa", e)
            }
        }
    }

    private fun atualizarPlanta(planta: Plant, mensagemErro: String) {
        viewModelScope.launch {
            try {
                plantRepository.atualizar(planta)
            } catch (e: Exception) {
                Log.e("BrotoApp", mensagemErro, e)
            }
        }
    }
}

class PlantDetailViewModelFactory(
    private val plantRepository: PlantRepository,
    private val taskRepository: CareTaskRepository,
    private val id: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlantDetailViewModel(plantRepository, taskRepository, id) as T
    }
}
