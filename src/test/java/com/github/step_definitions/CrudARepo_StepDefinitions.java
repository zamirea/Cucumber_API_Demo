package com.github.step_definitions;


import com.github.pojos.Repo;
import com.github.utilities.ConfigurationReader;
import com.github.utilities.GlobalDataUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;

import io.restassured.response.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CrudARepo_StepDefinitions {

    private GlobalDataUtils globalDataUtils;

    public CrudARepo_StepDefinitions(StepData stepData){
        globalDataUtils = stepData.globalDataUtils;
    }

    @Given("request for creating a new repo")
    public void request_for_creating_a_new_repo() {
        String repoName = "TestRepo" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMdhhmmsSSS"));
        Map<String, String> requestBody = new LinkedHashMap<>();
        requestBody.put("name", repoName);

        given()
                .log().all()
                .accept("application/vnd.github.v3+json")
                .contentType(ContentType.JSON)
                .header("Authorization", ConfigurationReader.getProperty("access_token"))
                .body(requestBody).
        when()
                .post("/user/repos").
        then()
                .statusCode(201)
                .log().ifError();

        globalDataUtils.setRepoName(repoName);
    }
    @When("request for retrieving the new repo")
    public void request_for_retrieving_the_new_repo() {
        ///repos/{owner}/{repo}
        String expectedRepoName = globalDataUtils.getRepoName();

        Response response = given()
                .log().all()
                .accept("application/vnd.github.v3+json")
                .pathParam("owner", "zamirea")
                .pathParam("repo", expectedRepoName).
                        when()
                .get("repos/{owner}/{repo}")
                .prettyPeek();

        globalDataUtils.setResponse(response);

    }
    @Then("new repo information matches expected")
    public void new_repo_information_matches_expected() {
//        Response response = globalDataUtils.getResponse();
//       JsonPath jp= response.jsonPath();
//        String expectedRepoName = globalDataUtils.getRepoName();
//        assertThat(response.statusCode(), is(200));
//       assertThat(jp.getString("name"), is(expectedRepoName));
//       assertThat(jp.getString("owner.login"), is("zamirea"));

        Response response = globalDataUtils.getResponse();
        String expectedRepoName = globalDataUtils.getRepoName();
        System.out.println("expectedRepoName = " + expectedRepoName);
        Repo newRepo = response.body().as(Repo.class);
        assertThat(response.statusCode(), is(200));
        assertThat(newRepo.getName(),  is(expectedRepoName));
        assertThat(newRepo.getOwner().getLogin(), is("zamirea"));
    }

    @When("updating this repo")
    public void updatingThisRepo() {
        String createdRepoName = globalDataUtils.getRepoName();
        System.out.println("createdRepoName = " + createdRepoName);
        String updatedRepoName = "UpdateRepo" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMdhhmmsSSS"));

        Map<String, String> requestBody = new LinkedHashMap<>();
        requestBody.put("name", updatedRepoName);


        given()
                .accept("application/vnd.github.v3+json")
                .contentType(ContentType.JSON)
                .pathParam("owner", "zamirea")
                .pathParam("repo", createdRepoName)
                .header("Authorization", ConfigurationReader.getProperty("access_token"))
                .body(requestBody)
                .when()
                .patch("/repos/{owner}/{repo}")
                .then()
                .statusCode(200);

        globalDataUtils.setRepoName(updatedRepoName);
        System.out.println("globalDataUtils.getRepoName() = " + globalDataUtils.getRepoName());

    }

    @When("request for deleting the repo")
    public void request_for_deleting_the_repo() {
        String repoName = globalDataUtils.getRepoName();

        Response response = given().log().all()
                .accept("application/vnd.github.v3+json")
                .pathParam("owner", "zamirea")
                .pathParam("repo", repoName)
                .header("Authorization", ConfigurationReader.getProperty("access_token"))
                .when()
                .delete("/repos/{owner}/{repo}")
                .prettyPeek();

        globalDataUtils.setResponse(response);
    }

    @Then("verify the repo is deleted")
    public void verify_the_repo_is_deleted() {
        Response response = globalDataUtils.getResponse();

        assertThat(response.statusCode(), equalTo( 204 ));
        assertThat(response.body().asString(), is( emptyString() ));
    }
}
