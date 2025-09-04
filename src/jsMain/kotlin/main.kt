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
import org.w3c.fetch.Headers
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import util.appendCorsOptions
import util.await
import util.getValidSteamIdHash
import util.runSuspend
import kotlin.js.Promise

private const val FILENAME = "names.json"

/**
 * Main code for the worker.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun handleRequest(request: Request, env: Env, ctx: dynamic): Promise<Response> =
    runSuspend { handleRequestAsync(request, env, ctx) }

suspend fun handleRequestAsync(request: Request, env: Env, ctx: dynamic): Response {

    /*
     * Handle CORS preflight requests.
     */
    if (request.method == "OPTIONS")
        return createDefaultResponse(204, "No Content")

    /*
     * Reject non-POST requests.
     */
    if (request.method != "POST")
        return createDefaultResponse(405, "Method not allowed")

    val username = request.text().await().trim()

    /*
     * Reject empty POST bodies.
     */
    if (username.isEmpty())
        return createDefaultResponse(400, "Missing content")

    val token = request.headers.get("token")

    /*
     * If the token is missing, return a 401 Unauthorized response.
     */
    if (token == null)
        return createDefaultResponse(401, "Missing header 'token'")

    /*
     * Validate the token.
     */
    val steamIdHash: String? = getValidSteamIdHash(token)

    /**
     * Reject invalid or outdated tokens.
     */
    if (steamIdHash == null)
        return createDefaultResponse(401, "Invalid token")

    /*
     * Read the existing name file from R2.
     */

    val objectBody = env.STEAM_NAMES.get(FILENAME).await()

    if (objectBody == null) {

        console.log("R2 GET operation failed: File $FILENAME not found")

        return createDefaultResponse(500, "File not found")
    }

    val jsonData = objectBody.json().await()

    val deleteUsername = username == "\"\"" || username == "null" || username == "undefined"

    /*
     * Reject requests for doubled usernames.
     */
    if (!deleteUsername) {

        val usernameTaken = js("Object.values(jsonData).includes(username)").unsafeCast<Boolean>()

        if (usernameTaken) {

            console.log("$steamIdHash tried to take '$username'.")

            return createDefaultResponse(409, "Username '$username' is already taken.")
        }
    }

    if (deleteUsername)
        js("delete jsonData[steamIdHash]")
    else
        jsonData[steamIdHash] = username

    try {

        @Suppress("UNUSED")
        val etag = objectBody.etag

        /*
         * Put the modified file on R2.
         *
         * To prevent lost updates, we use the ETag.
         */
        env.STEAM_NAMES.put(
            key = FILENAME,
            value = JSON.stringify(jsonData),
            options = js(
                """
                ({
                    httpMetadata: { contentType: "application/json" },
                    onlyIf: { etagMatches: etag }
                })
            """
            ).unsafeCast<R2PutOptions>()
        ).await()

        console.log("Name changed: $steamIdHash = '$username'")

        return Response(
            body = if (deleteUsername)
                "Update successful! Your entry was removed from the index."
            else
                "Update successful! You are now known as '$username'.",
            init = ResponseInit(
                status = 200,
                statusText = "Update successful",
                headers = Headers().apply {
                    appendCorsOptions()
                }
            )
        )

    } catch (ex: Throwable) {

        console.error("R2 PUT operation failed: " + ex.message)

        return createDefaultResponse(500, "Failed to update data")
    }
}

private fun createDefaultResponse(
    code: Short,
    text: String
) = Response(
    body = null,
    init = ResponseInit(
        status = code,
        statusText = text,
        headers = Headers().apply {
            appendCorsOptions()
        }
    )
)
