package me.rumbugen.sevdesk.objects.communicationWay.key

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializer(forClass = CommunicationWayKey::class)
object CommunicationWayKeySerializer : KSerializer<CommunicationWayKey> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CommunicationWayKey")

    override fun serialize(encoder: Encoder, value: CommunicationWayKey) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        val jsonObject = value.getJsonObjectBuilder {
            value.name?.let { put("name", it) }
            value.translationCode?.let { put("translationCode", it) }
        }

        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): CommunicationWayKey {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return CommunicationWayKey(
            id = jsonObject["id"]?.jsonPrimitive?.content?.toInt()
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            name = jsonObject["name"]?.jsonPrimitive?.contentOrNull,
            translationCode = jsonObject["translationCode"]?.jsonPrimitive?.contentOrNull
        )
    }
}