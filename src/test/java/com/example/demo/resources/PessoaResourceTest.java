package com.example.demo.resources;

import com.example.demo.DemoApplicationTests;
import com.example.demo.domain.Pessoa;
import com.example.demo.domain.Telefone;
import com.example.demo.repository.filtro.PessoaFiltro;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PessoaResourceTest extends DemoApplicationTests {

    @Test
    public void deve_procurar_pessoa_pelo_ddd_e_numero_do_telefone() throws Exception {
        given()
                .pathParam("ddd", "86")
                .pathParam("numero", "35006330")
        .get("/pessoas/{ddd}/{numero}")
        .then()
                .log().body().and()
                .statusCode(HttpStatus.OK.value())
                .body("codigo", comparesEqualTo(3),
    "nome", comparesEqualTo("Cauê"),
                        "cpf", comparesEqualTo("38767897100"));
    }

    @Test
    public void deve_retornar_erro_nao_encontrado_quando_buscar_pessoa_por_telefone_inexistente() throws Exception {
        given()
                .pathParam("ddd", "99")
                .pathParam("numero", "9924555")
        .get("/pessoas/{ddd}/{numero}")
        .then()
                .log().body().and()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("erro", equalTo("Não existe pessoa com o telefone (99)9924555"));
    }

    @Test
    public void deve_salvar_nova_pessoa_no_sistema() throws Exception {
        final Pessoa pessoa = new Pessoa();
        pessoa.setNome("Nicolas");
        pessoa.setCpf("96277565206");

        final Telefone telefone = new Telefone();
        telefone.setDdd("41");
        telefone.setNumero("25824727");

        pessoa.setTelefones(Arrays.asList(telefone));

        given()
                .request()
                .header("Accept", ContentType.ANY)
                .header("Content-type", ContentType.JSON)
                .body(pessoa)
        .when()
        .post("/pessoas")
        .then()
                    .log().headers()
                .and()
                    .log().body()
                .and()
                    .statusCode(HttpStatus.CREATED.value())
                    .header("Location", equalTo("http://localhost:"+porta+"/pessoas/41/25824727"))
                    .body("codigo", equalTo(6),
                            "nome", equalTo("Nicolas"),
                            "cpf", equalTo("96277565206"));
    }

    @Test
    public void nao_deve_salvar_duas_pessoas_com_o_mesmo_cpf() {
        final Pessoa pessoa = new Pessoa();
        pessoa.setNome("Nicolas");
        pessoa.setCpf("72788740417");

        final Telefone telefone = new Telefone();
        telefone.setDdd("41");
        telefone.setNumero("25824727");

        pessoa.setTelefones(Arrays.asList(telefone));

        given()
                .request()
                .header("Accept", ContentType.ANY)
                .header("Content-type", ContentType.JSON)
                .body(pessoa)
        .when()
        .post("/pessoas")
        .then()
                .log().body()
            .and()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("erro", equalTo("Já existe pessoa cadastrada com o CPF '72788740417'"));
    }

    @Test
    public void deve_filtrar_pessoas()throws Exception {
        final PessoaFiltro filtro = new PessoaFiltro();
        filtro.setNome("a");

        given()
                .request()
                .header("Accept", ContentType.ANY)
                .header("Content-type", ContentType.JSON)
                .body(filtro)
        .when()
        .post("/pessoas/filtrar")
        .then()
                    .log().body()
                .and()
                    .statusCode(HttpStatus.OK.value())
                .body("codigo", containsInAnyOrder(1, 3, 5),
                        "nome", containsInAnyOrder("Thiago", "Iago", "Cauê"),
                        "cpf", containsInAnyOrder("86730543540", "38767897100", "72788740417"));
    }
}
