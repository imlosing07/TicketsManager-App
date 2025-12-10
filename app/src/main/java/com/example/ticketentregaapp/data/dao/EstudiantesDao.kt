package com.example.ticketentregaapp.data.dao

import androidx.room.*
import com.example.ticketentregaapp.data.entity.EstudiantesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstudiantesDao {
    // Obtener TODOS los registros (activos e inactivos) ordenados por código
    @Query("SELECT * FROM estudiantes WHERE estadoRegistro != '*' ORDER BY codigo ASC")
    fun getAll(): Flow<List<EstudiantesEntity>>

    // Obtener TODOS los registros ordenados por nombre
    @Query("SELECT * FROM estudiantes WHERE estadoRegistro != '*' ORDER BY nombre ASC")
    fun getAllByNombre(): Flow<List<EstudiantesEntity>>

    @Query("SELECT * FROM estudiantes WHERE codigo = :codigo")
    suspend fun getByCodigo(codigo: String): EstudiantesEntity?

    // Búsqueda en todos los registros (activos e inactivos)
    @Query("SELECT * FROM estudiantes WHERE estadoRegistro != '*' AND (codigo LIKE '%' || :query || '%' OR nombre LIKE '%' || :query || '%') ORDER BY codigo ASC")
    fun search(query: String): Flow<List<EstudiantesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estudiante: EstudiantesEntity)

    @Update
    suspend fun update(estudiante: EstudiantesEntity)

    @Query("UPDATE estudiantes SET estadoRegistro = :estado WHERE codigo = :codigo")
    suspend fun updateEstado(codigo: String, estado: String)
}