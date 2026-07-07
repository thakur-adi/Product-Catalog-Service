package dev.aditya.productcatalogservice.Service;

import dev.aditya.productcatalogservice.Exception.ProductNotFoundException;
import dev.aditya.productcatalogservice.Model.Product;

import java.util.List;

public interface IProductService {

    Product getProductById(long prodId) throws ProductNotFoundException;

    public List<Product> getAllProducts()throws ProductNotFoundException;

    //We could've sent a product model object but, it's not a good practice as we'll have to declare id as either null or 0 (this isn't recommended).
    // "id" will be created inside this function, better to send in just properties and not set it to null or 0 by default first then overwrite.
    Product createNewProduct(String productName, String desc, String imageUrl, double price, String categoryName);

    Product deleteProductById(long prodId)throws ProductNotFoundException ;

    Product updateProductById(long prodId, String productName, String description, String imageUrl, double price, String categoryName) throws ProductNotFoundException;
}
