package com.officemanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class JerseyConfig extends JacksonJaxbJsonProvider {
    public JerseyConfig() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        setMapper(mapper);
    }
} 