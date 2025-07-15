package com.browserstack;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumBy;

public class ChallengeTwo extends AppiumTest {

    @Test
    public void testLocalContentAccess() throws Exception {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        
        try {
            System.out.println("Starting BrowserStack Local Testing...");
            
            // Wait for app to load
            System.out.println("Waiting for app to load...");
            WebElement filterSortButton = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                ExpectedConditions.elementToBeClickable(AppiumBy.xpath("//android.widget.TextView[@text='Filter & Sort']")));
            
            // Navigate to Settings
            System.out.println("Looking for Settings menu...");
            
            // Look for hamburger menu or settings
            WebElement menuButton = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.elementToBeClickable(AppiumBy.xpath("//android.widget.ImageView[@content-desc='More options'] | //android.widget.ImageView[@content-desc='Menu'] | //*[contains(@content-desc, 'menu')]")));
            menuButton.click();
            Thread.sleep(2000);
            
            // Click Settings
            WebElement settingsOption = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.elementToBeClickable(AppiumBy.xpath("//android.widget.TextView[@text='Settings']")));
            settingsOption.click();
            Thread.sleep(3000);
            
            // Configure API URL to localhost
            // FIRST: Click the "Url" tab to switch from "Mocked" to "Url" mode
            WebElement urlTab = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.xpath("//android.view.ViewGroup[@content-desc='url-tab']")));
            urlTab.click();
            Thread.sleep(2000);
            
            // THEN find and update the URL input field that appears
            System.out.println("Setting API URL to localhost:3000/api/...");
            WebElement urlField = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.xpath("//android.widget.EditText[@content-desc='url-input']")));

            urlField.clear();
            urlField.sendKeys("http://localhost:3000/api/");
            System.out.println("API URL set to localhost:3000/api/");
            
            // Save/Apply settings
            WebElement saveButton = driver.findElement(AppiumBy.xpath("//android.widget.Button[@content-desc=\"update-configuration-button\"]"));
            saveButton.click();
            WebElement updateContentButton = driver.findElement(AppiumBy.xpath("//android.widget.Button[@content-desc=\"reload-data-button\"]"));
            updateContentButton.click();
            System.out.println("Settings saved");
            
            Thread.sleep(3000);
            
            // Navigate back to home
            System.out.println("Navigating back to home...");
            driver.navigate().back();
            Thread.sleep(2000);
            
            // Try to go to home page
            try {
                WebElement homeButton = driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text='Home']"));
                homeButton.click();
            } catch (Exception e) {
                System.out.println("Already on home page");
            }
            
            Thread.sleep(5000); // Wait for local content to load
            
            // Verify local content is loading
            System.out.println("Verifying local content loads from localhost:3000...");
            
            // Check if products are loaded (should come from local server now)
            WebElement productsIndicator = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Product')] | //*[contains(@text, 'Galaxy')]")));
            
            System.out.println("Products loaded from local server!");
            
            // Count products to verify content
            int productCount = driver.findElements(AppiumBy.xpath("//*[contains(@text, 'Galaxy') or contains(@text, 'iPhone')]")).size();
            System.out.println("Found " + productCount + " products from localhost:3000");
            
            Assert.assertTrue(productCount > 0, "Products should load from local server");
            
            // Test API interaction by using filters
            System.out.println("Testing API interaction with local server...");
            
            WebElement filterButton = driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text='Filter & Sort']"));
            filterButton.click();
            Thread.sleep(2000);
            
            // Check if filter options load (these come from local API)
            WebElement samsungFilter = driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text='Samsung']"));
            System.out.println("Filter options loaded from local API!");
            
            samsungFilter.click();
            Thread.sleep(2000);
            
            driver.navigate().back(); // Close filter
            Thread.sleep(3000);
            
            // Generate more API traffic for monitoring
            System.out.println("Generating API traffic for monitoring...");
            
            for (int i = 0; i < 3; i++) {
                // Open and close filters to generate API calls
                filterButton.click();
                Thread.sleep(1000);
                
                WebElement appleFilter = driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text='Apple'] | //android.widget.TextView[@text='Samsung']"));
                appleFilter.click();
                Thread.sleep(1000);
                
                driver.navigate().back();
                Thread.sleep(1000);
                
            }
            
        } catch (Exception e) {
            // Any failure in the test will be caught here
            jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"Either UI elements not found on local OR another error " + e.getMessage() + "\"}}");            
            // Fail the test
            Assert.fail("Local content access test failed: " + e.getMessage(), e);
        }
    }
}