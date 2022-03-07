/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class AbstractPolicyResolver implements PolicyResolver {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PolicyManager policyManager;

    public AbstractPolicyResolver(final PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    protected Policy create(StreamType streamType, String policy, String configuration, String place) {
        return policyManager.create(streamType, policy, configuration, place);
    }
}
