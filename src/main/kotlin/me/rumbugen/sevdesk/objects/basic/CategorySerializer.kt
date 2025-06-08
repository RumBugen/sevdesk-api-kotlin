package me.rumbugen.sevdesk.objects.basic

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.Util.decodeFromJsonElement
import me.rumbugen.sevdesk.Util.toOffsetDateTime
import me.rumbugen.sevdesk.objects.checkAccount.CheckAccount
import java.time.OffsetDateTime

@Serializer(forClass = CheckAccount::class)
object CategorySerializer : KSerializer<Category> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Category")

    override fun serialize(encoder: Encoder, value: Category) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        val jsonObject = value.getJsonObjectBuilder {
            put("name", value.name)
            put("objectType", value.objectType.toString())
            put("priority", value.priority.toString())
            put("code", value.code)
            put("color", value.color)
            put("postingAccount", value.postingAccount)
            put("type", value.type)
            put("translationCode", value.translationCode)
        }

        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): Category {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return Category(
            id = jsonObject["id"]?.jsonPrimitive?.contentOrNull?.toInt()
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            sevClient = jsonObject["sevClient"]?.decodeFromJsonElement(SevClientSerializer),
            name = jsonObject["name"]?.jsonPrimitive?.content,
            objectType = jsonObject["objectType"]?.jsonPrimitive?.contentOrNull?.let {
                Category.Type.valueOf(it)
            },
            priority = jsonObject["priority"]?.jsonPrimitive?.intOrNull,
            code = jsonObject["code"]?.jsonPrimitive?.contentOrNull,
            color = jsonObject["color"]?.jsonPrimitive?.contentOrNull,
            postingAccount = jsonObject["postingAccount"]?.jsonPrimitive?.contentOrNull,
            type = jsonObject["type"]?.jsonPrimitive?.contentOrNull,
            translationCode = jsonObject["translationCode"]?.jsonPrimitive?.contentOrNull,
        )
    }
}