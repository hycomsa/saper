/**
 * MIT License
 *
 * Copyright (c) 2019 Hycom S.A.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package pl.hycom.integration;

import static java.lang.Thread.sleep;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.github.bonigarcia.wdm.WebDriverManager;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SeleniumIT {

    @LocalServerPort
    private int serverPort;

    private WebDriver driver;

    @Rule
    public TestName name = new TestName();

    @Before
    public void prepare() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @After
    public void destroy() throws IOException {
        if (driver != null) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            FileUtils.writeByteArrayToFile(new File("target/" + name.getMethodName() + ".png"), screenshot);

            driver.quit();
        }
    }

    private String getUrl(final String path) {
        return "http://localhost:" + serverPort + path;
    }

    private void waitSimulation() throws InterruptedException {
        sleep(100);
    }

    @Test
    public void game_over() throws InterruptedException {
        driver.get(getUrl("/saper-test"));

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='0:0']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='0:7']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='1:4']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='0:4']")).click();

        waitSimulation();
    }

    @Test
    public void winner() throws InterruptedException {
        driver.get(getUrl("/saper-test"));

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='0:0']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='0:2']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='0:3']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='0:7']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='1:3']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='1:4']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='7:0']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='7:6']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='6:6']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='6:7']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='2:1']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='2:2']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='2:3']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='2:4']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='3:0']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='3:1']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='3:2']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='4:0']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='4:2']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='4:3']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='4:7']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='5:2']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='5:3']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='5:5']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='5:6']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='6:3']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='6:4']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='7:2']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='7:3']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='7:4']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='2:5']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='5:7']")).click();

        waitSimulation();

        driver.findElement(By.xpath("//button[@value='4:4']")).click();

        waitSimulation();
    }

}
