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

data class PlantListUiState(
    val plantas: List<Plant> = listOf(),
    val atrasadas: Int = 0,
    val carregou: Boolean = false
)

class PlantListViewModel(
    private val plantRepository: PlantRepository,
    private val taskRepository: CareTaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantListUiState())
    val uiState: StateFlow<PlantListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            plantRepository.buscarTodos().collect { lista ->
                _uiState.value = PlantListUiState(
                    plantas = lista,
                    atrasadas = contarAtrasadas(lista),
                    carregou = true
                )
            }
        }
    }

    fun regar(planta: Plant) {
        viewModelScope.launch {
            try {
                plantRepository.atualizar(planta.comDiasDesdeRega(0))
            } catch (e: Exception) {
                Log.e("BrotoApp", "Erro ao regar planta", e)
            }
        }
    }

    fun remover(planta: Plant) {
        viewModelScope.launch {
            try {
                taskRepository.deletarPorPlanta(planta.id)
                plantRepository.deletar(planta)
            } catch (e: Exception) {
                Log.e("BrotoApp", "Erro ao remover planta", e)
            }
        }
    }
}

class PlantListViewModelFactory(
    private val plantRepository: PlantRepository,
    private val taskRepository: CareTaskRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlantListViewModel(plantRepository, taskRepository) as T
    }
}
