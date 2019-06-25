/*
 * Copyright 2018-2019 BellotApps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wolox.training.web.security;

import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Class containing constants to be used by the authentication module.
 */
/* package */ class Constants {


    /**
     * Private constructor to avoid instantiation.
     */
    private Constants() {
    }


    /**
     * Anonymous.
     */
    private static final String ANONYMOUS = "ANONYMOUS";

    /**
     * Indicates which HTTP header includes the authentication credentials.
     */
    /* package */ static final String AUTHENTICATION_HEADER = "Authorization";

    /**
     * Indicates the authentication scheme supported by the system.
     */
    /* package */ static final String AUTHENTICATION_SCHEME = "Bearer";

    /**
     * An {@link AnonymousAuthenticationToken} used when authentication fails.
     */
    /* package */ static final AnonymousAuthenticationToken ANONYMOUS_AUTHENTICATION_TOKEN =
        new AnonymousAuthenticationToken(
            ANONYMOUS,
            ANONYMOUS,
            List.of(new SimpleGrantedAuthority(ANONYMOUS))
        );
}
