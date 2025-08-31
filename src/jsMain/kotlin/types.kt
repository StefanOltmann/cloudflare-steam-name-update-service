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
import kotlin.js.Date
import kotlin.js.Json
import kotlin.js.Promise

/**
 * The environment variables that are available to the worker.
 */
external interface Env {
    val STEAM_NAMES: R2Bucket
}

/**
 * The methods we need from R2 Bucket
 */
external interface R2Bucket {

    fun get(
        key: String
    ): Promise<R2ObjectBody?>

    fun put(
        key: String,
        value: String,
        options: R2PutOptions = definedExternally
    ): Promise<R2Object>
}

/**
 * The result when you GET: object data and metadata
 */
external interface R2ObjectBody : R2Object {
    fun json(): Promise<Json>
}

/**
 * The result when you PUT: metadata only
 */
external interface R2Object {
    val size: Int
    val etag: String
}

/**
 * Options for put()
 */
external interface R2PutOptions {
    var httpMetadata: R2HTTPMetadata?
    var onlyIf: R2Conditional?
}

/**
 * HTTP metadata like contentType
 */
external interface R2HTTPMetadata {
    var contentType: String?
    var contentLanguage: String?
    var contentDisposition: String?
    var contentEncoding: String?
    var cacheControl: String?
    var cacheExpiry: Date?
}

/**
 * Conditional headers (preconditions)
 */
external interface R2Conditional {
    var etagMatches: String?
    var etagDoesNotMatch: String?
    var uploadedBefore: Date?
    var uploadedAfter: Date?
}
