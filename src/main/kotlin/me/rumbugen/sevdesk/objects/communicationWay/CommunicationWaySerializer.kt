package me.rumbugen.sevdesk.objects.communicationWay

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import me.rumbugen.sevdesk.Util.decodeFromJsonElement
import me.rumbugen.sevdesk.Util.putSerializedIfExists
import me.rumbugen.sevdesk.Util.toBoolean
import me.rumbugen.sevdesk.Util.toInt
import me.rumbugen.sevdesk.Util.toOffsetDateTime
import me.rumbugen.sevdesk.objects.basic.SevClientSerializer
import me.rumbugen.sevdesk.objects.contact.ContactSerializer
import me.rumbugen.sevdesk.objects.communicationWay.key.CommunicationWayKeySerializer

@Serializer(forClass = CommunicationWay::class)
object CommunicationWaySerializer : KSerializer<CommunicationWay> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CommunicationWay")

    override fun serialize(encoder: Encoder, value: CommunicationWay) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        val jsonObject = value.getJsonObjectBuilder {
            putSerializedIfExists("contact", ContactSerializer, value.contact)
            value.type?.let { put("type", it.toString()) }
            value.value?.let { put("value", it) }
            putSerializedIfExists("key", CommunicationWayKeySerializer, value.key)
            value.main?.let { put("main", it.toInt().toString()) }
        }

        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): CommunicationWay {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return CommunicationWay(
            id = jsonObject["id"]?.jsonPrimitive?.content?.toInt()
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            contact = jsonObject["contact"]?.decodeFromJsonElement(ContactSerializer),
            type = jsonObject["type"]?.jsonPrimitive?.contentOrNull?.let {
                CommunicationWay.Type.valueOf(it)
            },
            value = jsonObject["value"]?.jsonPrimitive?.contentOrNull,
            key = jsonObject["key"]?.decodeFromJsonElement(CommunicationWayKeySerializer),
            main = jsonObject["main"]?.jsonPrimitive?.intOrNull?.toBoolean(),
            sevClient = jsonObject["sevClient"]?.decodeFromJsonElement(SevClientSerializer)
        )
    }
}