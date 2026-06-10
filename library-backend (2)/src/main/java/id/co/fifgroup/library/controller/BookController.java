package id.co.fifgroup.library.controller;

import java.util.List;
import id.co.fifgroup.library.DTO.*;
import id.co.fifgroup.library.Entity.Book;
import id.co.fifgroup.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/stock")
    public ResponseEntity<List<BookStockDTO>> getAllBooksWithStock() {
        return bookService.getAllBooksWithStock();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countBooks() {
        return bookService.countBooks();
    }

    @PostMapping
    public ResponseEntity<?> createBookWithStock(@RequestBody CreateBookDTO request) {
        return bookService.createBookWithStock(request);
    }

    @PutMapping
    public ResponseEntity<?> updateBookWithStock(@RequestBody UpdateBookDTO request) {
        return bookService.updateBookWithStock(request);
    }

    @GetMapping("/{idBook}")
    public ResponseEntity<?> getBookDetailForDelete(@PathVariable Long idBook) {
        return bookService.getBookDetailForDelete(idBook);
    }

    @DeleteMapping("/{idBook}")
    public ResponseEntity<?> deleteBookWithStock(@PathVariable Long idBook) {
        return bookService.deleteBookWithStock(idBook);
    }
}