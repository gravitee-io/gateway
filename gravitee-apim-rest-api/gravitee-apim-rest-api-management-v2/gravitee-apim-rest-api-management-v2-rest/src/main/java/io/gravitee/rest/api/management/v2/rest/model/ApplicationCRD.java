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
package io.gravitee.rest.api.management.v2.rest.model;

import io.gravitee.kubernetes.mapper.CustomResource;
import io.gravitee.kubernetes.mapper.GroupVersionKind;
import io.gravitee.kubernetes.mapper.ObjectMeta;

/**
 * @author Kamiel Ahmadpour (kamiel.ahmadpour at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ApplicationCRD extends CustomResource<ApplicationCRDSpec> {

    public ApplicationCRD(ApplicationCRDSpec spec) {
        super(GroupVersionKind.GIO_V1_ALPHA_1_API_V4_DEFINITION, new ObjectMeta(spec.getName()), spec);
    }
}
