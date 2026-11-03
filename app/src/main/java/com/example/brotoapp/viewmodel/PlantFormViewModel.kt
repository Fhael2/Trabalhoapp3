package com.example.brotoapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.brotoapp.data.local.Plant
import com.example.brotoapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlantFormUiState(
    val nome: String = "",
    val especie: String = "",
    val frequenciaTexto: String = "",
    val erro: String = "",
    val salvando: Boolean = false
)

class PlantFormViewModel(
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantFormUiState())
    val uiState: StateFlow<PlantFormUiState> = _uiState.asStateFlow()

    private fun atualizarEstado(
        nome: String = _uiState.value.nome,
        especie: String = _uiState.value.especie,
        frequenciaTexto: String = _uiState.value.frequenciaTexto,
        erro: String = _uiState.value.erro,
        salvando: Boolean = _uiState.value.salvando
    ) {
        _uiState.value = PlantFormUiState(nome, especie, frequenciaTexto, erro, salvando)
    }

    fun onNomeChange(valor: String) {
        atualizarEstado(nome = valor)
    }

    fun onEspecieChange(valor: String) {
        atualizarEstado(especie = valor)
    }

    fun onFrequenciaChange(valor: String) {
        atualizarEstado(frequenciaTexto = valor)
    }

    fun salvar(aoSalvar: () -> Unit) {
        val estado = _uiState.value
        val frequencia = estado.frequenciaTexto.toIntOrNull()
        when {
            estado.nome.isBlank() || estado.especie.isBlank() ->
                atualizarEstado(erro = "Preencha todos os campos")
            frequencia == null || frequencia <= 0 ->
                atualizarEstado(erro = "Frequência de rega inválida")
            else -> {
                atualizarEstado(erro = "", salvando = true)
                val planta = Plant(
                    name = estado.nome,
                    species = estado.especie,
                    wateringFrequencyDays = frequencia,
                    daysSinceWatered = 0
                )
                viewModelScope.launch {
                    try {
                        plantRepository.inserir(planta)
                        aoSalvar()
                    } catch (e: Exception) {
                        Log.e("BrotoApp", "Erro ao inserir planta", e)
                        atualizarEstado(erro = "Erro ao salvar a planta", salvando = false)
                    }
                }
            }
        }
    }
}

class PlantFormViewModelFactory(
    private val plantRepository: PlantRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlantFormViewModel(plantRepository) as T
    }
}
