package com.example.brotoapp

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaListaTarefas(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val db = AppDatabase.getDatabase(LocalContext.current)
    val plantaDao = db.plantaDao()
    val tarefaDao = db.tarefaDao()

    var tarefas by remember { mutableStateOf(listOf<CareTask>()) }
    var plantas by remember { mutableStateOf(listOf<Plant>()) }
    var carregou by remember { mutableStateOf(false) }

    // busca as tarefas e as plantas (pra mostrar o nome da planta de cada tarefa)
    suspend fun carregar() {
        try {
            tarefas = tarefaDao.buscarTodos()
            plantas = plantaDao.buscarTodos()
            carregou = true
        } catch (e: Exception) {
            Log.e("BrotoApp", "Erro ao buscar tarefas", e)
        }
    }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch { carregar() }
    }

    val pendentes = tarefas.count { !it.done }
    val concluidas = tarefas.count { it.done }
    val subtitulo = if (carregou) "$pendentes pendentes · $concluidas concluídas" else ""

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Cabecalho(titulo = "Tarefas de cuidado", subtitulo = subtitulo)

        Spacer(modifier = Modifier.height(16.dp))

        if (tarefas.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (carregou) {
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
                items(items = tarefas, key = { it.id }) { tarefa ->
                    val nomePlanta = plantas.find { it.id == tarefa.plantId }?.name ?: "-"

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
                                onCheckedChange = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            tarefaDao.atualizar(tarefa.copy(done = !tarefa.done))
                                        } catch (e: Exception) {
                                            Log.e("BrotoApp", "Erro ao atualizar tarefa", e)
                                        }
                                        carregar()
                                    }
                                }
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
                                    text = "Planta: $nomePlanta",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        tarefaDao.deletar(tarefa)
                                    } catch (e: Exception) {
                                        Log.e("BrotoApp", "Erro ao remover tarefa", e)
                                    }
                                    carregar()
                                }
                            }) {
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
