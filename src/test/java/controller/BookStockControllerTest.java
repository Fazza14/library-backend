package controller;

import id.co.fifgroup.library.DTO.BookStockDto;
import id.co.fifgroup.library.controller.BookStockController;
import id.co.fifgroup.library.service.BookService;
import id.co.fifgroup.library.service.BookStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookStockControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookStockService bookStockService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookStockController bookStockController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Membuka dan menginisialisasi mock [cite: 44]
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookStockController).build();
    }

    @Test
    void getAllStocks_Success() throws Exception {

        List<BookStockDto> stockList = new ArrayList<>();
        ResponseEntity<List<BookStockDto>> responseEntity = new ResponseEntity<>(stockList, HttpStatus.OK);

        when(bookService.getAllBooksWithStock()).thenReturn(responseEntity);

        mockMvc.perform(get("/api/book-stocks"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).getAllBooksWithStock();
    }

    @Test
    void reduceStock_Success() throws Exception {

        Long idBook = 1L;
        ResponseEntity<?> responseEntity = new ResponseEntity<>("Stock reduced successfully", HttpStatus.OK);


        when(bookStockService.reduceBookStock(idBook)).thenReturn((ResponseEntity) responseEntity);

        mockMvc.perform(put("/api/book-stocks/reduce/{idBook}", idBook))
                .andExpect(status().isOk());

        verify(bookStockService, times(1)).reduceBookStock(idBook);
    }
}