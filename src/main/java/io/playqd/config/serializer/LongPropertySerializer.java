package io.playqd.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.beans.property.LongProperty;

import java.io.IOException;

public class LongPropertySerializer extends JsonSerializer<LongProperty> {

    @Override
    public void serialize(LongProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.get());
    }
}
