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

const AlertNotificationsComponent: ng.IComponentOptions = {
  bindings: {
    alert: '<',
  },
  require: {
    parent: '^alertComponent',
  },
  template: require('./alert-notifications.html'),
  /* @ngInject */
  controller: function () {
    this.addNotification = () => {
      if (this.alert.notifications === undefined) {
        this.alert.notifications = [];
      }

      this.alert.notifications.push({});
    };

    this.removeNotification = (idx: number) => {
      this.alert.notifications.splice(idx, 1);
      this.parent.formAlert.$setDirty();
    };
  },
};

export default AlertNotificationsComponent;
