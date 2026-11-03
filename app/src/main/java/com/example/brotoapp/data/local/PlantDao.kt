package com.example.brotoapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Insert
    suspend fun inserir(planta: Plant)

    @Query("SELECT * FROM plantas")
    fun buscarTodos(): Flow<List<Plant>>

    @Query("SELECT * FROM plantas WHERE id = :id")
    fun buscarPorId(id: Int): Flow<Plant?>

    @Update
    suspend fun atualizar(planta: Plant)

    @Delete
    suspend fun deletar(planta: Plant)
}
