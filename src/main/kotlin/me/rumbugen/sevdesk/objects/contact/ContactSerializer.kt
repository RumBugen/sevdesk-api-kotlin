package me.rumbugen.sevdesk.objects.contact

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
import java.time.LocalDate
import java.time.OffsetDateTime

@Serializer(forClass = Contact::class)
object ContactSerializer : KSerializer<Contact> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Contact")

    override fun serialize(encoder: Encoder, value: Contact) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        jsonEncoder.encodeJsonElement(value.getJsonObjectBuilder {
            put("name", value.name)
            put("status", value.status?.idRepresentation)
            put("customerNumber", value.customerNumber)
            putSerializedIfExists("parent", ContactSerializer, value.parent)
            put("surename", value.surename)
            put("familyname", value.familyname)
            put("titel", value.titel)
            putSerializedIfExists("category", CategorySerializer, value.category)
            put("description", value.description)
            put("academicTitle", value.academicTitle)
            put("gender", value.gender)
            putSerializedIfExists("sevClient", SevClientSerializer, value.sevClient)
            put("name2", value.name2)
            put("birthday", value.birthday.toString())
            put("vatNumber", value.vatNumber)
            put("bankAccount", value.bankAccount)
            put("bankNumber", value.bankNumber)
            put("defaultCashbackTime", value.defaultCashbackTime.toString())
            put("defaultCashbackPercent", value.defaultCashbackPercent)
            put("defaultTimeToPay", value.defaultTimeToPay.toString())
            put("taxNumber", value.taxNumber)
            put("taxOffice", value.taxOffice)
            put("exemptVat", value.exemptVat)
            put("defaultDiscountAmount", value.defaultDiscountAmount)
            put("defaultDiscountPercentage", value.defaultDiscountPercentage)
            put("buyerReference", value.buyerReference)
            put("governmentAgency", value.governmentAgency)
        })
    }

    override fun deserialize(decoder: Decoder): Contact {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return Contact(
            id = jsonObject["id"]?.jsonPrimitive?.int
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            name = jsonObject["name"]?.jsonPrimitive?.contentOrNull,
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            status = jsonObject["status"]?.jsonPrimitive?.intOrNull?.let {
                Contact.Status.valueOf(it)
            },
            customerNumber = jsonObject["customerNumber"]?.jsonPrimitive?.contentOrNull,
            parent = jsonObject["parent"]?.decodeFromJsonElement(ContactSerializer),
            surename = jsonObject["surename"]?.jsonPrimitive?.contentOrNull,
            familyname = jsonObject["familyname"]?.jsonPrimitive?.contentOrNull,
            titel = jsonObject["titel"]?.jsonPrimitive?.contentOrNull,
            category = jsonObject["category"]?.decodeFromJsonElement(CategorySerializer),
            description = jsonObject["description"]?.jsonPrimitive?.contentOrNull,
            academicTitle = jsonObject["academicTitle"]?.jsonPrimitive?.contentOrNull,
            gender = jsonObject["gender"]?.jsonPrimitive?.contentOrNull,
            sevClient = jsonObject["sevClient"]?.decodeFromJsonElement(SevClientSerializer),
            name2 = jsonObject["name2"]?.jsonPrimitive?.contentOrNull,
            birthday = jsonObject["birthday"]?.jsonPrimitive?.contentOrNull?.let {
                LocalDate.parse(it)
            },
            vatNumber = jsonObject["vatNumber"]?.jsonPrimitive?.contentOrNull,
            bankAccount = jsonObject["bankAccount"]?.jsonPrimitive?.contentOrNull,
            bankNumber = jsonObject["bankNumber"]?.jsonPrimitive?.contentOrNull,
            defaultCashbackTime = jsonObject["defaultCashbackTime"]?.jsonPrimitive?.intOrNull,
            defaultCashbackPercent = jsonObject["defaultCashbackPercent"]?.jsonPrimitive?.floatOrNull,
            defaultTimeToPay = jsonObject["defaultTimeToPay"]?.jsonPrimitive?.intOrNull,
            taxNumber = jsonObject["taxNumber"]?.jsonPrimitive?.contentOrNull,
            taxOffice = jsonObject["taxOffice"]?.jsonPrimitive?.contentOrNull,
            exemptVat = jsonObject["exemptVat"]?.jsonPrimitive?.booleanOrNull,
            defaultDiscountAmount = jsonObject["defaultDiscountPercentage"]?.jsonPrimitive?.floatOrNull,
            defaultDiscountPercentage = jsonObject["defaultDiscountPercentage"]?.jsonPrimitive?.booleanOrNull,
            buyerReference = jsonObject["buyerReference"]?.jsonPrimitive?.contentOrNull,
            governmentAgency = jsonObject["governmentAgency"]?.jsonPrimitive?.booleanOrNull
        )
    }
}