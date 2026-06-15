package service;

import id.co.fifgroup.library.Entity.BookStock;
import id.co.fifgroup.library.Entity.Book;
import id.co.fifgroup.library.repository.BookStockRepository;
import id.co.fifgroup.library.service.BookStockService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookStockTest {

    @Mock
    private BookStockRepository bookStockRepository;

    @InjectMocks
    private BookStockService bookStockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void reduceBookStock_Success() {
        Long bookId = 2L;

        Book mockBook = new Book();
        mockBook.setId(bookId);

        BookStock mockStock = new BookStock();
        mockStock.setId(1L);
        mockStock.setIdBook(mockBook);
        mockStock.setStock(50);

        List<BookStock> stockList = Collections.singletonList(mockStock);

        when(bookStockRepository.findAll()).thenReturn(stockList);
        when(bookStockRepository.save(any(BookStock.class))).thenReturn(mockStock);

        ResponseEntity<?> response = bookStockService.reduceBookStock(bookId);

        verify(bookStockRepository, times(1)).findAll();
        verify(bookStockRepository, times(1)).save(any(BookStock.class));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\": \"Stok berhasil dikurangi\"}", response.getBody());
        assertEquals(49, mockStock.getStock()); // Memastikan stok berkurang 1
    }

    @Test
    void reduceBookStock_StockEmptyOrNotFound() {
        Long bookId = 2L;

        Book mockBook = new Book();
        mockBook.setId(bookId);

        BookStock mockStock = new BookStock();
        mockStock.setId(1L);
        mockStock.setIdBook(mockBook);
        mockStock.setStock(0);

        List<BookStock> stockList = Collections.singletonList(mockStock);

        when(bookStockRepository.findAll()).thenReturn(stockList);

        ResponseEntity<?> response = bookStockService.reduceBookStock(bookId);

        verify(bookStockRepository, times(1)).findAll();
        verify(bookStockRepository, never()).save(any(BookStock.class));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\": \"Stok habis atau buku tidak ditemukan\"}", response.getBody());
    }

    @Test
    void reduceBookStock_Exception() {
        Long bookId = 2L;

       when(bookStockRepository.findAll()).thenThrow(new RuntimeException("Database Error"));

        ResponseEntity<?> response = bookStockService.reduceBookStock(bookId);

        verify(bookStockRepository, times(1)).findAll();
        verify(bookStockRepository, never()).save(any(BookStock.class));

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("{\"message\": \"Terjadi kesalahan di server\"}", response.getBody());
    }
}