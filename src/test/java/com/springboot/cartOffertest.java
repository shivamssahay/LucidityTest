package com.springboot;
import static org.hamcrest.Matchers.equalTo;

import static io.restassured.RestAssured.*;


import static org.hamcrest.Matchers.lessThan;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class cartOffertest {

    public static final String BASE_URL = "http://localhost:9001/api/v1/cart/apply_offer";
    @BeforeClass
    public static void setup() {
        
        RestAssured.baseURI = "http://localhost:9001"; 

    }
    
    @Test
    public void checkFlatXForOneSegment() {
        
        Map<String, Object> offerRequest = new HashMap<>();
        offerRequest.put("restaurant_id", 1);
        offerRequest.put("offer_type", "FLATX");
        offerRequest.put("offer_value", 10);
        offerRequest.put("customer_segment", Arrays.asList("p1"));

        
        Response response = given()
                .header("Content-Type", "application/json")
                .body(offerRequest)
                .when()
                .post("/api/v1/offer");

        System.out.println("Running REST-assured test");
        System.out.println("Response: " + response.getBody().asString());

        // Assertion
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertTrue(response.getBody().asString().contains("success"));
    }

    @Test
    public void CheckPercentOffer(){
        
        Map<String, Object> offerRequest = new HashMap<>();
        offerRequest.put("restaurant_id", 1);
        offerRequest.put("offer_type", "FLATX%");
        offerRequest.put("offer_value", 10);
        offerRequest.put("customer_segment", Arrays.asList("p2"));

        
        Response response = given()
                .header("Content-Type", "application/json")
                .body(offerRequest)
                .when()
                .post("/api/v1/offer");

        System.out.println("Running REST-assured test 2");
        System.out.println("Response: " + response.getBody().asString());

        // Assertion
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertTrue(response.getBody().asString().contains("success"));
    }

    @Test
    public void testFlatApplyCartOffer() {
        given()
            .header("Content-Type","application/json" )
            .body("{ \"cart_value\": 200, \"user_id\": 1, \"restaurant_id\": 1 }")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(190));
    }

    @Test
    public void testPercentApplyCartOffer() {
        given()
            .header("Content-Type","application/json" )
            .body("{ \"cart_value\": 200, \"user_id\": 2, \"restaurant_id\": 1 }")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(190));
    }

    @Test() //No Offer on Segment
    public void testNoOfferForUnknownSegment() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\":200,\"user_id\":3,\"restaurant_id\":1}")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(200));
    }
    @Test //Restaurant ID Mismatch
    public void testRestaurantIdMismatch() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\":200,\"user_id\":1,\"restaurant_id\":99}")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(200));
    }

    @Test // No Active Offers
    public void testNoActiveOffers() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\":200,\"user_id\":4,\"restaurant_id\":1}")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(200));
    }

    @Test //Rounding off ooffers
    public void testPercentageRounding() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\":199,\"user_id\":2,\"restaurant_id\":1}")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(179));  // Assuming floor/round
    }

    @Test //Case of Multiple Offers
    public void testMultipleOffersCorrectOneApplied() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\":250,\"user_id\":5,\"restaurant_id\":1}")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(225));
    }

    @Test //Invaild User
    public void testInvalidUserIdNoSegment() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\":200,\"user_id\":999,\"restaurant_id\":1}")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(200));
    }

    @Test //Offer Value is 0
    public void testZeroOfferValue() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\":200,\"user_id\":6,\"restaurant_id\":1}")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(200));
    }

    @Test //Offer value is higher than cart value
    public void testOfferGreaterThanCartValue() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\":200,\"user_id\":7,\"restaurant_id\":1}")
        .when()
            .post("/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(0));
    }

    @Test //Negative Cart Value
    public void testNegativeCartValue() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": -100, \"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post(BASE_URL)
        .then()
            .statusCode(400); 
    }

    @Test // Zero cart Value
    public void testZeroCartValue() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 0, \"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post(BASE_URL)
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(0));
    }

    @Test// Missing user ID
    public void testMissingUserId() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"restaurant_id\": 1}")
        .when()
            .post(BASE_URL)
        .then()
            .statusCode(400);
    }

    @Test // Misising Cart value
    public void testMissingCartValue() {
        given()
            .contentType("application/json")
            .body("{\"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post(BASE_URL)
        .then()
            .statusCode(400);
    }

    @Test // Large Cart Value
    public void testLargeCartValue() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 1000000, \"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post(BASE_URL)
        .then()
            .statusCode(200)
            .body("cart_value", lessThan(1000000));
    }

    @Test // Invalid JSON 
    public void testInvalidJsonBody() {
        given()
            .contentType("application/json")
            .body("{cart_value:200, user_id:1 restaurant_id:1}") // Invalid JSON
        .when()
            .post(BASE_URL)
        .then()
            .statusCode(400);
    }
}
