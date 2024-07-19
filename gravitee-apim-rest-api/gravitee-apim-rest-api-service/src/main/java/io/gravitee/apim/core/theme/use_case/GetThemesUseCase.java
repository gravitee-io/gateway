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
package io.gravitee.apim.core.theme.use_case;

import io.gravitee.apim.core.UseCase;
import io.gravitee.apim.core.theme.model.Theme;
import io.gravitee.apim.core.theme.model.ThemeSearchCriteria;
import io.gravitee.apim.core.theme.model.ThemeType;
import io.gravitee.apim.core.theme.query_service.ThemeQueryService;
import io.gravitee.common.data.domain.Page;
import io.gravitee.rest.api.model.common.PageableImpl;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class GetThemesUseCase {

    private final ThemeQueryService themeQueryService;

    public Output execute(Input input) {
        return new Output(
            themeQueryService.searchByCriteria(
                ThemeSearchCriteria.builder().enabled(input.enabled()).type(input.type()).build(),
                new PageableImpl(input.page(), input.size())
            )
        );
    }

    @Builder
    public record Input(ThemeType type, Boolean enabled, int page, int size) {}

    public record Output(Page<Theme> result) {}
}
