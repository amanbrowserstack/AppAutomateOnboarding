package com.browserstack;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

public class ChallengeThree extends AppiumTest {

    @Test
    public void testAppBrowserContextSwitching() throws Exception {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        
        try {
            System.out.println("Starting Challenge Three - App and Browser Context Switching...");
            
            // === PHASE 1: Initial App Interaction ===
            System.out.println("Phase 1: Establishing initial app state...");
            
            // Wait for BstackDemo app to load
            WebElement filterSortButton = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.xpath("//android.widget.TextView[@text='Filter & Sort']")));
            
            // Do some interaction to establish app state - navigate to a specific product
            filterSortButton.click();
            Thread.sleep(2000);

            String initialPackage = driver.getCurrentPackage();
            String initialActivity = driver.currentActivity();
            System.out.println("✓ CORRECT Package: " + initialPackage);
            System.out.println("✓ CORRECT Activity: " + initialActivity);
            
            // Select Samsung filter to establish a specific state
            WebElement samsungFilter = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.xpath("//android.widget.TextView[@text='Samsung']")));
            samsungFilter.click();
            Thread.sleep(1000);
            
            // Close filter
            driver.navigate().back();
            Thread.sleep(2000);
            
            // Verify we have Samsung products showing
            int samsungProductsCount = driver.findElements(AppiumBy.xpath("//*[contains(@text, 'Galaxy') or contains(@text, 'Samsung')]")).size();
            Assert.assertTrue(samsungProductsCount > 0, "Should have Samsung products visible in initial state");
            System.out.println("✓ Initial app state established - " + samsungProductsCount + " Samsung products visible");
            
            // === PHASE 2: Switch to Chrome Browser ===
            System.out.println("Phase 2: Switching to Chrome browser...");
            
            // Launch Chrome browser using startActivity with component format
            driver.executeScript("mobile: startActivity", 
                java.util.Map.of("component", "com.android.chrome/com.google.android.apps.chrome.Main"));
            
            Thread.sleep(5000); // Wait for Chrome to launch
            System.out.println("✓ Chrome browser launched");

            // === PHASE 3: Google Search ===
            System.out.println("Phase 4: Performing Google search...");
            
            // Find Google search box and search for "BrowserStack" - this must succeed
            WebElement searchBox = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.presenceOfElementLocated(AppiumBy.xpath("//android.widget.EditText")));
            
            searchBox.clear();
            searchBox.sendKeys("BrowserStack");
            
            // Press Enter to search
            driver.pressKey(new KeyEvent(AndroidKey.ENTER));
            
            Thread.sleep(5000);
            System.out.println("✓ Searched for 'BrowserStack' on Google");
            
            // Click on the first search result - this must succeed
            WebElement firstResult = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.elementToBeClickable(AppiumBy.xpath("(//android.widget.TextView[contains(@text, 'BrowserStack')])[1] | (//android.widget.TextView[contains(@text, 'browserstack')])[1]")));
            
            firstResult.click();
            Thread.sleep(5000);
            
            System.out.println("✓ Clicked first search result");
            
            // === PHASE 5: Switch Back to Original App ===
            System.out.println("Phase 5: Switching back to original BstackDemo app...");
            
            driver.context("NATIVE_APP");
            Thread.sleep(2000);
            
            // Activate the original app
            driver.activateApp("com.browserstack.demo.app");
            Thread.sleep(5000);
            
            System.out.println("✓ Switched back to BstackDemo app");
            
            // === PHASE 6: Verify We're Back in Original App ===
            System.out.println("Phase 6: Verifying we're back in the original app state...");

            Thread.sleep(5000);
            
            // Verify we're back in BstackDemo by checking for app-specific elements
            WebElement filterSortButtonAgain = (WebElement) new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                ExpectedConditions.elementToBeClickable(AppiumBy.xpath("//android.widget.TextView[@text='Filter & Sort']")));
            
            Assert.assertTrue(filterSortButtonAgain.isDisplayed(), "Should be back in BstackDemo app");
            
            // Verify the package is correct
            String finalPackage = driver.getCurrentPackage();
            Assert.assertEquals(finalPackage, initialPackage, "Should be back in the original app package");
            
        } catch (Exception e) {
            // Any failure in the test will be caught here
            String cleanMessage = e.getMessage()
            .replaceAll("\"", "'")        
            .replaceAll("\\\\", "/")
            .replaceAll("\n", " ")
            .replaceAll("\r", " ");
            jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"An error occured: " + cleanMessage + "\"}}");
            // Fail the test
            Assert.fail("App switching test failed: " + cleanMessage, e);
        }
    }
}