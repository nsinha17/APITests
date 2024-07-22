import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class APITests {
    static String accessToken;
    static String CLIENT_ID = "974d515d41f86868eccd2d22aae8d10e";
    static String CLIENT_SECRET = "tILYEqQRq5PnZ5nccQZ1IiVugUWhZN2UveJZ9rVa";
    static String GRANT_TYPE = "client_credentials";

    Map<String, Object> eSIMTestObject = new HashMap<>() {{
        put("package_id", "merhaba-7days-1gb");
        put("currency", "USD");
        put("quantity", 6);
        put("price", "4.5");
        put("esim_type", "Prepaid");
        put("validity", "7");
        put("data", "1 GB");
        put("status.name", "Completed");
        put("status.slug", "completed");
    }};

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://sandbox-partners-api.airalo.com/v2";
        accessToken = getOAuth2Token();
    }

    @Test()
    public void testESIMCreation() {
        var id = orderESIM(eSIMTestObject.get("package_id").toString(), (Integer) eSIMTestObject.get("quantity"));

        var eSIMEntries = getESIMs();
        assertThat(eSIMEntries.size(), greaterThanOrEqualTo(6));

        var filteredEntries = getFilteredEntries(eSIMEntries, id);
        verifyESIMProperties(filteredEntries, eSIMTestObject);
    }

    public static String getOAuth2Token() {
        var response = given()
                .accept(ContentType.JSON)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("grant_type", GRANT_TYPE)
                .when()
                .post("/token")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        JsonPath jsonPath = response.extract().jsonPath();
        return jsonPath.getString("data.access_token");
    }

    public List<Map<String, Object>> getESIMs() {
        var response =
                given()
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Accept", "application/json")
                        .queryParam("include", "order,order.status,order.user")
                        .when()
                        .get("/sims")
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK);

        JsonPath jsonPath = response.extract().jsonPath();
        return jsonPath.getList("data");
    }

    public List<Map<String, Object>> getFilteredEntries(List<Map<String, Object>> dataEntries, int id) {
        return dataEntries.stream()
                .filter(entry -> (((Map<String, Object>) entry.get("simable")).get("id")).equals(id))
                .collect(Collectors.toList());
    }

    public int orderESIM(String package_id, int quantity) {
        var response =
                given()
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Accept", "application/json")
                        .formParam("quantity", quantity)
                        .formParam("package_id", package_id)
                        .formParam("type", "sim")
                        .when()
                        .post("/orders")
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .body("data.sims.size()", equalTo(quantity))
                        .body("data.package_id", equalTo(package_id));

        JsonPath jsonPath = response.extract().jsonPath();
        return jsonPath.getInt("data.id");
    }

    public void verifyESIMProperties(List<Map<String, Object>> filteredEntries, Map<String, Object> eSIMTestObject) {
        for (Map<String, Object> entry : filteredEntries) {
            Map<String, Object> simable = (Map<String, Object>) entry.get("simable");

            // Assert specific order details
            assertThat(simable.get("package_id"), is(eSIMTestObject.get("package_id")));
            assertThat(simable.get("currency"), is(eSIMTestObject.get("currency")));
            assertThat(simable.get("quantity"), is(eSIMTestObject.get("quantity")));
            assertThat(simable.get("price"), is(eSIMTestObject.get("price")));

            // Assert specific eSIM properties
            assertThat(simable.get("esim_type"), is(eSIMTestObject.get("esim_type")));
            assertThat(simable.get("validity"), is(eSIMTestObject.get("validity")));
            assertThat(simable.get("data"), is(eSIMTestObject.get("data")));
            assertThat(simable.get("status"), is(notNullValue()));
            assertThat(((Map<String, Object>) simable.get("status")).get("name"), is(eSIMTestObject.get("status.name")));
            assertThat(((Map<String, Object>) simable.get("status")).get("slug"), is(eSIMTestObject.get("status.slug")));
        }
    }
}