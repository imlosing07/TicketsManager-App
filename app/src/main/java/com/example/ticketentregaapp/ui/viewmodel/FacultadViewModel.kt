package com.example.ticketentregaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketentregaapp.data.entity.FacultadEntity
import com.example.ticketentregaapp.data.repository.FacultadRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FacultadViewModel(private val repository: FacultadRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _sortByNombre = MutableStateFlow(false)

    val items: StateFlow<List<FacultadEntity>> = combine(
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

    fun insert(facultad: FacultadEntity) = viewModelScope.launch {
        repository.insert(facultad)
    }

    fun update(facultad: FacultadEntity) = viewModelScope.launch {
        repository.update(facultad)
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

    suspend fun getByCodigo(codigo: String): FacultadEntity? = repository.getByCodigo(codigo)

    suspend fun codigoExists(codigo: String): Boolean = repository.codigoExists(codigo)

}