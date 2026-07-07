package dev.aditya.productcatalogservice.Service;

import dev.aditya.productcatalogservice.Exception.ProductAlreadyExistsException;
import dev.aditya.productcatalogservice.Exception.ProductNotFoundException;
import dev.aditya.productcatalogservice.Model.Category;
import dev.aditya.productcatalogservice.Model.ModelStatus;
import dev.aditya.productcatalogservice.Model.Product;
import dev.aditya.productcatalogservice.Repository.CategoryRepo;
import dev.aditya.productcatalogservice.Repository.ProductRepo;
import dev.aditya.productcatalogservice.Validation.Validation;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;



@Service("StorageProductService")
public class StorageIProductService implements IProductService {

    private ProductRepo productRepo;
    private CategoryRepo categoryRepo;

    StorageIProductService(ProductRepo productRepo, CategoryRepo categoryRepo){
        this.productRepo=productRepo;
        this.categoryRepo=categoryRepo;
    }


    @Override
    public Product getProductById(long prodId) {
        return Validation.getValidProduct(productRepo.findById(prodId));
    }


    @Override
    public List<Product> getAllProducts() {
        List<Product> products = productRepo.findAll(); //Custom Query for finding active products not working for some reason. Will look into it later.
        if(products.size()==0)
        {
            throw new ProductNotFoundException("No Products exist yet.");
        }
        //for security purpose we send a copy of the list instead of the original list.
        return  List.copyOf(products.stream()
                                    .filter(product -> product.getStatus() == ModelStatus.ACTIVE) //using '==' over '.equals()' as the latter can lead to NPE if status doesn't exist.
                                    .toList());
    }


    @Override
    public Product createNewProduct(String productName, String desc, String imageUrl, double price, String categoryName)
    {
        if(productRepo.findProductByNameAndCategoryName(productName,categoryName).isPresent())
        {
            throw new ProductAlreadyExistsException("Product: " + productName +", Category: "+ categoryName +" already exists");
        }

        Product product = buildNewProductFromParams(productName, desc, imageUrl, price, categoryName);

        return productRepo.save(product);
    }

    @Override
    public Product updateProductById(long prodId, String productName, String description, String imageUrl, double price, String categoryName) throws ProductNotFoundException {
        Product currentProduct =  getProductById(prodId);
        Product newProduct = buildNewProductFromParams(productName, description, imageUrl, price, categoryName);
        newProduct.setId(currentProduct.getId());
        return productRepo.save(newProduct);
    }

    @Override
    public Product deleteProductById(long prodId) {
            Product product = getProductById(prodId);
            product.setStatus(ModelStatus.DELETED);
            productRepo.save(product);

            return product;
    }



    /*Helper Methods*/

    // for creating a Product Model Object
    private Product buildNewProductFromParams(String productName, String desc, String imageURL, double price, String categoryName)
    {
        //try to move it to a builder class next -> can't as it contains some parent fields as well, which can't be set from child class.
        Product product = new Product();
        product.setName(productName);
        product.setDescription(desc);
        product.setImageUrl(imageURL);
        product.setPrice(price);
        //It's a nested Property i.e. product has a separate 'category' class attribute, that means a product cannot be added if the category doesn't exist first.
        //It'll always fail as it demands the category to be present. That's why we first update category table then add the product.
        product.setCategory(getCategoryFromDB(categoryName));

        return product;
    }

    // for creating a Category Model Object in case it doesn't exist
    private Category getCategoryFromDB(String categoryName)
    {
        //If we don't perform this product addition to the table i.e. post/put api will fail.
        Optional<Category> optionalCategory = categoryRepo.findbyName(categoryName);
        if(optionalCategory.isEmpty())
        {
            //try to move this to a builder
            Category category = new Category();
            category.setName(categoryName);
            categoryRepo.save(category);
           return category;
        }
        else {
            return optionalCategory.get();
        }
    }






/*
    //Moved to Validations class
    //This performs various checks on Product object before it gets sent to controller
    private Product getValidProduct(Optional<Product> optionalProduct) throws ProductNotFoundException{
        if(optionalProduct.isEmpty())
        {
            throw new ProductNotFoundException();
        }
        else if (optionalProduct.get().getStatus().equals(ModelStatus.DELETED)) {
            throw new ProductNotFoundException();
        }
        return optionalProduct.get();
    }
 */
}
