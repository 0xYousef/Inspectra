package selenium.pages.products;

import selenium.components.ProductsService;
import data.exceptions.ExceptionHandler;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.BasePage;
import selenium.support.enums.BRAND;
import selenium.support.enums.CATEGORY;

public class ProductsPage extends BasePage {

    private CATEGORY current_category = null;
    private BRAND current_brand = null;

    public ProductsPage(WebDriver driver) {
        super(driver);
    }



    public ProductsService order(){
        return new ProductsService(driver);
    }


    @Step("Search for products using '{0}'")
    public void Search(String text) {
        if (text == null || text.trim().isEmpty()) {
            log.warn("Search text is empty or null.");
            return;
        }

        try {
            WebElement searchBox = driver.findElement(By.id("search_product"));
            WebElement searchButton = driver.findElement(By.id("submit_search"));
            searchBox.clear();
            searchBox.sendKeys(text);
            searchButton.click();
            log.info("Search submitted for text: '{}'", text);
        } catch (NoSuchElementException e) {
            ExceptionHandler.handleSilently(e, "finding search input/button");
        }
    }


    @Step("Reload all products via active directory bar")
    public ProductsPage reloadAllProducts() {
        try {
            WebElement breadcrumb = driver.findElement(By.cssSelector("ol.breadcrumb"));
            breadcrumb.findElement(By.linkText("Products")).click();
            log.info("Reloaded all products using breadcrumb.");

        } catch (NoSuchElementException e) {
            ExceptionHandler.handleSilently(e, "finding Products breadcrumb link");
        }
        return this;
    }

    @Step("Select product category: {category}")
    public ProductsPage selectCategory(CATEGORY category) {
        if (category == null) {
            log.warn("Provided category is null. Skipping category selection.");
            return this;
        }
        this.current_category = category;
        this.current_brand = null;
        log.info("Selected category: {}", category);
        return this;
    }


    @Step("Select product brand: {brand}")
    public ProductsPage selectBrand(BRAND brand) {
        if (brand == null) {
            log.warn("Provided brand is null. Skipping brand selection.");
            return this;
        }
        this.current_brand = brand;
        this.current_category = null;
        log.info("Selected brand: {}", brand);
        return this;
    }

    @Step("Verify active search label (category or brand)")
    public String verifyActiveSearch() {
        if (current_category != null) {
            return (current_category.getMainCategory()+ " > " + current_category).toLowerCase();
        } else if (current_brand != null) {
            return ("Brand > " + current_brand).toLowerCase();
        } else {
            return "no active search by category or brand";
        }
    }


    @Step("Get header title of the current products section")
    public String getHeaderTitle() {
        try {
            WebElement title = driver.findElement(By.cssSelector("div.features_items > h2.title.text-center"));
            String text = title.getText().trim();
            log.info("Page header found: '{}'", text);
            return text.toLowerCase();
        } catch (NoSuchElementException e) {
            ExceptionHandler.handleSilently(e, "finding header title element");
            return "no header title found";
        }
    }
}
