package com.example.ticketentregaapp.data.repository

import com.example.ticketentregaapp.data.dao.TicketDao
import com.example.ticketentregaapp.data.entity.TicketEntity
import kotlinx.coroutines.flow.Flow

class TicketRepository(private val dao: TicketDao) {
    fun getAll(sortByFecha: Boolean = false): Flow<List<TicketEntity>> {
        return if (sortByFecha) dao.getAllByFecha() else dao.getAll()
    }

    suspend fun getByNumero(numeroTicket: String): TicketEntity? = dao.getByNumero(numeroTicket)

    fun search(query: String): Flow<List<TicketEntity>> = dao.search(query)

    suspend fun insert(ticket: TicketEntity) = dao.insert(ticket)

    suspend fun update(ticket: TicketEntity) = dao.update(ticket)

    suspend fun delete(numeroTicket: String) = dao.updateEstado(numeroTicket, "*")

    suspend fun inactivate(numeroTicket: String) = dao.updateEstado(numeroTicket, "I")

    suspend fun reactivate(numeroTicket: String) = dao.updateEstado(numeroTicket, "A")

    // Obtener el siguiente número de ticket
    suspend fun getNextTicketNumber(): String {
        val lastTicket = dao.getLastTicketNumber()
        return if (lastTicket == null) {
            "T0001"
        } else {
            val number = lastTicket.replace("T", "").toIntOrNull() ?: 0
            "T" + (number + 1).toString().padStart(4, '0')
        }
    }
}