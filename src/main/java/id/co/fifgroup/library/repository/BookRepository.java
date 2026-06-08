package id.co.fifgroup.library.repository;

import org.springframework.stereotype.Repository;
import id.co.fifgroup.library.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
