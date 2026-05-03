package com.turkcell.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.data.model.BorrowRecord
import com.turkcell.libraryapp.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class BookViewModel : ViewModel() {
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
            _error.value = null

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

    // Ödev 2: Ödünç Alma Fonksiyonu (API 24 Uyumlu)
    fun borrowBook(book: Book, studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // ISO 8601 formatı için SimpleDateFormat (API 24 uyumlu)
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val calendar = Calendar.getInstance()

            // Şu anki zaman (Borrowed At)
            val borrowedAt = sdf.format(calendar.time)

            // 5 Gün sonrası (Due Date)
            calendar.add(Calendar.DAY_OF_YEAR, 5)
            val dueDate = sdf.format(calendar.time)

            val record = BorrowRecord(
                id = UUID.randomUUID().toString(),
                studentId = studentId,
                bookId = book.id,
                borrowedAt = borrowedAt,
                dueDate = dueDate,
                returnedAt = null
            )

            repository.borrowBook(record)
                .onSuccess {
                    loadBooks() // UI'daki stok miktarını güncellemek için tekrar yükle
                }
                .onFailure {
                    _error.value = "Ödünç alma başarısız: ${it.message}"
                }
            _isLoading.value = false
        }
    }
}