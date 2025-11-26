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
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.study.userservice.auth.Role;
import com.study.userservice.entity.User;
import com.study.userservice.exceptions.IdNotFoundException;
import com.study.userservice.repository.UserRepository;
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
 * Testing: UserController with Postgres and Redis containers
 *
 * <p>To avoid warning for Mockito with Java 21+ add the Mockito agent direct path: IntelliJ IDEA:
 * Run/Debug Configurations → Edit Configurations → Templates → JUnit. In the VM options field, add
 * (for Windows)
 * -javaagent:C:\Users\Den\.m2\repository\org\mockito\mockito-core\5.17.0\mockito-core-5.17.0.jar
 * -Xshare:off
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest extends AbstractIntegrationTest {

  @LocalServerPort private Integer port;

  UserService userService;
  UserRepository userRepository;

  static String token;

  @Autowired
  public UserControllerTest(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
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
    userRepository.deleteAll();
  }

  @Test
  void createUserTest() {
    String payload =
"""
{"name":"Y","surname":"WomanY", "birthDate":"2022-02-16T10:22:15", "email":"you33@and.me", "active":"true"}
        """;

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/api/v1/user")
            .then()
            .statusCode(200)
            .body("name", equalTo("Y"))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void createUsersTest() {
    String payload =
"""
[{"name":"XXX","surname":"ManX", "birthDate":"2022-02-16T10:22:15", "email":"come11@with.me", "active":"true"},
{"name":"ZZZ","surname":"WomanZ", "birthDate":"2022-02-16T10:22:15", "email":"zoomer@lonely.you", "active":"true"}]
        """;

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post("/api/v1/users")
            .then()
            .statusCode(200)
            .body(".", hasSize(2))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void getUserByIdTest() {
    Long id = userService.addTestUser().getLast().getId();

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/user/" + id)
            .then()
            .statusCode(200)
            .body("name", equalTo("X CODE"))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void getUsersByIdsTest() {
    Long id1 = userService.addTestUser().getLast().getId();
    Long id2 = userService.addTestUser().getLast().getId();
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/users/" + id1 + "," + id2)
            .then()
            .statusCode(200)
            .body(".", hasSize(2))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void getUserByEmailTest() {
    String email = userService.addTestUser().getLast().getEmail();

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/user/email/" + email)
            .then()
            .statusCode(200)
            .body("email", equalTo(email))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    //    assertTrue(responseController.asString().equals("[]"));
    printCurrentMethodName(responseController);
  }

  @Test
  void updateUserTest() {
    Long id = userService.addTestUser().getLast().getId();

    String payload =
"""
{"name":"Y","surname":"WomanY", "birthDate":"2022-02-16T10:22:15", "email":"you33@and.me", "active":"true"}
        """;

    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .put("/api/v1/user/" + id)
            .then()
            .statusCode(200)
            .body("name", equalTo("Y"))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void deleteUserById() {
    Long id = userService.addTestUser().getLast().getId();
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .delete("/api/v1/user/" + id)
            .then()
            .statusCode(200)
            .body("name", equalTo("X CODE"))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void addTestUserTest() {
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/admin/test")
            .then()
            .statusCode(200)
            .body(".", hasSize(2))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void deleteLastUserTest() {
    userService.addTestUser();
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("api/v1/admin/dellast")
            .then()
            .statusCode(200)
            .body("name", equalTo("NeedName"))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void getLastUserTest() {
    userService.addTestUser();
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("api/v1/user/last")
            .then()
            .statusCode(200)
            .body("name", equalTo("NeedName"))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void getAllUsersTest() {
    List<User> users =
        List.of(
            new User(
                null, "John", "Connor", LocalDateTime.now(), "john@mail.com", true, Role.ADMIN),
            new User(
                null, "Dennis", "Nix", LocalDateTime.now(), "dennis@mail.com", true, Role.ADMIN));
    userRepository.saveAll(users);
    assertTrue(userService.getAllUsers(0, 10).size() == 2);
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/users/0/2")
            .then()
            .statusCode(200)
            .body(".", hasSize(2))
            .extract()
            .response();
    printCurrentMethodName(responseController);
  }

  @Test
  void getRandomUserTest() {
    userService.addTestUser();
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("api/v1/user/random")
            .then()
            .statusCode(200)
            .body("active", equalTo(true))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void getRangeIdsTest() {
    userService.addTestUser();
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("api/v1/user/native/0")
            .then()
            .statusCode(200)
            .body(".", hasSize(2))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void findByJPQLTest() {
    userService.addTestUser();
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("api/v1/user/jpql/MANCODE")
            .then()
            .statusCode(200)
            .body(".", hasSize(1))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
  }

  @Test
  void testDeleteUser() {
    User userToDel =
        new User(null, "John", "Connor", LocalDateTime.now(), "tomjohn@mail.com", true, Role.ADMIN);
    userRepository.save(userToDel);
    assertNotNull(userService.getById(userToDel.getId()));
    userService.deleteById(userToDel.getId());
    assertThrows(IdNotFoundException.class, () -> userService.getById(userToDel.getId()));
    printCurrentMethodName();
  }

  @Test
  void changeActiveStatusTest() {
    Long id = userService.addTestUser().getLast().getId();
    Response responseController =
        RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .when()
            .get("api/v1/user/active/" + id)
            .then()
            .statusCode(200)
            .body("active", equalTo(false))
            .extract()
            .response();
    assertNotNull(responseController.asString());
    printCurrentMethodName(responseController);
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
