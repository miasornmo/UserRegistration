package stepdefs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyStepdefs {

    private WebDriver driver;
    private String uniqueEmail;

    @Before
    public void setUp() {
        uniqueEmail = generateUniqueEmail(); // Initialiserar testmiljö och Skapar unik e-mail för varje testkörning
    }

    private String generateUniqueEmail() { // Metod som genererar ny unik e-mail varje gång den anropas
        return "testuser" + Instant.now().toEpochMilli() + "@example.com";
    }

    @Given("I am on the registration page using {string}") // Öppnar registreringssidan i angiven webbläsare
    public void iAmOnTheRegistrationPageUsing(String browser) {
        if (browser.equals("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }
        driver.get("https://membership.basketballengland.co.uk/NewSupporterAccount");
    }

    private void enterUserDetails(String fieldStatus) { // Fyller i användaruppgifter i formuläret utifrån "field_status"
        WebElement dateOfBirthField = driver.findElement(By.id("dp"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('value', '12/04/1996');", dateOfBirthField);
        driver.findElement(By.id("member_firstname")).sendKeys("Test");

        if (!fieldStatus.equals("missing last name")) {
            driver.findElement(By.id("member_lastname")).sendKeys("User");
        }

        driver.findElement(By.id("member_emailaddress")).sendKeys(uniqueEmail);
        driver.findElement(By.id("member_confirmemailaddress")).sendKeys(uniqueEmail);

        boolean matchPasswords = !fieldStatus.equals("mismatched passwords");
        driver.findElement(By.id("signupunlicenced_password")).sendKeys("SecurePass123!");
        driver.findElement(By.id("signupunlicenced_confirmpassword")).sendKeys(matchPasswords ? "SecurePass123!" : "WrongPass123!");
    }

    @When("I enter user details with {string}")
    public void iEnterUserDetailsWith(String fieldStatus) {
        enterUserDetails(fieldStatus);
    }

    @And("I confirm acceptance of the terms and conditions if {string}") // Om användaren accepterar terms and conditions
    public void iConfirmAcceptanceOfTheTermsAndConditionsIf(String acceptTerms) {
        if (acceptTerms.equals("yes")) {
            WebElement termsLabel = driver.findElement(By.cssSelector("label[for='sign_up_25']"));
            termsLabel.click();
        }
    }

    @And("I confirm that I am over 18 years of age")
    public void iConfirmThatIAmOver18YearsOfAge() {
        WebElement ageLabel = driver.findElement(By.cssSelector("label[for='sign_up_26']"));
        ageLabel.click();
    }

    @And("I agree to the ethics policy")
    public void iAgreeToTheEthicsPolicy() {
        WebElement ethicsLabel = driver.findElement(By.cssSelector("label[for='fanmembersignup_agreetocodeofethicsandconduct']"));
        ethicsLabel.click();
    }

    @And("I submit the registration form") // Klickar på registreringsknappen för att skicka in formuläret
    public void iSubmitTheRegistrationForm() {
        WebElement submitButton = driver.findElement(By.cssSelector("input[name='join']"));
        submitButton.click();
    }

    @Then("I should see a {string} message") // Verifierar att ett specifikt meddelande visas efter registrering beroende på "message_type"
    public void iShouldSeeAMessage(String messageType) {
        WebElement messageElement = waitForMessage(messageType);
        assertTrue(messageElement.isDisplayed(), "Expected message not displayed for: " + messageType); // JUnit assert
    }

    private WebElement waitForMessage(String messageType) { // Privat metod med explicit wait som väntar på att ett specifikt meddelande ska visas
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        switch (messageType) {
            case "success":
                return wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(), 'THANK YOU FOR CREATING AN ACCOUNT')]")));
            case "missing last name":
                return wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("span.warning.field-validation-error[data-valmsg-for='Surname']")));
            case "mismatched passwords":
                return wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("span[data-valmsg-for='ConfirmPassword'] span")));
            case "missing terms and conditions":
                return wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("span.warning.field-validation-error[data-valmsg-for='TermsAccept']")));
            default:
                throw new IllegalArgumentException("Unknown message type: " + messageType);
        }
    }


    @And("the account should {string}") // Verifierar om kontot skapades eller inte
    public void theAccountShould(String accountStatus) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        if (accountStatus.equals("successfully be created")) {
            WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(), 'THANK YOU FOR CREATING AN ACCOUNT')]")));
            assertTrue(confirmationMessage.isDisplayed(), "Account creation confirmation message not displayed!"); // JUnit assert
        } else if (accountStatus.equals("not be created")) {
            String currentUrl = (driver != null) ? driver.getCurrentUrl() : "";
            assertTrue(currentUrl != null && currentUrl.contains("NewSupporterAccount"),
                    "Unexpected navigation, the account should not have been created!"); // JUnit assert
        }
    }


    @After
    public void tearDown() { // Stänger webbläsaren efter varje test
        if (driver != null) {
            driver.quit();
        }
    }
}
