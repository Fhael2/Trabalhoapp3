package com.example.brotoapp

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaResumo(modifier: Modifier = Modifier) {
    val db = AppDatabase.getDatabase(LocalContext.current)
    val plantaDao = db.plantaDao()
    val tarefaDao = db.tarefaDao()

    var plantas by remember { mutableStateOf(listOf<Plant>()) }
    var tarefas by remember { mutableStateOf(listOf<CareTask>()) }

    suspend fun carregar() {
        try {
            plantas = plantaDao.buscarTodos()
            tarefas = tarefaDao.buscarTodos()
        } catch (e: Exception) {
            Log.e("BrotoApp", "Erro ao buscar dados do resumo", e)
        }
    }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch { carregar() }
    }

    val atrasadas = plantas.count { it.precisaRegar() }
    val pendentes = tarefas.count { !it.done }
    val concluidas = tarefas.count { it.done }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Cabecalho(
            titulo = "Resumo",
            subtitulo = if (atrasadas > 0) {
                "$atrasadas planta(s) precisando de água"
            } else {
                "Todas as plantas estão em dia"
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CartaoNumero(Modifier.weight(1f), "Plantas", plantas.size)
            CartaoNumero(Modifier.weight(1f), "Pra regar", atrasadas, alerta = atrasadas > 0)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CartaoNumero(Modifier.weight(1f), "Tarefas pendentes", pendentes)
            CartaoNumero(Modifier.weight(1f), "Tarefas concluídas", concluidas)
        }

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        for (planta in plantas) {
                            if (planta.precisaRegar()) {
                                plantaDao.atualizar(planta.copy(daysSinceWatered = 0))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("BrotoApp", "Erro ao regar as atrasadas", e)
                    }
                    carregar()
                }
            },
            enabled = atrasadas > 0,
            shape = FormaCard,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Regar todas as atrasadas")
        }
    }
}

@Composable
private fun CartaoNumero(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: Int,
    alerta: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = FormaCard,
        colors = CardDefaults.cardColors(
            containerColor = if (alerta) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = titulo, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = if (alerta) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}
