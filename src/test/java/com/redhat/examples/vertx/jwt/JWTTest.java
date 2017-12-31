package com.redhat.examples.vertx.jwt;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(VertxUnitRunner.class)
public class JWTTest {

  private Vertx vertx;

  private String token;

  @Before
  public void setUp(TestContext testContext) {
    vertx = Vertx.vertx();
    vertx.deployVerticle(JWTAuthenticationVerticle.class.getName(), new DeploymentOptions(), testContext.asyncAssertSuccess());

    // Configure authentication with JWT
    JWTAuth jwtAuth = JWTAuth.create(vertx, new JsonObject().put("keyStore", new JsonObject()
      .put("type", "jceks")
      .put("path", "keystore.jceks")
      .put("password", "secret")));

    token = jwtAuth.generateToken(new JsonObject().put("sub", "jeremy"), new JWTOptions());
  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess());
  }

  @Test
  public void testPublicURL(TestContext testContext) {

    Async async = testContext.async();

    vertx.createHttpClient(new HttpClientOptions()
      .setSsl(true).setTrustAll(true)).post(8080, "localhost", "/")
      .putHeader("content-type", "application/json")
      .putHeader("Authorization: Token ", "")
      .handler(response -> {
        testContext.assertEquals(200, response.statusCode());
        testContext.assertEquals("text/plain", response.headers().get("content-type"));
        response.bodyHandler(body -> {
          assertEquals(VerticleMessages.PUBLIC_MESSAGE, body.toString());
          async.complete();
        });
      }).end();
  }

  @Test
  public void testProtectedURLIsProtected(TestContext testContext) {
    Async async = testContext.async();

    vertx.createHttpClient(new HttpClientOptions()
      .setSsl(true).setTrustAll(true)).post(8080, "localhost", "/protected/")
      .putHeader("content-type", "application/json")
      .putHeader("Authorization: Token ", "")
      .handler(response -> {
        testContext.assertEquals(401, response.statusCode());
        async.complete();
      }).end();
  }

  @Test
  public void testSuccessfulAuthentication(TestContext testContext) {
    Async async = testContext.async();

    vertx.createHttpClient(new HttpClientOptions()
      .setSsl(true).setTrustAll(true)).post(8080, "localhost", "/protected/")
      .putHeader("content-type", "application/json")
      .putHeader("Authorization", "Bearer " + token)
      .handler(response -> {
        testContext.assertEquals(200, response.statusCode());
        response.bodyHandler(body -> {
          assertEquals(VerticleMessages.PROTECTED_MESSAGE, body.toString());
          async.complete();
        });
      }).end();
  }
}
