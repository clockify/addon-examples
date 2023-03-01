package com.cake.clockify.helloworld;

import com.cake.clockify.addonsdk.shared.utils.Utils;
import com.cake.clockify.helloworld.model.Installation;
import com.cake.clockify.helloworld.model.Setting;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.List;

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

    public void updateSettings(String addonId, List<Setting> settings) {
        Document update = new Document("$set", new Document("settings", settings));

        client.getDatabase(mongoDatabase)
                .getCollection(COLLECTION_INSTALLATIONS)
                .updateOne(new Document("addonId", addonId), update);
    }

    public Installation getInstallation(String workspaceId) {
        Document filter = new Document()
                .append("workspaceId", workspaceId);

        Document result = client.getDatabase(mongoDatabase)
                .getCollection(COLLECTION_INSTALLATIONS)
                .find(filter)
                .sort(Sorts.descending("_id"))
                .first();

        if (result == null) {
            return null;
        }

        return Utils.GSON.fromJson(result.toJson(), Installation.class);
    }
}
