package com.example.brotoapp.data.repository

import com.example.brotoapp.data.local.CareTask
import com.example.brotoapp.data.local.CareTaskDao
import kotlinx.coroutines.flow.Flow

class CareTaskRepository(private val careTaskDao: CareTaskDao) {

    fun buscarTodos(): Flow<List<CareTask>> = careTaskDao.buscarTodos()

    fun buscarPorId(id: Int): Flow<CareTask?> = careTaskDao.buscarPorId(id)

    fun buscarPorPlanta(plantId: Int): Flow<List<CareTask>> = careTaskDao.buscarPorPlanta(plantId)

    suspend fun inserir(tarefa: CareTask) = careTaskDao.inserir(tarefa)

    suspend fun atualizar(tarefa: CareTask) = careTaskDao.atualizar(tarefa)

    suspend fun deletar(tarefa: CareTask) = careTaskDao.deletar(tarefa)

    suspend fun deletarPorPlanta(plantId: Int) = careTaskDao.deletarPorPlanta(plantId)
}
