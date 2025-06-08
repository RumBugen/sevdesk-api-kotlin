package me.rumbugen.sevdesk.objects.contact.address

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import me.rumbugen.sevdesk.Util.decodeFromJsonElement
import me.rumbugen.sevdesk.Util.putSerializedIfExists
import me.rumbugen.sevdesk.Util.toOffsetDateTime
import me.rumbugen.sevdesk.objects.basic.SevClientSerializer
import me.rumbugen.sevdesk.objects.basic.CategorySerializer
import me.rumbugen.sevdesk.objects.contact.ContactSerializer
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountrySerializer

@Serializer(forClass = ContactAddress::class)
object ContactAddressSerializer : KSerializer<ContactAddress> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ContactAddress")

    override fun serialize(encoder: Encoder, value: ContactAddress) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        jsonEncoder.encodeJsonElement(value.getJsonObjectBuilder {
            putSerializedIfExists("contact", ContactSerializer, value.contact)
            put("street", value.street)
            put("zip", value.zip)
            put("city", value.city)
            putSerializedIfExists("country", StaticCountrySerializer, value.country)
            putSerializedIfExists("category", CategorySerializer, value.category)
            putSerializedIfExists("sevClient", SevClientSerializer, value.sevClient)
            put("name", value.name)
        })
    }

    override fun deserialize(decoder: Decoder): ContactAddress {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return ContactAddress(
            id = jsonObject["id"]?.jsonPrimitive?.int
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            contact = jsonObject["contact"]?.decodeFromJsonElement(ContactSerializer),
            street = jsonObject["street"]?.jsonPrimitive?.contentOrNull,
            zip = jsonObject["zip"]?.jsonPrimitive?.contentOrNull,
            city = jsonObject["city"]?.jsonPrimitive?.contentOrNull,
            country = jsonObject["country"]?.decodeFromJsonElement(StaticCountrySerializer),
            category = jsonObject["category"]?.decodeFromJsonElement(CategorySerializer),
            sevClient = jsonObject["sevClient"]?.decodeFromJsonElement(SevClientSerializer),
            name = jsonObject["name"]?.jsonPrimitive?.contentOrNull
        )
    }
}