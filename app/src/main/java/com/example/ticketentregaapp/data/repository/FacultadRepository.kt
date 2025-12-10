package com.example.ticketentregaapp.data.repository

import com.example.ticketentregaapp.data.dao.FacultadDao
import com.example.ticketentregaapp.data.entity.FacultadEntity
import kotlinx.coroutines.flow.Flow

class FacultadRepository(private val dao: FacultadDao) {
    fun getAll(sortByNombre: Boolean = false): Flow<List<FacultadEntity>> {
        return if (sortByNombre) dao.getAllByNombre() else dao.getAll()
    }

    suspend fun getByCodigo(codigo: String): FacultadEntity? = dao.getByCodigo(codigo)

    fun search(query: String): Flow<List<FacultadEntity>> = dao.search(query)

    suspend fun insert(facultad: FacultadEntity) = dao.insert(facultad)

    suspend fun update(facultad: FacultadEntity) = dao.update(facultad)

    suspend fun delete(codigo: String) = dao.updateEstado(codigo, "*")

    suspend fun inactivate(codigo: String) = dao.updateEstado(codigo, "I")

    suspend fun reactivate(codigo: String) = dao.updateEstado(codigo, "A")

    suspend fun codigoExists(codigo: String): Boolean {
        return dao.getByCodigo(codigo) != null
    }
}