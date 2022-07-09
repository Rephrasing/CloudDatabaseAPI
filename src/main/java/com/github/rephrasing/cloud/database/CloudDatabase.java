package com.github.rephrasing.cloud.database;

import com.github.rephrasing.cloud.CloudDatabaseAPI;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;

public class CloudDatabase {

    private String uri;

    private MongoClient client;
    private CodecRegistry[] codecRegistries;

    public CloudDatabase() {
        Validate.isTrue(CloudDatabaseAPI.isInitiated(), "Attempted to construct CloudDatabase object from CloudDatabase but API is not initiated.");
    }

    public void applyConnectionURI(String uri) {
        Validate.isTrue(client == null, "Attempted to apply connection URI after connecting to MongoDB!");
        this.uri = uri;
    }

    public void applyCodecRegistries(CodecRegistry... registries) {
        Validate.isTrue(client == null, "Attempted to add codec registries after connecting to MongoDB!");
        this.codecRegistries = registries;
    }

    public void connect() {
        Validate.isTrue(client == null, "Attempted connection to MongoDB but found an existing connection");
        Validate.notNull(uri, "Attempted connect to MongoDB but did not find a connection URI");

        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();
        settingsBuilder.applyConnectionString(new ConnectionString(uri));

        if (codecRegistries != null) {
            if (codecRegistries.length > 0) {
                for (CodecRegistry registry : codecRegistries) {
                    settingsBuilder.codecRegistry(registry);
                }
            }
        }

        this.client = MongoClients.create(settingsBuilder.build());
    }

    public void disconnect() {
        Validate.notNull(client, "Attempted to disconnect from MongoDB but did not find a connection.");
        client.close();
    }

    public MongoClient getClient() {
        Validate.notNull(client, "Attempted to use MongoClient but did not find a MongoDB connection.");
        return client;
    }

    public MongoDatabase getDatabase(String name) {
        Validate.notNull(client, "Attempted to fetch a MongoDatabase but did not find a MongoDB connection.");
        return getClient().getDatabase(name);
    }

    public MongoCollection<Document> getCollection(String databaseName, String collectionName) {
        Validate.notNull(client, "Attempted to fetch a MongoCollection but did not find a MongoDB connection.");
        return getDatabase(databaseName).getCollection(collectionName, Document.class);
    }


}
