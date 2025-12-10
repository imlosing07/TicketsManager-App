package com.example.ticketentregaapp.data.dao

import androidx.room.*
import com.example.ticketentregaapp.data.entity.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Query("SELECT * FROM ticket WHERE estadoRegistro != '*' ORDER BY numeroTicket ASC")
    fun getAll(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM ticket WHERE estadoRegistro != '*' ORDER BY fecha DESC")
    fun getAllByFecha(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM ticket WHERE numeroTicket = :numeroTicket")
    suspend fun getByNumero(numeroTicket: String): TicketEntity?

    @Query("SELECT * FROM ticket WHERE estadoRegistro != '*' AND (numeroTicket LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%') ORDER BY numeroTicket ASC")
    fun search(query: String): Flow<List<TicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ticket: TicketEntity)

    @Update
    suspend fun update(ticket: TicketEntity)

    @Query("UPDATE ticket SET estadoRegistro = :estado WHERE numeroTicket = :numeroTicket")
    suspend fun updateEstado(numeroTicket: String, estado: String)

    @Query("SELECT numeroTicket FROM ticket WHERE estadoRegistro != '*' ORDER BY numeroTicket DESC LIMIT 1")
    suspend fun getLastTicketNumber(): String?
}