package com.example.produktapi;


import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SeleniumTest {
/* skriv
    @Test
    public void checkSVTplayTitle() {

        // Hämta in den webDriver som ska användas
        WebDriver driver = new ChromeDriver();

        // Navigera till den webbsida som ska testas -  navigate().to kan va get()
        driver.navigate().to("https://java22.netlify.app/");

        // fail "SVT Playy"
        assertEquals("Webbutik", driver.getTitle(), "Felmeddelande: Title don't match as expected! Note it's Case Sensitive");

        driver.quit();

    }

    @Test
    void checkSTItitle() { // G - check 1/3

        WebDriver driver = new ChromeDriver();

        driver.get("https://sti.se");

        // fail = "STI"
        assertEquals("STI - YH-program och vidareutbildningar inom teknik & IT", driver.getTitle(),"Felmeddelande: Title don't match as expected! Note it's Case Sensitive");

        driver.quit();
    }

    @Test
    void checkH1Text() {

        WebDriver driver = new ChromeDriver();

        driver.get("https://java22.netlify.app/");




        /*
        // Ett sätt
        WebElement h1Element =  driver.findElement(By.xpath("//*[@id='root']/div/div[1]/div/h1"));
        String h1Text2 = h1Element.getText();
         */



/* skriv
        // sätt 2
        String h1Text = driver.findElement(By.xpath("//*[@id='root']/div/div[1]/div/h1")).getText();

        // Testdrive = fail
        assertEquals(h1Text, "Testdriven utveckling - projekt", "fuck!");

        driver.quit();

    }

    @Test // Kontrollera att textinnehållet i valfritt HTML-element på sti.se
    void checkContentInElement() {

        WebDriver driver = new ChromeDriver();

        driver.get("https://sti.se");


        // Get element with tag name 'div'
        WebElement element = driver.findElement(By.tagName("div"));

        //WebElement lastNavText = element.findElement(By.className("totaraNav_prim--list_item_label"));
        WebElement lastNavText = element.findElement(By.xpath("//*[@id='panel-24-0-0-0']/div/div/p"));

        // betyder root

        System.out.println("Min text: \n" + "\n\n" + lastNavText.getText() + "\n\n");

        assertEquals("Våra YH-program är 1-2 år långa och kostnadsfria. De kräver oftast ingen tidigare erfarenhet inom området, utan passar dig som vill ge dig in i en helt ny bransch och karriär.", lastNavText.getText());
        driver.quit();
    }

    @Test // 1.Använd insektionsverktyget på svtplay.se, 4. kontrollera vilket program som läs ut först på startsidan-kategori,titel,beskrivning.
    void checkFirstRead_category_title_Description() {

    }

    @Test // 5 kontrollera meny visar rätt text. - ej klart
    void checkMenuShowRightText () {

        WebDriver driver = new ChromeDriver();

        driver.get("https://sti.se");

        // Get element with tag name 'div'
        WebElement element = driver.findElement(By.tagName("li"));

        // Get all the elements available with tag name 'p'
        List<WebElement> elements = element.findElements(By.tagName("p"));
        for (WebElement e : elements) {
            System.out.println(e.getText());
        }
        driver.quit();
    }

    @Test // 6. kontrollera bild visas för det rekommenderade programmet.
    void checkIfPictureIsShowned() {

        // Hämta in den webDriver som ska användas
        WebDriver driver = new ChromeDriver();

        // Navigera rätt
        driver.get("https://java22.netlify.app/");

        WebElement productImage = new WebDriverWait(driver, Duration.ofSeconds(10)) // 0 fail, tiden räcker inte till
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[@class='card-img-top' and contains(@src, 'https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg')]")));

        assertTrue(productImage.isDisplayed(), "Bilden verkar inte läsas in");

        driver.quit();
    }

/*
    @Test
    void checkTitleMozilla() {

        // Hämta in den webDriver som ska användas
        WebDriver driver = new FirefoxDriver();                 // "//*[@id="productsContainer"]/div/div[1]/div/img"

        // Navigera till den webbsida som ska testas -  navigate().to kan va get()
        driver.navigate().to("https://svtplay.se");

        // fail "SVT Playy"
        assertEquals("SVT Play", driver.getTitle(), "Felmeddelande: Title don't match as expected! Note it's Case Sensitive");

        driver.quit();
    }
 */

/* skriv
    @Test // G - check! 2/3
    public void numberOfProductsShouldBeTwenty() {

        //
        WebDriver driver = new ChromeDriver();

        driver.get("https://java22.netlify.app/");
        //
       driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        List <WebElement> products = driver.findElements(By.className("productItem"));

        // 21 = fail
        assertEquals(20,products.size(), "Antalet do not match!");

        driver.quit();
    }

    @Test // G - check! 3/3
    public void checkIfPriceIsRightOnThreeProducts () {

        WebDriver driver = new ChromeDriver();

        driver.get("https://java22.netlify.app/");

        // ta ut priset från texten
        WebElement priceElement = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(), 'Fin väska me plats för dator' )]")));

        // Ta ut pris från texten
        String priceText = priceElement.getText();
        String price = priceText.replaceAll("[^\\d.]", "");

        // testar att priset stämmer överens - fail genom att ge tex 95.4
        assertEquals("109.95", price, "fel pris");

        driver.quit();

    }
/*
    @Test
    public void checkProductsHaveRightPrices() {

        WebDriver driver = new ChromeDriver();

        driver.get("https://java22.netlify.app/");

                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        String productOne = driver.findElement(By.xpath("//[@id='productsContainer']/div/div[1]/div/div/p")).getText();
        String productTwo = driver.findElement(By.xpath("//[@id='productsContainer']/div/div[2]/div/div/p")).getText();
        String productThree = driver.findElement(By.xpath("//[@id='productsContainer']/div/div[3]/div/div/p")).getText();

        String findPriceForProductOne = "109.95";
        String findPriceForProductTwo = "22.3";
        String findPriceForProductThree = "55.99";

        boolean validatePriceForProductOne = productOne.contains(findPriceForProductOne);
        boolean validatePriceForProductTwo = productTwo.contains(findPriceForProductTwo);
        boolean validatePriceForProductThree = productThree.contains(findPriceForProductThree);

        if (validatePriceForProductOne && validatePriceForProductTwo && validatePriceForProductThree) {
            System.out.println("Produkt one Correct price " + findPriceForProductOne);
            System.out.println("Produkt two Correct price " + findPriceForProductTwo);
            System.out.println("Produkt three Correct price " + findPriceForProductThree);
        }

        assertTrue(validatePriceForProductOne, "Priset på produkt 1 stämmer inte!");
        assertTrue(validatePriceForProductTwo, "Priset på produkt 2 stämmer inte!");
        assertTrue(validatePriceForProductThree, "Priset på produkt 3 stämmer inte!");

        driver.quit();
    }

 kommenterar mera */

}