package com.example.brotoapp.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brotoapp.data.repository.CareTaskRepository
import com.example.brotoapp.data.repository.PlantRepository
import com.example.brotoapp.viewmodel.SummaryViewModel
import com.example.brotoapp.viewmodel.SummaryViewModelFactory

@Composable
fun TelaResumo(
    plantRepository: PlantRepository,
    taskRepository: CareTaskRepository,
    modifier: Modifier = Modifier
) {
    val viewModel: SummaryViewModel = viewModel(
        factory = SummaryViewModelFactory(plantRepository, taskRepository)
    )
    val estado by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Cabecalho(
            titulo = "Resumo",
            subtitulo = if (estado.atrasadas > 0) {
                "${estado.atrasadas} planta(s) precisando de água"
            } else {
                "Todas as plantas estão em dia"
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CartaoNumero(Modifier.weight(1f), "Plantas", estado.totalPlantas)
            CartaoNumero(Modifier.weight(1f), "Pra regar", estado.atrasadas, alerta = estado.atrasadas > 0)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CartaoNumero(Modifier.weight(1f), "Tarefas pendentes", estado.pendentes)
            CartaoNumero(Modifier.weight(1f), "Tarefas concluídas", estado.concluidas)
        }

        Button(
            onClick = { viewModel.regarAtrasadas() },
            enabled = estado.atrasadas > 0,
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
