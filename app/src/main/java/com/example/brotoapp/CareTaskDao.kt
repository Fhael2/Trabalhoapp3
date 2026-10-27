package com.example.brotoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CareTaskDao {

    @Insert
    suspend fun inserir(tarefa: CareTask)

    @Query("SELECT * FROM tarefas")
    suspend fun buscarTodos(): List<CareTask>

    @Query("SELECT * FROM tarefas WHERE id = :id")
    suspend fun buscarPorId(id: Int): CareTask?

    @Query("SELECT * FROM tarefas WHERE plantId = :plantId")
    suspend fun buscarPorPlanta(plantId: Int): List<CareTask>

    @Update
    suspend fun atualizar(tarefa: CareTask)

    @Delete
    suspend fun deletar(tarefa: CareTask)

    @Query("DELETE FROM tarefas WHERE plantId = :plantId")
    suspend fun deletarPorPlanta(plantId: Int)
}
