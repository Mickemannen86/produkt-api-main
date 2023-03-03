package com.example.produktapi.repository;


import com.example.produktapi.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Testar i databasen, mot databasen
class ProductRepositoryTest {

    @Autowired // En liten constructor av Product
    private ProductRepository underTest;

    @Test
    void testingOurRepository() {
        List <Product> products = underTest.findAll();
        Assertions.assertFalse(products.isEmpty()); // True = fel
    }

    @Test // findByCategory
    void findByCategory_givenValidCategory_whenSearchingInFindByCategory_thenReturnProductsInSpecificCategory() {

        // given
        String category = "electric";
        Product product = new Product("Dator", 2000.0, category, "", "");
        underTest.save(product);

        // when
        List <Product> productList = underTest.findByCategory("electric");

        // then
        assertFalse(productList.isEmpty()); // ska inte va tom
        assertEquals(category, productList.get(0).getCategory()); // electronic , 0 index - fail index annat än 0

    }

    @Test // findByCategory, test 2
    void findByNonExistingCategory_givenDeleteAll_whenFindByCategory_thenCheckCategoryIsEmpty() {

        // given
        underTest.deleteAll();

        // when
        List <Product> listProduct = underTest.findByCategory("electronic");

        // then
        assertTrue(listProduct.isEmpty());

    }

    @Test
    void findByTitle_whenSearchingForANonExistingTitle_thenReturnThatProduct() {

        // given
        String title = "radio";
        Product product = new Product(title, 2000.0, "electronic", "", "");
        underTest.save(product);

        // when
        Optional <Product> optionalProduct = underTest.findByTitle("radio");  // fail Radion

        // then
        Assertions.assertAll(
                () -> assertTrue(optionalProduct.isPresent()),
                () -> assertFalse(optionalProduct.isEmpty()),
                () -> assertEquals(product.getTitle(), optionalProduct.get().getTitle())
        );
    }

    @Test
    void findByTitle_whenSearchingForANonExistingTitle_thenReturnEmptyProduct() {

        // given
        String title = "en annan dator";

        // when
        Optional <Product> optionalProduct = underTest.findByTitle(title);

        // then
        Assertions.assertAll(
                () -> assertFalse(optionalProduct.isPresent()),
                () -> assertTrue(optionalProduct.isEmpty()),
                () -> assertThrows(NoSuchElementException.class, ()-> optionalProduct.get(), "skicka eget felmeddelande om de blir fel")
        );
    }

    @Test
    void findAllCategories() {

        // when
        List <String> listProduduct = underTest.findAllCategories();

        // then
        assertFalse(listProduduct.isEmpty());
        assertEquals(listProduduct.size(), 4);

    }

    @Test
    void findNoDuplicatedCategory() {

        // given
        List <String> acualCategories = new ArrayList<>(Arrays.asList("electronics", "jewelery", "men's clothing", "women's clothing")); // fail electronic
        acualCategories.stream().distinct().collect(Collectors.toList());

        // when
        List <String> listProduct = underTest.findAllCategories();

        // then
        assertFalse(listProduct.size() > 4); // Kollar antalet kategorier - fail annat än 4
        assertEquals(acualCategories, listProduct); // Kollar om categorys är duplicated

    }
}