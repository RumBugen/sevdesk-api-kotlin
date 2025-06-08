package me.rumbugen.sevdesk.objects.checkAccount

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.Util.decodeFromJsonElement
import me.rumbugen.sevdesk.Util.toOffsetDateTime
import me.rumbugen.sevdesk.objects.basic.SevClientSerializer
import java.time.OffsetDateTime

@Serializer(forClass = CheckAccount::class)
class CheckAccountSerializer : KSerializer<CheckAccount> {

    /**
     * Default constructor
     */
    constructor()

    /**
     * TODO
     */
    var whitelistKeys: List<String> = emptyList();

    constructor(whitelistKeys: List<String>) {
        this.whitelistKeys = whitelistKeys
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CheckAccount")

    override fun serialize(encoder: Encoder, value: CheckAccount) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        val jsonObject = value.getJsonObjectBuilder {
            put("name", value.name)
            put("iban", value.iban)
            put("type", value.type.toString().lowercase())
            put("importType", value.importType?.toString()?.lowercase())
            put("currency", value.currency)
            put("defaultAccount", value.defaultAccount.toString())
            put("baseAccount", value.baseAccount.toString())
            put("priority", value.priority.toString())
            value.status?.let { put("status", it.idRepresentation) }
            put("balance", value.balance)
            put("bankServer", value.bankServer)
            put("autoMapTransaction", value.autoMapTransaction)
            put("autoSyncTransactions", value.autoSyncTransactions)
            put("lastSync", value.lastSync.toString())
            put("accountingNumber", value.accountingNumber)
            // Undocumented Fields below
            put("countryCode", value.countryCode)
            put("checkAccId", value.checkAccId)
            put("pin", value.pin)
            put("translationCode", value.translationCode)
            put("bic", value.bic)
        }

        var element: JsonObject = jsonObject

        if (whitelistKeys.isNotEmpty()) {
            element = JsonObject(jsonObject.filterKeys { whitelistKeys.contains(it) })
        }

        jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): CheckAccount {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return CheckAccount(
            id = jsonObject["id"]?.jsonPrimitive?.contentOrNull?.toInt()
                ?: throw IllegalArgumentException("id is needed to deserialize this"),
            create = jsonObject["create"]?.toOffsetDateTime(),
            update = jsonObject["update"]?.toOffsetDateTime(),
            sevClient = jsonObject["sevClient"]?.decodeFromJsonElement(SevClientSerializer),
            name = jsonObject["name"]?.jsonPrimitive?.contentOrNull,
            iban = jsonObject["iban"]?.jsonPrimitive?.contentOrNull,
            type = jsonObject["type"]?.jsonPrimitive?.contentOrNull?.let {
                if (it.isEmpty()) null else CheckAccount.Type.valueOf(it.uppercase())
            },
            importType = jsonObject["importType"]?.jsonPrimitive?.contentOrNull?.let {
                CheckAccount.ImportType.valueOf(it.uppercase())
            },
            currency = jsonObject["currency"]?.jsonPrimitive?.contentOrNull,
            defaultAccount = jsonObject["defaultAccount"]?.jsonPrimitive?.contentOrNull?.toBoolean() ?: false,
            baseAccount = jsonObject["baseAccount"]?.jsonPrimitive?.contentOrNull?.toBoolean() ?: false,
            priority = jsonObject["priority"]?.jsonPrimitive?.contentOrNull?.toInt() ?: 0,
            status = jsonObject["status"]?.jsonPrimitive?.intOrNull?.let {
                CheckAccount.Status.valueOf(it)
            },
            balance = jsonObject["balance"]?.jsonPrimitive?.contentOrNull?.toDouble(),
            bankServer = jsonObject["bankServer"]?.jsonPrimitive?.contentOrNull,
            autoMapTransaction = jsonObject["autoMapTransactions"]?.jsonPrimitive?.contentOrNull?.toBoolean(),
            autoSyncTransactions = jsonObject["autoSyncTransactions"]?.jsonPrimitive?.contentOrNull?.toBoolean() ?: false,
            lastSync = jsonObject["lastSync"]?.toOffsetDateTime(),
            accountingNumber = jsonObject["accountingNumber"]?.jsonPrimitive?.contentOrNull?.toInt(),
            // Undocumented Fields below
            countryCode = jsonObject["countryCode"]?.jsonPrimitive?.contentOrNull,
            checkAccId = jsonObject["checkAccId"]?.jsonPrimitive?.contentOrNull,
            pin = jsonObject["pin"]?.jsonPrimitive?.contentOrNull,
            translationCode = jsonObject["translationCode"]?.jsonPrimitive?.contentOrNull,
            bic = jsonObject["bic"]?.jsonPrimitive?.contentOrNull
        )
    }
}