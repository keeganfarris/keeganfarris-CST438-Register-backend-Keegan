package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *    Make sure that TEST_COURSE_ID is a valid course for TEST_SEMESTER.
 *    
 *    URL is the server on which Node.js is running.
 */

@SpringBootTest
public class EndToEndRegisterTest {

	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/keeganfarris/Downloads/chromedriver_mac_arm64/chromedriver";

	public static final String URL = "http://localhost:3000";

	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_USER_FIRST_NAME = "john";
	public static final String TEST_USER_LAST_NAME = "doe";
	public static final String TEST_USER_STATUS = "register hold";
	public static final String TEST_USER_CODE = "1";

	public static final int TEST_COURSE_ID = 40443; 

	public static final String TEST_SEMESTER = "2021 Fall";

	public static final int SLEEP_DURATION = 1000; // 1 second.

	/*
	 * When running in @SpringBootTest environment, database repositories can be used
	 * with the actual database.
	 */
	
	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;

	/*
	 * Student add course TEST_COURSE_ID to schedule for 2021 Fall semester.
	 */
	
	@Test
	public void addStudentTest() throws Exception {

		/*
		 * if student is already enrolled, then delete the enrollment.
		 */
		
		Student x = null;
		do {
			x = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (x != null)
				studentRepository.delete(x);
		} while (x != null);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			
			// Navigates to URL
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			
			// Clicks add student button
			driver.findElement(By.id("add-student-home")).click();
			Thread.sleep(SLEEP_DURATION);

			// Inputs student values
			driver.findElement(By.id("firstName")).sendKeys(TEST_USER_FIRST_NAME);
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.id("lastName")).sendKeys(TEST_USER_LAST_NAME);
			Thread.sleep(SLEEP_DURATION);

			driver.findElement(By.id("email")).sendKeys(TEST_USER_EMAIL);
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.id("status_code")).sendKeys(TEST_USER_CODE);
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.id("status")).sendKeys(TEST_USER_STATUS);
			Thread.sleep(SLEEP_DURATION);
			
			// Clicks add student
			driver.findElement(By.id("add-student")).click();
			Thread.sleep(SLEEP_DURATION);
		
			// Checks if student repository has student that was added.
			Student s = studentRepository.findByEmail(TEST_USER_EMAIL);
			
			assertNotNull(s, "Student not found in database.");
			
			// Checks if values are the same entered on front end.
			assertEquals(driver.findElement(By.id("addedEmail")).getText(), s.getEmail());
			Thread.sleep(500);

			assertEquals(driver.findElement(By.id("addedName")).getText(), s.getName());
			Thread.sleep(500);
			
			assertEquals(driver.findElement(By.id("addedCode")).getText(), s.getStatusCode() + "");
			Thread.sleep(500);
			
			assertEquals(driver.findElement(By.id("addedStatus")).getText(), s.getStatus() + "");
			Thread.sleep(500);

		} catch (Exception ex) {
			throw ex;
		} finally {

			// clean up database.
			
			Student s = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (s != null)
				studentRepository.delete(s);

			driver.quit();
		}

	}
}
