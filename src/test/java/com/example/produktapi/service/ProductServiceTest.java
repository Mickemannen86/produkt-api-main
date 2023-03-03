package com.example.produktapi.service;

import com.example.produktapi.exception.BadRequestException;
import com.example.produktapi.exception.EntityNotFoundException;
import com.example.produktapi.model.Product;
import com.example.produktapi.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// Vi lägger till ExtendWith(Mockito~.class) för att komma åt bibliotek med funktionalitet för Mock-metoderna, testar mot påhittad klass
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock // för att matcha mot databasen? mockar
    private ProductRepository repository;

    @InjectMocks // en underklass av originalet?
    private ProductService underTest;

    @Captor // för att fånga argument
    ArgumentCaptor<Product> productCaptor;

    @Test // getAllProducts
    void whenGetAllProducts_thenExactlyOneInteractionWithRepositoryMethodFindAll() { //notis, inte TDD                                  //  -check!

        //when - vad är det vi testar
        underTest.getAllProducts();

        //then
        verify(repository).findAll(); // kan vi nå metoden findAll() när 'repository' körs? svar ja
        //verify(repository, times(2)).findAll(); // 2 = fail - vi har ju bara 1 interaction, times(1) skulle funka
        //verify(repository).deleteAll(); // testar fel metod för att säkerställa att vi inte når delete metoden i denna metod.
        verifyNoMoreInteractions(repository);
        // System.out.println(underTest.getAllProducts()); micke testar
    }

    @Test // getAllCategories
    void whenGetAllCategories_thenExactlyOneInteractionWithRepositoryMethodFindAllCategories() {                                        //  -check!

        //when
        underTest.getAllCategories();

        //then
        verify(repository, times(1)).findAllCategories(); // 2 - fail, 1 success, hämtas 1 gång
        verifyNoMoreInteractions(repository);
    }

    @Test // getProductsByCategory
    void givenAnExistingCategoryList_whenGetProductsByCategory_thenReceivesANonEmptyList() { // kollar så listan inte ska va tom om product finns - - check

        // given
        String existingCategory = "men";
        Product product = new Product("hockeyklubba", 300.0, "men","mera","bild");
        given(repository.findByCategory(existingCategory)).willReturn(List.of(product));

        // when
        List<Product> productsByCategory = underTest.getProductsByCategory(existingCategory);

        // then
        assertEquals(1, productsByCategory.size()); // kollar om producter ligger i categorin
        assertEquals("hockeyklubba", productsByCategory.get(0).getTitle()); // kollar så det är samma titel
        assertEquals("men", productsByCategory.get(0).getCategory()); // Hämtar även ut categorinamn för att dubbel kolla

        /*
        verify(repository).findByCategory(existingCategory);
        verifyNoMoreInteractions(repository);
        System.out.println(productsByCategory);

        underTest.getProductsByCategory(existingCategory);
        verify(repository, times(1)).findByCategory(existingCategory); // 2 - fail, 1 success, hämtas 1 gång

         */
    }

    @Test // getProductById()
    void getProductById_givenExistingId_whenGetProductById_thenRecieveProduct() { // fel, behöver underTest

        // given
        Integer id = 1;

        Product product = new Product(
                "",
                520.0,
                "",
                "",
                ""
        );

        product.setId(id);

        // when
        underTest.addProduct(product);

        // then
        given(repository.findById(product.getId())).willReturn(Optional.of(product)); // Rätt id returnerar en product
        assertTrue(repository.findById(id).isPresent()); // fail annat än id 1

    }

    @Test // getProductById
    void givenNotExistingID_whenGetProductById_thenThrowEntityNotFoundException() { //                                  -check

        // product.setId går att göra, overkill

        //given
        Integer id = 1; // vi ger id vi skapar

        // Produkten behövs inte
        Product product = new Product(
                "Titel",
                200.0,
                "desc",
                "category",
                "url"
        );

        // product.setId(id);

        // when             // returnera tomt id om inte id  finns
        when(repository.findById(id)).thenReturn(Optional.empty()); // finns den ska den komma tbx, annars som empty

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()-> {
                    underTest.getProductById(id);
                });

        // then
        assertEquals("Produkt med id " + id + " hittades inte", exception.getMessage());
/*
        Assertions.assertAll(
                ()-> assertEquals(1, 2, "Produkt med id %d hittades inte"),
                ()-> assertThrows(EntityNotFoundException.class, ()-> underTest.getProductById(id))
        );

 */
    }

    @Test //
    void tryAddingAnId_thenReturnTrue() {

        Product product = new Product(
                "Titel",
                200.0,
                "desc",
                "category",
                "url");

        underTest.addProduct(product);

        product.setId(1);

        given(repository.findById(1)).willReturn(Optional.of(product)); // id returnerar en produkt
        Assertions.assertTrue(repository.findById(1).isPresent()); // fail annat än 1 = fail
    }

    @Test // addProduct()
    void givenNewProducts_whenAddingAProduct_thenSaveMethodShouldBeCalled() {

        // given
        Product product = new Product("Dator",4000.0,"","","");
        Product product2 = new Product("Fel-Dator",4000.0,"","","");

        // when
        underTest.addProduct(product);

        // then
        // verify(repository).save(product2); // fail men prudct skulle funka tex
        verify(repository).save(productCaptor.capture()); // fångar alla argument(producter som lagts in)
        assertEquals(product, productCaptor.getValue()); // success
        // assertEquals(product2, productCaptor.getValue()); // fail
        // assertNotEquals(product, productCaptor.getValue()); // fail

    }

    @Test
    void givenNewProduct_whenAddingProductWithDuplicateTitle_thenThrowError() {

        // given
        String title = "Vår Test-titel";
        Product product = new Product(title,300.0,"","","");
        given(repository.findByTitle(title)).willReturn(Optional.of(product));

        // when
        //underTest.addProduct(product); // flytta ner til then testet för att få ihop vår kod

        // then
        BadRequestException exception = assertThrows(BadRequestException.class,
                //when
                ()-> underTest.addProduct(product));
        verify(repository, times(1)).findByTitle(title); // success
        verify(repository, times(0)).save(any()); // times() kan bytas till never()
        assertEquals("En produkt med titeln: Vår Test-titel finns redan", exception.getMessage());

    }

    @Test // updateProduct
    void updateProduct_givenValidId_whenTryingToUpdateProduct_thenUpdateProductById() {

        //given
        Integer id = 1; // vi ger id vi skapar


        Product product = new Product(
                "Titel",
                2000.0,
                "desc",
                "category",
                "url"
        );
        product.setId(id);

        // when
        Product updatedProduct = new Product(
                "Konrad",
                5000.0,
                "",
                "best",
                "Picen"
        );

        // updatedProduct.setTitle("updated by Micke"); uppdaterar bara titel tex..

        //when
        when(repository.findById(id)).thenReturn(Optional.of(updatedProduct));
        when(repository.save(updatedProduct)).thenReturn(updatedProduct);

        Product resultatet = underTest.updateProduct(updatedProduct, id);

        // then
        verify(repository).save(productCaptor.capture());
        assertEquals("Konrad",resultatet.getTitle()); // vid fel byt till "updated by Micke"

    }

    @Test // updateProduct - Felhanteringen - felflöde
    void updateProduct_givenNotValidId_whenTryingToUpdateProduct_thenThrowEntityNotFoundException() {

        // given
        Integer id = 1; // vi ger id vi skapar

        //when(repository.findById(id)).thenReturn(Optional.empty());

        Product updateProduct = new Product("", 45.0, "", "", "");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()-> {
            // when
            underTest.updateProduct(updateProduct, id); // om id byts med 2 = throw exception.
        });

        // then
        assertEquals(id,id);
        assertEquals("Produkt med id " + id + " hittades inte", exception.getMessage());

    }

    @Test // deleteProduct - Normalflöde
    void testDeleteProduct_givenValidId_whenTryingToDeleteProduct_thenDeleteProductById() {

        // given
        Integer id = 1; // vi ger id vi skapar

        Product product = new Product(
                "Computer",
                300.0,
                "el",
                "hej",
                "urban"
        );

        product.setId(id);

        // when
        when(repository.findById(id)).thenReturn(Optional.of(product)); // expect a fetch, return a fetched product

        underTest.deleteProduct(id);

        // then
        verify(repository, times(1)).deleteById(id);

        assertNotNull(underTest.getProductById(id)); // NotNull pass
        assertEquals(id,id);

    }

    @Test // deleteProduct - Felhantering aka felflöde
    void deleteProduct_givenNotValidId_whenTryingToDelete_thenThrowEntityNotFoundException() {

        // Felhanteringen
        // given
        Integer id = 1; // vi ger id vi skapar

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()-> {
        // when
        underTest.deleteProduct(id); // om id byts med 2 = throw exception.
        });

        // then
        assertEquals(id,id);
        assertEquals("Produkt med id " + id + " hittades inte", exception.getMessage());

    }
}