package com.github.rephrasing.cloud;

import com.github.rephrasing.cloud.bson.ICloudBson;
import com.github.rephrasing.cloud.bson.IdentifierClassChecker;
import lombok.SneakyThrows;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

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
