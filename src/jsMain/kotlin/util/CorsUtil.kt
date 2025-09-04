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

import org.w3c.fetch.Headers

internal fun Headers.appendCorsOptions() {

    set("Access-Control-Allow-Origin", "*")
    set("Access-Control-Allow-Methods", "POST, OPTIONS")
    set("Access-Control-Allow-Headers", "Content-Type, token")

    /*
     * Cache CORS options for one year because they won't change.
     * Also, repeated CORS preflight requests cost us traffic and server time.
     */
    set("Access-Control-Max-Age", "31536000")
}
