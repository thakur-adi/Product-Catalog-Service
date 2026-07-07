package dev.aditya.productcatalogservice.Service;

import dev.aditya.productcatalogservice.Exception.ProductAlreadyExistsException;
import dev.aditya.productcatalogservice.Exception.ProductNotFoundException;
import dev.aditya.productcatalogservice.Model.Category;
import dev.aditya.productcatalogservice.Model.ModelStatus;
import dev.aditya.productcatalogservice.Model.Product;
import dev.aditya.productcatalogservice.Repository.ProductRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class StorageIProductServiceTest {
    //ideally we never mock Validations since it'll be a static method. So not required, let it run normally
    @MockitoBean
    private ProductRepo productRepo;

    @Qualifier("StorageProductService")
    @Autowired
    private IProductService IProductService;
    

    //Create a dummy Product for testing -> This is a stub
    private Product createNewDummyProduct() {
        Product dummyProduct = new Product();
        //Since we are not using ProductDTO in our business logic we would need Id here
        dummyProduct.setId(1);
        dummyProduct.setName("Dummy Product");
        dummyProduct.setDescription("Description");
        dummyProduct.setImageUrl("imageURL");
        dummyProduct.setPrice(100.0);
        Category category = new Category();
        category.setId(1);
        category.setName("categoryName");
        dummyProduct.setCategory(category);

        return dummyProduct;
    }

    Product dummyProduct =createNewDummyProduct();
    
    //Happy path
    @Test
    void testGetProductByIdReturnsProduct() throws ProductNotFoundException {
        //Arrange
        //Product dummyProduct = createNewDummyProduct();
        Optional<Product> dummyProductOptional = Optional.of(dummyProduct);
        //This is how we mock our external Dependency. This will return what the original method is supposed to return.
        //i.e. If original method returns an object it has to return an object nothing else.
        //All we can do is create a dummy Object with data type as returned by the original method.
        when(productRepo.findById(1L)).thenReturn(dummyProductOptional);

        //Act
        Product expectedProduct =  IProductService.getProductById(1);

        //Assert
        assertEquals(1,expectedProduct.getId());
        assertEquals("Dummy Product",expectedProduct.getName());
        assertEquals(dummyProduct,expectedProduct);
    }

    //Unhappy path
    @Test
    void testProductByIdThrowsException() {
        //Arrange
        Optional<Product> dummyOptionalProduct = Optional.empty(); //creates an optional<>(null)

        when(productRepo.findById(1l)).thenReturn(dummyOptionalProduct); //we can't send the optional wrapper object itself as null cause that'll never happen


        //Act & Assert
        assertThrows(ProductNotFoundException.class,() -> IProductService.getProductById(1));
    }

    //Happy path
    @Test
    void testGetAllProductsReturnsListOfProducts() throws ProductNotFoundException {
        //Arrange
        Product dummyProduct1 = createNewDummyProduct();
        Product dummyProduct2 = createNewDummyProduct();
        dummyProduct2.setId(2);

        List<Product> productList = List.of(dummyProduct1,dummyProduct2);

        when(productRepo.findAll()).thenReturn(productList);

        //Act
        List<Product> expectedProducts = IProductService.getAllProducts();

        //Assert
        assertEquals(1,expectedProducts.get(0).getId());
        assertEquals(2,expectedProducts.get(1).getId());
    }

    //Unhappy path
    @Test
    void testGetAllProductsThrowsException() {
        //Arrange
        List<Product> dummyEmptyProductList = new ArrayList<>();
        when(productRepo.findAll()).thenReturn(dummyEmptyProductList);

        //Act and Assert
        assertThrows(ProductNotFoundException.class,() -> IProductService.getAllProducts());
    }

    //Happy path
    @Test
    void testCreateNewProductReturnsProduct(){
        //Arrange
        //Product dummyProduct = createNewDummyProduct();

        /*
        Here we can't use .save(dummyProduct). --->  this won't work, always fails
        When we are sending the dummy product details in the ACT section it essentially creates a complete new Product object in the service class.
        So when we mock "productRepo.save()" (using a dummyProduct Object), test will always fail as the method called inside service class(productRepo.save(new Product object)) is with completely different object.
        Because we use Product prod = new Product(), this will be a completely different object from dummyProduct(complete new Address, completely new Object) because we are using new keyword.
        So to overcome this we use any() this says whatever object is passed through that function mock it and return the dummy object.
        */
        when(productRepo.save(any())).thenReturn(dummyProduct);


        //Act
        Product expectedProduct = IProductService.createNewProduct(dummyProduct.getName(), dummyProduct.getDescription(),
                                                                  dummyProduct.getImageUrl(), dummyProduct.getPrice(),
                                                                  dummyProduct.getCategory().getName());

        //Assert
        assertEquals(1,expectedProduct.getId());
        assertEquals("Dummy Product",expectedProduct.getName());
        assertEquals(dummyProduct,expectedProduct);
    }
    
    //Unhappy path
    @Test
    void testCreateNewProductThrowsException(){
        //Arrange
        when(productRepo.findProductByNameAndCategoryName("name","categoryName")).thenReturn(Optional.of(dummyProduct));
        
        //Act and Assert
        assertThrows(ProductAlreadyExistsException.class, 
                    ()-> IProductService.createNewProduct("name","desc","image",10.0,"categoryName"));
        
    }

    //Happy path
    @Test
    void testUpdateProductReturnsProduct() throws ProductNotFoundException {
        //Arrange
        Optional<Product> dummyProductOptional = Optional.of(dummyProduct);
        //This(when()) only works with mocked Objects not real objects like IProductService.getById(1) will fail.
        // It would use the original object since "IProductService" is not mocked, and would give the actual result instead of using when().thenReturn()(this method won't get used at all).
        when(productRepo.findById(1L)).thenReturn(dummyProductOptional);

        when(productRepo.save(any())).thenReturn(dummyProduct);

        //Act
        Product expectedProduct = IProductService.updateProductById(1,dummyProduct.getName(), dummyProduct.getDescription(),
                                                                    dummyProduct.getImageUrl(), dummyProduct.getPrice(),
                                                                    dummyProduct.getCategory().getName());

        //Assert
        assertEquals(1,expectedProduct.getId());
        assertEquals("Dummy Product",expectedProduct.getName());
        assertEquals(dummyProduct,expectedProduct);
    }

    //Unhappy path
    @Test
    void testUpdateProductThrowsException(){
        //Arrange
        when(productRepo.findProductByNameAndCategoryName("name","categoryName")).thenReturn(Optional.of(dummyProduct));

        //Act and Assert
        assertThrows(ProductNotFoundException.class,
                ()-> IProductService.updateProductById(1,"name","desc","image",10.0,"categoryName"));

    }


    //Happy path
    @Test
    void testDeleteProductByIdReturnsString() throws ProductNotFoundException {
        //Arrange
        //Product dummyProduct = createNewDummyProduct();

        Optional<Product> dummyProductOptional = Optional.of(dummyProduct);

        when(productRepo.findById(1L)).thenReturn(dummyProductOptional);

        //like we discussed earlier even tho we wanted boolean, we can't return boolean as the mocked method returns a Product object not a boolean.
        when(productRepo.save(any())).thenReturn(dummyProduct);
        
        //Act
        Product expectedProductResult = IProductService.deleteProductById(1);

        //Assert
        assertEquals(1, expectedProductResult.getId());
        assertEquals(dummyProduct.getName(),expectedProductResult.getName());
        assertEquals(dummyProduct,expectedProductResult);

        //This is to check whether findById() method was called wanted  number times or not.
        verify(productRepo,times(1)).findById(1l);

    }

    //Unhappy path
    @Test
    void testDeleteProductByIdThrowsException(){
        //Arrange
        dummyProduct.setStatus(ModelStatus.DELETED);
        when(productRepo.findById(1l)).thenReturn(Optional.of(dummyProduct));
        //Act & Assert
        assertThrows(ProductNotFoundException.class, ()-> IProductService.deleteProductById(1));
    }

}