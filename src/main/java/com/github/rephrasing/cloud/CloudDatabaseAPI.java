package com.github.rephrasing.cloud;

import com.github.rephrasing.cloud.bson.ICloudBson;
import com.github.rephrasing.cloud.bson.IdentifierClassChecker;
import lombok.SneakyThrows;
import org.apache.commons.lang.Validate;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Logger;

public class CloudDatabaseAPI {

    private static JavaPlugin plugin;
    private static CloudDatabaseAPI instance;

    private static boolean initiated = false;
    private CloudDatabaseAPI() {}

    @SneakyThrows
    public static void init(JavaPlugin plugin) {
        Validate.isTrue(!initiated, "Tried initiating CloudDatabase twice! (Likely conducted outside this plugin)");
        CloudDatabaseAPI.plugin = plugin;
        instance = new CloudDatabaseAPI();


        for (Class<? extends ICloudBson> clazz : new Reflections().getSubTypesOf(ICloudBson.class)) {

            Object classifier = clazz.newInstance().getIdentifierObject();

            boolean isPrimitiveOrWrapper = IdentifierClassChecker.isWrapperOrPrimitive(classifier);
            Validate.isTrue(isPrimitiveOrWrapper, "Identifier Object of \"" + clazz.getSimpleName() + "\" is not of type Primitive or Wrapper. API cannot run.");

            try {
                Method deserializeMethod = clazz.getMethod("deserialize", Document.class);
                ICloudBson object = (ICloudBson) deserializeMethod.invoke(null, new Document());
                Validate.notNull(object, "return type of deserialize method of class \"" + clazz.getSimpleName() + "\" is null!");
            } catch (NoSuchMethodException e) {
                CloudDatabaseAPI.getInstance().getPlugin().getLogger().severe("did not find the static deserialize method in \"" + clazz.getSimpleName() + "\" API cannot run.");
                return;
            } catch (IllegalAccessException e) {
                CloudDatabaseAPI.getInstance().getPlugin().getLogger().severe("deserialize method in \"" + clazz.getSimpleName() + "\" is not accessible! API cannot run.");
                return;
            }
        }
        initiated = true;
        Logger.getLogger("CloudDatabaseAPI").info("Successfully initiated CloudDatabaseAPI by " + plugin.getName());
    }

    public static boolean isInitiated() {
        return initiated;
    }

    public static CloudDatabaseAPI getInstance() {
        return instance;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
