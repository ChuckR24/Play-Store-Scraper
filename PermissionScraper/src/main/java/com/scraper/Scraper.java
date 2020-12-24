package com.scraper;

import java.net.*;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class Scraper {

	static String privpolfolder = "R:\\DocumentsHDD\\CWRU Junior Year\\Semester 1 - Fall 2020\\Xiao Research\\permission count scraper\\PermissionScraper\\privpols";
	static String[] appIds;
	static String[] ppurls;

	public static void main(String[] args) throws Exception {
		appIds = getAppIds();
		getPermCounts(appIds);
		System.out.println("Contacts: " + totalContactCount);
		System.out.println("Microphones: " + totalMicCount);
		System.out.println("Cameras: " + totalCamCount);
		System.out.println("Locations: "+  totalLocationCount);
		System.out.println("Storages: "+ totalStorageCount);
		System.out.println("SMSs: "+ totalSMSCount);
		System.out.println("Calendars: "+ totalCalendarCount );
		ppurls = getPrivPolURLs(appIds);

		if (ppurls.length < 1) {
			System.out.println("no ppurls returned");
			return;
		}

		if (ppurls.length != appIds.length) {
			System.out.println("not the same num of ppurls and appids");
			return;
		}

		downloadpps(ppurls);

	}

	private static void getPermCounts(String[] inputappIds) {
		System.out.println("getpermcounts called");
		System.setProperty("webdriver.gecko.driver", "C:\\WebDriver\\bin\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		System.out.println("driver just got initialized");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("R:\\DocumentsHDD\\CWRU Junior Year\\Semester 1 - Fall 2020\\Xiao Research\\permission count scraper\\PermissionScraper\\permCounts.txt", true));

			for (String id : inputappIds) {
				System.out.println("appid iteration started");
				int appPermCount = 0;
				boolean found = true;
				try {
					String pageURL = "https://play.google.com/store/apps/details?id=" + id;
					driver.get(pageURL);
					driver.findElements(By.className("hrTbp")).get(2).click();
					appPermCount = countPermsInText(driver.getPageSource());
				} catch (Exception e) {
					System.out.println("ERROR" + e.getMessage() + e.getLocalizedMessage());
					e.printStackTrace();
					found = false;
				}
				// write line to permcounts
				if(found) {
					bw.write(String.valueOf(appPermCount));
				}else {
					bw.write("not found");
				}
				bw.newLine();
				bw.flush();
				//bw.newLine();

				System.out.println("appid iteration ended");
			}
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	static int totalContactCount = 0;
	static int totalMicCount = 0;
	static int totalCamCount = 0;
	static int totalLocationCount = 0;
	static int totalStorageCount = 0;
	static int totalSMSCount = 0;
	static int totalCalendarCount = 0;

	private static int countPermsInText(String pageSource) {
		int result = 0;
		
		if (pageSource.contains("<span>Storage</span>")) {
			result++;
			totalStorageCount++;
		}
		if (pageSource.contains("<span>Calendar</span>")) {
			result++;
			totalCalendarCount++;
		}
		if (pageSource.contains("<span>Location</span>")) {
			result++;
			totalLocationCount++;
		}
		if (pageSource.contains("<span>Camera</span>")) {
			result++;
			totalCamCount++;
		}
		if (pageSource.contains("<span>Microphone</span>")) {
			result++;
			totalMicCount++;
		}
		if (pageSource.contains("<span>Contacts</span>")) {
			result++;
			totalContactCount++;
		}
		if (pageSource.contains("<span>SMS</span>")) {
			result++;
			totalSMSCount++;
		}
			
		return result;
	}
	

	private static void downloadpps(String[] ppurls) {
		// at this point, we just care that we have things named correctly.
		// if some download doesn't work for some reason, that's okay.

		for (int i = 0; i < ppurls.length; i++) {
			String ppurl = ppurls[i];
			if (!(ppurl.contains("NOURLFOUND") || ppurl.contains("EXCEPTION"))) {
				String writePath = privpolfolder + "\\" + appIds[i] + ".txt";
				String pageTxt = "";

				if (ppurl.contains(".pdf")) {
					pageTxt = getTextFromPDFpp(ppurl, appIds[i]);
				} else if (ppurl.contains("https://docs.google.com/")) {
					pageTxt = getTextFromGoogleDoc(ppurl);
				} else {
					try {
						Document pageDoc = Jsoup.connect(ppurl).get();
						pageTxt = pageDoc.text();

					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
				writeTxt(writePath, pageTxt);
			}
		}

	}

	private static String getTextFromGoogleDoc(String ppurl) {
		return ("GOOGLE DOC: " + ppurl);
	}

	private static void writeTxt(String writePath, String pageTxt) {

		try {
			// createfile
			File appPpFile = new File(writePath);
			appPpFile.createNewFile();

			// write to file
			FileWriter myWriter = new FileWriter(appPpFile);

			pageTxt = breakUpText(pageTxt);

			myWriter.write(pageTxt);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	private static String breakUpText(String pageTxt) {

		final String[] words = pageTxt.split(" ");
		final StringBuilder bd = new StringBuilder();

		for (int i = 0; i < words.length; i++) {
			if ((i > 0) && (0 == (i % 10))) {
				bd.append('\n');
			}

			bd.append(words[i]);

			if (i != (words.length - 1)) {
				bd.append(' ');
			}
		}

		return bd.toString();
	}

	private static String getTextFromPDFpp(String fileUrl, String appName) {
		// download as pdf
		try {
			System.out.println("PDF detected");
			URL url = new URL(fileUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();

			InputStream inputStream = urlConnection.getInputStream();
			String PDFdest = privpolfolder + "\\" + appName + ".pdf";
			FileOutputStream fileOutputStream = new FileOutputStream(PDFdest);

			byte[] buffer = new byte[1024];
			int bufferLength = 0;
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, bufferLength);
			}
			fileOutputStream.close();
			System.out.println("PDF downloaded");

			// convert pdf to raw txt
			String resultTxt = convertPdfToRawTxt(PDFdest);

			// delete PDF that was made
			File createdPDF = new File(PDFdest);
			createdPDF.delete();

			return resultTxt;

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "could not extract pdf text";

	}

	private static String convertPdfToRawTxt(String fileDest) {

		try (PDDocument document = PDDocument.load(new File(fileDest))) {

			PDFTextStripper stripper = new PDFTextStripper();

			String text = stripper.getText(document);

			// Closing the document
			document.close();

			return text;

		} catch (Exception e) {

			System.out.println(e.getMessage());

		}

		return "could not extract pdf text";
	}

	private static String[] getAppIds() {
		ArrayList<String> idList = new ArrayList<String>();
		try {
			Scanner scan = new Scanner(new File("input_to_scrape"));

			while (scan.hasNextLine()) {
				String id = scan.nextLine();
				idList.add(id);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		String[] idArray = new String[idList.size()];
		idList.toArray(idArray);

		return idArray;
	}

	public static String[] getPrivPolURLs(String[] appIds) {
		ArrayList<String> result = new ArrayList<String>();

		for (String id : appIds) {
			String pageURL = "https://play.google.com/store/apps/details?id=" + id;
			String foundURL = "NOURLFOUND";

			try {
				String pagehtml = Jsoup.connect(pageURL).get().html();
				Document doc = Jsoup.parse(pagehtml);

				Elements pirvpolicyelements = doc.select("a.hrTbp");

				for (Element e : pirvpolicyelements) {
					if (e.text().contains("Privacy Policy")) {
						foundURL = e.attr("href");
					}

				}

			} catch (Exception e) {
				foundURL = "EXCEPTION on " + id;
			}

			result.add(foundURL);
			System.out.println("found: " + foundURL + "(" + result.size() + ")");
		}

		String[] ppurlarr = new String[result.size()];
		result.toArray(ppurlarr);

		return ppurlarr;
	}
}
