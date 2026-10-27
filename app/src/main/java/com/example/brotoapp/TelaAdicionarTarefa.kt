package com.example.brotoapp

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TelaAdicionarTarefa(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val db = AppDatabase.getDatabase(LocalContext.current)
    val plantaDao = db.plantaDao()
    val tarefaDao = db.tarefaDao()

    var plantas by remember { mutableStateOf(listOf<Plant>()) }
    var carregou by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var plantaId by remember { mutableStateOf(-1) }
    var erro by remember { mutableStateOf("") }
    var salvando by remember { mutableStateOf(false) }

    // as plantas vêm do banco pra escolher a dona da tarefa
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                plantas = plantaDao.buscarTodos()
                carregou = true
            } catch (e: Exception) {
                Log.e("BrotoApp", "Erro ao buscar plantas", e)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título da tarefa") },
            modifier = Modifier.fillMaxWidth(),
            shape = FormaCard
        )

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = FormaCard
        )

        Text(text = "Planta", style = MaterialTheme.typography.titleMedium)

        if (carregou && plantas.isEmpty()) {
            Text("Cadastre uma planta primeiro")
        }

        plantas.forEach { planta ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { plantaId = planta.id },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = plantaId == planta.id,
                    onClick = { plantaId = planta.id }
                )
                Text(planta.name)
            }
        }

        if (erro.isNotEmpty()) {
            Text(text = erro, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                when {
                    titulo.isBlank() -> erro = "Dê um título pra tarefa"
                    plantaId == -1 -> erro = "Escolha uma planta"
                    else -> {
                        erro = ""
                        salvando = true
                        val tarefa = CareTask(
                            title = titulo,
                            description = descricao,
                            plantId = plantaId
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                tarefaDao.inserir(tarefa)
                                withContext(Dispatchers.Main) {
                                    navController.popBackStack()
                                }
                            } catch (e: Exception) {
                                Log.e("BrotoApp", "Erro ao inserir tarefa", e)
                                erro = "Erro ao salvar a tarefa"
                                salvando = false
                            }
                        }
                    }
                }
            },
            enabled = !salvando,
            shape = FormaCard,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Salvar")
        }
    }
}
