package com.example.brotoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlantDao {

    @Insert
    suspend fun inserir(planta: Plant)

    @Query("SELECT * FROM plantas")
    suspend fun buscarTodos(): List<Plant>

    @Query("SELECT * FROM plantas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Plant?

    @Update
    suspend fun atualizar(planta: Plant)

    @Delete
    suspend fun deletar(planta: Plant)
}
