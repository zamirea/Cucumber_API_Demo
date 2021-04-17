package com.github.step_definitions;

import com.github.utilities.ConfigurationReader;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.reset;

public class Hooks {
    @Before
    public void setUp(){
        baseURI = ConfigurationReader.getProperty("base_uri");
    }

    @After
    public void tearDown(){
        reset();
    }
}
