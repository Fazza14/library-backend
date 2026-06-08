package id.co.fifgroup.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import id.co.fifgroup.library.service.BookStockService;
import id.co.fifgroup.library.service.BookService;
import id.co.fifgroup.library.DTO.BookStockDTO;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/book-stocks")
@CrossOrigin(origins = "*")
public class BookStockController {

    @Autowired
    private BookStockService bookStockService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookStockDTO>> getAllStocks() {
        try {
            List<BookStockDTO> stocks = bookService.getAllBooksWithStock();
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    @PutMapping("/reduce/{idBook}")
    public ResponseEntity<?> reduceStock(@PathVariable Long idBook) {
        try {
            boolean isReduced = bookStockService.reduceBookStock(idBook);
            if (isReduced) {
                return ResponseEntity.ok().body("{\"message\": \"Stok berhasil dikurangi\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"Stok habis atau buku tidak ditemukan\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"message\": \"Terjadi kesalahan di server\"}");
        }
    }
}
