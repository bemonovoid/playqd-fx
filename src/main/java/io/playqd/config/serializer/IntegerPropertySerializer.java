package io.playqd.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.beans.property.IntegerProperty;

import java.io.IOException;

public class IntegerPropertySerializer extends JsonSerializer<IntegerProperty> {

    @Override
    public void serialize(IntegerProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.get());
    }
}
