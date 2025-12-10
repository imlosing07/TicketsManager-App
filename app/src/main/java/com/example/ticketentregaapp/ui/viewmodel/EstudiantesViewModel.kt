package com.example.ticketentregaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketentregaapp.data.entity.EstudiantesEntity
import com.example.ticketentregaapp.data.repository.EstudiantesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EstudiantesViewModel(private val repository: EstudiantesRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _sortByNombre = MutableStateFlow(false)

    val items: StateFlow<List<EstudiantesEntity>> = combine(
        _searchQuery,
        _sortByNombre
    ) { query, sortByNombre ->
        if (query.isBlank()) {
            repository.getAll(sortByNombre)
        } else {
            repository.search(query)
        }
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortByNombre(sortByNombre: Boolean) {
        _sortByNombre.value = sortByNombre
    }

    fun insert(estudiante: EstudiantesEntity) = viewModelScope.launch {
        repository.insert(estudiante)
    }

    fun update(estudiante: EstudiantesEntity) = viewModelScope.launch {
        repository.update(estudiante)
    }

    fun delete(codigo: String) = viewModelScope.launch {
        repository.delete(codigo)
    }

    fun inactivate(codigo: String) = viewModelScope.launch {
        repository.inactivate(codigo)
    }

    fun reactivate(codigo: String) = viewModelScope.launch {
        repository.reactivate(codigo)
    }

    suspend fun getByCodigo(codigo: String): EstudiantesEntity? = repository.getByCodigo(codigo)

    suspend fun codigoExists(codigo: String): Boolean = repository.codigoExists(codigo)
}