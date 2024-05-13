package org.acme;

import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import io.vertx.mutiny.pgclient.PgPool;
import jakarta.ws.rs.core.MediaType;
import org.acme.reactive.crud.Fruit;

@Path("fruits")
public class FruitResource {

    @Inject
    PgPool client;
    //private final PgPool client;

    public FruitResource(PgPool client) {
        this.client = client;
    }
    @GET
    public Multi<Fruit> get() {
        return Fruit.findAll(client);
    }

    @POST
    public Multi<Fruit> post(Fruit fruit) {
        return fruit.save(client)
                .onItem().transformToMulti(id -> Fruit.findAll(client));
    }
    @PUT
    public Multi<Fruit> update(Fruit fruit) {
        return fruit.save(client)
                .onItem().transformToMulti(id -> Fruit.findAll(client));
    }

    @DELETE
    public Multi<Fruit> deleteFruit(Fruit fruit) {
        return fruit.delete(client)
                .onItem().transformToMulti(id -> Fruit.findAll(client));
    }
}
