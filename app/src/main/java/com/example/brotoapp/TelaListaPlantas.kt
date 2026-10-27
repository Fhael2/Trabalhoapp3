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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaListaPlantas(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val db = AppDatabase.getDatabase(LocalContext.current)
    val plantaDao = db.plantaDao()
    val tarefaDao = db.tarefaDao()

    var plantas by remember { mutableStateOf(listOf<Plant>()) }
    var carregou by remember { mutableStateOf(false) }

    // busca as plantas no banco e atualiza a lista da tela
    suspend fun carregar() {
        try {
            plantas = plantaDao.buscarTodos()
            carregou = true
        } catch (e: Exception) {
            Log.e("BrotoApp", "Erro ao buscar plantas", e)
        }
    }

    // roda toda vez que a tela abre
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch { carregar() }
    }

    val atrasadas = plantas.count { it.precisaRegar() }
    val subtitulo = when {
        !carregou -> ""
        plantas.isEmpty() -> "Cadastre sua primeira planta"
        atrasadas > 0 -> "$atrasadas precisando de água"
        else -> "Tudo em dia por aqui"
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Cabecalho(titulo = "Suas plantas", subtitulo = subtitulo)

        Spacer(modifier = Modifier.height(16.dp))

        if (plantas.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (carregou) {
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
                items(items = plantas, key = { it.id }) { planta ->
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
                                    onClick = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                plantaDao.atualizar(planta.copy(daysSinceWatered = 0))
                                            } catch (e: Exception) {
                                                Log.e("BrotoApp", "Erro ao regar planta", e)
                                            }
                                            carregar()
                                        }
                                    },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Regar")
                                }
                            }
                            IconButton(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        // as tarefas da planta vão embora junto com ela
                                        tarefaDao.deletarPorPlanta(planta.id)
                                        plantaDao.deletar(planta)
                                    } catch (e: Exception) {
                                        Log.e("BrotoApp", "Erro ao remover planta", e)
                                    }
                                    carregar()
                                }
                            }) {
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
