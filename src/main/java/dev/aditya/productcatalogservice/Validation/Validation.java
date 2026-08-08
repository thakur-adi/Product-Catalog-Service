package dev.aditya.productcatalogservice.Validation;

import dev.aditya.productcatalogservice.Exception.ProductNotFoundException;
import dev.aditya.productcatalogservice.Model.ModelStatus;
import dev.aditya.productcatalogservice.Model.Product;


import java.util.Optional;

public class Validation {

    public static Product getValidProduct(Optional<Product> optionalProduct) throws ProductNotFoundException {
        if(optionalProduct.isEmpty()){
            throw new ProductNotFoundException();
        }
        else if (optionalProduct.get().getStatus().equals(ModelStatus.DELETED)) {
            throw new ProductNotFoundException();
        }
        return optionalProduct.get();
    }

    public static Boolean checkAuthorization(String role){
            if(role.equalsIgnoreCase("ADMIN")||role.equalsIgnoreCase("SUPER_ADMIN")||role.equalsIgnoreCase("SELLER")||role.equalsIgnoreCase("OWNER")){
                return true;
            }
        return false;
    }
}
