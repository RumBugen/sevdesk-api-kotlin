package me.rumbugen.sevdesk.objects.contact.customField

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
import me.rumbugen.sevdesk.objects.contact.customField.setting.ContactCustomFieldSetting
import me.rumbugen.sevdesk.objects.contact.customField.setting.ContactCustomFieldSettingSerializer
import java.time.LocalDate
import java.time.OffsetDateTime

@Serializer(forClass = ContactCustomField::class)
object ContactCustomFieldSerializer : KSerializer<ContactCustomField> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ContactCustomField")

    override fun serialize(encoder: Encoder, value: ContactCustomField) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        jsonEncoder.encodeJsonElement(value.getJsonObjectBuilder {
            putSerializedIfExists("contact", ContactSerializer, value.contact)
            putSerializedIfExists("contactCustomFieldSetting", TODO(), value.contactCustomFieldSetting)
            put("value", value.value)
        })
    }

    override fun deserialize(decoder: Decoder): ContactCustomField {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return ContactCustomField(
            id = jsonObject["id"]?.jsonPrimitive?.int
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            contact = jsonObject["contact"]?.decodeFromJsonElement(ContactSerializer),
            contactCustomFieldSetting = jsonObject["contactCustomFieldSetting"]?.decodeFromJsonElement(ContactCustomFieldSettingSerializer),
            value = jsonObject["value"]?.jsonPrimitive?.content
        )
    }
}