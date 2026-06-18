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

public class BookTest {

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
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }


    @Test
    void countBooks_Success() {
        when(bookRepository.count()).thenReturn(5L);

        ResponseEntity<Long> response = bookService.countBooks();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(5L, response.getBody());
    }

    @Test
    void countBooks_Exception() {
        when(bookRepository.count()).thenThrow(new RuntimeException("Database Error"));

        ResponseEntity<Long> response = bookService.countBooks();

        assertEquals(500, response.getStatusCode().value());
        assertEquals(0L, response.getBody());
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
    }

    @Test
    void getAllBooksWithStock_Exception() {
        when(bookRepository.findAll()).thenThrow(new RuntimeException("Database Error"));

        ResponseEntity<List<BookStockDto>> response = bookService.getAllBooksWithStock();

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllBooksWithStock_FilterOutWhenIdBookIsNull() {
        Book book = new Book();
        book.setId(1L);

        BookStock stockWithNullBook = new BookStock();
        stockWithNullBook.setId(10L);
        stockWithNullBook.setIdBook(null);

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        when(bookStockRepository.findAll()).thenReturn(Arrays.asList(stockWithNullBook));

        ResponseEntity<List<BookStockDto>> response = bookService.getAllBooksWithStock();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(0, response.getBody().get(0).getStock());
    }

    @Test
    void getAllBooksWithStock_DuplicateKeysAndNullId() {
        Book book = new Book();
        book.setId(1L);

        BookStock stock1 = new BookStock();
        stock1.setId(10L);
        stock1.setIdBook(book);
        stock1.setStock(5);

        BookStock stockDuplicate = new BookStock();
        stockDuplicate.setId(11L);
        stockDuplicate.setIdBook(book);
        stockDuplicate.setStock(20);

        Book badBook = new Book();
        badBook.setId(null);
        BookStock stockWithBadBookId = new BookStock();
        stockWithBadBookId.setId(12L);
        stockWithBadBookId.setIdBook(badBook);

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
        when(bookStockRepository.findAll()).thenReturn(Arrays.asList(stock1, stockDuplicate, stockWithBadBookId));

        ResponseEntity<List<BookStockDto>> response = bookService.getAllBooksWithStock();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(5, response.getBody().get(0).getStock());
    }


    @Test
    void createBookWithStock_Success() {
        CreateBookDto request = new CreateBookDto();
        request.setIdBook(1L);
        request.setIdStock(10L);
        request.setTitle("Sample");
        request.setAuthor("Author");
        request.setStock(5);

        Book book = new Book();
        book.setId(1L);

        when(bookRepository.existsById(anyLong())).thenReturn(false);
        when(bookStockRepository.existsById(anyLong())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ResponseEntity<?> response = bookService.createBookWithStock(request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void createBookWithStock_DuplicateBookId() {
        CreateBookDto request = new CreateBookDto();
        request.setIdBook(1L);

        when(bookRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<?> response = bookService.createBookWithStock(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void createBookWithStock_DuplicateStockId() {
        CreateBookDto request = new CreateBookDto();
        request.setIdBook(1L);
        request.setIdStock(10L);

        when(bookRepository.existsById(1L)).thenReturn(false);
        when(bookStockRepository.existsById(10L)).thenReturn(true);

        ResponseEntity<?> response = bookService.createBookWithStock(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void createBookWithStock_GeneralException() {
        CreateBookDto request = new CreateBookDto();
        request.setIdBook(1L);

        when(bookRepository.existsById(anyLong())).thenAnswer(invocation -> {
            throw new Exception("Database crash murni");
        });

        ResponseEntity<?> response = bookService.createBookWithStock(request);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Terjadi kesalahan pada server"));
    }


    @Test
    void updateBookWithStock_Success() {
        UpdateBookDto request = new UpdateBookDto();
        request.setIdBook(1L);
        request.setIdStock(10L);
        request.setTitle("New Title");
        request.setAuthor("New Author");
        request.setStock(50);

        Book existingBook = new Book();
        existingBook.setId(1L);

        BookStock existingStock = new BookStock();
        existingStock.setId(10L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookStockRepository.findById(10L)).thenReturn(Optional.of(existingStock));

        ResponseEntity<?> response = bookService.updateBookWithStock(request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void updateBookWithStock_BookNotFound() {
        UpdateBookDto request = new UpdateBookDto();
        request.setIdBook(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookService.updateBookWithStock(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void updateBookWithStock_StockNotFound() {
        UpdateBookDto request = new UpdateBookDto();
        request.setIdBook(1L);
        request.setIdStock(10L);

        Book existingBook = new Book();
        existingBook.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookStockRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookService.updateBookWithStock(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void updateBookWithStock_RuntimeException_General() {
        UpdateBookDto request = new UpdateBookDto();
        request.setIdBook(1L);

        when(bookRepository.findById(1L)).thenThrow(new RuntimeException("Crash"));

        ResponseEntity<?> response = bookService.updateBookWithStock(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void updateBookWithStock_GeneralException_ReturnsInternalServerError() {
        UpdateBookDto request = new UpdateBookDto();
        request.setIdBook(1L);

       when(bookRepository.findById(anyLong())).thenAnswer(invocation -> {
            throw new Exception("Database crash murni");
        });

        ResponseEntity<?> response = bookService.updateBookWithStock(request);

        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Terjadi kesalahan pada server"));
    }


    @Test
    void getBookDetailForDelete_Success() {
        Long idBook = 1L;
        Book book = new Book();
        book.setId(idBook);

        BookStock stock = new BookStock();
        stock.setId(10L);
        stock.setIdBook(book);
        stock.setStock(5);

        when(bookRepository.findById(idBook)).thenReturn(Optional.of(book));
        when(bookStockRepository.findAll()).thenReturn(Arrays.asList(stock));

        ResponseEntity<?> response = bookService.getBookDetailForDelete(idBook);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getBookDetailForDelete_BookNotFound() {
        Long idBook = 1L;
        when(bookRepository.findById(idBook)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookService.getBookDetailForDelete(idBook);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void getBookDetailForDelete_StockNotFound() {
        Long idBook = 1L;
        Book book = new Book();
        book.setId(idBook);

        when(bookRepository.findById(idBook)).thenReturn(Optional.of(book));
        when(bookStockRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = bookService.getBookDetailForDelete(idBook);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void getBookDetailForDelete_StockFilterMismatches() {
        Long idBook = 1L;
        Book book = new Book();
        book.setId(idBook);

        BookStock badStock1 = new BookStock();
        badStock1.setIdBook(null);

        Book otherBook = new Book();
        otherBook.setId(99L);
        BookStock badStock2 = new BookStock();
        badStock2.setIdBook(otherBook);

        when(bookRepository.findById(idBook)).thenReturn(Optional.of(book));
        when(bookStockRepository.findAll()).thenReturn(Arrays.asList(badStock1, badStock2));

        ResponseEntity<?> response = bookService.getBookDetailForDelete(idBook);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void getBookDetailForDelete_GeneralException() {
        Long idBook = 1L;

        when(bookRepository.findById(idBook)).thenAnswer(invocation -> {
            throw new Exception("Database crash murni");
        });

        ResponseEntity<?> response = bookService.getBookDetailForDelete(idBook);

        assertEquals(500, response.getStatusCode().value());
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
    }

    @Test
    void deleteBookWithStock_BookNotFound() {
        Long idBook = 1L;
        when(bookRepository.findById(idBook)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookService.deleteBookWithStock(idBook);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void deleteBookWithStock_StockAlreadyEmpty() {
        Long idBook = 1L;
        Book existingBook = new Book();
        existingBook.setId(idBook);

        when(bookRepository.findById(idBook)).thenReturn(Optional.of(existingBook));
        when(bookStockRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = bookService.deleteBookWithStock(idBook);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteBookWithStock_StockFilterMismatches() {
        Long idBook = 1L;
        Book existingBook = new Book();
        existingBook.setId(idBook);

        BookStock badStock = new BookStock();
        badStock.setIdBook(null);

        when(bookRepository.findById(idBook)).thenReturn(Optional.of(existingBook));
        when(bookStockRepository.findAll()).thenReturn(Arrays.asList(badStock));

        ResponseEntity<?> response = bookService.deleteBookWithStock(idBook);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteBookWithStock_GeneralException() {
        Long idBook = 1L;

        when(bookRepository.findById(idBook)).thenAnswer(invocation -> {
            throw new Exception("Database crash murni");
        });

        ResponseEntity<?> response = bookService.deleteBookWithStock(idBook);

        assertEquals(500, response.getStatusCode().value());
    }
}