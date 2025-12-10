package com.example.ticketentregaapp.data.dao

import androidx.room.*
import com.example.ticketentregaapp.data.entity.FacultadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FacultadDao {
    @Query("SELECT * FROM facultad WHERE estadoRegistro != '*' ORDER BY codigo ASC")
    fun getAll(): Flow<List<FacultadEntity>>

    @Query("SELECT * FROM facultad WHERE estadoRegistro != '*' ORDER BY nombre ASC")
    fun getAllByNombre(): Flow<List<FacultadEntity>>

    @Query("SELECT * FROM facultad WHERE codigo = :codigo")
    suspend fun getByCodigo(codigo: String): FacultadEntity?

    @Query("SELECT * FROM facultad WHERE estadoRegistro != '*' AND (codigo LIKE '%' || :query || '%' OR nombre LIKE '%' || :query || '%') ORDER BY codigo ASC")
    fun search(query: String): Flow<List<FacultadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(facultad: FacultadEntity)

    @Update
    suspend fun update(facultad: FacultadEntity)

    @Query("UPDATE facultad SET estadoRegistro = :estado WHERE codigo = :codigo")
    suspend fun updateEstado(codigo: String, estado: String)
}