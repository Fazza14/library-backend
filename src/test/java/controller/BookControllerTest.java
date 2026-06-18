package controller;

//import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.fifgroup.library.DTO.BookStockDto;
import id.co.fifgroup.library.DTO.CreateBookDto;
import id.co.fifgroup.library.DTO.UpdateBookDto;
import id.co.fifgroup.library.Entity.Book;
import id.co.fifgroup.library.controller.BookController;
import id.co.fifgroup.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private final tools.jackson.databind.ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void getAllBooks_Success() throws Exception {

        List<Book> books = new ArrayList<>();
        ResponseEntity<List<Book>> responseEntity = new ResponseEntity<>(books, HttpStatus.OK);

        when(bookService.getAllBooks()).thenReturn(responseEntity);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void getAllBooksWithStock_Success() throws Exception {

        List<BookStockDto> stockList = new ArrayList<>();
        ResponseEntity<List<BookStockDto>> responseEntity = new ResponseEntity<>(stockList, HttpStatus.OK);

         when(bookService.getAllBooksWithStock()).thenReturn(responseEntity);

         mockMvc.perform(get("/api/books/stock"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).getAllBooksWithStock();
    }

    @Test
    void countBooks_Success() throws Exception {

        ResponseEntity<Long> responseEntity = new ResponseEntity<>(10L, HttpStatus.OK);

       when(bookService.countBooks()).thenReturn(responseEntity);

        mockMvc.perform(get("/api/books/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        verify(bookService, times(1)).countBooks();
    }

    @Test
    void createBookWithStock_Success() throws Exception {

        CreateBookDto request = new CreateBookDto();

        ResponseEntity<?> responseEntity = new ResponseEntity<>("Book created successfully", HttpStatus.CREATED);

        when(bookService.createBookWithStock(any(CreateBookDto.class))).thenReturn((ResponseEntity) responseEntity);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());

        verify(bookService, times(1)).createBookWithStock(any(CreateBookDto.class));
    }

    @Test
    void updateBookWithStock_Success() throws Exception {
        UpdateBookDto request = new UpdateBookDto();

        ResponseEntity<?> responseEntity = new ResponseEntity<>("Book updated successfully", HttpStatus.OK);

        when(bookService.updateBookWithStock(any(UpdateBookDto.class))).thenReturn((ResponseEntity) responseEntity);

       mockMvc.perform(put("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookService, times(1)).updateBookWithStock(any(UpdateBookDto.class));
    }

    @Test
    void getBookDetailForDelete_Success() throws Exception {

        Long idBook = 1L;
        ResponseEntity<?> responseEntity = new ResponseEntity<>("Book Details", HttpStatus.OK);

        when(bookService.getBookDetailForDelete(idBook)).thenReturn((ResponseEntity) responseEntity);

       mockMvc.perform(get("/api/books/{idBook}", idBook))
                .andExpect(status().isOk());

        verify(bookService, times(1)).getBookDetailForDelete(idBook);
    }

    @Test
    void deleteBookWithStock_Success() throws Exception {

        Long idBook = 1L;
        ResponseEntity<?> responseEntity = new ResponseEntity<>("Book deleted successfully", HttpStatus.OK);

        when(bookService.deleteBookWithStock(idBook)).thenReturn((ResponseEntity) responseEntity);

        mockMvc.perform(delete("/api/books/{idBook}", idBook))
                .andExpect(status().isOk());

        verify(bookService, times(1)).deleteBookWithStock(idBook);
    }
}