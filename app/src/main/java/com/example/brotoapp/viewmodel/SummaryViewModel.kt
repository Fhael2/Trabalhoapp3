package com.example.brotoapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brotoapp.data.local.Plant
import com.example.brotoapp.data.repository.CareTaskRepository
import com.example.brotoapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SummaryUiState(
    val totalPlantas: Int = 0,
    val atrasadas: Int = 0,
    val pendentes: Int = 0,
    val concluidas: Int = 0
)

class SummaryViewModel(
    private val plantRepository: PlantRepository,
    private val taskRepository: CareTaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    private var plantas: List<Plant> = listOf()

    private fun atualizarEstado(
        totalPlantas: Int = _uiState.value.totalPlantas,
        atrasadas: Int = _uiState.value.atrasadas,
        pendentes: Int = _uiState.value.pendentes,
        concluidas: Int = _uiState.value.concluidas
    ) {
        _uiState.value = SummaryUiState(totalPlantas, atrasadas, pendentes, concluidas)
    }

    init {
        viewModelScope.launch {
            plantRepository.buscarTodos().collect { lista ->
                plantas = lista
                atualizarEstado(
                    totalPlantas = lista.size,
                    atrasadas = contarAtrasadas(lista)
                )
            }
        }
        viewModelScope.launch {
            taskRepository.buscarTodos().collect { lista ->
                atualizarEstado(
                    pendentes = contarPendentes(lista),
                    concluidas = contarConcluidas(lista)
                )
            }
        }
    }

    fun regarAtrasadas() {
        viewModelScope.launch {
            try {
                for (planta in plantas) {
                    if (planta.precisaRegar()) {
                        plantRepository.atualizar(planta.comDiasDesdeRega(0))
                    }
                }
            } catch (e: Exception) {
                Log.e("BrotoApp", "Erro ao regar as atrasadas", e)
            }
        }
    }
}

class SummaryViewModelFactory(
    private val plantRepository: PlantRepository,
    private val taskRepository: CareTaskRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SummaryViewModel(plantRepository, taskRepository) as T
    }
}
