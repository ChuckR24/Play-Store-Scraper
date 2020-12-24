package com.scraper;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.pdfbox.*;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;

public class ClassEnroller {
//	go to https://case.edu/sis/
//
//		document.getElementById("userid").value = "cjr105"
//
//		document.getElementById("pwd").value = "Aquaries24!"
//
//		document.getElementById("login").submit()
//
//		document.getElementById("win0divPTNUI_LAND_REC_GROUPLET$2").click() //classes and enrollment
//
//		document.getElementById("SCC_LO_FL_WRK_SCC_VIEW_BTN$3").click() //shopping cart i think this changes win#
//
//		document.getElementById("DERIVED_REGFRM1_SSR_SELECT$0").click() //check box
//
//		document.getElementById("DERIVED_SSR_FL_SSR_ENROLL_FL").click() //enroll
//
//		document.getElementById("#ICYes").click() //confirm enroll
	
//		document.getElementById("PT_WORK_PT_BUTTON_BACK").click() //back button
	
	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "C:\\WebDriver\\bin\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		while(true) {
			try {
					String pageURL = "https://case.edu/sis/";
					driver.get(pageURL);
					
					WebElement unBox = new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.id("userid")));
					unBox.sendKeys("cjr105");
					
					WebElement pwBox = new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.id("pwd")));
					pwBox.sendKeys("Aquaries24!");
					
					WebElement loginSubmit = new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.id("login")));
					loginSubmit.submit();
					
					WebElement classesAndEnrollmentButton = new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.id("win0divPTNUI_LAND_REC_GROUPLET$2")));
					classesAndEnrollmentButton.click();
					
					
					while(true) { //try to get into that shopping cart
						try {
								
								WebElement cartButton = new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.id("SCC_LO_FL_WRK_SCC_VIEW_BTN$3")));
								cartButton.click();
								
								WebElement firstCheckBox = new WebDriverWait(driver, 5).until(ExpectedConditions.elementToBeClickable(By.id("DERIVED_REGFRM1_SSR_SELECT$0")));
								firstCheckBox.click();
								
								WebElement enrollButton = new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.id("DERIVED_SSR_FL_SSR_ENROLL_FL")));
								enrollButton.click();
								
								WebElement confirmEnrollButton = new WebDriverWait(driver, 5).until(ExpectedConditions.elementToBeClickable(By.id("#ICYes")));
								confirmEnrollButton.click();
								
								TimeUnit.SECONDS.sleep(5);
								
								WebElement allStuff = new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.id("PT_MAIN")));
								System.out.println(allStuff.getText());
								
								if(allStuff.getText().contains("Instructor Consent Required You must obtain permission to take this class. In order to request permission online, click the Request Permission link below.")) {
										System.out.println("could not enroll");
										for(int i = 0 ; i < 60; i++) {
											System.out.println(i+1);
											TimeUnit.SECONDS.sleep(1);
										}
										
								}else {
									System.out.println("-----------MIGHT HAVE ENROLLED OR NO MORE SEATS------------");
									break;
								}
							
						}catch(Exception e){
							System.out.println(e.getMessage());
						}
					}
	//					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
		

	

}
