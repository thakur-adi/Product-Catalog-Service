package dev.aditya.productcatalogservice.Service;

import dev.aditya.productcatalogservice.Model.Product;
import org.springframework.data.domain.Page;

public interface ISearchService {
    Page<Product> searchProduct(String query, int pageNumber, int pageSize);
}
