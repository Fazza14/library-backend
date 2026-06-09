package id.co.fifgroup.library.repository;

import org.springframework.stereotype.Repository;

import id.co.fifgroup.library.Entity.BookStock;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface BookStockRepository extends JpaRepository<BookStock, Long> {
    
}
