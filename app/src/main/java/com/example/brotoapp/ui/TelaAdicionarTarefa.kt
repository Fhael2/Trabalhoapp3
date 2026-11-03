package com.example.brotoapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.brotoapp.data.repository.CareTaskRepository
import com.example.brotoapp.data.repository.PlantRepository
import com.example.brotoapp.viewmodel.TaskFormViewModel
import com.example.brotoapp.viewmodel.TaskFormViewModelFactory

@Composable
fun TelaAdicionarTarefa(
    navController: NavController,
    taskRepository: CareTaskRepository,
    plantRepository: PlantRepository,
    modifier: Modifier = Modifier
) {
    val viewModel: TaskFormViewModel = viewModel(
        factory = TaskFormViewModelFactory(taskRepository, plantRepository)
    )
    val estado by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = estado.titulo,
            onValueChange = { viewModel.onTituloChange(it) },
            label = { Text("Título da tarefa") },
            modifier = Modifier.fillMaxWidth(),
            shape = FormaCard
        )

        OutlinedTextField(
            value = estado.descricao,
            onValueChange = { viewModel.onDescricaoChange(it) },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = FormaCard
        )

        Text(text = "Planta", style = MaterialTheme.typography.titleMedium)

        if (estado.carregou && estado.plantas.isEmpty()) {
            Text("Cadastre uma planta primeiro")
        }

        estado.plantas.forEach { planta ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.escolherPlanta(planta.id) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = estado.plantaId == planta.id,
                    onClick = { viewModel.escolherPlanta(planta.id) }
                )
                Text(planta.name)
            }
        }

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
