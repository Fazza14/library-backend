package service;

import id.co.fifgroup.library.repository.BookRepository;
import id.co.fifgroup.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }
}
