package com.nlu.store.core.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlu.store.core.web.bind.BodyParser;
import com.nlu.store.core.web.bind.ConversionService;
import com.nlu.store.core.web.bind.DataBinder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class WebInfrastructure {

    @Inject
    private ViewResolver viewResolver;
    @Inject

    private ObjectMapper objectMapper;
    @Inject

    private DataBinder dataBinder;
    @Inject
    private ConversionService conversionService;
    @Inject
    private BodyParser bodyParser;


    public WebInfrastructure() {
    }

    public WebInfrastructure(ViewResolver viewResolver, ObjectMapper objectMapper, DataBinder dataBinder, ConversionService conversionService, BodyParser bodyParser) {
        this.viewResolver = viewResolver;
        this.objectMapper = objectMapper;
        this.dataBinder = dataBinder;
        this.conversionService = conversionService;
        this.bodyParser = bodyParser;
    }


    // --- Fluent Accessors (No 'get' prefix) ---

    public ViewResolver viewResolver() {
        return viewResolver;
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }


    public DataBinder dataBinder() {
        return dataBinder;
    }

    public ConversionService conversionService() {
        return conversionService;
    }

    public BodyParser bodyParser() {
        return bodyParser;
    }
}
