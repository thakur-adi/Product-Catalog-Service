package dev.aditya.productcatalogservice.Controller;

import dev.aditya.productcatalogservice.DTO.ProductResponseDTO;
import dev.aditya.productcatalogservice.DTO.SearchRequestDTO;
import dev.aditya.productcatalogservice.Model.Product;
import dev.aditya.productcatalogservice.Service.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private ISearchService searchService;

    @GetMapping
    public Page<ProductResponseDTO> searchProductByNameQuery(@RequestParam String query,@RequestParam int pageNumber, @RequestParam int pageSize){
        Page<Product> pageProducts = searchService.searchProduct(query,pageNumber,pageSize);
        return new PageImpl<>(convertPageProductToResponseDTO(pageProducts),pageProducts.getPageable(),pageProducts.getTotalElements());
    }

    @PostMapping
    public Page<ProductResponseDTO> searchProductByCategoryQuery(@RequestBody SearchRequestDTO searchRequestDTO){
        Page<Product> pageProducts = searchService.searchProduct(searchRequestDTO.getQuery(),searchRequestDTO.getPageNumber(),searchRequestDTO.getPageSize());

        return new PageImpl<>(convertPageProductToResponseDTO(pageProducts),pageProducts.getPageable(),pageProducts.getTotalElements());
    }


    //Helper Methods

    private List<ProductResponseDTO> convertPageProductToResponseDTO(Page<Product> pageProducts){
        List<Product> products = pageProducts.getContent();
        List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();
        for (Product p: products){
            productResponseDTOS.add(p.convertToResponseDTO());
        }
        return  productResponseDTOS;
    }
}
