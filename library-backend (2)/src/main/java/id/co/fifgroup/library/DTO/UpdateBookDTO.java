package id.co.fifgroup.library.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookDTO {
    private Long idBook;
    private String title;
    private String author;

    private Long idStock;
    private Integer stock;
}