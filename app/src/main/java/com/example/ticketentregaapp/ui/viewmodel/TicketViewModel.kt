package com.example.ticketentregaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketentregaapp.data.entity.TicketEntity
import com.example.ticketentregaapp.data.repository.TicketRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TicketViewModel(private val repository: TicketRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _sortByFecha = MutableStateFlow(false)

    val items: StateFlow<List<TicketEntity>> = combine(
        _searchQuery,
        _sortByFecha
    ) { query, sortByFecha ->
        if (query.isBlank()) {
            repository.getAll(sortByFecha)
        } else {
            repository.search(query)
        }
    }.flatMapLatest { it }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortByFecha(sortByFecha: Boolean) {
        _sortByFecha.value = sortByFecha
    }

    fun insert(ticket: TicketEntity) = viewModelScope.launch {
        repository.insert(ticket)
    }

    fun update(ticket: TicketEntity) = viewModelScope.launch {
        repository.update(ticket)
    }

    fun delete(numeroTicket: String) = viewModelScope.launch {
        repository.delete(numeroTicket)
    }

    fun inactivate(numeroTicket: String) = viewModelScope.launch {
        repository.inactivate(numeroTicket)
    }

    fun reactivate(numeroTicket: String) = viewModelScope.launch {
        repository.reactivate(numeroTicket)
    }

    suspend fun getByNumero(numeroTicket: String): TicketEntity? = repository.getByNumero(numeroTicket)

    suspend fun getNextTicketNumber(): String = repository.getNextTicketNumber()
}