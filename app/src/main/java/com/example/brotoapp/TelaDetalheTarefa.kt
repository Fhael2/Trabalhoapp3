package com.example.brotoapp

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaDetalheTarefa(
    navController: NavController,
    modifier: Modifier = Modifier,
    id: Int
) {
    val db = AppDatabase.getDatabase(LocalContext.current)
    val plantaDao = db.plantaDao()
    val tarefaDao = db.tarefaDao()

    var tarefa by remember { mutableStateOf<CareTask?>(null) }
    var planta by remember { mutableStateOf<Plant?>(null) }

    // busca a tarefa pelo id e depois a planta que ela aponta
    suspend fun carregar() {
        try {
            val t = tarefaDao.buscarPorId(id)
            tarefa = t
            planta = if (t != null) plantaDao.buscarPorId(t.plantId) else null
        } catch (e: Exception) {
            Log.e("BrotoApp", "Erro ao buscar tarefa", e)
        }
    }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch { carregar() }
    }

    val t = tarefa
    val p = planta
    if (t != null) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = FormaGrande,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = t.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    if (t.description.isNotBlank()) {
                        Text(
                            text = t.description,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = t.done,
                            onCheckedChange = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        tarefaDao.atualizar(t.copy(done = !t.done))
                                    } catch (e: Exception) {
                                        Log.e("BrotoApp", "Erro ao atualizar tarefa", e)
                                    }
                                    carregar()
                                }
                            }
                        )
                        Text(
                            text = if (t.done) "Concluída" else "Pendente",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (p != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = FormaCard,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Avatar(texto = p.name)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Planta", style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = p.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = p.species, fontStyle = FontStyle.Italic)
                            Text(
                                text = p.textoRega(),
                                color = if (p.precisaRegar()) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = { navController.navigate(Rotas.detalhePlanta(p.id)) },
                    shape = FormaCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Ver planta")
                }
            }
        }
    }
}
