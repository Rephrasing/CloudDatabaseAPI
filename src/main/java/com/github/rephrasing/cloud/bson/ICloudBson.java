package com.github.rephrasing.cloud.bson;

import java.util.Map;

/**
 * @implNote This class must have an empty public constructor
 * @implNote {@link ICloudBson#getIdentifierObject()} MUST BE of Type primitive or wrapper (String, Integer, Long, etc)
 * @implNote This class must implement a static deserialize method.
 */
public interface ICloudBson {

    Object getIdentifierObject();
    Map<String, Object> serialize();

}
