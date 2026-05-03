package com.turkcell.libraryapp.data.repository

import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.data.model.BorrowRecord
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.postgrest.postgrest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookRepository {

    // --- MEVCUT KİTAP FONKSİYONLARI ---

    suspend fun getAllBooks(): Result<List<Book>> = runCatching {
        supabase.postgrest["books"]
            .select()
            .decodeList<Book>()
    }

    suspend fun getBookById(id: String): Result<Book> = runCatching {
        supabase.postgrest["books"]
            .select {
                filter { eq("id", id) }
            }
            .decodeSingle<Book>()
    }

    suspend fun addBook(book: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"].insert(book)
    }

    suspend fun updateBook(id: String, updatedBook: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"].update(updatedBook) {
            filter { eq("id", id) }
        }
    }

    suspend fun deleteBook(id: String): Result<Unit> = runCatching {
        supabase.postgrest["books"].delete {
            filter { eq("id", id) }
        }
    }

    suspend fun searchBooks(query: String): Result<List<Book>> = runCatching {
        supabase.postgrest["books"]
            .select {
                filter {
                    ilike("title", "%$query%")
                }
            }
            .decodeList<Book>()
    }

    // --- ÖDEV 1-3: ÖDÜNÇ ALMA VE LİSTELEME İŞLEMLERİ ---

    suspend fun borrowBook(record: BorrowRecord): Result<Unit> = runCatching {
        // 1. Önce BorrowRecord tablosuna kaydı atıyoruz
        supabase.postgrest["BorrowRecord"].insert(record)

        // 2. Kitabı ID ile çekip stok kontrolü yapıyoruz
        val book = getBookById(record.bookId).getOrThrow()

        if (book.avaiableCopies > 0) {
            val updatedBook = book.copy(avaiableCopies = book.avaiableCopies - 1)
            updateBook(book.id, updatedBook).getOrThrow()
        } else {
            throw Exception("Stokta kitap kalmadı!")
        }
    }

    suspend fun getStudentBorrowRecords(studentId: String): Result<List<BorrowRecord>> = runCatching {
        supabase.postgrest["BorrowRecord"]
            .select {
                filter { eq("student_id", studentId) }
            }
            .decodeList<BorrowRecord>()
    }


    suspend fun returnBook(recordId: String, bookId: String): Result<Unit> = runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val now = sdf.format(Calendar.getInstance().time)


        supabase.postgrest["BorrowRecord"].update({
            set("returned_at", now)
        }) {
            filter { eq("id", recordId) }
        }


        val book = getBookById(bookId).getOrThrow()
        val updatedBook = book.copy(avaiableCopies = book.avaiableCopies + 1)

        updateBook(bookId, updatedBook).getOrThrow()
    }
}