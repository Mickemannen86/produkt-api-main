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
    void whenGetAllProducts_thenExactlyOneInteractionWithRepositoryMethodFindAll() { // notis, inte TDD                 - check!

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
    void whenGetAllCategories_thenExactlyOneInteractionWithRepositoryMethodFindAllCategories() { //                     - check!

        //when
        underTest.getAllCategories();

        //then
        verify(repository, times(1)).findAllCategories(); // 2 - fail, 1 success, hämtas 1 gång
        verifyNoMoreInteractions(repository);
    }

    @Test // getProductsByCategory                                                                                      - check
    void givenAnExistingCategoryList_whenGetProductsByCategory_thenReceivesANonEmptyList() { // kollar så listan inte ska va tom om product finns

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
        System.out.println(productsByCategory);
         */
    }

    @Test // getProductById() - normalflöde
    void getProductById_givenExistingId_whenGetProductById_thenRecieveProduct() { //                                    - check!

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

    @Test // getProductById - felflöde
    void givenNotExistingID_whenGetProductById_thenThrowEntityNotFoundException() { //                                  - check!

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

        // when            returnera tomt id om inte id  finns
        when(repository.findById(id)).thenReturn(Optional.empty()); // finns den ska den komma tbx, annars som empty

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()-> {
                    underTest.getProductById(id);
                });

        // then
        assertEquals("Produkt med id " + id + " hittades inte", exception.getMessage());

    }

    @Test // addProduct() - normalflöde
    void givenNewProduct_whenAddingAProductAndGivingAnValidId_thenReturnTrueIfProductIsFound() { //                     - check!

        // given
        Product product = new Product(
                "Titel",
                200.0,
                "desc",
                "category",
                "url");

        // when
        underTest.addProduct(product);
        product.setId(1);

        // then
        given(repository.findById(1)).willReturn(Optional.of(product)); // id returnerar en produkt
        Assertions.assertTrue(repository.findById(1).isPresent()); // fail annat än 1 eller om -  isEmpty() = fail
    }

    @Test // addProduct - felflöde
    void givenNewProduct_whenAddingProductWithDuplicateTitle_thenThrowError() { //                                      - check!

        // given
        String title = "Vår Test-titel";
        Product product = new Product(title,300.0,"","","");
        given(repository.findByTitle(title)).willReturn(Optional.of(product));

        // then
        BadRequestException exception = assertThrows(BadRequestException.class,
                //when
                ()-> underTest.addProduct(product));
        verify(repository, times(1)).findByTitle(title); // success
        verify(repository, times(0)).save(any()); // times() kan bytas till never()
        assertEquals("En produkt med titeln: Vår Test-titel finns redan", exception.getMessage()); // fail vid Test-Titel

    }

    @Test // updateProduct - normalflöde
    void updateProduct_givenValidId_whenTryingToUpdateProduct_thenUpdateProductById() { //                              - check!

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

        //updatedProduct.setTitle("updated by Micke"); //uppdaterar bara titel tex..

        when(repository.findById(id)).thenReturn(Optional.of(updatedProduct));
        when(repository.save(updatedProduct)).thenReturn(updatedProduct);

        Product resultatet = underTest.updateProduct(updatedProduct, id);

        // then
        verify(repository).save(productCaptor.capture());
        assertEquals("Konrad",resultatet.getTitle()); // vid fel byt till "updated by Micke"

    }

    @Test // updateProduct - Felhanteringen - felflöde
    void updateProduct_givenNotValidId_whenTryingToUpdateProduct_thenThrowEntityNotFoundException() { //                - check!

        // given
        Integer id = 1; // vi ger id vi skapar

        Product updateProduct = new Product("", 45.0, "", "", "");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()-> {
            // when
            underTest.updateProduct(updateProduct, id); // om id byts med 2 = throw exception.
        });

        // then
        assertEquals(id,id);
        assertEquals("Produkt med id " + id + " hittades inte", exception.getMessage());

    }

    @Test // deleteProduct - Normalflöde                                                                                - check!
    void testDeleteProduct_givenValidId_whenTryingToDeleteProduct_thenExactlyOneInteractionWithRepositoryMethodDeleteById() {

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
        verify(repository, times(1)).deleteById(id); // fail om deleteById anropas fler gånger än 1 gång.
        verifyNoMoreInteractions(repository);

    }

    @Test // deleteProduct - Felhantering aka felflöde
    void deleteProduct_givenNotValidId_whenTryingToDelete_thenThrowEntityNotFoundException() { //                       - check!

        // Felhanteringen
        // given
        Integer id = 1; // vi ger id vi skapar

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()-> {
        // when
        underTest.deleteProduct(id); // om id byts med 2 = throw exception.
        });

        // then
        assertEquals(id,id); // checkar id vad ja får å vad som expects
        assertEquals("Produkt med id " + id + " hittades inte", exception.getMessage());

    }
}