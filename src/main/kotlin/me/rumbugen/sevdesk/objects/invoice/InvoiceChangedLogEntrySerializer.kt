package me.rumbugen.sevdesk.objects.invoice

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.Util.decodeFromJsonElement
import me.rumbugen.sevdesk.Util.putSerializedIfExists
import me.rumbugen.sevdesk.Util.toBoolean
import me.rumbugen.sevdesk.Util.toInt
import me.rumbugen.sevdesk.Util.toOffsetDateTime
import me.rumbugen.sevdesk.objects.basic.SevClientSerializer
import me.rumbugen.sevdesk.objects.contact.ContactSerializer
import me.rumbugen.sevdesk.objects.sevDeskFile.SevDeskFile
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountry
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountrySerializer
import java.util.Currency

@Serializer(forClass = InvoiceChangedLogEntry::class)
object InvoiceChangedLogEntrySerializer : KSerializer<InvoiceChangedLogEntry> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InvoiceChangedLogEntry")

    override fun serialize(encoder: Encoder, value: InvoiceChangedLogEntry) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        val jsonObject = value.getJsonObjectBuilder {
            put("fromStatus", value.fromStatus?.idRepresentation)
            put("toStatus", value.toStatus?.idRepresentation)
            put("ammountPayed", value.ammountPayed)
            put("bookingDate", value.bookingDate.toString())
        }

        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): InvoiceChangedLogEntry {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return InvoiceChangedLogEntry(
            id = jsonObject["id"]?.jsonPrimitive?.content?.toInt()
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            fromStatus = jsonObject["fromStatus"]?.jsonPrimitive?.int?.let {
                Invoice.Status.getByID(it)
            },
            toStatus = jsonObject["toStatus"]?.jsonPrimitive?.int?.let {
                Invoice.Status.getByID(it)
            },
            ammountPayed = jsonObject["ammountPayed"]?.jsonPrimitive?.contentOrNull,
            bookingDate = jsonObject["bookingDate"]?.toOffsetDateTime(),
            sevClient = jsonObject["sevClient"]?.decodeFromJsonElement(SevClientSerializer)
        )
    }
}