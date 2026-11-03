package com.example.brotoapp.data.repository

import com.example.brotoapp.data.local.Plant
import com.example.brotoapp.data.local.PlantDao
import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {

    fun buscarTodos(): Flow<List<Plant>> = plantDao.buscarTodos()

    fun buscarPorId(id: Int): Flow<Plant?> = plantDao.buscarPorId(id)

    suspend fun inserir(planta: Plant) = plantDao.inserir(planta)

    suspend fun atualizar(planta: Plant) = plantDao.atualizar(planta)

    suspend fun deletar(planta: Plant) = plantDao.deletar(planta)
}
