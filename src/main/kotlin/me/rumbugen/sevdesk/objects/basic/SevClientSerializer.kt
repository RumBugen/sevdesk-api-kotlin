package me.rumbugen.sevdesk.objects.basic

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.Util.toOffsetDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime

@Serializer(forClass = SevClient::class)
object SevClientSerializer : KSerializer<SevClient> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SevClient")

    override fun serialize(encoder: Encoder, value: SevClient) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        jsonEncoder.encodeJsonElement(value.getJsonObjectBuilder {
            put("name", value.name)
            put("additionalInformation", value.additionalInformation)
            put("templateMainColor", value.templateMainColor)
            put("templateSubColor", value.templateSubColor)
            put("status", value.status)
        })
    }

    override fun deserialize(decoder: Decoder): SevClient {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return SevClient(
            id = jsonObject["id"]?.jsonPrimitive?.contentOrNull?.toInt()
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            additionalInformation = jsonObject["additional_information"]?.jsonPrimitive?.contentOrNull,
            name = jsonObject["name"]?.jsonPrimitive?.contentOrNull,
            create = jsonObject["create"].toOffsetDateTime(),
            update = jsonObject["update"].toOffsetDateTime(),
            templateMainColor = jsonObject["templateMainColor"]?.jsonPrimitive?.contentOrNull,
            templateSubColor = jsonObject["templateSubColor"]?.jsonPrimitive?.contentOrNull,
            status = jsonObject["status"]?.jsonPrimitive?.intOrNull
        )
    }
}