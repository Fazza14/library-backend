package service;

import id.co.fifgroup.library.DTO.*;
import id.co.fifgroup.library.Entity.Book;
import id.co.fifgroup.library.Entity.BookStock;
import id.co.fifgroup.library.service.BookService;
import id.co.fifgroup.library.repository.BookRepository;
import id.co.fifgroup.library.repository.BookStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookStockRepository bookStockRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBooks_Success() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Bumi Manusia");
        book.setAuthor("Pramoedya");

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        ResponseEntity<List<Book>> response = bookService.getAllBooks();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllBooks_Exception() {

        when(bookRepository.findAll()).thenThrow(new RuntimeException("Database Error"));

        ResponseEntity<List<Book>> response = bookService.getAllBooks();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void countBooks_Success() {
        when(bookRepository.count()).thenReturn(5L);

        ResponseEntity<Long> response = bookService.countBooks();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(5L, response.getBody());
        verify(bookRepository, times(1)).count();
    }

    @Test
    void getAllBooksWithStock_Success() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Java Programming");
        book.setAuthor("Fazza");

        BookStock stock = new BookStock();
        stock.setId(10L);
        stock.setIdBook(book);
        stock.setStock(15);

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        when(bookStockRepository.findAll()).thenReturn(Arrays.asList(stock));

        ResponseEntity<List<BookStockDto>> response = bookService.getAllBooksWithStock();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(15, response.getBody().get(0).getStock());
    }

    @Test
    void createBookWithStock_Success() {
        CreateBookDto request = new CreateBookDto();
        request.setIdBook(1L);
        request.setIdStock(10L);
        request.setTitle("Sample Title");
        request.setAuthor("Sample Author");
        request.setStock(5);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Sample Title");
        book.setAuthor("Sample Author");

        when(bookRepository.existsById(1L)).thenReturn(false);
        when(bookStockRepository.existsById(10L)).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ResponseEntity<?> response = bookService.createBookWithStock(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookStockRepository, times(1)).save(any(BookStock.class));
    }

    @Test
    void createBookWithStock_DuplicateBookId() {
        CreateBookDto request = new CreateBookDto();
        request.setIdBook(1L);

        when(bookRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<?> response = bookService.createBookWithStock(request);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("ID Buku sudah terdaftar!"));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBookWithStock_Success() {
        UpdateBookDto request = new UpdateBookDto(1L, "New Title", "New Author", 10L, 50);

        Book existingBook = new Book();
        existingBook.setId(1L);

        BookStock existingStock = new BookStock();
        existingStock.setId(10L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookStockRepository.findById(10L)).thenReturn(Optional.of(existingStock));

        ResponseEntity<?> response = bookService.updateBookWithStock(request);

        assertEquals(200, response.getStatusCode().value());
        verify(bookRepository, times(1)).save(existingBook);
        verify(bookStockRepository, times(1)).save(existingStock);
    }

    @Test
    void updateBookWithStock_BookNotFound() {
        UpdateBookDto request = new UpdateBookDto(1L, "New Title", "New Author", 10L, 50);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookService.updateBookWithStock(request);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("tidak ditemukan!"));
        verify(bookRepository, never()).save(any(Book.class));
    }
    @Test
    void deleteBookWithStock_Success() {
        Long idBook = 1L;
        Book existingBook = new Book();
        existingBook.setId(idBook);

        BookStock stock = new BookStock();
        stock.setId(10L);
        stock.setIdBook(existingBook);

        when(bookRepository.findById(idBook)).thenReturn(Optional.of(existingBook));
        when(bookStockRepository.findAll()).thenReturn(Arrays.asList(stock));

        ResponseEntity<?> response = bookService.deleteBookWithStock(idBook);

        assertEquals(200, response.getStatusCode().value());
        verify(bookStockRepository, times(1)).delete(stock);
        verify(bookRepository, times(1)).delete(existingBook);
    }

}
