package id.co.fifgroup.library.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import id.co.fifgroup.library.Entity.Book;
import id.co.fifgroup.library.Entity.BookStock;
import id.co.fifgroup.library.DTO.*;
import id.co.fifgroup.library.repository.BookRepository;
import id.co.fifgroup.library.repository.BookStockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookStockRepository bookStockRepository;

    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            return ResponseEntity.ok(bookRepository.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    public ResponseEntity<Long> countBooks() {
        try {
            return ResponseEntity.ok(bookRepository.count());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(0L);
        }
    }

    public ResponseEntity<List<BookStockDTO>> getAllBooksWithStock() {
        try {
            List<Book> allBooks = bookRepository.findAll();
            List<BookStock> allStocks = bookStockRepository.findAll();

            Map<Long, Integer> stockMap = allStocks.stream()
                    .filter(stock -> stock.getIdBook() != null && stock.getIdBook().getId() != null)
                    .collect(
                            Collectors.toMap(
                                    stock -> stock.getIdBook().getId(),
                                    BookStock::getStock,
                                    (existing, replacement) -> existing
                            )
                    );

            List<BookStockDTO> bookStockList = allBooks.stream().map(book -> {
                Integer currentStock = stockMap.getOrDefault(book.getId(), 0);
                return new BookStockDTO(book.getId(), book.getTitle(), book.getAuthor(), currentStock);
            }).collect(Collectors.toList());

            return ResponseEntity.ok(bookStockList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @Transactional
    public ResponseEntity<?> createBookWithStock(CreateBookDTO request) {
        try {
            if (bookRepository.existsById(request.getIdBook())) {
                return ResponseEntity.badRequest().body("{\"message\": \"ID Buku sudah terdaftar!\"}");
            }
            if (bookStockRepository.existsById(request.getIdStock())) {
                return ResponseEntity.badRequest().body("{\"message\": \"ID Stok sudah terdaftar!\"}");
            }

            Book book = new Book();
            book.setId(request.getIdBook());
            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            Book savedBook = bookRepository.save(book);

            BookStock bookStock = new BookStock();
            bookStock.setId(request.getIdStock());
            bookStock.setIdBook(savedBook);
            bookStock.setStock(request.getStock());
            bookStockRepository.save(bookStock);

            BookStockDTO result = new BookStockDTO(
                    savedBook.getId(), savedBook.getTitle(), savedBook.getAuthor(), bookStock.getStock()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"message\": \"Terjadi kesalahan pada server\"}");
        }
    }

    @Transactional
    public ResponseEntity<?> updateBookWithStock(UpdateBookDTO request) {
        try {
            Book book = bookRepository.findById(request.getIdBook())
                    .orElseThrow(() -> new RuntimeException("Buku dengan ID " + request.getIdBook() + " tidak ditemukan!"));

            BookStock bookStock = bookStockRepository.findById(request.getIdStock())
                    .orElseThrow(() -> new RuntimeException("Stok dengan ID " + request.getIdStock() + " tidak ditemukan!"));

            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            bookRepository.save(book);

            bookStock.setStock(request.getStock());
            bookStockRepository.save(bookStock);

            UpdateBookDTO result = new UpdateBookDTO(
                    book.getId(), book.getTitle(), book.getAuthor(), bookStock.getId(), bookStock.getStock()
            );
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"message\": \"Terjadi kesalahan pada server\"}");
        }
    }

    public ResponseEntity<?> getBookDetailForDelete(Long idBook) {
        try {
            Book book = bookRepository.findById(idBook)
                    .orElseThrow(() -> new RuntimeException("Buku dengan ID " + idBook + " tidak ditemukan!"));

            BookStock bookStock = bookStockRepository.findAll().stream()
                    .filter(s -> s.getIdBook() != null && s.getIdBook().getId().equals(idBook))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Data stok untuk buku ini tidak ditemukan!"));

            DeleteBookDTO result = new DeleteBookDTO(
                    book.getId(), book.getTitle(), book.getAuthor(), bookStock.getId(), bookStock.getStock()
            );
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"message\": \"Terjadi kesalahan pada server\"}");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteBookWithStock(Long idBook) {
        try {
            Book book = bookRepository.findById(idBook)
                    .orElseThrow(() -> new RuntimeException("Buku dengan ID " + idBook + " tidak ditemukan!"));

            bookStockRepository.findAll().stream()
                    .filter(s -> s.getIdBook() != null && s.getIdBook().getId().equals(idBook))
                    .findFirst()
                    .ifPresent(stock -> bookStockRepository.delete(stock));

            bookRepository.delete(book);
            return ResponseEntity.ok().body("{\"message\": \"Buku dan data stok berhasil dihapus secara permanen\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"message\": \"Terjadi kesalahan pada server\"}");
        }
    }
}