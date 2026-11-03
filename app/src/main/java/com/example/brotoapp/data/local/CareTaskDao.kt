package com.example.brotoapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CareTaskDao {

    @Insert
    suspend fun inserir(tarefa: CareTask)

    @Query("SELECT * FROM tarefas")
    fun buscarTodos(): Flow<List<CareTask>>

    @Query("SELECT * FROM tarefas WHERE id = :id")
    fun buscarPorId(id: Int): Flow<CareTask?>

    @Query("SELECT * FROM tarefas WHERE plantId = :plantId")
    fun buscarPorPlanta(plantId: Int): Flow<List<CareTask>>

    @Update
    suspend fun atualizar(tarefa: CareTask)

    @Delete
    suspend fun deletar(tarefa: CareTask)

    @Query("DELETE FROM tarefas WHERE plantId = :plantId")
    suspend fun deletarPorPlanta(plantId: Int)
}
