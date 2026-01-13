package com.nlu.store.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nlu.store.core.cache.Cache;
import com.nlu.store.core.cache.InMemoryCache;
import com.nlu.store.core.config.PropertySource;
import com.nlu.store.core.config.ULIDDeserializer;
import com.nlu.store.core.config.ULIDSerializer;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.email.EmailSender;
import com.nlu.store.core.email.SmtpEmailSender;
import com.nlu.store.core.jawire.InternalResourceJawireViewResolver;
import com.nlu.store.core.jawire.JawireViewResolver;
import com.nlu.store.core.web.DefaultPathExtractor;
import com.nlu.store.core.web.InternalResourceViewResolver;
import com.nlu.store.core.web.PathExtractor;
import com.nlu.store.core.web.ViewResolver;
import com.nlu.store.core.web.bind.*;
import com.nlu.store.modules.user.impl.BCryptPasswordEncoder;
import com.nlu.store.modules.user.impl.SecureTokenGenerator;
import com.nlu.store.modules.user.services.PasswordEncoder;
import com.nlu.store.modules.user.services.TokenGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import java.util.List;

@ApplicationScoped
public class WebConfiguration {

    @Produces
    @Singleton
    public ObjectMapper objectMapper() {// Đăng ký module để parse ngày tháng (LocalDate/LocalDateTime) chuẩn Java 8
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ULID.class, new ULIDDeserializer());
        module.addSerializer(ULID.class, new ULIDSerializer());
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }


    @Produces
    @Singleton
    public ViewResolver viewResolver() {
        return new InternalResourceViewResolver("/WEB-INF/views/", ".jsp");
    }

    @Produces
    @Singleton
    public DataBinder dataBinder(BodyParser bodyParser) {
        return new ServletDataBinder(bodyParser);
    }

    @Produces
    @Singleton
    public ConversionService conversionService() {
        ConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(ULID.class, ULID::from);
        return conversionService;
    }

    @Produces
    @Singleton
    public BodyParser bodyParser(ObjectMapper objectMapper, ConversionService conversionService) {
        JsonBodyParser jsonBodyParser = new JsonBodyParser(objectMapper);
        FormBodyParser formBodyParser = new FormBodyParser(conversionService);
        MultipartBodyParser multipartBodyParser = new MultipartBodyParser(conversionService);
        return new CompositeBodyParser(List.of(jsonBodyParser, formBodyParser, multipartBodyParser));
    }

    @Produces
    @ApplicationScoped
    public JawireViewResolver jawireViewResolver() {
        return new InternalResourceJawireViewResolver("/WEB-INF/components/", ".jsp");
    }

    @Produces
    @ApplicationScoped
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Produces
    @ApplicationScoped
    public TokenGenerator tokenGenerator() {
        return new SecureTokenGenerator();
    }

    @Produces
    @ApplicationScoped
    public Cache cache() {
        return new InMemoryCache();
    }

    @Produces
    @ApplicationScoped
    public PathExtractor pathExtractor() {
        return new DefaultPathExtractor();
    }

    @Produces
    @ApplicationScoped
    public EmailSender emailSender(PropertySource propertySource) {
        String host = propertySource.getProperty("mail.host");
        String port = propertySource.getProperty("mail.port");
        String username = propertySource.getProperty("mail.username");
        String password = propertySource.getProperty("mail.password");

        return new SmtpEmailSender(host, port, username, password);
    }
}
