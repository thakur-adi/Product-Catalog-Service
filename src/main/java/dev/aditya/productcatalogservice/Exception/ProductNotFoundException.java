package dev.aditya.productcatalogservice.Exception;


public class ProductNotFoundException extends RuntimeException{

    private static final String message ="Product doesn't exist";
    //for default message
    public ProductNotFoundException() {
        super(message);
    }
    //for some personalized message
    public ProductNotFoundException(String message) {
        super(message);
    }

}
