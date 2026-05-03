package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List // Güncel ikon paketi
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // 'Property delegate' hatasını bu çözer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel,
    onNavigateToBorrows: () -> Unit
) {
    val books by bookViewModel.books.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()
    val error by bookViewModel.error.collectAsState()

    // DÜZELTME: AuthViewModel'de 'currentUser' yerine 'profile' kullanıyoruz
    val userProfile by authViewModel.profile.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Kütüphane Yönetimi", fontWeight = FontWeight.ExtraBold)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToBorrows) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Kiralamalarım"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Bir hata oluştu: $error", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { bookViewModel.loadBooks() }, modifier = Modifier.padding(top = 12.dp)) {
                            Text("Tekrar Dene")
                        }
                    }
                }
                books.isEmpty() -> {
                    Text(
                        text = "Kütüphanede henüz kitap yok.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp)
                    ) {
                        items(books) { book ->
                            BookItem(
                                book = book,
                                onBookClick = { /* Detay yönlendirmesi */ },
                                onBorrowClick = { selectedBook ->
                                    // Profile nesnesi varsa içindeki ID ile ödünç alma işlemini başlat
                                    userProfile?.let { p ->
                                        // Not: Profile modelinde 'id' alanı olduğundan emin ol
                                        bookViewModel.borrowBook(selectedBook, p.userId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}