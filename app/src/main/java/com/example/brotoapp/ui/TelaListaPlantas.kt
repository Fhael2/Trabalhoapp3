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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.brotoapp.data.repository.CareTaskRepository
import com.example.brotoapp.data.repository.PlantRepository
import com.example.brotoapp.viewmodel.PlantListViewModel
import com.example.brotoapp.viewmodel.PlantListViewModelFactory

@Composable
fun TelaListaPlantas(
    navController: NavController,
    plantRepository: PlantRepository,
    taskRepository: CareTaskRepository,
    modifier: Modifier = Modifier
) {
    val viewModel: PlantListViewModel = viewModel(
        factory = PlantListViewModelFactory(plantRepository, taskRepository)
    )
    val estado by viewModel.uiState.collectAsState()

    val subtitulo = when {
        !estado.carregou -> ""
        estado.plantas.isEmpty() -> "Cadastre sua primeira planta"
        estado.atrasadas > 0 -> "${estado.atrasadas} precisando de água"
        else -> "Tudo em dia por aqui"
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Cabecalho(titulo = "Suas plantas", subtitulo = subtitulo)

        Spacer(modifier = Modifier.height(16.dp))

        if (estado.plantas.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (estado.carregou) {
                    Text(
                        text = "Nenhuma planta ainda.\nToque em + Nova Planta pra começar",
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = estado.plantas, key = { it.id }) { planta ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Rotas.detalhePlanta(planta.id)) },
                        shape = FormaCard,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Avatar(texto = planta.name)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = planta.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = planta.species,
                                    fontStyle = FontStyle.Italic,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = planta.textoRega(),
                                    modifier = Modifier.padding(top = 4.dp),
                                    fontWeight = FontWeight.Medium,
                                    color = if (planta.precisaRegar()) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                                Button(
                                    onClick = { viewModel.regar(planta) },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Regar")
                                }
                            }
                            IconButton(onClick = { viewModel.remover(planta) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover planta")
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate(Rotas.NOVA_PLANTA) },
            shape = FormaCard,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("+ Nova Planta")
        }
    }
}
