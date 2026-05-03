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
import java.util.*

class BookViewModel : ViewModel() {
    private val repository = BookRepository()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books


    private val _myBorrows = MutableStateFlow<List<BorrowRecord>>(emptyList())
    val myBorrows: StateFlow<List<BorrowRecord>> = _myBorrows

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


    fun loadMyBorrows(studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getStudentBorrowRecords(studentId)
                .onSuccess { records ->
                    _myBorrows.value = records
                }
                .onFailure {
                    _error.value = "Kiralama geçmişi yüklenemedi: ${it.message}"
                }
            _isLoading.value = false
        }
    }


    fun borrowBook(book: Book, studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true


            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val calendar = Calendar.getInstance()


            val borrowedAt = sdf.format(calendar.time)


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
                    loadBooks()
                }
                .onFailure {
                    _error.value = "Ödünç alma başarısız: ${it.message}"
                }
            _isLoading.value = false
        }
    }


    fun returnBook(record: BorrowRecord) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.returnBook(record.id, record.bookId)
                .onSuccess {

                    loadBooks()
                    loadMyBorrows(record.studentId)
                }
                .onFailure {
                    _error.value = "Kitap iade edilemedi: ${it.message}"
                }
            _isLoading.value = false
        }
    }
}