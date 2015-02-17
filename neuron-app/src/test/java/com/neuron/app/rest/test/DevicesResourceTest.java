package com.neuron.app.rest.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;

public class DevicesResourceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 9998;
		RestAssured.basePath = "/api/";
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHelloWorld() {		
		expect().statusCode(200)
				.body("message", equalTo("Hello World"))
				.when()
				.get("http://localhost:9998/api/devices/hello");
	}

}
