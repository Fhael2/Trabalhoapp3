package com.example.brotoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private data class ItemAba(val rota: String, val titulo: String, val icone: ImageVector)

private val abas = listOf(
    ItemAba(Rotas.PLANTAS, "Plantas", Icons.Default.Home),
    ItemAba(Rotas.TAREFAS, "Tarefas", Icons.Default.Check),
    ItemAba(Rotas.RESUMO, "Resumo", Icons.Default.Info)
)

private fun tituloDaTela(rota: String?) = when (rota) {
    Rotas.PLANTAS -> "Minhas Plantas"
    Rotas.TAREFAS -> "Tarefas de Cuidado"
    Rotas.RESUMO -> "Resumo"
    Rotas.NOVA_PLANTA -> "Nova Planta"
    Rotas.NOVA_TAREFA -> "Nova Tarefa"
    Rotas.DETALHE_PLANTA -> "Detalhes da Planta"
    Rotas.DETALHE_TAREFA -> "Detalhes da Tarefa"
    else -> "BrotoApp"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrotoApp() {
    val navController = rememberNavController()

    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route
    val naAbaPrincipal = abas.any { it.rota == rotaAtual }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloDaTela(rotaAtual)) },
                navigationIcon = {
                    if (!naAbaPrincipal) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            if (naAbaPrincipal) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    abas.forEach { aba ->
                        NavigationBarItem(
                            selected = rotaAtual == aba.rota,
                            onClick = {
                                navController.navigate(aba.rota) {
                                    popUpTo(Rotas.PLANTAS) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(aba.icone, contentDescription = aba.titulo) },
                            label = { Text(aba.titulo) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.PLANTAS,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.PLANTAS) {
                TelaListaPlantas(navController = navController)
            }

            composable(Rotas.TAREFAS) {
                TelaListaTarefas(navController = navController)
            }

            composable(Rotas.RESUMO) {
                TelaResumo()
            }

            composable(Rotas.NOVA_PLANTA) {
                TelaAdicionarPlanta(navController = navController)
            }

            composable(Rotas.NOVA_TAREFA) {
                TelaAdicionarTarefa(navController = navController)
            }

            // as telas de detalhe só recebem o id e buscam o item no banco
            composable(route = Rotas.DETALHE_PLANTA) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt() ?: -1
                TelaDetalhePlanta(navController = navController, id = id)
            }

            composable(route = Rotas.DETALHE_TAREFA) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt() ?: -1
                TelaDetalheTarefa(navController = navController, id = id)
            }
        }
    }
}
