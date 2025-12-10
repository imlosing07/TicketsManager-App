package com.example.ticketentregaapp.data.dao

import androidx.room.*
import com.example.ticketentregaapp.data.entity.ProgramaProfesionalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramaProfesionalDao {
    @Query("SELECT * FROM programa_profesional WHERE estadoRegistro != '*' ORDER BY codigo ASC")
    fun getAll(): Flow<List<ProgramaProfesionalEntity>>

    @Query("SELECT * FROM programa_profesional WHERE estadoRegistro != '*' ORDER BY nombre ASC")
    fun getAllByNombre(): Flow<List<ProgramaProfesionalEntity>>

    @Query("SELECT * FROM programa_profesional WHERE codigo = :codigo")
    suspend fun getByCodigo(codigo: String): ProgramaProfesionalEntity?

    @Query("SELECT * FROM programa_profesional WHERE estadoRegistro != '*' AND (codigo LIKE '%' || :query || '%' OR nombre LIKE '%' || :query || '%') ORDER BY codigo ASC")
    fun search(query: String): Flow<List<ProgramaProfesionalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(programa: ProgramaProfesionalEntity)

    @Update
    suspend fun update(programa: ProgramaProfesionalEntity)

    @Query("UPDATE programa_profesional SET estadoRegistro = :estado WHERE codigo = :codigo")
    suspend fun updateEstado(codigo: String, estado: String)


}