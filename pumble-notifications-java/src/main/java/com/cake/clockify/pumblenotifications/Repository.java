package com.cake.clockify.pumblenotifications;

import com.cake.clockify.pumblenotifications.model.Installation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

public class Repository {
    private static final String COLLECTION_INSTALLATIONS = "installations";
    private final String mongoDatabase = System.getenv("MONGO_DATABASE");
    private final MongoClient client = MongoClients.create(System.getenv("MONGO_URI"));

    private final Gson gson = new Gson();

    public void persistInstallation(Installation installation) {
        Document document = Document.parse(gson.toJson(installation));

        client.getDatabase(mongoDatabase)
                .getCollection(COLLECTION_INSTALLATIONS)
                .insertOne(document);
    }

    public void removeInstallation(Installation installation) {
        Document document = new Document("addonId", installation.addonId());

        client.getDatabase(mongoDatabase)
                .getCollection(COLLECTION_INSTALLATIONS)
                .deleteOne(document);
    }

    public Installation getInstallation(String addonId) {
        Document filter = new Document().append("addonId", addonId);

        Document result = client.getDatabase(mongoDatabase)
                .getCollection(COLLECTION_INSTALLATIONS)
                .find(filter)
                .first();

        if (result == null) {
            return null;
        }

        return gson.fromJson(result.toJson(), Installation.class);
    }
}
