package com.example.brotoapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.brotoapp.data.repository.PlantRepository
import com.example.brotoapp.viewmodel.PlantFormViewModel
import com.example.brotoapp.viewmodel.PlantFormViewModelFactory

@Composable
fun TelaAdicionarPlanta(
    navController: NavController,
    plantRepository: PlantRepository,
    modifier: Modifier = Modifier
) {
    val viewModel: PlantFormViewModel = viewModel(
        factory = PlantFormViewModelFactory(plantRepository)
    )
    val estado by viewModel.uiState.collectAsState()

    val coresInputs = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
    )

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = estado.nome,
            onValueChange = { viewModel.onNomeChange(it) },
            label = { Text("Nome da planta") },
            modifier = Modifier.fillMaxWidth(),
            colors = coresInputs,
            shape = FormaCard
        )

        OutlinedTextField(
            value = estado.especie,
            onValueChange = { viewModel.onEspecieChange(it) },
            label = { Text("Espécie") },
            modifier = Modifier.fillMaxWidth(),
            colors = coresInputs,
            shape = FormaCard
        )

        OutlinedTextField(
            value = estado.frequenciaTexto,
            onValueChange = { viewModel.onFrequenciaChange(it) },
            label = { Text("Frequência de rega (dias)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = coresInputs,
            shape = FormaCard
        )

        if (estado.erro.isNotEmpty()) {
            Text(text = estado.erro, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { viewModel.salvar { navController.popBackStack() } },
            enabled = !estado.salvando,
            shape = FormaCard,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Salvar")
        }
    }
}
