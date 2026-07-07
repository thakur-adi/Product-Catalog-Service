package dev.aditya.productcatalogservice.Service;

import dev.aditya.productcatalogservice.Exception.ProductNotFoundException;
import dev.aditya.productcatalogservice.Model.Product;
import dev.aditya.productcatalogservice.Repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SearchService implements ISearchService{

    @Autowired
    ProductRepo productRepo;

    @Override
    public Page<Product> searchProduct(String query, int pageNumber, int pageSize) {
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> productsPage = productRepo.findByNameContaining(query,pageable);
        if(productsPage.isEmpty()){
            throw new ProductNotFoundException("No product found!!");
        }
        return productsPage;
    }
}
