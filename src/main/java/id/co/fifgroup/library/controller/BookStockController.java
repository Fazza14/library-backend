package id.co.fifgroup.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import id.co.fifgroup.library.service.BookStockService;
import id.co.fifgroup.library.service.BookService;
import id.co.fifgroup.library.DTO.BookStockDto;
import java.util.List;

@RestController
@RequestMapping("/api/book-stocks")
@CrossOrigin(origins = "*")
public class BookStockController {

    @Autowired
    private BookStockService bookStockService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookStockDto>> getAllStocks() {
        return bookService.getAllBooksWithStock();
    }

    @PutMapping("/reduce/{idBook}")
    public ResponseEntity<?> reduceStock(@PathVariable Long idBook) {
        return bookStockService.reduceBookStock(idBook);
    }
}