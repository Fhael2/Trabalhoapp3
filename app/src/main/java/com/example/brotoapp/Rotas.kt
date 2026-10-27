package com.example.brotoapp

object Rotas {
    const val PLANTAS = "plantas"
    const val TAREFAS = "tarefas"
    const val RESUMO = "resumo"
    const val NOVA_PLANTA = "nova_planta"
    const val NOVA_TAREFA = "nova_tarefa"
    const val DETALHE_PLANTA = "detalhe_planta/{id}"
    const val DETALHE_TAREFA = "detalhe_tarefa/{id}"

    fun detalhePlanta(id: Int) = "detalhe_planta/$id"
    fun detalheTarefa(id: Int) = "detalhe_tarefa/$id"
}
