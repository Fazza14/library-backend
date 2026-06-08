package id.co.fifgroup.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.co.fifgroup.library.Entity.BookStock;
import id.co.fifgroup.library.repository.BookStockRepository;
import java.util.Optional;

@Service
public class BookStockService {

    @Autowired
    private BookStockRepository bookStockRepository;

    @Transactional
    public boolean reduceBookStock(Long idBook) {
        Optional<BookStock> stockOpt = bookStockRepository.findAll().stream()
                .filter(s -> s.getIdBook() != null && s.getIdBook().getId().equals(idBook))
                .findFirst();

        if (stockOpt.isPresent()) {
            BookStock bookStock = stockOpt.get();
            if (bookStock.getStock() > 0) {
                bookStock.setStock(bookStock.getStock() - 1);
                bookStockRepository.save(bookStock);
                return true;
            }
        }
        return false;
    }
}