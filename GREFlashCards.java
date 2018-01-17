package myTestPack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class GREFlashCards {

	public static void main(String[] args) throws InterruptedException, IOException {
		System.setProperty("webdriver.chrome.driver", "D:\\driver\\chromedriver.exe"); 
		WebDriver  cdriver = new ChromeDriver();
		cdriver.get("https://gre.magoosh.com/flashcards/vocabulary/decks");
		List <WebElement> decks =cdriver.findElements(By.partialLinkText("Practice this deck"));
		ArrayList<String> tabs = new ArrayList<String> (cdriver.getWindowHandles());
		String main_tab= cdriver.getWindowHandle();
		String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL,Keys.RETURN);
		HashMap <String, String> wordDefinitions= new HashMap<String, String>();
		int i=0;
		for(WebElement eachDeck :decks)
		{
			i=i+1;
			if(i<18)
				continue;
			eachDeck.sendKeys(selectLinkOpeninNewTab);
			Thread.sleep(1000);
			tabs = new ArrayList<String> (cdriver.getWindowHandles());
			System.out.println(" tabs opened " +tabs.size());
			cdriver.switchTo().window(tabs.get(1));
			WebElement wordCountInDeckEle = cdriver.findElement(By.id("mastered-flashcards-progress"));
			String wordCountInDeck= wordCountInDeckEle.getAttribute("data-remaining");
			int wordCount = Integer.parseInt(wordCountInDeck);
			System.out.println(" Word Count in Deck " +wordCount);
			int count =0;
			File fout = new File("C:/Users/vijetha/Documents/Vocab/deck"+i+".txt");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			do
			{
				System.out.println(" ^^^^ in loop again "+count+"words added so far");
				Thread.sleep(1000);
				try
				{
					cdriver.findElement(By.partialLinkText("Click to see meaning")).click();
				}
				catch(WebDriverException we)
				{
					Thread.sleep(2000);
					WebElement clickMean =cdriver.findElement(By.className("flashcard-content"));
					clickMean.click();
				}
				WebElement backFlashCard =cdriver.findElement(By.className("flashcard-content"));
				WebElement wordNewOrNot = backFlashCard.findElement(By.xpath("(./descendant::div|./following::div)[1]"));
				System.out.println("word new or not"+wordNewOrNot.getText());
				if(wordNewOrNot.getText().equalsIgnoreCase("new word"))
				{
					WebElement word = wordNewOrNot.findElement(By.xpath("(./descendant::h3|./following::h3)[1]"));
					WebElement definition = word.findElement(By.xpath("(./descendant::div|./following::div)[1]"));
					WebElement example = word.findElement(By.xpath("(./descendant::em|./following::em)[1]"));
					System.out.println(" Word " + word.getText() +" Definition " +definition.getText() +" Example :" +example.getText());
					bw.write("Word: "+word.getText());
					bw.newLine();
					bw.write("Definition: " +definition.getText());
					bw.newLine();
					bw.write("Example: "+example.getText());
					bw.newLine();
					bw.newLine();
					bw.flush();
					wordDefinitions.put(word.getText(), definition.getText());
					count=count+1;
					if(count==wordCount)
						break;
				}
				cdriver.findElement(By.partialLinkText("I knew this word")).click(); 
				Thread.sleep(1000);
			}while(count<=wordCount);
			bw.close();
			cdriver.close();
			cdriver.switchTo().window(tabs.get(0));
		}
		cdriver.close();
	}
}
