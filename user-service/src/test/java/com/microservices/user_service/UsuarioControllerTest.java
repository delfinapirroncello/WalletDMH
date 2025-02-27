package com.microservices.user_service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UsuarioControllerTest {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8086;
        RestAssured.basePath = "/api/usuarios";
    }

    @Test
    public void testRegistroUsuarioExitoso() {
        given()
                .contentType("application/json")
                .body("{ \"nombreyApellido\": \"Juan Pérez\", \"email\": \"juan.perez@example.com\", \"contraseña\": \"123456\" }")
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("mensaje", equalTo("Usuario registrado exitosamente"))
                .body("usuario.email", equalTo("juan.perez@example.com"));
    }

    @Test
    public void testObtenerUsuarios() {
        given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0)) // Al menos un usuario en la lista
                .body("[0].email", notNullValue()); // El primer usuario debe tener un email no nulo
    }
    @Test
    public void testObtenerUsuarioPorId() {
        int userId = 1; // Cambia al ID de un usuario existente en tu BD o datos iniciales

        given()
                .when()
                .get("/{id}", userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("nombre", notNullValue());
    }

    @Test
    public void testActualizarUsuario() {
        int userId = 1; // Cambia al ID de un usuario existente

        given()
                .contentType("application/json")
                .body("{ \"nombre\": \"Juan Pérez actualizado\", \"email\": \"juan.actualizado@example.com\" }")
                .when()
                .put("/{id}", userId)
                .then()
                .statusCode(200)
                .body("mensaje", equalTo("Usuario actualizado exitosamente"))
                .body("usuario.email", equalTo("juan.actualizado@example.com"));
    }
    @Test
    public void testEliminarUsuario() {
        int userId = 1; // Cambia al ID de un usuario existente

        given()
                .when()
                .delete("/{id}", userId)
                .then()
                .statusCode(200)
                .body("mensaje", equalTo("Usuario eliminado exitosamente"));
    }


}
