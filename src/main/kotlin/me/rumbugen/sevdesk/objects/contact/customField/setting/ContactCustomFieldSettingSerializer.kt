package me.rumbugen.sevdesk.objects.contact.customField.setting

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
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.contact.ContactSerializer
import me.rumbugen.sevdesk.objects.contact.customField.ContactCustomField
import me.rumbugen.sevdesk.objects.contact.customField.setting.ContactCustomFieldSetting
import java.time.LocalDate
import java.time.OffsetDateTime

@Serializer(forClass = ContactCustomFieldSetting::class)
object ContactCustomFieldSettingSerializer : KSerializer<ContactCustomFieldSetting> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ContactCustomFieldSetting")

    override fun serialize(encoder: Encoder, value: ContactCustomFieldSetting) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        jsonEncoder.encodeJsonElement(value.getJsonObjectBuilder {
            put("name", value.name)
            put("identifier", value.identifier)
            put("description", value.description)
        })
    }

    override fun deserialize(decoder: Decoder): ContactCustomFieldSetting {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return ContactCustomFieldSetting(
            id = jsonObject["id"]?.jsonPrimitive?.int
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            name = jsonObject["name"]?.jsonPrimitive?.content,
            identifier = jsonObject["identifier"]?.jsonPrimitive?.content,
            description = jsonObject["description"]?.jsonPrimitive?.content,
        )
    }
}