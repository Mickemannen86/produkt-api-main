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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// Vi lägger till ExtendWith(Mockito~.class) för att komma åt bibliotek med funktionalitet för Mock-metoderna, testar mot påhittad klass
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock // En kopia av productRepository, inte den riktiga
    private ProductRepository repository;

    @InjectMocks // Här ska fake productRepository användas
    private ProductService underTest;

    @Captor // för att fånga product argument
    ArgumentCaptor<Product> productCaptor;

    @Captor // för att fånga string argument
    ArgumentCaptor<String> stringCaptor;


    @Test // getAllProducts
    void whenGetAllProducts_thenExactlyOneInteractionWithRepositoryMethodFindAll() { //                                 - check!

        //when - vad är det vi testar
        underTest.getAllProducts();

        //then
        verify(repository).findAll(); // kan vi nå metoden findAll() när 'repository' körs? svar ja
        //verify(repository, times(2)).findAll(); // 2 = fail - vi har ju bara 1 interaction, times(1) skulle funka
        //verify(repository).deleteAll(); // testar fel metod för att säkerställa att vi inte når delete metoden i denna metod.
        verifyNoMoreInteractions(repository);

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
        Product product = new Product("hockeyklubba", 300.0, existingCategory,"mera","bild");

        // when
        underTest.getProductsByCategory(existingCategory);

        // then
        verify(repository, times(1)).findByCategory(stringCaptor.capture()); // annat än 1
        verifyNoMoreInteractions(repository);
        assertEquals(existingCategory, stringCaptor.getValue()); // fail - "woman". Kollar så det är samma category

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

        given(repository.findById(product.getId())).willReturn(Optional.of(product)); // Rätt id returnerar en product

        // when
        Product fakeProduct = underTest.getProductById(product.getId());

        // then
        assertEquals(product.getId(),fakeProduct.getId()); // fail vid annat id
        assertEquals(product,fakeProduct);

    }

    @Test // getProductById - felflöde
    void givenNotExistingID_whenGetProductById_thenThrowEntityNotFoundException() { //                                  - check!

        //given            returnera tomt id om inte id  finns
        Integer id = 1;
        given(repository.findById(id)).willReturn(Optional.empty()); // finns den ska den komma tbx, annars som empty

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()-> {
                    underTest.getProductById(id);
                });

        // then
        assertEquals(String.format("Produkt med id %d hittades inte", id), exception.getMessage()); // failar om id = 2 tex
    }

    @Test // addProduct() - normalflöde
    void givenNewProduct_whenAddingAProductAndGivingAnValidId_thenReturnTrueIfProductIsFound() { //                     - check!

        Product product2 = new Product();
        // given
        Product product = new Product(
                "Titel",
                200.0,
                "desc",
                "category",
                "url");

        // when
        underTest.addProduct(product); // 1. addar product
        product.setId(1);

        verify(repository).save(productCaptor.capture()); // fångar upp argument av vår sparade product.

        // then
        assertEquals(product, productCaptor.getValue()); // fail - product2 - Vi testar att product & fångade argument stämmer överens med product vi testar på.

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
                ()-> underTest.addProduct(product)); // when flyttas ner för att kunna hanteras av assertThrows() så vi kan kasta exception
        verify(repository, times(1)).findByTitle(title); // success
        verify(repository, times(0)).save(product); // times() kan bytas till never()
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

        when(repository.findById(id)).thenReturn(Optional.of(product));


        underTest.updateProduct(updatedProduct, id);

        // then
        verify(repository).save(productCaptor.capture());
        assertEquals("Titel",productCaptor.getValue().getTitle()); // vid fel blir det grönt, fel i original metoden

    }

    @Test // updateProduct - Felhanteringen - felflöde
    void updateProduct_givenNotValidId_whenTryingToUpdateProduct_thenThrowEntityNotFoundException() { //                - check!

        // given
        given(repository.findById(1)).willReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()-> {
            // when
            underTest.updateProduct(any(), 1);
        });

        // then
        assertEquals(1,1);
        assertEquals(String.format("Produkt med id %d hittades inte",1), exception.getMessage()); // om id byts med 2 = throw exception.

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

        given(repository.findById(id)).willReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()-> {
        // when
        underTest.deleteProduct(id);
        });

        // then
        verify(repository, never()).deleteById(any());
        assertEquals(String.format("Produkt med id %d hittades inte",1), exception.getMessage()); // om id byts med 2 = throw exception.

    }
}