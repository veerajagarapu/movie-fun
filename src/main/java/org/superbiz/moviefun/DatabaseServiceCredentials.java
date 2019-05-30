package org.superbiz.moviefun;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatabaseServiceCredentials {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String vcapServicesJson;
    private static final TypeReference<Map<String, List<VcapService>>> jsonType = new TypeReference<Map<String, List<VcapService>>>() {
    };
    Logger logger = LoggerFactory.getLogger(getClass());
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    static class VcapService {
        String name;
        Map<String, Object> credentials;

        void setName(String name) {
            this.name = name;
        }

        void setCredentials(Map<String, Object> credentials) {
            this.credentials = credentials;
        }
    }

    public DatabaseServiceCredentials (String vcapServicesJson){
        this.vcapServicesJson = vcapServicesJson;
    }

    public String jdbcUrl(String name) {
        Map<String, List<VcapService>> vcapServices;
        try {
            vcapServices = objectMapper.readValue(vcapServicesJson, jsonType);
            return vcapServices
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(service -> service.name.equalsIgnoreCase(name))
                    .findFirst()
                    .map(service -> service.credentials)
                    .flatMap(credentials -> Optional.ofNullable((String) credentials.get("jdbcUrl")))
                    .orElseThrow(() -> new IllegalStateException("No " + name + " found in VCAP_SERVICES"));
        } catch (IOException e) {
            throw new IllegalStateException("No VCAP_SERVICES found", e);
        }
    }

}
