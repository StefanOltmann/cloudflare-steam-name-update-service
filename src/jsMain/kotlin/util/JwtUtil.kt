/*
 * Cloudflare Steam Name Update Service
 * Copyright (C) 2025 Stefan Oltmann
 * https://github.com/StefanOltmann/cloudflare-steam-name-update-service
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package util

import com.appstractive.jwt.JWT
import com.appstractive.jwt.from
import com.appstractive.jwt.signatures.es256
import com.appstractive.jwt.verify
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

private val JWT_PUBLIC_KEY =
    """
        -----BEGIN PUBLIC KEY-----
        MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEBHeRvXUxh4O12jjfoGNN/naxqfXboyYY7Ma+pkALk2hk9PYPhVoHk5Ar03k94kyhE9v0i1AEVLXN9WuSqE5+eA==
        -----END PUBLIC KEY-----
    """.trimIndent()

/**
 * Validates tokens from https://steam.auth.stefanoltmann.de/login,
 * which runs https://github.com/StefanOltmann/steam-login-helper
 */
internal suspend fun getValidSteamIdHash(token: String): String? {

    try {

        val jwt: JWT = JWT.from(token)

        val verified = jwt.verify {

            es256 { pem(JWT_PUBLIC_KEY) }
        }

        if (!verified)
            return null

        /* Valid tokens contain the "hash" claim. */
        val hash = jwt.claims["hash"]?.jsonPrimitive?.contentOrNull
            ?: return null

        return hash

    } catch (ex: Exception) {

        /*
         * These are most likely errors caused by invalid tokens.
         */

        ex.printStackTrace()

        return null
    }
}
