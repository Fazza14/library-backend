package id.co.fifgroup.library.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import id.co.fifgroup.library.Entity.Book;
import id.co.fifgroup.library.Entity.BookStock;
import id.co.fifgroup.library.DTO.BookStockDTO;
import id.co.fifgroup.library.DTO.CreateBookDTO;
import id.co.fifgroup.library.DTO.UpdateBookDTO;
import id.co.fifgroup.library.DTO.DeleteBookDTO;
import id.co.fifgroup.library.repository.BookRepository;
import id.co.fifgroup.library.repository.BookStockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookStockRepository bookStockRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Long countBooks() {
        return bookRepository.count();
    }

    public List<BookStockDTO> getAllBooksWithStock() {
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
            return new BookStockDTO(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    currentStock
            );
        }).collect(Collectors.toList());

        return bookStockList;
    }

    @Transactional
    public BookStockDTO createBookWithStock(CreateBookDTO request) {
        if (bookRepository.existsById(request.getIdBook())) {
            throw new RuntimeException("ID Buku sudah terdaftar!");
        }
        if (bookStockRepository.existsById(request.getIdStock())) {
            throw new RuntimeException("ID Stok sudah terdaftar!");
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

        return new BookStockDTO(
                savedBook.getId(),
                savedBook.getTitle(),
                savedBook.getAuthor(),
                bookStock.getStock()
        );
    }

    @Transactional
    public UpdateBookDTO updateBookWithStock(UpdateBookDTO request) {
        Book book = bookRepository.findById(request.getIdBook())
                .orElseThrow(() -> new RuntimeException("Buku dengan ID " + request.getIdBook() + " tidak ditemukan!"));

        BookStock bookStock = bookStockRepository.findById(request.getIdStock())
                .orElseThrow(() -> new RuntimeException("Stok dengan ID " + request.getIdStock() + " tidak ditemukan!"));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        bookRepository.save(book);

        bookStock.setStock(request.getStock());
        bookStockRepository.save(bookStock);

        return new UpdateBookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                bookStock.getId(),
                bookStock.getStock()
        );
    }

    public DeleteBookDTO getBookDetailForDelete(Long idBook) {
        Book book = bookRepository.findById(idBook)
                .orElseThrow(() -> new RuntimeException("Buku dengan ID " + idBook + " tidak ditemukan!"));

        BookStock bookStock = bookStockRepository.findAll().stream()
                .filter(s -> s.getIdBook() != null && s.getIdBook().getId().equals(idBook))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Data stok untuk buku ini tidak ditemukan!"));

        return new DeleteBookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                bookStock.getId(),
                bookStock.getStock()
        );
    }

    @Transactional
    public void deleteBookWithStock(Long idBook) {
        Book book = bookRepository.findById(idBook)
                .orElseThrow(() -> new RuntimeException("Buku dengan ID " + idBook + " tidak ditemukan!"));

         bookStockRepository.findAll().stream()
                .filter(s -> s.getIdBook() != null && s.getIdBook().getId().equals(idBook))
                .findFirst()
                .ifPresent(stock -> bookStockRepository.delete(stock));

        bookRepository.delete(book);
    }
}