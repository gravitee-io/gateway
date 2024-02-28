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
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs/operators';

import { MetadataSaveServices } from '../../../../components/gio-metadata/gio-metadata.component';
import { ApiService } from '../../../../services-ngx/api.service';
import { ApiMetadataV2Service } from '../../../../services-ngx/api-metadata-v2.service';
import { Metadata } from '../../../../entities/metadata/metadata';

@Component({
  selector: 'gio-api-metadata-list',
  templateUrl: './gio-api-metadata-list.component.html',
  styleUrls: ['./gio-api-metadata-list.component.scss'],
})
export class GioApiMetadataListComponent implements OnInit {
  metadataSaveServices: MetadataSaveServices;
  description: string;

  constructor(
    private readonly apiService: ApiService,
    private readonly apiMetadataV2Service: ApiMetadataV2Service,
    private readonly activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit() {
    this.metadataSaveServices = {
      type: 'API',
      paginate: true,
      list: (searchMetadata) =>
        this.apiMetadataV2Service.search(this.activatedRoute.snapshot.params.apiId, searchMetadata).pipe(
          map((resp) => ({
            data: resp.data?.map((metadata) => ({ ...metadata } as Metadata)),
            totalResults: resp.pagination?.totalCount ?? 0,
          })),
        ),
      create: (newMetadata) => this.apiService.createMetadata(this.activatedRoute.snapshot.params.apiId, newMetadata),
      update: (updateMetadata) => this.apiService.updateMetadata(this.activatedRoute.snapshot.params.apiId, updateMetadata),
      delete: (metadataKey) => this.apiService.deleteMetadata(this.activatedRoute.snapshot.params.apiId, metadataKey),
    };
    this.description = `Set metadata information on the API that can be easily accessed through Markdown templating`;
  }
}
