package com.example.brotoapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.brotoapp.data.repository.CareTaskRepository
import com.example.brotoapp.data.repository.PlantRepository
import com.example.brotoapp.viewmodel.PlantDetailViewModel
import com.example.brotoapp.viewmodel.PlantDetailViewModelFactory

@Composable
fun TelaDetalhePlanta(
    navController: NavController,
    plantRepository: PlantRepository,
    taskRepository: CareTaskRepository,
    id: Int,
    modifier: Modifier = Modifier
) {
    val viewModel: PlantDetailViewModel = viewModel(
        factory = PlantDetailViewModelFactory(plantRepository, taskRepository, id)
    )
    val estado by viewModel.uiState.collectAsState()

    val p = estado.planta
    if (p != null) {
        val ultimaRega = if (p.daysSinceWatered == 0) "hoje" else "há ${p.daysSinceWatered} dia(s)"

        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = FormaGrande,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(texto = p.name, tamanho = 64.dp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = p.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = p.species,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = FormaCard,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = p.textoRega(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (p.precisaRegar()) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                    Text(text = "Última rega: $ultimaRega")

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Regar a cada ${p.wateringFrequencyDays} dia(s)",
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = { viewModel.diminuirFrequencia() }) {
                            Text("-")
                        }
                        OutlinedButton(
                            onClick = { viewModel.aumentarFrequencia() },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("+")
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.regar() },
                shape = FormaCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Regar agora")
            }

            Text(
                text = "Tarefas desta planta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (estado.tarefas.isEmpty()) {
                Text("Nenhuma tarefa pra essa planta")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = estado.tarefas, key = { it.id }) { tarefa ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate(Rotas.detalheTarefa(tarefa.id)) },
                            shape = FormaCard,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = tarefa.done,
                                    onCheckedChange = { viewModel.alternarTarefa(tarefa) }
                                )
                                Text(
                                    text = tarefa.title,
                                    textDecoration = if (tarefa.done) {
                                        TextDecoration.LineThrough
                                    } else {
                                        TextDecoration.None
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
