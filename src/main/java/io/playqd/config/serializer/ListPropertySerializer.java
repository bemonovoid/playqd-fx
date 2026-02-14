package io.playqd.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;

public class ListPropertySerializer<T> extends JsonSerializer<ObservableList<T>> {

    @Override
    public void serialize(ObservableList<T> value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeObject(new ArrayList<>(value));
    }

    public static final class BrowserState extends ListPropertySerializer<BrowserState> {

    }

    public static final class TabDetails extends ListPropertySerializer<TabDetails> {

    }
}
