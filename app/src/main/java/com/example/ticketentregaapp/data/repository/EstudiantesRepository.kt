package com.example.ticketentregaapp.data.repository

import com.example.ticketentregaapp.data.dao.EstudiantesDao
import com.example.ticketentregaapp.data.entity.EstudiantesEntity
import kotlinx.coroutines.flow.Flow

class EstudiantesRepository(private val dao: EstudiantesDao) {
    // Ahora obtiene TODOS los registros (activos E inactivos)
    fun getAll(sortByNombre: Boolean = false): Flow<List<EstudiantesEntity>> {
        return if (sortByNombre) dao.getAllByNombre() else dao.getAll()
    }

    suspend fun getByCodigo(codigo: String): EstudiantesEntity? = dao.getByCodigo(codigo)

    // Búsqueda en todos los registros
    fun search(query: String): Flow<List<EstudiantesEntity>> = dao.search(query)

    suspend fun insert(estudiante: EstudiantesEntity) = dao.insert(estudiante)

    suspend fun update(estudiante: EstudiantesEntity) = dao.update(estudiante)

    suspend fun delete(codigo: String) = dao.updateEstado(codigo, "*")

    suspend fun inactivate(codigo: String) = dao.updateEstado(codigo, "I")

    suspend fun reactivate(codigo: String) = dao.updateEstado(codigo, "A")

    // Nuevo método para validar si el código ya existe
    suspend fun codigoExists(codigo: String): Boolean {
        return dao.getByCodigo(codigo) != null
    }
}