package org.superbiz.moviefun.blobstore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

public class ServiceCredentials {
    private String vcapServices;

    public ServiceCredentials(String vcapServices) {
        this.vcapServices = vcapServices;
    }

    public String getCredentials(String serviceName, String serviceType, String credentialKey) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeRoot;
        JsonNode jsonNodeServices;
        try {
            jsonNodeRoot = objectMapper.readTree(vcapServices);
        } catch (IOException ioEx) {
            throw new IllegalStateException("No VCAP_SERVICES found", ioEx);
        }
        jsonNodeServices = jsonNodeRoot.path(serviceType);
        for (JsonNode jsonNodeService : jsonNodeServices) {
            if (Objects.equals(jsonNodeService.get("name").asText(), serviceName)) {
                return jsonNodeService.get("credentials").get(credentialKey).asText();
            }
        }
        throw new IllegalStateException("No " + serviceName + " found in VCAP_SERVICES");
    }


}
