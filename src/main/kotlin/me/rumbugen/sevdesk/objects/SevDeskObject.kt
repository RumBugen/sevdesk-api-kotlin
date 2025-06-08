package me.rumbugen.sevdesk.objects

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.objects.basic.SevClient
import me.rumbugen.sevdesk.objects.basic.SevClientSerializer
import java.time.OffsetDateTime

abstract class SevDeskObject(
    open val id: Int? = null,
    open val create: OffsetDateTime? = null,
    open val update: OffsetDateTime? = null,
    open var sevClient: SevClient? = null
) {
    val objectName
        get() = this::class.simpleName ?: "SevDeskObject"

    fun getJsonObjectBuilder(builderAction: JsonObjectBuilder.() -> Unit): JsonObject {
        return buildJsonObject {
            put("id", id)
            create?.let { put("create", create.toString()) }
            update?.let { put("update", update.toString()) }
            sevClient?.let { put("sevClient", Json.encodeToJsonElement(SevClientSerializer, it)) }
            put("objectName", objectName)

            this.builderAction()
        }
    }
}