package com.redhat.examples.vertx.jwt;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;

@RunWith(VertxUnitRunner.class)
public class JWTTest {

  private Vertx vertx;

  private JWTAuth jwtAuth;

  @Before
  public void setUp(TestContext testContext) {
    vertx = Vertx.vertx();
    vertx.deployVerticle(JWTAuthenticationVerticle.class.getName(), new DeploymentOptions(), testContext.asyncAssertSuccess());

    // Configure authentication with JWT
    jwtAuth = JWTAuth.create(vertx, new JsonObject().put("keyStore", new JsonObject()
      .put("type", "jceks")
      .put("path", "keystore.jceks")
      .put("password", "secret")));

  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess());
  }

  @Test
  public void testJWTAuthentication(TestContext testContext) {
/*
    Async async = testContext.async();

    vertx.createHttpClient(new HttpClientOptions()
      .setSsl(true).setTrustAll(true)).post(8080, "localhost", "/protected")
      .putHeader("content-type", "application/json")
      .putHeader("Authorization: Token ", "")
      .handler(response -> {
        testContext.assertEquals(response.statusCode(), 201);
        testContext.assertTrue(response.headers().get("content-type").contains("application/json"));
        response.bodyHandler(body -> {
          final User userResult = new User(body.toJsonObject().getJsonObject("user"));
          System.out.println(userResult.toJson().toString());
          testContext.assertEquals("username", userResult.getUsername());
          testContext.assertEquals("user@domain.com", userResult.getEmail());
          testContext.assertNotNull("token");
          testContext.assertNotNull("bio");
          testContext.assertNotNull("image");
          testContext.assertNull(userResult.get_id());
          async.complete();
        });
      }).end();
*/
    assertTrue(false);
  }
}
