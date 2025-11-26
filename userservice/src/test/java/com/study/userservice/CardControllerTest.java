package com.study.userservice;

// import static org.junit.Assert.assertNotNull;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.study.userservice.auth.Role;
import com.study.userservice.dto.UserResponseDTO;
import com.study.userservice.entity.User;
import com.study.userservice.exceptions.IdNotFoundException;
import com.study.userservice.repository.CardRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.interfaces.CardService;
import com.study.userservice.service.interfaces.UserService;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * Testing: CardController with Postgres and Redis containers
 *
 * <p>To avoid warning for Mockito with Java 21+ add the Mockito agent direct path: IntelliJ IDEA:
 * Run/Debug Configurations → Edit Configurations → Templates → JUnit. In the VM options field, add
 * (for Windows)
 * -javaagent:C:\Users\Den\.m2\repository\org\mockito\mockito-core\5.17.0\mockito-core-5.17.0.jar
 * -Xshare:off
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CardControllerTest extends AbstractIntegrationTest {

  static long userId = 0;
  static String token;

  @LocalServerPort private Integer port;

  UserService userService;
  UserRepository userRepository;

  CardService cardService;
  CardRepository cardRepository;

  @Autowired
  public CardControllerTest(
      UserService userService,
      UserRepository userRepository,
      CardService cardService,
      CardRepository cardRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
    this.cardService = cardService;
    this.cardRepository = cardRepository;
  }

  @BeforeAll
  static void generateToken() {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
    String key = dotenv.get("MY_SECRET_KEY");
    String str = new String(Decoders.BASE64.decode(key));
    byte[] keyBytes = str.getBytes();
    Key readyKey = Keys.hmacShaKeyFor(keyBytes);
    JwtBuilder builder =
        Jwts.builder()
            .setSubject("userT@m.com")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
            .claim("Role", "ADMIN")
            .claim("UserId", "8")
            .signWith(readyKey);
    token = builder.compact();
  }

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + port;
    cardRepository.deleteAll();
    if (userId == 0) {
      userId = userService.addTestUser().getFirst().getId();
    }
  }

  @Test
  void givenRedisContainerConfiguredWithDynamicPropertiesIsRunning() {
    printCurrentMethodName();
    assertTrue(redis.isRunning());
  }

  @Test
  void createCardTest() {
    String payload =
"""
{"userId":currentUserId,"number":"123456789","holder":"CardHolder","expirationDate":"2022-02-16T10:22:15","active":"true"}
        """;

    payload = payload.replace("currentUserId", "" + userId);

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/api/v1/card")
            .then()
            .statusCode(200)
            .body("holder", equalTo("CardHolder"))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void createCardsTest() {
    String payload =
        """
  [{"userId":currentUserId,"number":"111111","holder":"CardHolder","expirationDate":"2022-02-16T10:22:15","active":"true"},
  {"userId":currentUserId,"number":"222222","holder":"CardHolder","expirationDate":"2022-02-16T10:22:15","active":"true"}]
          """;
    payload = payload.replace("currentUserId", "" + userId);

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/api/v1/cards")
            .then()
            .statusCode(200)
            .body(".", hasSize(2))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void getCardByIdTest() {
    UserResponseDTO userResponseDTO = userService.getRandomUser();
    long cardId = cardService.addRandomCard(userResponseDTO).getId();

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/card/" + cardId)
            .then()
            .statusCode(200)
            .body("holder", equalTo(userResponseDTO.getName()))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void getCardsByIdsTest() {
    UserResponseDTO userResponseDTO = userService.getRandomUser();
    Long id1 = cardService.addRandomCard(userResponseDTO).getId();
    Long id2 = cardService.addRandomCard(userResponseDTO).getId();

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/cards/" + id1 + "," + id2)
            .then()
            .statusCode(200)
            .body(".", hasSize(2))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void updateCardTest() {
    UserResponseDTO userResponseDTO = userService.getRandomUser();
    Long id = cardService.addRandomCard(userResponseDTO).getId();

    String payload =
        """
  {"userId":currentUserId,"number":"987654","holder":"CannotChangeCardHolder","expirationDate":"2022-02-16T10:22:15","active":"true"}
          """;
    payload = payload.replace("currentUserId", "" + userId);

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .put("/api/v1/card/" + id)
            .then()
            .statusCode(200)
            .body("active", equalTo(!userResponseDTO.isActive()))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    //    assertTrue(responseController.asString().equals("[]"));
    printCurrentMethodName(responseController);
  }

  @Test
  void addCardTest() {
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/card/random")
            .then()
            .statusCode(200)
            .body("active", equalTo(true))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void getAllCardsTest() {
    UserResponseDTO userResponseDTO = userService.getRandomUser();
    cardService.addRandomCard(userResponseDTO);
    cardService.addRandomCard(userResponseDTO);
    assertTrue(cardService.getAllCards(0, 10).size() == 2);
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/cards/0/10")
            .then()
            .statusCode(200)
            .body(".", hasSize(2))
            .extract()
            .response();
    printCurrentMethodName(responseController);
  }

  @Test
  void deleteCardById() {
    UserResponseDTO userResponseDTO = userService.getRandomUser();
    long id = cardService.addRandomCard(userResponseDTO).getId();

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .delete("/api/v1/card/" + id)
            .then()
            .statusCode(200)
            .body("holder", equalTo(userResponseDTO.getName()))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void deleteLastCardTest() {
    UserResponseDTO userResponseDTO = userService.getRandomUser();
    cardService.addRandomCard(userResponseDTO).getId();

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("api/v1/card/last")
            .then()
            .statusCode(200)
            .body("holder", equalTo(userResponseDTO.getName()))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void deleteCustomerTest() {
    User userToDel =
        new User(null, "John", "Connor", LocalDateTime.now(), "tomjohn@mail.com", true, Role.ADMIN);
    userRepository.save(userToDel);
    UserResponseDTO userResponseDTO = userService.getRandomUser();
    long id = cardService.addRandomCard(userResponseDTO).getId();
    assertNotNull(cardService.getById(id));
    cardService.deleteById(id);
    assertThrows(IdNotFoundException.class, () -> cardService.getById(userToDel.getId()));
    printCurrentMethodName();
  }

  @Test
  void getByUserIdTest() {
    assertThrows(IdNotFoundException.class, () -> cardService.getByUserId(404404L));
    printCurrentMethodName();
  }

  public static void printCurrentMethodName() {
    Exception e = new Exception();
    StackTraceElement[] stackTraceElements = e.getStackTrace();
    String methodName = stackTraceElements[1].getMethodName();
    System.out.println("Method name: " + methodName + "()");
    System.out.println("_________________\n");
  }

  public static void printCurrentMethodName(Response responseController) {
    Exception e = new Exception();
    StackTraceElement[] stackTraceElements = e.getStackTrace();
    String methodName = stackTraceElements[1].getMethodName();
    System.out.println("Response of controller: " + responseController.asString());
    System.out.println("Method name: " + methodName + "()");
    System.out.println("_________________\n");
  }
}
