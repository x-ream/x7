package io.xream.x7.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author sim
 */
public class LocalDateTimeToLongSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (localDateTime != null) {
            long time = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            jsonGenerator.writeNumber(time);
        }
    }
}