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

package wolox.training.services.authentication;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Class containing constants to be reused by the jwt token authentication system.
 */
/* package */ class Constants {

    /**
     * Private constructor to avoid instantiation.
     */
    private Constants() {
    }

    /**
     * Claims name for roles in a jwt token.
     */
    /* package */ static final String ROLES_CLAIM_NAME = "grants";

    /**
     * Signature algorithm used to sign jwt tokens. Change {@link #KEY_FACTORY_ALGORITHM} if this is
     * changed.
     */
    /* package */ static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.RS512;

    /**
     * {@link String} used to indicate the algorithm to be used when passed to a {@link
     * java.security.KeyFactory}. Note that if {@link #SIGNATURE_ALGORITHM} is changed, this must be
     * changed to.
     */
    /* package */ static final String KEY_FACTORY_ALGORITHM = "RSA";
}
