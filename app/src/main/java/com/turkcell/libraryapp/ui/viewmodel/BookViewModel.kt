package com.turkcell.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    // Eğer BookRepository() hata veriyorsa, içine SupabaseClient.client eklemen gerekebilir
    private val repository = BookRepository()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Her aramada hatayı sıfırla

            repository.getAllBooks()
                .onSuccess { bookList ->
                    _books.value = bookList
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Bilinmeyen bir hata oluştu"
                }

            _isLoading.value = false
        }
    }

    private fun BookRepository.getAllBooks() {
        TODO("Not yet implemented")
    }
}