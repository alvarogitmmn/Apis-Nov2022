import com.github.javafaker.Faker;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class eCommerce {
    static private String url_base = "webapi.segundamano.mx";
    static private String email = "pruebas_api19@mailinator.com";
    static private String password = "abcde12345";
    static private String account_id = "";
    static private String access_token = "";
    static private String uuid = "";
    static private String ad_id;
    static private String account_id_solo;
    static private String addressID;

  //  @Name("Obtener token")
    private String obtener_Token(){

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String body_request = "{\"account\":{\"email\":\""+email+"\"}}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(email,password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response del token: " + body_response);

        JsonPath jsonResponse = response.jsonPath();

        access_token = jsonResponse.get("access_token");
        //pm.environment.set("token",pm.response.json().access_token)
        System.out.println("Token:"+ access_token);

        account_id = jsonResponse.get("account.account_id");
        System.out.println("Account ID: "+ account_id);

        uuid = jsonResponse.get("account.uuid");
        System.out.println("UUID: "+ uuid);


        return access_token;
    }

    @Test
    @Order(1)
    @DisplayName("Test case: Validar el filtrado de categorias")
    public void obtener_Categorias_filtrado_200(){
        RestAssured.baseURI = String.format("https://%s/urls/v1/public",url_base);

        String body_request = "{\n" +
                "\t\"filters\": [{\n" +
                "\t\t\"price\": \"-60000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"60000-80000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"80000-100000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"100000-150000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"150000-\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}]\n" +
                "}";

        Response response=given()
                .log().all()
                .filter(new AllureRestAssured())
                .queryParam("lang","es")
                .contentType("application/json")
                .headers("Accept","application/json, text/plain, */*")
                .body(body_request)
                .post("/ad-listing");

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("urls"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(2)
    @DisplayName("Test case: Listado de Anuncios")
    public void listado_anuncios_200(){
        RestAssured.baseURI = String.format("https://%s/highlights/v1",url_base);

        Response response=given()
                .log().all()
                .filter(new AllureRestAssured())
                .queryParam("prio",1)
                .queryParam("cat","2020")
                .queryParam("lim",1)
                .headers("Accept","*/*")
                .get("/public/highlights");


        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("list_id"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 2500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(3)
    @DisplayName("Test case: Crear Usuario")
    public void crear_Usuario_401(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String new_user = "agente_ventas_test" + (Math.floor(Math.random()*6789)) + "@mailinator.com";
        String new_password = "12345";
        String body_request = "{\"account\":{\"email\":\""+new_user+"\"}}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(new_user,new_password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(401,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("ACCOUNT_VERIFICATION_REQUIRED"));
        assertTrue(body_response.contains("error"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(4)
    @DisplayName("Test case: Obtener información del usuario")
    public void obtener_info_usuario(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String body_request = "{\"account\":{\"email\":\""+email+"\"}}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(email,password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("token"));
        assertTrue(body_response.contains("uuid"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 2500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

        //Asignar valores a nuestras variables globales

        JsonPath jsonResponse =response.jsonPath();

        access_token = jsonResponse.get("access_token");
        //pm.environment.set("token",pm.response.json().access_token)
        System.out.println("Token:"+ access_token);

        account_id = jsonResponse.get("account.account_id");
        //pm.environment.set("account_id",pm.response.json().account.account_id)
        System.out.println("Account ID: "+ account_id);

        uuid = jsonResponse.get("account.uuid");
        //pm.environment.set('uuid',pm.response.json().account.uuid)
        System.out.println("UUID: "+ uuid);

        String account_id_solo_local = account_id.substring(18);
        account_id_solo = account_id_solo_local;

        System.out.println("account id solo : " + account_id_solo);

    }

    @Test
    @Order(5)
    @DisplayName("Test case: Editar datos del usuario")
    public void editar_datos_de_usuario(){

        //Datos random -  fakerjs
        Faker faker = new Faker();
        String name = faker.name().fullName();
        //String fname = faker.name().firstName();
        //String lastname = faker.name().lastName();

        String randomData = faker.country().name();

        String phone = faker.phoneNumber().cellPhone();

        System.out.println("Nombre completo: " + name);

        String body_request= "{\n" +
                "    \"account\": {\n" +
                "        \"name\": \""+name+"\",\n" +
                "        \"phone\": \""+phone+"\",\n" +
                "        \"professional\": true,\n" +
                "        \"phone_hidden\": true\n" +
                "    }\n" +
                "}";

        String token = obtener_Token();
        System.out.println(("Token en la prueba Crear anuncio " + token));

        RestAssured.baseURI=String.format("https://%s/nga/api/v1%s?lang=es",url_base,account_id);

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .header("Accept","*/*")
                .header("Authorization","tag:scmcoord.com,2013:api "+token)
                .body(body_request)
                .patch();

        String body_response = response.getBody().prettyPrint();

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());

        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("email"));
        assertTrue(body_response.contains("rfc"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 5000);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));


    }

    @Test
    @Order(6)
    @DisplayName("Test case: Crear un Anuncio")
    public void crear_un_anuncio(){


        String token = obtener_Token();
        System.out.println(("Token en la prueba Crear anuncio " + token));

        String body_request = "{\n" +
                "    \"images\": \"2265766694.jpg\",\n" +
                "    \"category\": \"4100\",\n" +
                "    \"subject\": \"Unbranded estampillas para colección\",\n" +
                "    \"body\": \"Unbranded Compra y venta e intercambio de estampillas para colección seria\",\n" +
                "    \"is_new\": \"0\",\n" +
                "    \"region\": \"12\",\n" +
                "    \"municipality\": \"316\",\n" +
                "    \"area\": \"69520\",\n" +
                "    \"price\": \"804\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"267-941-5539\"\n" +
                "}";

        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up",url_base,uuid);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("x-source", "PHOENIX_DESKTOP")
                .header("Accept","*/*")
                .header("Content-type","application/json")
                .auth().preemptive().basic(uuid,token)
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();

        JsonPath jsonResponse =response.jsonPath();
        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("ad_id"));
        assertTrue(body_response.contains("subject"));
        assertTrue(body_response.contains("category"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 5000);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

        //pm.environment.set("ad_id",pm.response.json().data.ad.ad_id)
         String local_ad_id = jsonResponse.get("data.ad.ad_id");
         ad_id = local_ad_id;

         System.out.println("ad_id local: " + ad_id);



    }
    @Test
    @Order(7)
    @DisplayName("Test case: Ver un Anuncio")
    public void ver_una_Direccion(){
        RestAssured.baseURI = String.format("https://%s/ad-stats/v1/public/accounts/%s/ads/%s",url_base, account_id_solo, ad_id);

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("Accept", "*/*")
                .get();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Body bonito del ver un anuncio: " + body_response);

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("list_id"));
        assertTrue(body_response.contains("total"));
        assertTrue(body_response.contains("interested"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

    }
    @Test
    @Order(8)
    @DisplayName("Test case: Editar_un_Anuncio")
    public void editar_un_Anuncio(){
        //Datos random -  fakerjs
        Faker faker = new Faker();
        String name = faker.name().fullName();
        //String fname = faker.name().firstName();
        //String lastname = faker.name().lastName();

        String randomData = faker.country().name();

        String phone = faker.phoneNumber().cellPhone();

        System.out.println("Nombre completo: " + name);

        String body_request= "{\n" +
                "    \"category\": \"8143\",\n" +
                "    \"subject\": \"Organizamos tu evento y mas\",\n" +
                "    \"body\": \"trabajamos todo tipo de eventos, desde bautizos hasta bodas. Pregunte sin compromiso. Hacemos Cotizaciones\",\n" +
                "    \"region\": \"5\",\n" +
                "    \"municipality\": \"51\",\n" +
                "    \"area\": \"36611\",\n" +
                "    \"price\": \"20000\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"76013183\"\n" +
                "}";

        String token = obtener_Token();
        System.out.println(("Token en la prueba Crear anuncio " + token));

        RestAssured.baseURI=String.format("https://%s/v2/accounts/%s/up/%s",url_base,uuid,ad_id);

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("x-source","PHOENIX_DESKTOP")
                .contentType("application/json")
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .body(body_request)
                .put();

        String body_response = response.getBody().prettyPrint();

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());

        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("ad_id"));
        assertTrue(body_response.contains("subject"));
        assertTrue(body_response.contains("label"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 5000);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(9)
    @DisplayName("Test case: Ver un Anuncio despues de editarlo")
    public void ver_anuncio_Actualizado(){
        RestAssured.baseURI = String.format("https://%s/ad-stats/v1/public/accounts/%s/ads/%s",url_base, account_id_solo, ad_id);

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("Accept", "*/*")
                .get();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Body bonito del ver un anuncio: " + body_response);

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("list_id"));
        assertTrue(body_response.contains("total"));
        assertTrue(body_response.contains("interested"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

    }
    @Test
    @Order(10)
    @DisplayName("Test case: Crear una direccion")
    public void crear_una_Direccion(){

        String token = obtener_Token();

        RestAssured.baseURI = String.format("https://%s/addresses/v1/create",url_base);

        Response response =  given()
                .log().all()
                .filter(new AllureRestAssured())
                .formParam("contact","Agente de ventas")
                .formParam("phone","8979876576")
                .formParam("rfc","XAXX010101000")
                .formParam("zipCode","11011")
                .formParam("exteriorInfo","Hidalgo 897")
                .formParam("interiorInfo","3")
                .formParam("region","11")
                .formParam("municipality","300")
                .formParam("area","8094")
                .formParam("alias","Oficina")
                .contentType("application/x-www-form-urlencoded")
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,token)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Body bonito:"+body_response);

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(201,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("addressID"));


        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

        JsonPath jsonResponse = response.jsonPath();
        addressID = jsonResponse.get("addressID");


    }
    @Test
    @Order(11)
    @DisplayName("Test case: Obtener Direcciones del Usuario")
    public void obtener_direcciones_del_Usuario(){
        String token = obtener_Token();

        RestAssured.baseURI = String.format("https://%s/addresses/v1/get",url_base);

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .get();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Body bonito:"+body_response);

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("exteriorInfo"));
        assertTrue(body_response.contains("region"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(12)
    @DisplayName("Test case: Editar Direccion del Usuario")
    public void editar_direccion_del_Usuario(){
        String token = obtener_Token();
        Faker faker = new Faker();
        String fullName = faker.name().fullName();
        String interiorInfo = faker.random().nextInt(1,1000).toString();
        RestAssured.baseURI = String.format("https://%s/addresses/v1/modify/%s",url_base,addressID);

        Response response =  given()
                .log().all()
                .filter(new AllureRestAssured())
                .formParam("contact",fullName)
                .formParam("phone","8979876576")
                .formParam("rfc","XAXX010101000")
                .formParam("zipCode","11011")
                .formParam("exteriorInfo","Hidalgo 897")
                .formParam("interiorInfo",interiorInfo)
                .formParam("region","11")
                .formParam("municipality","300")
                .formParam("area","8094")
                .formParam("alias","Oficina")
                .contentType("application/x-www-form-urlencoded")
                .header("Accept","application/json, text/plain, */*")
                .auth().preemptive().basic(uuid,token)
                .put();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Body bonito:"+body_response);

        System.out.println("status response: " + response.getStatusCode());
        assertEquals(200, response.getStatusCode());

        assertNotNull(body_response);

        assertTrue(body_response.contains(addressID));
        assertTrue(body_response.contains("modified correctly"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 3500);
    }

    @Test
    @Order(13)
    @DisplayName("Test case: Eliminar Direccion del Usuario")
    public void eliminar_direccion_del_Usuario(){
        String token = obtener_Token();
        RestAssured.baseURI = String.format("https://%s/addresses/v1/delete/%s",url_base,addressID);

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/x-www-form-urlencoded")
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .delete();

        String body_response = response.getBody().prettyPrint();

        System.out.println("status response: " + response.getStatusCode());
        assertEquals(200, response.getStatusCode());

        assertNotNull(body_response);

        assertTrue(body_response.contains(addressID));
        assertTrue(body_response.contains("deleted correctly"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 3500);
    }

    @Test
    @Order(14)
    @DisplayName("Test case: Eliminar Anuncio")
    public void eliminar_Anuncio(){

        String body_request= "{\n" +
                "    \"delete_reason\": {\n" +
                "        \"code\": \"2\"\n" +
                "    }\n" +
                "}";
        String token = obtener_Token();
        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s/klfst/%s",url_base,account_id,ad_id);

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .header("Accept","*/*")
                .header("Authorization","tag:scmcoord.com,2013:api "+token)
                .body(body_request)
                .delete();
        String body_response = response.getBody().prettyPrint();

        System.out.println("status response: " + response.getStatusCode());
        assertEquals(403, response.getStatusCode());

        assertNotNull(body_response);

        assertTrue(body_response.contains("error"));
        assertTrue(body_response.contains("ERROR_AD_ALREADY_DELETED"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 3500);

        System.out.println("AQUI ESTA EL RESPONSE: " + body_response);

    }

    @Test
    @Order(15)
    @DisplayName("Test case: Consultar si el anuncio fue publicado")
    public void consultar_anuncio_Publicado(){
        RestAssured.baseURI = String.format("https://%s/urls/v1/public/ad-view", url_base);
        String body_request = "{\n" +
                "    \"ids\": [\n" +
                "        \""+ad_id+"\"\n" +
                "    ]\n" +
                "}";
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .queryParam("lang","es")
                .headers("Accept", "application/json,text/plain,*/*")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        System.out.println("status response: " + response.getStatusCode());
        assertEquals(200, response.getStatusCode());

        assertNotNull(body_response);

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

        assertTrue(body_response.contains(ad_id));;
    }


}
