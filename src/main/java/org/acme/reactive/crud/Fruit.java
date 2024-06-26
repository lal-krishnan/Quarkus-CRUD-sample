package org.acme.reactive.crud;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class Fruit {

    public Long id;

    public String name;

    public Fruit() {
        // default constructor.
    }

    public Fruit(String name) {
        this.name = name;
    }

    public Fruit(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Multi<Fruit> findAll(PgPool client) {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Fruit::from);
    }

    private static Fruit from(Row row) {
        return new Fruit(row.getLong("id"), row.getString("name"));
    }

    public Uni<Long> save(PgPool client) {
        if (id == null) {
            return client.preparedQuery("INSERT INTO fruits (name) VALUES ($1) RETURNING id")
                    .execute(Tuple.of(name))
                    .onItem().transform(rowSet -> rowSet.iterator().next().getLong("id"));
        } else {
            return client.preparedQuery("UPDATE fruits SET name = $1 WHERE id = $2")
                    .execute(Tuple.of(name, id))
                    .onItem().transform(rowSet -> id);
        }
    }


    public Uni<Void> delete(PgPool client) {
        if (id == null) {
            return Uni.createFrom().nullItem();
        }
        return client.preparedQuery("DELETE FROM fruits WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem().transformToUni(rowSet -> Uni.createFrom().voidItem());
    }
}