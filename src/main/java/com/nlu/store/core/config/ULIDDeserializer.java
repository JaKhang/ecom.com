package com.nlu.store.core.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nlu.store.core.data.ULID;

import java.io.IOException;

public class ULIDDeserializer extends JsonDeserializer<ULID>{
    @Override
    public ULID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String ulidString = p.getText();
        return ULID.from(ulidString); // Assuming Ulid has a static method from(String)
    }



}