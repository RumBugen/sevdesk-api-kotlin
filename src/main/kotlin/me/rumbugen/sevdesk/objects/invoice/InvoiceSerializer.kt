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

@Serializer(forClass = Invoice::class)
object InvoiceSerializer : KSerializer<Invoice> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Invoice")

    override fun serialize(encoder: Encoder, value: Invoice) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        val jsonObject = value.getJsonObjectBuilder {
            put("invoiceNumber", value.invoiceNumber)
            putSerializedIfExists("contact", ContactSerializer, value.contact)
            put("invoiceDate", value.invoiceDate.toString())
            put("header", value.header)
            put("headText", value.headText)
            put("footText", value.footText)
            put("timeToPay", value.timeToPay)
            put("discountTime", value.discountTime)
            put("discount", value.discount)
            putSerializedIfExists("addressCountry", StaticCountrySerializer, value.addressCountry)
            put("payDate", value.payDate.toString())
            put("deliveryDate", value.deliveryDate.toString())
            put("status", value.status?.idRepresentation)
            put("smallSettlement", value.smallSettlement?.toInt().toString())
            put("taxRate", value.taxRate)
            put("taxText", value.taxText)
            put("dunningLevel", value.dunningLevel)
            put("sendDate", value.sendDate.toString())
            put("invoiceType", value.invoiceType.toString())
            put("accountIntervall", value.accountIntervall)
            put("accountNextInvoice", value.accountNextInvoice)
            put("reminderTotal", value.reminderTotal)
            put("reminderDebit", value.reminderDebit)
            put("reminderDeadline", value.reminderDeadline.toString())
            put("reminderCharge", value.reminderCharge)
            put("address", value.address)
            put("currency", value.currency.toString())
            put("address", value.address)
            put("sumNet", value.sumNet.toString())
            put("sumTax", value.sumTax.toString())
            put("sumGross", value.sumGross.toString())
            put("sumDiscounts", value.sumDiscounts.toString())
            put("sumNetForeignCurrency", value.sumNetForeignCurrency.toString())
            put("sumTaxForeignCurrency", value.sumTaxForeignCurrency.toString())
            put("sumGrossForeignCurrency", value.sumGrossForeignCurrency.toString())
            put("sumDiscountsForeignCurrency", value.sumDiscountsForeignCurrency.toString())
            put("sumNetAccounting", value.sumNetAccounting.toString())
            put("sumTaxAccounting", value.sumTaxAccounting.toString())
            put("sumGrossAccounting", value.sumGrossAccounting.toString())
            put("paidAmount", value.paidAmount)
            put("customerInternalNote", value.customerInternalNote)
            put("showNet", value.showNet?.toInt().toString())
            put("enshrined", value.enshrined.toString())
            put("sendType", value.sendType.toString())
        }

        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): Invoice {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return Invoice(
            id = jsonObject["id"]?.jsonPrimitive?.content?.toInt()
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            invoiceNumber = jsonObject["invoiceNumber"]?.jsonPrimitive?.content,
            contact = jsonObject["contact"]?.decodeFromJsonElement(ContactSerializer),
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            sevClient = jsonObject["sevClient"]?.decodeFromJsonElement(SevClientSerializer),
            invoiceDate = jsonObject["invoiceDate"]?.toOffsetDateTime(),
            header = jsonObject["header"]?.jsonPrimitive?.content,
            headText = jsonObject["headText"]?.jsonPrimitive?.content,
            footText = jsonObject["footText"]?.jsonPrimitive?.content,
            timeToPay = jsonObject["timeToPay"]?.jsonPrimitive?.content,
            discountTime = jsonObject["discountTime"]?.jsonPrimitive?.content,
            addressCountry = jsonObject["addressCountry"]?.decodeFromJsonElement(StaticCountrySerializer),
            payDate = jsonObject["payDate"]?.toOffsetDateTime(),
            deliveryDate = jsonObject["deliveryDate"]?.toOffsetDateTime(),
            status = jsonObject["status"]?.jsonPrimitive?.int?.let {
                Invoice.Status.getByID(it)
            },
            smallSettlement = jsonObject["smallSettlement"]?.jsonPrimitive?.intOrNull?.toBoolean(),
            taxRate = jsonObject["taxRate"]?.jsonPrimitive?.content,
            taxText = jsonObject["taxText"]?.jsonPrimitive?.content,
            dunningLevel = jsonObject["dunningLevel"]?.jsonPrimitive?.content,
            sendDate = jsonObject["sendDate"]?.toOffsetDateTime(),
            invoiceType = jsonObject["invoiceType"]?.jsonPrimitive?.content?.let {
                InvoiceType.valueOf(it)
            },
            accountIntervall = jsonObject["accountIntervall"]?.jsonPrimitive?.content,
            accountNextInvoice = jsonObject["accountNextInvoice"]?.jsonPrimitive?.content,
            reminderTotal = jsonObject["reminderTotal"]?.jsonPrimitive?.content,
            reminderDebit = jsonObject["reminderDebit"]?.jsonPrimitive?.content,
            reminderDeadline = jsonObject["reminderDeadline"]?.toOffsetDateTime(),
            reminderCharge = jsonObject["reminderCharge"]?.jsonPrimitive?.content,
            address = jsonObject["address"]?.jsonPrimitive?.content,
            currency = jsonObject["currency"]?.jsonPrimitive?.content?.let {
                Currency.getInstance(it)
            },
            sumNet = jsonObject["sumNet"]?.jsonPrimitive?.float,
            sumTax = jsonObject["sumTax"]?.jsonPrimitive?.float,
            sumGross = jsonObject["sumGross"]?.jsonPrimitive?.float,
            sumDiscounts = jsonObject["sumDiscounts"]?.jsonPrimitive?.float,
            sumNetForeignCurrency = jsonObject["sumNetForeignCurrency"]?.jsonPrimitive?.float,
            sumTaxForeignCurrency = jsonObject["sumTaxForeignCurrency"]?.jsonPrimitive?.float,
            sumGrossForeignCurrency = jsonObject["sumGrossForeignCurrency"]?.jsonPrimitive?.float,
            sumDiscountsForeignCurrency = jsonObject["sumDiscountsForeignCurrency"]?.jsonPrimitive?.float,
            sumNetAccounting = jsonObject["sumNetAccounting"]?.jsonPrimitive?.float,
            sumTaxAccounting = jsonObject["sumTaxAccounting"]?.jsonPrimitive?.float,
            sumGrossAccounting = jsonObject["sumGrossAccounting"]?.jsonPrimitive?.float,
            paidAmount = jsonObject["paidAmount"]?.jsonPrimitive?.float,
            customerInternalNote = jsonObject["customerInternalNote"]?.jsonPrimitive?.content,
            showNet = jsonObject["showNet"]?.jsonPrimitive?.int?.toBoolean(),
            enshrined = jsonObject["enshrined"]?.toOffsetDateTime(),
            sendType = jsonObject["sendType"]?.jsonPrimitive?.contentOrNull?.let {
                Invoice.SendType.valueOf(it)
            },
            deliveryDateUntil = jsonObject["deliveryDateUntil"]?.jsonPrimitive?.content
        )
    }
}