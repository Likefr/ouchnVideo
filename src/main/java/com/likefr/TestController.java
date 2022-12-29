package com.likefr;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;

/**
 * @version 1.0 2022/12/27
 * @outhor Likefr
 */
public class TestController {
    // 需要刷的视频地址 详见文档 别填错了
    private static final String VIDEO_URL = "https://lms.ouchn.cn/course/xxxxxxxxxx";
    // 用户名
    private static final String USERNAME = "2035001409362";
    // 密码
    private static final String PASSWORD = "Ouchn@2021";
    public static Integer count = 0; // 这一行不要修改

    public static void main(String[] args) throws InterruptedException, IOException {
        System.getProperties().setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/main/resources/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions
        ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        // 指定登录地址
        chromeDriver.get("https://iam.pt.ouchn.cn/am/UI/Login?realm=%2F&service=initService&goto=https%3A%2F%2Fiam.pt.ouchn.cn%2Fam%2Foauth2%2Fauthorize%3Fservice%3DinitService%26response_type%3Dcode%26client_id%3D345fcbaf076a4f8a%26scope%3Dall%26redirect_uri%3Dhttps%253A%252F%252Fmenhu.pt.ouchn.cn%252Fouchnapp%252Fwap%252Flogin%252Findex%26decision%3DAllow");
        chromeDriver.findElement(By.tagName("html")).sendKeys(Keys.F11);
        // 注入js 将网页禁止操作
//        chromeDriver.executeScript("function handler(e) {e.stopPropagation(); e.preventDefault()} document.addEventListener('click', handler, true)");
        // 以下修改成自己的账号密码 确保 一定不能输入错误  运行程序后 就不要在网页上输入账号密码了 只需要输入验证码即可自动登录！！！！
        chromeDriver.findElement(By.className("inputs")).sendKeys(USERNAME);
        chromeDriver.findElement(By.id("password")).sendKeys(PASSWORD);
        isInputCode(chromeDriver);
    }

    // 递归获取是否输入验证码
    public static void isInputCode(ChromeDriver chromeDriver) throws InterruptedException {
        Thread.sleep(2000);
        String validateCode = (String) chromeDriver.executeScript("return document.getElementById('validateCode').value");
        if (count >= 10) {
            System.out.println("超过10次未输入 程序已结束");
            chromeDriver.close();
            return;
        }
        if (validateCode.length() < 4) {
//            System.out.println("验证码不足四位");
            isInputCode(chromeDriver);
            return;
        }
        if (validateCode.equals("")) {
            count++;
            System.out.println("未输入验证码 请输入");
            isInputCode(chromeDriver);
            return;
        }
        chromeDriver.findElement(By.id("button")).click();
        Thread.sleep(2000);
        try {
            WebElement element = chromeDriver.findElement(By.className("l-btn"));
            if (element == null) {
                System.out.println("验证码 输入错误");
                chromeDriver.close();
            }
            element.click();
        } catch (Exception e) {
            System.out.println("无修改密码弹窗");
        }

        Thread.sleep(500);
        System.out.println("登录成功 正在获取视频列表...");
        // 微积分开始刷视频 刷文档
//        这边 改成你要刷的课程
        chromeDriver.get(VIDEO_URL);
        Thread.sleep(5000);
        startVideo(chromeDriver);
    }

    // 获取视频
    public static void startVideo(ChromeDriver chromeDriver) throws InterruptedException {
        Boolean success = new WebDriverWait(chromeDriver, 8000).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        if (success) {
            WebElement element = chromeDriver.findElement(By.className("pre-next-wrapper"));
            WebElement nexts = element.findElement(By.cssSelector("a.next"));
            if (nexts == null) {
                System.out.println("已刷完");
                chromeDriver.close();
                return;
            }
            try {
//                WebElement video = chromeDriver.findElement(By.className("vjs-tech")); 微积分
                Thread.sleep(2000);
                WebElement video = chromeDriver.findElement(By.className("mvp-toggle-play"));
                Thread.sleep(1000);
                System.out.println("开始处理视频");
                chromeDriver.executeScript("document.getElementsByTagName('video')[0].currentTime = 0");
                Double time = (Double) chromeDriver.executeScript("return document.getElementsByTagName('video')[0].duration");
                System.out.println("当前视频长度： " + (time - 6));
                Double cur = 1.0;
                Thread.sleep(1000);
//                chromeDriver.executeScript("document.getElementsByTagName('video')[0].play()");
                video.click();
                Thread.sleep(2000);
                Double c = time - (time / 2) - 15;
                do {
                    video.sendKeys(Keys.RIGHT);
                    cur = (Double) chromeDriver.executeScript("return document.getElementsByTagName('video')[0].currentTime");
                    if (cur >= c && cur <= (c + 20)) {
                        System.out.println("时间停滞中...");
                        Thread.sleep(2000);
                    } else {

                        Thread.sleep(35);
                    }
                } while ((time - cur) >= 25);
                System.out.println("当前视频剩余：" + (time - cur) + " 秒 自动播放中 ");
//                Thread.sleep(500);
//                video.sendKeys(Keys.LEFT);
//                video.sendKeys(Keys.LEFT);
                Thread.sleep(30000);
                nexts.click();
                System.out.println("点击下一个");
                // 带宽越快时间可填越小
                Thread.sleep(4000);
                startVideo(chromeDriver);
                System.out.println("当前视频结束");
            } catch (Exception eX) {

                // 查找需要看的文档
                Thread.sleep(2000);
                try {
                    chromeDriver.findElement(By.className("font-table-edit-view")).click();
                    Thread.sleep(2000);
                    List<WebElement> elements = chromeDriver.findElements(By.cssSelector("a.close"));
                    elements.get(1).click();
                } catch (Exception ea) {

                }
                Thread.sleep(4000);
                nexts.click();
                System.out.println("点击下一个");
                startVideo(chromeDriver);
            } finally {
                chromeDriver.close();
            }

        }
//        chromeDriver.close();
    }
}

