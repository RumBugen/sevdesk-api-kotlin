package me.rumbugen.sevdesk.objects.checkAccount

import kotlinx.serialization.Serializable
import me.rumbugen.sevdesk.objects.SevDeskObject
import me.rumbugen.sevdesk.objects.basic.SevClient
import java.time.OffsetDateTime

/**
 * CheckAccount model.
 */
@Serializable(with = CheckAccountSerializer::class)
data class CheckAccount(
    /**
     * The check account id
     */
    override val id: Int,

    // objectName is not included because the class does the job

    /**
     * Date of check account creation
     */
    override var create: OffsetDateTime? = null,

    /**
     * Date of last check account update
     */
    override var update: OffsetDateTime? = null,

    /**
     * Client to which check account belongs. Will be filled automatically
     */
    override var sevClient: SevClient? = null,

    /**
     * Name of the check account
     */
    var name: String? = null,

    /**
     * The IBAN of the account
     */
    var iban: String? = null,

    /**
     * The type of the check account. Account with a CSV or MT940 import are regarded as online.
     */
    var type: Type? = null,

    /**
     * Import type, for accounts that are type "online" but not connected to a data provider.
     */
    var importType: ImportType? = null,

    /**
     * The currency of the check account.
     */
    var currency: String? = null, // TODO: ENUM later?

    /**
     * Defines if this check account is the default account.
     *
     * In the API that is a String, but we can represent it as an Boolean
     */
    var defaultAccount: Boolean = false,

    /**
     * This will be 1 if the account is your base account that comes with every sevdesk setup.
     */
    var baseAccount: Boolean = false,

    /**
     * Defines the sorting of accounts, highest is first.
     *
     * In the API that is a String, but we can represent it as an Int
     */
    var priority: Int? = null,

    /**
     * Status of the check account.
     */
    var status: Status? = null,

    /**
     * The account balance as reported by PayPal or finAPI. Not set for other types of accounts.
     */
    var balance: Double? = null,

    /**
     * Bank server of check account, only set if the account is connected to a data provider
     */
    var bankServer: String? = null,

    /**
     * Defines if transactions on this account are automatically mapped to invoice and vouchers when imported if possible.
     */
    var autoMapTransaction: Boolean? = null,

    /**
     * If this is true the account will be automatically updated through PayPal or finAPI. Only applicable for connected online accounts.
     */
    var autoSyncTransactions: Boolean? = false,

    /**
     * Timepoint of the last payment import through PayPal or finAPI.
     */
    var lastSync: OffsetDateTime? = null,

    /**
     * The booking account used for this account, e.g. 1800 in SKR04 and 1200 in SKR03. Must be unique among all your CheckAccounts.
     */
    var accountingNumber: Int? = null, // TODO: SKR04 & SKR03 Enum for functions to represent this as an Enum

    // the following I found with Reverse Engineering and its not official documented

    /**
     * The country code.
     *
     * This property represents a two-letter ISO country code (e.g., "US" for United States, "CA" for Canada).
     * It may be null if the country code is unknown or not applicable.
     *
     * Note: This field is undocumented in the SevDesk API, so its usage and requirements may be subject to change.
     * TODO: Found out if this is
     */
    var countryCode: String? = null,

    /**
     * I dont know what this is
     *
     * Note: This field is undocumented in the SevDesk API, so its usage and requirements may be subject to change.
     * TODO: Found out if this is
     */
    var checkAccId: String? = null,

    /**
     * PIN for the Account?
     *
     * Note: This field is undocumented in the SevDesk API, so its usage and requirements may be subject to change.
     * TODO: Found out if this is
     */
    var pin: String? = null,

    /**
     * ??
     *
     * Note: This field is undocumented in the SevDesk API, so its usage and requirements may be subject to change.
     * TODO: Found out if this is
     */
    var translationCode: String? = null,

    /**
     * The Bank Identifier Code (BIC) associated with the bank account.
     *
     * This property stores the BIC, which is an international standard for identifying banks and financial institutions.
     * It is used to facilitate international wire transfers and other banking transactions.
     *
     * Note: This field is undocumented in the SevDesk API, so its usage and requirements may be subject to change.
     */
    var bic: String? = null
): SevDeskObject(id, create, update, sevClient) {
    /**
     * The type of the check account. Account with a CSV or MT940 import are regarded as online.
     */
    enum class Type {
        OFFLINE, ONLINE, REGISTER;

        /**
         * TODO
         */
        override fun toString(): String {
            return super.toString().lowercase()
        }
    }

    /**
     * Import type, for accounts that are type "online" but not connected to a data provider.
     */
    enum class ImportType {
        CSV, MT940
    }

    /**
     * Status of the check account.
     */
    enum class Status(
        /**
         * The id to represent this to SevDesk and vise versa
         */
        val idRepresentation: Int
    ) {
        ARCHIVED(0), ACTIVE(100);

        companion object {
            fun valueOf(idRepresentation: Int): Status {
                return entries.first { it.idRepresentation == idRepresentation }
            }
        }
    }
}