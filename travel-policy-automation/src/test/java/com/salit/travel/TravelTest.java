package com.salit.travel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;



import java.time.LocalDate;


public class TravelTest {

    WebDriver driver;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://digital.harel-group.co.il/travel-policy");
    }


    @Test
    public void testTravelFlow() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // שלג 2: לחיצה על "לרכישה בפעם הראשונה"
        WebElement firstPurchaseButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("button[data-hrl-bo='purchase-for-new-customer']")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", firstPurchaseButton);
        wait.until(ExpectedConditions.elementToBeClickable(firstPurchaseButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstPurchaseButton);

        // שלב 3: בחירת ארה"ב
        WebElement usa = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("destination-0")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", usa);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", usa);

        // שלב 4: לחיצה על "הלאה לבחירת תאריכי הנסיעה"
        WebElement nextButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[contains(text(),'הלאה') or contains(text(),'תאריכי')]")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);


        // שלב 5: בחירת תאריכים
        LocalDate today = LocalDate.now();
        LocalDate departureDate = today.plusDays(7);

        //תאריך חזרה מחושב על ידי הוספת 30 ימים לתאריך היציאה
        LocalDate returnDate = departureDate.plusDays(30);

        int depDay = departureDate.getDayOfMonth();
        int retDay = returnDate.getDayOfMonth();

        // פתיחת תאריך יציאה
        wait.until(ExpectedConditions.elementToBeClickable(By.id("travel_start_date"))).click();

        // בחירת תאריך יציאה
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='" + depDay + "']")
        )).click();

        //מעבר לחודש הבא
        WebElement nextMonthArrow = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("(//button[.//*[name()='svg']])[2]")
                )
        );
        nextMonthArrow.click();

        //בחירת יום החזרה
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='" + retDay + "']")
        )).click();

// שלב 6: בדיקה שסה"כ הימים 31
// ( אם מוסיפים 30 יום לתאריך היציאה אז מתקבל סה"כ 31 ימים קלנדריים
// כיוון שהספירה כוללת גם את תאריך היציאה וגם את תאריך החזרה)
        WebElement totalDaysElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@data-hrl-bo='total-days']")
                )
        );

        String totalDaysText = totalDaysElement.getText().replaceAll("[^0-9]", "");
        int actualDays = Integer.parseInt(totalDaysText);

        Assert.assertEquals(actualDays, 31, "סך כל הימים אינו תקין");


       //שלב 7: לחיצה על הכפתור "הלאה לפרטי נוסעים"
        WebElement nextToPassengers = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[contains(text(),'הלאה לפרטי הנוסעים')]")
                )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextToPassengers);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextToPassengers);

        // שלב 8: בדיקה שהדף נפתח
        WebElement passengersTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@data-hrl-bo='screen_title' and contains(text(),'נשמח להכיר')]")
                )
        );

        Assert.assertTrue(passengersTitle.isDisplayed(), "דף פרטי הנוסעים לא נפתח");
}

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

}