package service;

import id.co.fifgroup.library.Entity.BookStock;
import id.co.fifgroup.library.repository.BookRepository;
import id.co.fifgroup.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BookStockTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void reduceBookStock() {
        Long bookId = 2L;

        BookStock mockStock = new BookStock();
        mockStock.setId(bookId);
        mockStock.setStock(50);




    }

}
