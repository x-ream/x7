package io.xream.x7.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author sim
 */
public class LongToLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        long time = jsonParser.getValueAsLong();
        if (time > 0) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        } else {
            return null;
        }
    }
}

