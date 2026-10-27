package com.example.brotoapp

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TelaAdicionarPlanta(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val plantaDao = AppDatabase.getDatabase(LocalContext.current).plantaDao()

    var nome by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var frequenciaTexto by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }
    var salvando by remember { mutableStateOf(false) }

    val coresInputs = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
    )

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome da planta") },
            modifier = Modifier.fillMaxWidth(),
            colors = coresInputs,
            shape = FormaCard
        )

        OutlinedTextField(
            value = especie,
            onValueChange = { especie = it },
            label = { Text("Espécie") },
            modifier = Modifier.fillMaxWidth(),
            colors = coresInputs,
            shape = FormaCard
        )

        OutlinedTextField(
            value = frequenciaTexto,
            onValueChange = { frequenciaTexto = it },
            label = { Text("Frequência de rega (dias)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = coresInputs,
            shape = FormaCard
        )

        if (erro.isNotEmpty()) {
            Text(text = erro, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val frequencia = frequenciaTexto.toIntOrNull()
                when {
                    nome.isBlank() || especie.isBlank() -> erro = "Preencha todos os campos"
                    frequencia == null || frequencia <= 0 -> erro = "Frequência de rega inválida"
                    else -> {
                        erro = ""
                        salvando = true
                        val planta = Plant(
                            name = nome,
                            species = especie,
                            wateringFrequencyDays = frequencia,
                            daysSinceWatered = 0
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                plantaDao.inserir(planta)
                                // só volta pra lista depois de salvar, senão ela pode abrir sem a planta nova
                                withContext(Dispatchers.Main) {
                                    navController.popBackStack()
                                }
                            } catch (e: Exception) {
                                Log.e("BrotoApp", "Erro ao inserir planta", e)
                                erro = "Erro ao salvar a planta"
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
