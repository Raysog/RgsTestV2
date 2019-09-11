import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by Ivan on 01.09.2019.
 * + • Сборка проекта с помощью Maven
 * + • Аннотации JUnit (@Before, @After,....)
 * + • Assert, AssertThat
 * + • Параметризация (заполнять страницу с фио 3 раза)
 * + • Выбор браузера (IE, Chrome, FireFox) через переменную и запуск теста в выбранном браузере через командную строку
 */

@RunWith(Parameterized.class)
public class RgsTest {
    /**
     * @param args
     */
    private static WebDriver driver;
    private static Map<WebElement, String> enterValues = new HashMap<>();

    @Parameterized.Parameter
    public String firstName;
    @Parameterized.Parameter(1)
    public String lastName;
    @Parameterized.Parameter(2)
    public String middleName;




    @BeforeClass
    public static void startValues() throws Exception {
        System.setProperty("webdriver.gecko.driver", "src/main/resources/drivers/geckodriver.exe");
        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
        switch (System.getProperty("driver")) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            default:
                System.out.println("Нет такого браузера");
                Assert.fail("Нет такого браузера");
                break;
        }

        driver.manage().window().maximize();

        driver.navigate().to("http://www.rgs.ru");

        driver.findElement(By.xpath("//a[@class='hidden-xs'][contains(text(), 'Меню')]")).click();

        driver.findElement(By.xpath("//a[contains(text(), 'ДМС')][ancestor::form]")).click();

        try {
            driver.findElement(By.xpath("//h1[contains(text(), 'добровольное медицинское страхование')]"));
            System.out.println("Заголовок существует");
        } catch (NoSuchElementException ex) {
            System.out.println("Заголовок не найден");
            Assert.fail("Заголовок не найден");
        }

        driver.findElement(By.xpath("//a[@class=\"btn btn-default text-uppercase hidden-xs adv-analytics-navigation-desktop-floating-menu-button\"]")).click();

    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Василий", "Васильев", "Васильевич"},
                {"Иван", "Иванов", "Иванович"},
                {"Петр", "Петров", "Петрович"}
        });
    }

    @Test (timeout=60000)
    public void formTest(){

        addElement("text","//input[@name=\"LastName\"]", lastName, null);
        addElement("text","//input[@name=\"FirstName\"]", firstName, null);
        addElement("text","//input[@name=\"MiddleName\"]", middleName, null);
        addElement("select", "//select", "Москва", "77");
        addElement("phone","//input[contains(@data-bind,'Phone')]", "(999) 987-65-43", null);
        addElement("text","//input[@name=\"Email\"]", "qwertyqwerty", null);
        addElement("text","//textarea[@name=\"Comment\"]", "testtesttesttest", null);
        addElement("checkBox","//input[@class=\"checkbox\"]", "on", null);


        for (WebElement webElement : enterValues.keySet()) {
            assertEquals("value of element \""+ webElement.getAttribute("name") + "\" is false",enterValues.get(webElement) , webElement.getAttribute("value"));
        }

        By buttonLocator = By.xpath("//button[@id=\"button-m\"]");
        WebElement button = driver.findElement(buttonLocator);
        button.click();
        assertEquals("У Поля Эл. почта не присутствует сообщение об ошибке Введите корректный email",
                "Введите адрес электронной почты",
                driver.findElement(By.xpath("//span[@class=\"validation-error-text\"][ancestor::div[@class='form-group col-md-6 col-xs-12 validation-group-has-error']]")).getAttribute("innerText"));
    }

    @AfterClass
    public static void stopTest() throws Exception {
        driver.quit();
    }

    
    private void addElement(String element, String xpath, String value, String idValue) {
        By locator = By.xpath(xpath);
        WebElement webElement = driver.findElement(locator);
        WebDriverWait elementWaiter = new WebDriverWait(driver, 50, 1000);
        String elementValue = value;

        elementWaiter.until((ExpectedCondition<Object>) webDriver -> webDriver.findElement(locator).isEnabled());
        switch (element) {
            case "text":
                elementWaiter.until(ExpectedConditions.elementToBeClickable(locator));
                double endTime = System.currentTimeMillis() + 30000;
                boolean flag = false;
                while(System.currentTimeMillis() < endTime) {
                	if (!webElement.getAttribute("value").equals(elementValue)) {
                		webElement.clear();
                    	webElement.sendKeys(elementValue);
                	} else {
                		flag = true;
                		break;
                	}
                }
                if (!flag) {
                	Assert.fail();
                }
                enterValues.put(webElement, elementValue);
                break;
            case "phone":
                webElement.sendKeys(elementValue);
                enterValues.put(webElement, "+7 " + elementValue);
                break;
            case "select":
                Select select = new Select(webElement);
                select.selectByVisibleText(elementValue);
                enterValues.put(webElement, idValue);
                break;
            case "checkBox":
                if (!webElement.isSelected()) {
                    webElement.click();
                }
                enterValues.put(webElement, elementValue);
                break;
            default:
                System.out.println("Нет такого элемента");
                Assert.fail("Элемент " + element + " не найден");
        }


    }

}