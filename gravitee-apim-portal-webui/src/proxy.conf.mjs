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

const env = process.env.BACKEND_ENV;
const target = `${env ? `https://${env}` : 'http://localhost:8083'}`
export default {
  '/portal/ui/bootstrap': {
    target,
    secure: false,
    changeOrigin: true,
    onProxyRes: function(proxyRes, req, res) {
      // Replace bootstrap full baseURL to use proxy configured bellow (/portal)
      // This allows to bypass cors security with cloud env
      var body = new Buffer('');
      proxyRes.on('data', function(data) {
        body = Buffer.concat([body, data]);
      });
      proxyRes.on('end', function() {
        body = body.toString();
        res.end(body.replace(`${target}/`, ""));
      });
    },
    selfHandleResponse: true,
    logLevel: 'debug',
  },
  '/portal': {
    target,
    secure: false,
    changeOrigin: true,
    onProxyReq: function (proxyReq, req, res) {
      proxyReq.setHeader('origin', target.replace("-api", "-portal"));
    },
    logLevel: 'debug',
  },
};
