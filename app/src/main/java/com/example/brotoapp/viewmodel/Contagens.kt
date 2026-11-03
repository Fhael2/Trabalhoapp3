package com.example.brotoapp.viewmodel

import com.example.brotoapp.data.local.CareTask
import com.example.brotoapp.data.local.Plant

fun contarAtrasadas(plantas: List<Plant>): Int {
    var total = 0
    for (planta in plantas) {
        if (planta.precisaRegar()) {
            total++
        }
    }
    return total
}

fun contarPendentes(tarefas: List<CareTask>): Int {
    var total = 0
    for (tarefa in tarefas) {
        if (!tarefa.done) {
            total++
        }
    }
    return total
}

fun contarConcluidas(tarefas: List<CareTask>): Int {
    var total = 0
    for (tarefa in tarefas) {
        if (tarefa.done) {
            total++
        }
    }
    return total
}
