package io.playqd.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.beans.property.StringProperty;

import java.io.IOException;

public class StringPropertySerializer extends JsonSerializer<StringProperty> {

    @Override
    public void serialize(StringProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.get());
    }
}
