/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.Select;
import pojo.SearchResult;

/**
 * @author lenovo
 */
public class TestWebDriver {

    public SearchResult getResult(SearchResult params) throws InterruptedException {

        SearchResult searchResult = new SearchResult();
        System.setProperty("webdriver.firefox.bin", "e:\\firefox\\firefox.exe");
        ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile profile = allProfiles.getProfile("test");
        WebDriver driver = new FirefoxDriver(profile);
//        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://epub.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
        System.out.println("__________________________________________________________________");
        SearchResult param = params;
        searchResult.setAuthor(params.getAuthor());
        searchResult.setTitle(params.getTitle());
        searchResult.setPublisher(params.getPublisher());
        searchResult.setPubTime(params.getPubTime());
        if(((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")) {
            //点击加号
            List<WebElement> link = driver.findElements(By.tagName("a"));
            for (WebElement a : link) {
                if (a.getAttribute("href") != null) {
                    if (a.getAttribute("href").equals("javascript:AddRowEx('txt','txt_i','txt_c');")) {
                        a.click();
                        a.click();
                    }
                }
            }
            //拼接查询条件
            for (int i = 1; i <= 3; i++) {
                Select select1 = new Select(driver.findElement(By.id("txt_" + i + "_sel")));
                select1.selectByValue("RF");
                WebElement txt1 = driver.findElement(By.id("txt_" + i + "_value1"));
                if (i == 1) {
                    txt1.sendKeys(param.getTitle());
                    System.out.print("标题："+param.getTitle());
                }
                if (i == 2) {
                    txt1.sendKeys(param.getAuthor());
                    System.out.print("  作者："+param.getAuthor());
                }
                if (i == 3) {
                    txt1.sendKeys(param.getPublisher());
                    System.out.print("  出版社："+param.getPublisher());
                }
                if (i == 4) {
                    txt1.sendKeys(param.getPubTime().toString());
                    System.out.print("  出版时间："+param.getPubTime());
                }
            }
            System.out.println("");
            WebElement submit = driver.findElement(By.id("btnSearch"));
            submit.click();

            Thread.sleep(3000);
                //查询结果
                List<WebElement> years = driver.findElements(By.className("colorTipContainer"));
                Integer[] yearResult = new Integer[years.size()];
                System.out.print("年份:   ");
                for (int i = 0; i < years.size(); i++) {
                    String value = years.get(i).getText();
                    if (value != null) {
                        System.out.print(value + "\t");
                        Integer numValue = Integer.parseInt(value);
                        yearResult[i] = numValue;
                    }
                }
                System.out.println("");
                searchResult.setYearResult(yearResult);
                List<WebElement> counts = driver.findElements(By.xpath("//span[@style='color:#999;']"));
                Integer[] countResult = new Integer[counts.size()];
                System.out.print("被引次数:   ");
                for (int i = 0; i < counts.size(); i++) {
                    String value = counts.get(i).getText();
                    if (value != null) {
                        value = value.substring(1, value.length() - 1);
                        System.out.print(value + "\t");
                        Integer numValue = Integer.parseInt(value);
                        countResult[i] = numValue;
                    }
                }
                System.out.println("");
                searchResult.setCountResult(countResult);
                driver.close();
            }

        return searchResult;
    }


    public SearchResult getTimeResult(SearchResult params) throws InterruptedException {

        SearchResult searchResult = new SearchResult();
        System.setProperty("webdriver.firefox.bin", "E:\\firefox\\firefox.exe");
        WebDriver driver = new FirefoxDriver();
//        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://epub.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
        SearchResult param = params;
        searchResult.setAuthor(params.getAuthor());
        searchResult.setTitle(params.getTitle());
        searchResult.setPublisher(params.getPublisher());
        searchResult.setPubTime(params.getPubTime());
        if (((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")) {
            //点击加号
            List<WebElement> link = driver.findElements(By.tagName("a"));
            for (WebElement a : link) {
                if (a.getAttribute("href") != null) {
                    if (a.getAttribute("href").equals("javascript:AddRowEx('txt','txt_i','txt_c');")) {
                        a.click();
                        a.click();
                        a.click();
                    }
                }
            }
            //拼接查询条件
            for (int i = 1; i <= 4; i++) {
                Select select1 = new Select(driver.findElement(By.id("txt_" + i + "_sel")));
                select1.selectByValue("RF");
                WebElement txt1 = driver.findElement(By.id("txt_" + i + "_value1"));
                if (i == 1) {
                    txt1.sendKeys(param.getTitle());
                    System.out.print("标题："+param.getTitle());
                }
                if (i == 2) {
                    txt1.sendKeys(param.getAuthor());
                    System.out.print("  作者："+param.getAuthor());
                }
                if (i == 3) {
                    txt1.sendKeys(param.getPublisher());
                    System.out.print("  出版社："+param.getPublisher());
                }
                if (i == 4 && param.getPubTime() != null) {
                    txt1.sendKeys(param.getPubTime().toString());
                    System.out.print("  出版时间："+param.getPubTime());
                }
            }

            System.out.println("");
            WebElement submit = driver.findElement(By.id("btnSearch"));
            submit.click();

            Thread.sleep(3000);
                //查询结果
                List<WebElement> years = driver.findElements(By.className("colorTipContainer"));
                Integer[] yearResult = new Integer[years.size()];
                System.out.print("年份:   ");
                for (int i = 0; i < years.size(); i++) {
                    String value = years.get(i).getText();
                    if (value != null) {
                        System.out.print(value + "\t");
                        Integer numValue = Integer.parseInt(value);
                        yearResult[i] = numValue;
                    }
                }
                System.out.println("");
                searchResult.setYearResult(yearResult);
                List<WebElement> counts = driver.findElements(By.xpath("//span[@style='color:#999;']"));
                Integer[] countResult = new Integer[counts.size()];
                System.out.print("被引次数:   ");
                for (int i = 0; i < counts.size(); i++) {
                    String value = counts.get(i).getText();
                    if (value != null) {
                        value = value.substring(1, value.length() - 1);
                        System.out.print(value + "\t");
                        Integer numValue = Integer.parseInt(value);
                        countResult[i] = numValue;
                    }
                }
                System.out.println("");
                searchResult.setCountResult(countResult);
                driver.close();
            }
        return searchResult;
    }

    public Double[] getDouBanResult(SearchResult params) throws InterruptedException {
        SearchResult searchResult = params;
        Double persons = 0.0;
        Double commentRes = 0.0;
        Double[] returns = new Double[2];
        System.setProperty("webdriver.firefox.bin", "E:\\firefox\\firefox.exe");
        WebDriver driver = new FirefoxDriver();
        WebDriver window = null;
        WebDriver window1 = null;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("http://www.douban.com/search");
        if (((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")) {
                WebElement query = driver.findElement(By.xpath("//input[@size='22']"));
                query.sendKeys(searchResult.getTitle()+" "+searchResult.getAuthor());
                WebElement search = driver.findElement(By.xpath("//input[@type='submit']"));
                search.click();
            if(((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")) {
                List<WebElement> bookList = driver.findElements(By.tagName("a"));
                for(WebElement aBook:bookList) {
                    if (aBook.getText().equals("书籍")) {
                        aBook.click();
                        break;
                    }
                }

                if(((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")){
                List<WebElement> list = driver.findElements(By.className("content"));
                for (WebElement div : list) {
                    //书目列表
                    WebElement a = div.findElement(By.tagName("a"));
                        System.out.println(a.getText());
                    int state0 = 0;
                    if(a.getText().contains(searchResult.getTitle()) || searchResult.getTitle().contains(a.getText())){
                        state0 = 1;
                    }
                            List<WebElement> span0 = div.findElements(By.tagName("span"));
                            WebElement span = div.findElement(By.className("subject-cast"));
                            String text = span.getText().replaceAll(" ", "");
                            String[] splits = text.split("/");
                            int state1 = 0;
                            int state2 = 0;
                            int state3 = 0;
                    System.out.println("excel作者"+searchResult.getAuthor());
                            for (String split : splits) {
                                System.out.print(split + "  ");
                                if (split.contains(searchResult.getAuthor())) {
                                    state1 = 1;
                                }
                                if (split.equals(searchResult.getPublisher())) {
                                    state2 = 1;
                                }
                            }
                    System.out.println("state(标题):"+state0);
                    //控制条件：1是作者like，0是标题like，2是出版社
                            if ( state0 == 1) {
                                //（目前有xx条评价 少于10条评价）
                                System.out.print(span0.get(1).getText()+"  ");
                                System.out.print(span0.get(2).getText()+"  ");
                                if (!span0.get(1).getText().equals("(目前无评价)")) {
                                    System.out.println(a.getText());
                                    a.click();
                                    //焦点跳转
//                                    String currentWindow = driver.getWindowHandle();
//                                    Set<String> handles = driver.getWindowHandles();
//                                    Iterator<String> it = handles.iterator();
//                                    while (it.hasNext()) {
//                                        if (currentWindow == it.next()){
//                                            continue;
//                                        }else {
//                                            window = driver.switchTo().window(it.next());
//                                        }
                                    String currentHandle = driver.getWindowHandle();
                                    Set handles = driver.getWindowHandles();
                                    handles.remove(currentHandle);
                                    if (handles.size() > 0) {
                                            window = driver.switchTo().window((String) handles.iterator().next());
                                    }
                                    break;
                                } else {
                                    persons = 0.0;
                                    commentRes = 0.0;
                                    returns[0] = persons;
                                    returns[1] = commentRes;
                                    driver.quit();
                                    return returns;
                                }
                            }
                        }
                    }
                }
            }
        //一级书目详情
        Thread.sleep(5000);
        if (((JavascriptExecutor) window).executeScript("return document.readyState").equals("complete")) {
            System.out.println("二级页面进入");
            WebElement rating_wrap = window.findElement(By.className("rating_wrap"));
            WebElement isComment = rating_wrap.findElement(By.tagName("span"));
            WebElement a = window.findElement(By.xpath("//a[@href='collections']"));
            a.click();
//            String currentWindow = driver.getWindowHandle();
//            Set<String> handles = driver.getWindowHandles();
//            Iterator<String> it = handles.iterator();
//            while (it.hasNext()) {
//                System.out.println(it);
//                if (currentWindow == it.next()){
//                    continue;
//                }else {
//                    window = driver.switchTo().window(it.next());
//                }
//            }
            if (((JavascriptExecutor) window).executeScript("return document.readyState").equals("complete")) {
                WebElement div = window.findElement(By.className("grid-16-8"));
                WebElement span = div.findElement(By.tagName("span"));
                //最后页面：4人参与评价
                System.out.println(span.getText());
                persons = Double.parseDouble(span.getText().substring(0, span.getText().indexOf("人")));
                WebElement rating_detail_star = window.findElement(By.className("rating_detail_star"));
                String[] comment = rating_detail_star.getText().split(" ");
                Double[] number = new Double[5];
                int count = 0;
                for(String cal:comment){
                    if(cal.contains("%")){
                        cal = cal.substring(0,cal.indexOf("."));
                        number[count] = Double.parseDouble(cal);
                        count++;
                    }
                }
                commentRes = (number[0]*5+number[1]*4+number[2]*3+number[3]*2+number[4]*1)/100;
                returns[0] = persons;
                returns[1] = commentRes;
                driver.quit();
                return returns;
            }
        }
        return null;
    }

    public Integer getDangDangResult(SearchResult params) {
        SearchResult searchResult = params;
        Integer result = null;
        System.setProperty("webdriver.firefox.bin", "E:\\firefox\\firefox.exe");
        WebDriver driver = new FirefoxDriver();
        WebDriver window = null;
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get("http://www.dangdang.com");
        WebElement key_S = driver.findElement(By.id("key_S"));
        key_S.sendKeys(searchResult.getTitle());
        WebElement search_btn = driver.findElement(By.xpath("//input[@dd_name='搜索按钮']"));
        search_btn.click();
        System.out.print("书名："+searchResult.getTitle()+"   作者名："+searchResult.getAuthor());
        if (((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")) {
//            WebElement opt = driver.findElement(By.className("opt"));
//            WebElement sort_self_run = opt.findElement(By.xpath("//a[@name='sort-self-run']"));
//            sort_self_run.click();
            if (((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")) {
                WebElement line1 = driver.findElement(By.className("line1"));
                WebElement itemlist_title = driver.findElement(By.xpath("//a[@name='itemlist-title']"));
                WebElement itemlist_review = driver.findElement(By.xpath("//a[@name='itemlist-review']"));
                System.out.print("   书名："+itemlist_title.getText());
                    System.out.print("   匹配成功    ");
                    System.out.println(itemlist_review.getText());
                    String count = itemlist_review.getText();
                    count = count.substring(0, count.indexOf("条"));
                    result = Integer.parseInt(count);
            }
        }
        if(result == null){
            System.out.println("作者匹配失败----------------------------");
        }
        driver.quit();
        return result;
    }
    //        driver.switchTo().frame("iframeResult");
    //WebElement next = driver.findElement(By.id("Page_next"));
    //next.click();
//        List<WebElement> ets=driver.findElements(By.className("fz14"));
//        for(WebElement et:ets)
//        {
//            System.out.println(et.getText());
//            et.click();
//        }
    //browser.close();
    //browser.quit();
}
