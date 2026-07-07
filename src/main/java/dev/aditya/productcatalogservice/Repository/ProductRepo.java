package dev.aditya.productcatalogservice.Repository;

import dev.aditya.productcatalogservice.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    @Override
    Optional<Product> findById(Long prodId);

    //This works as well
    //Product findProductById(long id);

    //Writing custom Query for getAllProducts() as it should only return objects that are still active(not deleted).
    //@Query(value = "SELECT p from Product as p where p.status = dev.aditya.productcatalogservice.Model.ModelStatus.ACTIVE")
    @Override
    List<Product> findAll();

    //This is used for mapping to Post/Put/Delete API.
    // As we update/create the product in the service all we need to do is save the object into the table.
    //Its smart enough to understand whether we want to create a new product or update an existing one.
    @Override
    Product save(Product product);


    Optional<Product> findProductByNameAndCategoryName(String name, String categoryName);

    Page<Product> findByNameContaining(String name, Pageable pageable);
}
