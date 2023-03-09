package com.cake.clockify.pumblenotifications;

import com.cake.clockify.addonsdk.shared.utils.Utils;
import com.cake.clockify.pumblenotifications.model.Installation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

public class Repository {
    private static final String COLLECTION_INSTALLATIONS = "installations";
    private final String mongoDatabase = System.getenv("MONGO_DATABASE");
    private final MongoClient client = MongoClients.create(System.getenv("MONGO_URI"));

    public void persistInstallation(Installation installation) {
        Document document = Document.parse(Utils.GSON.toJson(installation));

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

        return Utils.GSON.fromJson(result.toJson(), Installation.class);
    }
}
