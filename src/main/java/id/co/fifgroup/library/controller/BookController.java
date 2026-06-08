package id.co.fifgroup.library.controller;

import java.util.Collections;
import java.util.List;

import id.co.fifgroup.library.DTO.BookStockDTO;
import id.co.fifgroup.library.DTO.CreateBookDTO;
import id.co.fifgroup.library.DTO.UpdateBookDTO;
import id.co.fifgroup.library.DTO.DeleteBookDTO;
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
    public List<Book> getAllBooks() {
        try {
            return bookService.getAllBooks();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @GetMapping("/stock")
    public List<BookStockDTO> getAllBooksWithStock() {
        try {
            return bookService.getAllBooksWithStock();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @GetMapping("/count")
    public Long countBooks() {
        try {
            return bookService.countBooks();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @PostMapping
    public ResponseEntity<?> createBookWithStock(@RequestBody CreateBookDTO request) {
        try {
            BookStockDTO result = bookService.createBookWithStock(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"message\": \"Terjadi kesalahan pada server\"}");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateBookWithStock(@RequestBody UpdateBookDTO request) {
        try {
            UpdateBookDTO result = bookService.updateBookWithStock(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"message\": \"Terjadi kesalahan pada server\"}");
        }
    }

    @GetMapping("/{idBook}")
    public ResponseEntity<?> getBookDetailForDelete(@PathVariable Long idBook) {
        try {
            DeleteBookDTO result = bookService.getBookDetailForDelete(idBook);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"message\": \"Terjadi kesalahan pada server\"}");
        }
    }

    @DeleteMapping("/{idBook}")
    public ResponseEntity<?> deleteBookWithStock(@PathVariable Long idBook) {
        try {
            bookService.deleteBookWithStock(idBook);
            return ResponseEntity.ok().body("{\"message\": \"Buku dan data stok berhasil dihapus secara permanen\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"message\": \"Terjadi kesalahan pada server\"}");
        }
    }
}