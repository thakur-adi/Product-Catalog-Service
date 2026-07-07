package dev.aditya.productcatalogservice.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDTO {

    private String query;
    private int pageNumber;
    private int pageSize;
}
