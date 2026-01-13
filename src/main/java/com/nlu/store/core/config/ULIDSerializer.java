package com.nlu.store.core.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nlu.store.core.data.ULID;

import java.io.IOException;

public class ULIDSerializer extends JsonSerializer<ULID> {

    @Override
    public void serialize(ULID value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }


        gen.writeString(value.toString());
    }
}