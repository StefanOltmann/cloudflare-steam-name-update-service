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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.w3c.dom.events.EventListener
import org.w3c.fetch.Request

@OptIn(DelicateCoroutinesApi::class)
fun main() {

    @Suppress("UNUSED_VARIABLE")
    val eventListener = EventListener { event ->

        event.asDynamic().respondWith(

            args = GlobalScope.promise {

                val req = event.asDynamic().request as Request
                val env = js("self") as Env

                handleRequest(req, env)
            }
        )
        Unit
    }

    js("addEventListener('fetch', eventListener)")
}
