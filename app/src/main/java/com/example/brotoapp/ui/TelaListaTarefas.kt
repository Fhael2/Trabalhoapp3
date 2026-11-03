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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.brotoapp.data.repository.CareTaskRepository
import com.example.brotoapp.data.repository.PlantRepository
import com.example.brotoapp.viewmodel.TaskListViewModel
import com.example.brotoapp.viewmodel.TaskListViewModelFactory

@Composable
fun TelaListaTarefas(
    navController: NavController,
    taskRepository: CareTaskRepository,
    plantRepository: PlantRepository,
    modifier: Modifier = Modifier
) {
    val viewModel: TaskListViewModel = viewModel(
        factory = TaskListViewModelFactory(taskRepository, plantRepository)
    )
    val estado by viewModel.uiState.collectAsState()

    val subtitulo = if (estado.carregou) {
        "${estado.pendentes} pendentes · ${estado.concluidas} concluídas"
    } else {
        ""
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Cabecalho(titulo = "Tarefas de cuidado", subtitulo = subtitulo)

        Spacer(modifier = Modifier.height(16.dp))

        if (estado.tarefas.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (estado.carregou) {
                    Text(
                        text = "Nenhuma tarefa ainda.\nToque em + Nova Tarefa pra começar",
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = estado.tarefas, key = { it.id }) { tarefa ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Rotas.detalheTarefa(tarefa.id)) },
                        shape = FormaCard,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = tarefa.done,
                                onCheckedChange = { viewModel.alternar(tarefa) }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tarefa.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (tarefa.done) {
                                        TextDecoration.LineThrough
                                    } else {
                                        TextDecoration.None
                                    }
                                )
                                Text(
                                    text = "Planta: ${estado.nomeDaPlanta(tarefa.plantId)}",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { viewModel.remover(tarefa) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover tarefa")
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate(Rotas.NOVA_TAREFA) },
            shape = FormaCard,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("+ Nova Tarefa")
        }
    }
}
