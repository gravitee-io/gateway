/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.definition.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.definition.model.endpoint.EndpointStatusListener;
import io.gravitee.definition.model.services.healthcheck.EndpointHealthCheckService;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Endpoint implements Serializable {

    private static final String DEFAULT_TYPE = "http";

    private final Set<EndpointStatusListener> listeners = new HashSet<>();

    public static final int DEFAULT_WEIGHT = 1;

    @JsonProperty("name")
    private String name;

    @JsonProperty("target")
    private String target;

    @JsonProperty("weight")
    @Builder.Default
    private int weight = DEFAULT_WEIGHT;

    @JsonProperty("backup")
    private boolean backup;

    @JsonIgnore
    @Builder.Default
    private Status status = Status.UP;

    @JsonProperty("tenants")
    private List<String> tenants; // TODO was not serialized

    @JsonProperty("type")
    @Builder.Default
    private String type = DEFAULT_TYPE;

    @JsonProperty("inherit")
    private Boolean inherit;

    @JsonProperty("healthcheck")
    private EndpointHealthCheckService healthCheck;

    @JsonIgnore
    private transient String configuration;

    public Endpoint(String type, String name, String target) {
        this.type = type != null ? type : DEFAULT_TYPE;
        this.name = name;
        this.target = target;
        this.status = Status.UP;
        this.weight = DEFAULT_WEIGHT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isBackup() {
        return backup;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * This method should only be used by listeners to update the status of the endpoint.
     * Indeed, if listeners call updateStatus, it will end in an infinite loop
     * @param status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * This method is used by the HealtchCheck service of the gateway to update the status and notify listeners (actually io.gravitee.gateway.core.endpoint.ManagedEndpoint).
     * @param status
     */
    public void updateStatus(Status status) {
        this.status = status;
        listeners.forEach(endpointStatusListener -> endpointStatusListener.onStatusChanged(status));
    }

    public List<String> getTenants() {
        return tenants;
    }

    public void setTenants(List<String> tenants) {
        this.tenants = tenants;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Boolean getInherit() {
        return inherit;
    }

    public void setInherit(Boolean inherit) {
        this.inherit = inherit;
    }

    public void addEndpointStatusListener(EndpointStatusListener listener) {
        listeners.add(listener);
    }

    @JsonIgnore
    // Need to add @JsonIgnore too because transient + @JsonIgnore on the property does not work anymore in 2.15
    // See https://github.com/FasterXML/jackson-databind/issues/3874#issuecomment-1505211857
    // Before 2.15, it was an unexpected feature.
    public String getConfiguration() {
        return configuration;
    }

    @JsonIgnore
    // Need to add @JsonIgnore too because transient + @JsonIgnore on the property does not work anymore in 2.15
    // See https://github.com/FasterXML/jackson-databind/issues/3874#issuecomment-1505211857
    // Before 2.15, it was an unexpected feature.
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public EndpointHealthCheckService getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(EndpointHealthCheckService healthCheck) {
        this.healthCheck = healthCheck;
    }

    @JsonIgnore
    public Set<EndpointStatusListener> getEndpointStatusListeners() {
        return this.listeners;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Endpoint endpoint = (Endpoint) o;

        return name.equals(endpoint.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public enum Status {
        UP(3),
        DOWN(0),
        TRANSITIONALLY_DOWN(1),
        TRANSITIONALLY_UP(2);

        private final int code;

        Status(int code) {
            this.code = code;
        }

        public int code() {
            return this.code;
        }

        public boolean isDown() {
            return this == DOWN || this == TRANSITIONALLY_UP;
        }
    }
}
