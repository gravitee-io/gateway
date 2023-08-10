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
import { TransitionService } from '@uirouter/angularjs';
import { GioLicenseService } from '@gravitee/ui-particles-angular';

export interface GioLicenseRouterData {
  license: {
    feature: string;
  };
  redirect: string;
}

export function licenseGuard(transitionService: TransitionService) {
  // Matches if the destination state's data property has a truthy 'requireLicense' property
  let licenseRouterData: GioLicenseRouterData = null;
  const requireLicense = {
    to: (state) => {
      if (state?.data?.requireLicense != null) {
        licenseRouterData = state.data.requireLicense;
        return true;
      }
      return false;
    },
  };

  const redirectTo = async (transition) => {
    const licenseService: GioLicenseService = transition.injector().get(GioLicenseService);
    const $state = transition.router.stateService;

    const notAllowed = await licenseService.isMissingFeature$(licenseRouterData.license).toPromise();
    if (notAllowed) {
      return $state.target(licenseRouterData.redirect);
    }
  };

  // Register the "requires license" hook with the TransitionsService
  transitionService.onBefore(requireLicense, redirectTo, { priority: 10 });
}
