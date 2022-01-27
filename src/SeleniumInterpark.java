import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SeleniumInterpark {
    private static WebDriver driver;
    private static String id = "";
    private static String pw = "";
    private static String bir = "";

    public static void main(String[] args){
        JFrame jFrame = new JFrame();
        jFrame.setLocation(1000,300);
        jFrame.setSize(250,400);
        jFrame.setLayout(new FlowLayout());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel idL = new JLabel("ID");
        JTextField idField = new JTextField(25);
        idField.setHorizontalAlignment(JTextField.CENTER);

        JLabel pwL = new JLabel("PW");
        JPasswordField pwField = new JPasswordField(25);
        pwField.setHorizontalAlignment(JTextField.CENTER);

        JLabel birL = new JLabel("생년월일 6자리");
        JTextField birField = new JTextField(25);
        birField.setHorizontalAlignment(JTextField.CENTER);

        JButton runButton = new JButton("로그인");
        runButton.setFont( new Font("맑은 고딕", Font.BOLD, 30));
        runButton.setPreferredSize(new java.awt.Dimension(200,50));

        JButton runButton2 = new JButton("시작");
        runButton2.setFont( new Font("맑은 고딕", Font.BOLD, 30));
        runButton2.setPreferredSize(new java.awt.Dimension(200,50));

        jFrame.add(idL);
        jFrame.add(idField);

        jFrame.add(pwL);
        jFrame.add(pwField);

        jFrame.add(birL);
        jFrame.add(birField);

        jFrame.add(runButton);
//        jFrame.add(runButton2);

        jFrame.setVisible(true);

        runButton.addActionListener(e -> {
            try {
                id = idField.getText();
                pw = pwField.getText();
                bir = birField.getText();
                loginModule();
                ticketModule();
            } catch (NoSuchWindowException ex){
                System.exit(0);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }

    private static void loginModule() {
        setDriver();
        Acting.loginProcess();
    }
    private static void ticketModule() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("/html/body/div[6]/div[4]/div[4]"))));
        Acting.btnClickProcess();

        Thread.sleep(300);
        List<String> tab = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tab.get(1));
        System.out.println(driver.getTitle());

        Acting.captchaType();

        Acting.seatSelect();
        Acting.phase1();
        Acting.phase2();
        Acting.phase3();
        Acting.phase4();
    }
    private static void setDriver() {
        //크롬드라이버 경로
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(800, 800));
        driver.get("https://accounts.interpark.com/authorize/ticket-pc?origin=https%3A%2F%2Fticket%2Einterpark%2Ecom%2FGate%2FTPLoginConfirmGate%2Easp%3FGroupCode%3D%26Tiki%3D%26Point%3D%26PlayDate%3D%26PlaySeq%3D%26HeartYN%3D%26TikiAutoPop%3D%26BookingBizCode%3D%26MemBizCD%3DWEBBR%26CPage%3DB%26GPage%3Dhttp%253A%252F%252Fticket%252Einterpark%252Ecom%252FContents%252FSports%252FGoodsInfo%253FSportsCode%253D07032%2526TeamCode%253DPE015");
    }

    private static class Acting {
        private static void loginProcess() {
            WebElement idBox = driver.findElement(By.id("userId"));
            WebElement pwBox = driver.findElement(By.id("userPwd"));
            WebElement login = driver.findElement(By.id("btn_login"));
            //아이디, 비밀번호
            idBox.sendKeys(id);
            pwBox.sendKeys(pw);
            login.click();
        }

        private static void btnClickProcess() {
            try {
                List<WebElement> btnColor_y = driver.findElements(By.className("BtnColor_Y"));
                //맨 끝에 get 활성화된 버튼 배열 인덱스(0~ )
                WebElement tarBtn = btnColor_y.get(btnColor_y.size()-1);
                tarBtn.sendKeys(Keys.ENTER);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("btn");
                btnClickProcess();
            }
        }

        private static void captchaType() throws InterruptedException {
            Thread.sleep(300);
            driver.switchTo().defaultContent();
            driver.switchTo().frame(1);
//        String ff = driver.findElement(By.id("imgCaptcha")).getAttribute("src");
            List<WebElement> captchaList = driver.findElements(By.className("validationTxt"));
            WebElement captcha = captchaList.get(0);
            captcha.click();
            WebElement captchaInput = captcha.findElement(By.tagName("input"));
            //captcha 입력 타임아웃
            captcha(captchaInput);
        }
        private static void captcha(WebElement captchaInput){
            try {
                Thread.sleep(100);
                System.out.println(captchaInput.getAttribute("value").length());
                if (captchaInput.getAttribute("value").length() == 6){
                    driver.findElements(By.className("capchaBtns")).get(0).findElement(By.tagName("a")).click();
                }
                else {
                    throw new RuntimeException();
                }
            }catch (RuntimeException e){
                captcha(captchaInput);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private static void seatSelect() throws InterruptedException {
            try {
                Thread.sleep(100);
                driver.switchTo().defaultContent();
                driver.switchTo().frame(1);
                //328.5
                //맨 끝에 get 일반석 0, 휠체어석 1
                driver.findElements(By.className("list")).get(0).findElements(By.tagName("a")).get(0).click();
                driver.findElements(By.className("twoBtn")).get(0).findElement(By.tagName("a")).click();

                driver.switchTo().defaultContent();
                driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(1));
                driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(0));
                int checker = 0;
                List<WebElement> elSeats = driver.findElements(By.className("stySeat"));
                for (WebElement elSeat : elSeats) {
                    //레드팀 블루팀 선택에 중요
                    //부등호 '<'는 왼쪽자리 '>'는 오른쪽자리328.5
                    if (elSeat.getLocation().getX() > 328.5) {
                        elSeat.click();
                        checker = 1;
                        break;
                    }
                }
                if (checker == 0) {
                    throw new RuntimeException();
                }
                driver.switchTo().defaultContent();
                driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(1));
                driver.findElements(By.className("btnWrap")).get(0).findElement(By.tagName("a")).click();
                driver.switchTo().defaultContent();
            } catch (IndexOutOfBoundsException e) {
                driver.navigate().refresh();
                seatSelect();
//                driver.switchTo().defaultContent();
//                driver.switchTo().frame(driver.findElements(By.tagName("iframe")).get(1));
//                driver.findElements(By.className("btnWrap")).get(0).findElement(By.tagName("p")).click();
//                seatSelect();
            } catch (UnhandledAlertException e) {
                driver.navigate().refresh();
                seatSelect();
            } catch (ElementNotInteractableException e) {
                driver.navigate().refresh();
                seatSelect();
            } catch (RuntimeException e) {
                driver.navigate().refresh();
                seatSelect();
            }
        }

        private static void phase1() {
            driver.switchTo().defaultContent();
            driver.switchTo().frame(0);
            WebElement selEl = driver.findElements(By.className("taL")).get(0).findElement(By.tagName("select"));
            Select select = new Select(selEl);
            select.selectByIndex(1);
            driver.switchTo().defaultContent();
            driver.findElement(By.id("SmallNextBtnImage")).click();
        }

        private static void phase2() throws InterruptedException {
            driver.switchTo().defaultContent();
            driver.switchTo().frame(0);
            Thread.sleep(300);
            WebElement chkEl = driver.findElements(By.className("chk")).get(0);
            chkEl.click();
            //생년월일입력
            driver.findElement(By.id("YYMMDD")).sendKeys(bir);
            driver.switchTo().defaultContent();
            driver.findElement(By.id("SmallNextBtnImage")).click();
        }

        private static void phase3() throws InterruptedException {
            driver.switchTo().defaultContent();
            driver.switchTo().frame(0);
            Thread.sleep(300);
            WebElement chkEl2 = driver.findElements(By.className("chk")).get(3);
            chkEl2.click();
            driver.switchTo().defaultContent();
            driver.findElement(By.id("SmallNextBtnImage")).click();
        }

        private static void phase4() throws InterruptedException {
            driver.switchTo().defaultContent();
            driver.switchTo().frame(0);
            Thread.sleep(300);
            WebElement chkEl3 = driver.findElement(By.id("checkAll"));
            chkEl3.click();
            driver.switchTo().defaultContent();
            driver.findElement(By.id("LargeNextBtn")).click();
        }
    }

}

