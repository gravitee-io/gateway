/*
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

/**
 * Membership can be created for existing users, or for users provided by external identity provider. Thus, either userId or externalReference are required.
 */
export interface CreateApiMember {
  /**
   * Technical identifier for existing user.
   */
  userId?: string;
  /**
   * User's reference for user provided from an identity provider.
   */
  externalReference?: string;
  /**
   * The name of the role
   */
  roleName: string;
}
