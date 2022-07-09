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
            Validate.isTrue(isPrimitiveOrWrapper, "Identifier Object of Class " + clazz.getName() + " is not of type Primitive or Wrapper. API cannot run.");

            try {
                Method deserializeMethod = clazz.getMethod("deserialize", Document.class);
                deserializeMethod.invoke(null, new Document());
            } catch (NoSuchMethodException e) {
                CloudDatabaseAPI.getInstance().getPlugin().getLogger().severe("did not find the static deserialize method in class " + clazz.getName() + " API cannot run.");
                return;
            } catch (IllegalAccessException e) {
                CloudDatabaseAPI.getInstance().getPlugin().getLogger().severe("deserialize method in class " + clazz.getName() + " is not accessible! API cannot run.");
                return;
            }
        }
        initiated = true;
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
