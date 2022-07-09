package com.github.rephrasing.cloud.cache;

import com.github.rephrasing.cloud.CloudDatabaseAPI;
import com.github.rephrasing.cloud.bson.ICloudBson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import lombok.SneakyThrows;
import org.bson.Document;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CloudCacheHolder<Data extends ICloudBson> {

    private final List<Data> cache = new ArrayList<>();

    public List<Data> getCachedData() {
        return cache;
    }

    @SneakyThrows
    public void fetchFromCollection(MongoCollection<Document> data, Class<Data> clazz) {
        Method deserializeMethod;
        try {
            deserializeMethod = clazz.getMethod("deserialize", Document.class);
        } catch (NoSuchMethodException e) {
            CloudDatabaseAPI.getInstance().getPlugin().getLogger().severe("Tried to fetch from database collection but did not find the static deserialize method in class " + clazz.getName());
            return;
        }

        for (Document doc : data.find()) {
            deserializeMethod.setAccessible(true);
            Data result = (Data) deserializeMethod.invoke(null, doc);
            cache.add(result);
        }
    }

    /**
     * Pushes cached data to a {@link MongoCollection}
     * @param data the {@link MongoCollection}
     * @return the newly pushed/replaced data amount, 0 if no new data was pushed.
     */
    public int pushToCollection(MongoCollection<Document> data) {
        int newPushedData = 0;
        for (Data object : cache) {
            Document doc = new Document(object.serialize());
            doc.append("cloud_id", object.getIdentifierObject());
            DeleteResult result = data.deleteOne(Filters.eq("cloud_id", object.getIdentifierObject()));
            if (result.getDeletedCount() < 1) newPushedData++;
            data.insertOne(doc);
        }
        return newPushedData;
    }
}
