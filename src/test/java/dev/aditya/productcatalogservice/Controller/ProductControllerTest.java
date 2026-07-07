package dev.aditya.productcatalogservice.Controller;

import dev.aditya.productcatalogservice.DTO.ProductRequestDTO;
import dev.aditya.productcatalogservice.DTO.ProductResponseDTO;
import dev.aditya.productcatalogservice.Model.Category;
import dev.aditya.productcatalogservice.Model.Product;
import dev.aditya.productcatalogservice.Service.IProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockitoBean
    @Qualifier("StorageProductService")
    IProductService IProductService;

    @Autowired
    private MockMvc mockMvc;

    //Since we are testing APIs and will be using mockMvc to perform an action and expect an o/p in json we need this to map json o/p to string for comparison.
    @Autowired
    private ObjectMapper objectMapper;


    //Create a dummy Product for testing -> This is a stub
    private Product createNewDummyProduct(String name) {
        Product dummyProduct = new Product();
        //Id not required as we are not setting it up in DTO anyway.
        //dummyProduct.setId(1l);
        dummyProduct.setName(name);
        dummyProduct.setDescription("Description");
        dummyProduct.setImageUrl("imageURL");
        dummyProduct.setPrice(100.0);
        Category category = new Category();
        //category.setId(1);
        category.setName("category");
        dummyProduct.setCategory(category);

        return dummyProduct;
    }


    @Test
    void testGetProductByIdRunsSuccessfully() throws Exception {
       //Arrange
        Product dummyProduct = createNewDummyProduct("Dummy Product for getById() method");

        when(IProductService.getProductById(1)).thenReturn(dummyProduct);

        ProductResponseDTO expectedResponseDTO = dummyProduct.convertToResponseDTO();

        //Act & Assert
        mockMvc.perform(get("/products/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseDTO)));
    }

    @Test
    void testGetAllProductRunsSuccessfully() throws Exception {
        //Arrange
        Product dummyProduct1 = createNewDummyProduct("Dummy Prod 1 for GetAll() method");
        Product dummyProduct2 = createNewDummyProduct("Dummy Prod 2 for GetAll() method");

//       Again not required as we are not setting it up in DTO
//        dummyProduct2.setId(2);
//        dummyProduct2.getCategory().setId(2);

        List<Product> dummyProductList = List.of(dummyProduct1,dummyProduct2);

        when(IProductService.getAllProducts()).thenReturn(dummyProductList);

        //Have to create a list of DTOs cause our method returns "response DTO" and not the "Model class". So we compare with Response DTO and not the Dummy Product List
        ProductResponseDTO productResponseDTO1 = dummyProduct1.convertToResponseDTO() ;
        ProductResponseDTO productResponseDTO2 = dummyProduct2.convertToResponseDTO();

        List<ProductResponseDTO> dummyProductDTOList = List.of(productResponseDTO1,productResponseDTO2);

        //Act and Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //We have to expect the o/p for comparison as in returned by the original method i.e. if it returns a DTO compare it with DTO not the mocked result.
                .andExpect(content().json(objectMapper.writeValueAsString(dummyProductDTOList))); //This will compare the response Json and give the o/p for this test
        /*
                This we can use to match the attributer 1 by 1. More robust way.
               .andExpect(jsonPath("$[0].name").value("DummyProd"));

        */
    }

    @Test
    void testDeleteProductByIdRunsSuccessfully() throws Exception {
        Product dummyProduct = createNewDummyProduct("Dummy for Delete() Method");

        when(IProductService.deleteProductById(1)).thenReturn(dummyProduct);

        String expectedResponse = "Product: " + dummyProduct.getName() +"of Category: "+ dummyProduct.getCategory().getName() +" has been deleted Successfully";

        mockMvc.perform(delete("/products/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    void testCreateProductRunsSuccessfully() throws Exception {
        //Arrange
        Product dummyProduct = createNewDummyProduct("Dummy for Create() Method");

        ProductRequestDTO dummyRequestDTO = new ProductRequestDTO();
        dummyRequestDTO.setProductName("Dummy Request DTO");
        dummyRequestDTO.setDescription("desc");
        dummyRequestDTO.setPrice(100);
        dummyRequestDTO.setImageUrl("imageUrl");
        dummyRequestDTO.setCategoryName("category");

        ProductResponseDTO expectedResponseDTO = dummyProduct.convertToResponseDTO();


        when(IProductService.createNewProduct(dummyRequestDTO.getProductName(),dummyRequestDTO.getDescription(),
                dummyRequestDTO.getImageUrl(),dummyRequestDTO.getPrice(),dummyRequestDTO.getCategoryName()))
        .thenReturn(dummyProduct);

        //Act and Assert
        mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
                                            //We'll have to pass payload as a string.It onl works with String it seems
                                                    .content(objectMapper.writeValueAsString(dummyRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseDTO)));
    }

    @Test
    void testUpdateProductByIdRunsSuccessfully() throws Exception {
        //Arrange
        Product dummyProduct = createNewDummyProduct("Dummy for Create() Method");

        ProductRequestDTO dummyRequestDTO = new ProductRequestDTO();
        dummyRequestDTO.setProductName("Dummy Request DTO");
        dummyRequestDTO.setDescription("desc");
        dummyRequestDTO.setPrice(100);
        dummyRequestDTO.setImageUrl("imageUrl");
        dummyRequestDTO.setCategoryName("category");

        ProductResponseDTO expectedResponseDTO = dummyProduct.convertToResponseDTO();


        when(IProductService.updateProductById(1, dummyRequestDTO.getProductName(),dummyRequestDTO.getDescription(),
                dummyRequestDTO.getImageUrl(),dummyRequestDTO.getPrice(),dummyRequestDTO.getCategoryName()))
                .thenReturn(dummyProduct);

        //Act and Assert
        mockMvc.perform(put("/products/{id}",1).contentType(MediaType.APPLICATION_JSON)
                        //We'll have to pass payload as a string.It onl works with String it seems
                        .content(objectMapper.writeValueAsString(dummyRequestDTO)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseDTO)));
    }

}
