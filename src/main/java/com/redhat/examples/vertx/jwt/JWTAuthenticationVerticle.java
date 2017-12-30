package com.redhat.examples.vertx.jwt;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;

public class JWTAuthenticationVerticle extends AbstractVerticle{

    @Override
    public void start(Future<Void> future) {

        // Configure authentication with JWT
        JWTAuth jwtAuth = JWTAuth.create(vertx, new JsonObject().put("keyStore", new JsonObject()
                .put("type", "jceks")
                .put("path", "keystore.jceks")
                .put("password", "secret")));

        // create a apiRouter to handle the API
        Router baseRouter = Router.router(vertx);

        baseRouter.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain").end(VerticleMessages.PUBLIC_MESSAGE);
        });

/*
        baseRouter.route("/protected").handler(JWTAuthHandler.create(jwtAuth));
        baseRouter.route("/protected").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain").end(VerticleMessages.PROTECTED_MESSAGE);
        });
*/
        vertx.createHttpServer(new HttpServerOptions()
                .setSsl(true)
                .setKeyStoreOptions(new JksOptions().setPath("server-keystore.jks").setPassword("secret")))
                .requestHandler(baseRouter::accept)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        future.complete();
                    }else {
                        future.fail(result.cause());
                    }
                });

    }
}
