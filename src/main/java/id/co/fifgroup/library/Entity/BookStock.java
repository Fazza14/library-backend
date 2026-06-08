package id.co.fifgroup.library.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "TB_TRN_BOOK_STOCK")
public class BookStock {
 
    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_BOOK", referencedColumnName = "ID")
    private Book idBook;

    @Column(name = "STOCK")
    private Integer stock;

 
}
