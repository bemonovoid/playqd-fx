package io.playqd.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.beans.property.ObjectProperty;

import java.io.IOException;

public class ObjectPropertySerializer<T> extends JsonSerializer<ObjectProperty<T>> {

    @Override
    public void serialize(ObjectProperty<T> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.get());
    }
}
