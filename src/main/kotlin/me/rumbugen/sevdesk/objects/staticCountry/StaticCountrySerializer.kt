package me.rumbugen.sevdesk.objects.staticCountry

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Serializer(forClass = StaticCountry::class)
object StaticCountrySerializer : KSerializer<StaticCountry> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StaticCountry")

    override fun serialize(encoder: Encoder, value: StaticCountry) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        val jsonObject = value.getJsonObjectBuilder {
            put("code", value.code)
            put("name", value.name)
            put("nameEn", value.nameEn)
            put("translationCode", value.translationCode)
        }

        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): StaticCountry {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return StaticCountry(
            id = jsonObject["id"]?.jsonPrimitive?.content?.toInt()
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            code = jsonObject["code"]?.jsonPrimitive?.content,
            name = jsonObject["name"]?.jsonPrimitive?.content,
            nameEn = jsonObject["nameEn"]?.jsonPrimitive?.content,
            translationCode = jsonObject["translationCode"]?.jsonPrimitive?.content,
        )
    }
}