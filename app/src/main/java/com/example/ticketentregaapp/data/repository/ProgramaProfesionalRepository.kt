package com.example.ticketentregaapp.data.repository

import com.example.ticketentregaapp.data.dao.ProgramaProfesionalDao
import com.example.ticketentregaapp.data.entity.ProgramaProfesionalEntity
import kotlinx.coroutines.flow.Flow

class ProgramaProfesionalRepository(private val dao: ProgramaProfesionalDao) {
    fun getAll(sortByNombre: Boolean = false): Flow<List<ProgramaProfesionalEntity>> {
        return if (sortByNombre) dao.getAllByNombre() else dao.getAll()
    }

    suspend fun getByCodigo(codigo: String): ProgramaProfesionalEntity? = dao.getByCodigo(codigo)

    fun search(query: String): Flow<List<ProgramaProfesionalEntity>> = dao.search(query)

    suspend fun insert(programa: ProgramaProfesionalEntity) = dao.insert(programa)

    suspend fun update(programa: ProgramaProfesionalEntity) = dao.update(programa)

    suspend fun delete(codigo: String) = dao.updateEstado(codigo, "*")

    suspend fun inactivate(codigo: String) = dao.updateEstado(codigo, "I")

    suspend fun reactivate(codigo: String) = dao.updateEstado(codigo, "A")

    suspend fun codigoExists(codigo: String): Boolean {
        return dao.getByCodigo(codigo) != null
    }
}
