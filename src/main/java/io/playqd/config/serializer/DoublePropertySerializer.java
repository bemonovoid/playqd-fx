package io.playqd.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.beans.property.DoubleProperty;

import java.io.IOException;

public class DoublePropertySerializer extends JsonSerializer<DoubleProperty> {

    @Override
    public void serialize(DoubleProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.get());
    }
}
