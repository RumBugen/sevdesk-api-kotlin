package me.rumbugen.sevdesk

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.rumbugen.sevdesk.objects.SevQuery
import java.time.OffsetDateTime

internal object Util {
    internal fun Boolean.toInt() = if (this) 1 else 0
    internal fun Int.toBoolean() = this == 1
    internal fun JsonElement?.toOffsetDateTime(): OffsetDateTime? = this?.jsonPrimitive?.contentOrNull?.let { OffsetDateTime.parse(it) }
    internal fun <T> JsonElement?.decodeFromJsonElement(deserializer: KSerializer<T>): T? = this?.jsonObject?.let {
        Json.decodeFromJsonElement(deserializer, it)
    }

    internal fun <T> JsonObjectBuilder?.putSerializedIfExists(key: String, deserializer: KSerializer<T>, value: T?) {
        if (value != null) this?.put(key, Json.encodeToJsonElement(deserializer, value))
    }

    fun <T> getObjectList(jsonObject: JsonObject, serializer: KSerializer<T>): List<T> {
        val objects = jsonObject["objects"] as? JsonArray ?: return emptyList()
        val items = mutableListOf<T>()

        for (jsonObject in objects) {
            val decodedElement = Json.Default.decodeFromJsonElement(serializer, jsonObject)
            items.add(decodedElement)
        }

        return items
    }

    fun <T> getObjectFromObjectList(jsonObject: JsonObject, serializer: KSerializer<T>): T? {
        val list = getObjectList(jsonObject, serializer)

        if (list.isEmpty()) {
            return null
        }

        return list.first()
    }

    fun HttpRequestBuilder.setJSONObjectBody(obj: JsonObject) {
        this.setBody(Json {
            explicitNulls = false
        }.encodeToString(obj))
    }
}