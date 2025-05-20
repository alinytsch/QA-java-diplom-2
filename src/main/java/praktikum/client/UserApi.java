package praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.model.User;
import praktikum.model.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserApi {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(user)
                .post("/auth/register");
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken) {
        given()
                .header("Authorization", accessToken)
                .delete(BASE_URL + "/auth/user");
    }

    @Step("Логин пользователя")
    public Response login(UserCredentials creds) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(creds)
                .post("/auth/login");
    }
}