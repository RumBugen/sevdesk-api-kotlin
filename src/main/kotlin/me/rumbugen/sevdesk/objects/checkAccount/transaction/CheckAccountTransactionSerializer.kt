package me.rumbugen.sevdesk.objects.checkAccount.transaction

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.Util.decodeFromJsonElement
import me.rumbugen.sevdesk.Util.putSerializedIfExists
import me.rumbugen.sevdesk.Util.toOffsetDateTime
import me.rumbugen.sevdesk.objects.basic.SevClientSerializer
import me.rumbugen.sevdesk.objects.checkAccount.CheckAccountSerializer

@Serializer(forClass = CheckAccountTransaction::class)
class CheckAccountTransactionSerializer : KSerializer<CheckAccountTransaction> {

    /**
     * Default constructor
     */
    constructor()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CheckAccountTransaction")

    override fun serialize(encoder: Encoder, value: CheckAccountTransaction) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        val jsonObject = value.getJsonObjectBuilder {
            put("valueDate", value.valueDate.toString())
            put("entryDate", value.entryDate.toString())
            put("paymtPurpose", value.paymtPurpose)
            put("amount", value.amount) // TODO .toString()?
            put("payeePayerName", value.payeePayerName)
            put("payeePayerAcctNo", value.payeePayerAcctNo)
            put("payeePayerBankCode", value.payeePayerBankCode)
            put("gvCode", value.gvCode)
            put("entryText", value.entryText)
            put("primaNotaNo", value.primaNotaNo)
            putSerializedIfExists("checkAccount", CheckAccountSerializer(), value.checkAccount)
            put("status", value.status!!.idRepresentation)
            putSerializedIfExists("sourceTransaction", CheckAccountTransactionSerializer(), value.sourceTransaction)
            putSerializedIfExists("targetTransaction", CheckAccountTransactionSerializer(), value.targetTransaction)
            put("enshrined", value.enshrined.toString())
        }

        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): CheckAccountTransaction {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return CheckAccountTransaction(
            id = jsonObject["id"]?.jsonPrimitive?.content?.toInt()
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            sevClient = jsonObject["sevClient"]?.decodeFromJsonElement(SevClientSerializer),
            valueDate = jsonObject["valueDate"]?.toOffsetDateTime(),
            entryDate = jsonObject["entryDate"]?.toOffsetDateTime(),
            paymtPurpose = jsonObject["paymtPurpose"]?.jsonPrimitive?.contentOrNull,
            amount = jsonObject["amount"]?.jsonPrimitive?.contentOrNull?.toFloatOrNull(),
            payeePayerName = jsonObject["payeePayerName"]?.jsonPrimitive?.contentOrNull,
            payeePayerAcctNo = jsonObject["payeePayerAcctNo"]?.jsonPrimitive?.contentOrNull,
            payeePayerBankCode = jsonObject["payeePayerBankCode"]?.jsonPrimitive?.contentOrNull,
            gvCode = jsonObject["gvCode"]?.jsonPrimitive?.contentOrNull,
            entryText = jsonObject["entryText"]?.jsonPrimitive?.contentOrNull,
            primaNotaNo = jsonObject["primaNotaNo"]?.jsonPrimitive?.contentOrNull,
            checkAccount = jsonObject["checkAccount"]?.decodeFromJsonElement(CheckAccountSerializer()),
            status = jsonObject["status"]?.jsonPrimitive?.intOrNull?.let {
                CheckAccountTransaction.Status.valueOf(it)
            },
            sourceTransaction = jsonObject["sourceTransaction"]?.decodeFromJsonElement(CheckAccountTransactionSerializer()),
            targetTransaction = jsonObject["targetTransaction"]?.decodeFromJsonElement(CheckAccountTransactionSerializer()),
            enshrined = jsonObject["enshrined"]?.toOffsetDateTime()
        )
    }
}