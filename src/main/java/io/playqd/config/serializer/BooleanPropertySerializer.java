package io.playqd.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.beans.property.BooleanProperty;

import java.io.IOException;

public class BooleanPropertySerializer extends JsonSerializer<BooleanProperty> {

    @Override
    public void serialize(BooleanProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeBoolean(value.get());
    }
}
