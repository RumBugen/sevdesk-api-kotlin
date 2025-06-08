package me.rumbugen.sevdesk.requests

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.SevDeskInvalidResponse
import me.rumbugen.sevdesk.Util
import me.rumbugen.sevdesk.Util.setJSONObjectBody
import me.rumbugen.sevdesk.Util.toInt
import me.rumbugen.sevdesk.objects.checkAccount.CheckAccount
import me.rumbugen.sevdesk.objects.checkAccount.CheckAccountPage
import me.rumbugen.sevdesk.objects.checkAccount.CheckAccountSerializer
import java.time.LocalDate

/**
 * A check account is a payment account on which payments to or from the customer are stored.
 * There are three general types of check accounts:
 *
 *     Clearing Account (type=offline)
 *     Bank Accounts including PayPal and CSV import accounts (type=online)
 *     Cash Register (type=register)
 *
 * Bank accounts can be linked to PayPal or Banks, these must be created through the UI. File import accounts can be created through an endpoint.
 * Clearing accounts can be created through an endpoint.
 * If you want to create payment entries via API, use a file import account.
 * Register accounts represent the cash register for sevdesk account holders needing one and they behave the same way as offline accounts.
 *
 * Regarding the check accounts, you will most certainly only need to request existing check accounts for using their ID in further requests.
 * Therefore, you only need to send normal GET requests to the CheckAccount endpoint.
 */
class CheckAccountRequests internal constructor(private var sevDeskAPI: SevDeskAPI) {

    /**
     * Retrieves check accounts from the sevDesk API based on specified parameters.
     *
     * This function performs a GET request to the sevDesk API endpoint for retrieving check accounts.
     * Refer to the official sevDesk API documentation (https://api.sevdesk.de/#tag/CheckAccount/operation/getCheckAccounts)
     * for more details on the request and response format.
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return [Pair] A Pair containing:
     *   - A List of [CheckAccount] objects representing the retrieved check accounts.
     *   - An optional Integer representing the total number of check accounts if `countAll` is true, otherwise null.
     */
    suspend fun retrieveCheckAccounts(
        countAll: Boolean = false,
        limit: Int?,
        offset: Int?
    ): Pair<List<CheckAccount>, Int?> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("CheckAccount") {
                    parameter("countAll", countAll)
                    if (limit != null) parameter("limit", limit)
                    if (offset != null) parameter("offset", offset)
                }
            },
            {
                Util.getObjectList(it, CheckAccountSerializer()).to(it["total"]?.jsonPrimitive?.contentOrNull?.toInt())
            }) ?: mutableListOf<CheckAccount>().to(null)
    }

    /**
     * Retrieves all check accounts available in the sevDesk system.
     *
     * This function performs a GET request to the sevDesk API endpoint for retrieving check accounts.
     * Refer to the official sevDesk API documentation (https://api.sevdesk.de/#tag/CheckAccount/operation/getCheckAccounts)
     * for more details on the request and response format.
     *
     * @see retrieveCheckAccounts
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return A [List] of [CheckAccount] objects representing all retrieved check accounts.
     */
    suspend fun retrieveCheckAccountsWithoutPagination(
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null
    ): List<CheckAccount> {
        return retrieveCheckAccounts(countAll, limit, offset).first
    }

    /**
     * Retrieves a paginated list of check accounts.
     *
     * This method fetches check accounts, applying pagination based on the provided `perPage` and `page` values.
     * It internally uses `retrieveCheckAccountsWithoutPagination` to fetch the data with specified limit and offset.
     *
     * @see retrieveCheckAccountsWithoutPagination Is used internally to manual pagination
     *
     * @param perPage The number of objects to retrieve per page. Defaults to 10.
     * @param page The page number to retrieve. Defaults to 1.
     *
     * @return A [Page] object containing a mutable list of [CheckAccount] objects representing the check accounts for the current page.
     */
    suspend fun retrieveCheckAccountsWithPagination(perPage: Int = 10, page: Int = 1): Page<CheckAccount> {
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = retrieveCheckAccounts(true, limit, offset)

        return CheckAccountPage(
            checkAccount = this,
            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!
        )
    }

    /**
     * Create a new file import account
     *
     * Creates a new banking account for file imports (CSV, MT940).
     *
     * @param name Name of the check account
     * @param importType Import type. Transactions can be imported by this method on the check account.
     * @param accountingNumber The booking account used for this bank account, e.g. 1800 in SKR04 and 1200 in SKR03. Must be unique among all your CheckAccounts. Ignore to use a sensible default.
     * @param iban IBAN of the bank account, without spaces
     *
     * @return [CheckAccount] model.
     */
    suspend fun createFileImportAccount(
        name: String,
        importType: CheckAccount.ImportType,
        accountingNumber: Int? = null,
        iban: String? = null
    ): CheckAccount? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.post("CheckAccount/Factory/fileImportAccount") {
                    setJSONObjectBody(buildJsonObject {
                        put("name", name)
                        put("importType", importType.toString())
                        put("accountingNumber", accountingNumber)
                        put("iban", iban)
                    })
                }
            },
            {
                return@requestWithHandling Json.Default.decodeFromJsonElement(
                    CheckAccountSerializer(),
                    it["objects"]?.jsonObject!!
                )
            })
    }

    /**
     * Create a new clearing account
     *
     * Creates a new clearing account.
     *
     * @param name Name of the check account
     * @param accountingNumber The booking account used for this clearing account, e.g. 3320 in SKR04 and 1723 in SKR03. Must be unique among all your CheckAccounts. Ask your tax consultant what to choose.
     *
     * @return [CheckAccount] model.
     */
    suspend fun createFileClearingAccount(
        name: String,
        accountingNumber: Int? = null
    ): CheckAccount? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.post("CheckAccount/Factory/clearingAccount") {
                    setJSONObjectBody(buildJsonObject {
                        put("name", name)
                        put("accountingNumber", accountingNumber)
                    })
                }
            },
            {
                return@requestWithHandling Json.Default.decodeFromJsonElement(
                    CheckAccountSerializer(),
                    it["objects"]?.jsonObject!!
                )
            })
    }

    /**
     * Find check account by ID.
     *
     * Retrieves an existing check account from the sevDesk API based on its ID.
     *
     * @param checkAccountId ID of check account
     *
     * @return The [CheckAccount] object.
     */
    suspend fun findCheckAccountById(
        checkAccountId: Int
    ): CheckAccount? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("CheckAccount/${checkAccountId}")
            },
            {
                val objects = it["objects"] as? JsonArray ?: throw SevDeskInvalidResponse("Objects not found")
                return@requestWithHandling if (objects.isEmpty()) null else Json.Default.decodeFromJsonElement(
                    CheckAccountSerializer(),
                    objects[0]
                )
            })
    }

    private suspend fun updateExistingCheckAccountRaw(id: Int, body: String): CheckAccount? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.put("CheckAccount/${id}") {
                    setBody(body)
                }
            },
            {
                return@requestWithHandling Json.Default.decodeFromJsonElement(
                    CheckAccountSerializer(),
                    it["objects"]?.jsonObject!!
                )
            })
    }

    /**
     * Updates an existing check account.
     *
     * Updates the properties of an existing check account. The account to update is identified by the `id` property of the provided `newCheckAccount`.
     * Only the `name`, `type`, `importType`, `currency`, `defaultAccount`, `status`, `autoMapTransactions`, and `accountingNumber` properties of the `newCheckAccount` are considered for the update.  Other properties are ignored.
     *
     * @param newCheckAccount The `CheckAccount` object containing the updated properties and the ID of the account to update.
     *
     * @return The updated `CheckAccount` object if the update was successful, otherwise `null`.
     */
    suspend fun updateExistingCheckAccount(
        newCheckAccount: CheckAccount
    ): CheckAccount? {
        return updateExistingCheckAccountRaw(
            newCheckAccount.id, Json.encodeToString(
                CheckAccountSerializer(
                    listOf(
                        "name",
                        "type",
                        "importType",
                        "currency",
                        "defaultAccount",
                        "status",
                        "autoMapTransactions",
                        "accountingNumber"
                    )
                ), newCheckAccount
            )
        )
    }

    /**
     * Update an existing check account.
     *
     * Updates the properties of an existing check account identified by its ID.
     *
     * @param checkAccountId ID of check account to update
     * @param name Name of the check account
     * @param type The type of the check account. Account with a CSV or MT940 import are regarded as online.
     * Apart from that, created check accounts over the API need to be offline, as online accounts with an active connection to a bank application can not be managed over the API.
     * @param currency The currency of the check account.
     * @param accountingNumber The booking account used for this bank account, e.g. 1800 in SKR04 and 1200 in SKR03. Must be unique among all your CheckAccounts.
     * @param defaultAccount Defines if this check account is the default account.
     * @param status Status of the check account.
     * @param autoMapTransactions Defines if transactions on this account are automatically mapped to invoice and vouchers when imported if possible.
     * @param importType Import type. Transactions can be imported by this method on the check account.
     *
     * @return The updated `CheckAccount` object if the update was successful, or `null` if the check account was not found.
     */
    suspend fun updateExistingCheckAccount(
        checkAccountId: Int,
        name: String,
        type: CheckAccount.Type,
        currency: String,
        accountingNumber: Int,
        defaultAccount: Int = 0,
        status: CheckAccount.Status = CheckAccount.Status.ACTIVE,
        autoMapTransactions: Boolean = true,
        importType: CheckAccount.ImportType? = null
    ): CheckAccount? {
        val body = buildJsonObject {
            put("name", name)
            put("type", type.toString())
            put("currency", currency)
            put("accountingNumber", accountingNumber)
            put("defaultAccount", defaultAccount)
            put("status", status.idRepresentation)
            put("autoMapTransactions", autoMapTransactions.toInt())
            put("importType", importType?.toString())
        }

        return updateExistingCheckAccountRaw(checkAccountId, Json {
            explicitNulls = false
        }.encodeToString(body))
    }

    /**
     * Deletes a check account
     *
     * @see findCheckAccountById WARNING: Deletes means in SevDesk that the status is just sets to archived, it still shows up on finding
     * @param checkAccountId Id of check account to delete
     */
    suspend fun deleteCheckAccount(
        checkAccountId: Int
    ) {
        sevDeskAPI.request(
            {
                sevDeskAPI.client.delete("CheckAccount/${checkAccountId}")
            })
    }

    /**
     * Deletes a check account
     *
     * @see findCheckAccountById WARNING: Deletes means in SevDesk that the status is just sets to archived, it still shows up on finding
     * @param checkAccount The `CheckAccount` object representing the check account to be deleted. The ID of this object
     *                     is used to identify the account for deletion.
     */
    suspend fun deleteCheckAccount(
        checkAccount: CheckAccount
    ) {
        deleteCheckAccount(checkAccount.id)
    }

    /**
     * Get the balance at a given date.
     *
     * Calculates the balance by summing all transactions known to sevDesk up to and including the specified date.
     * Important: This balance might not reflect the actual bank account balance if sevDesk lacks older transactions.
     *
     * @param checkAccountId ID of check account
     * @param date Only consider transactions up to this date at 23:59:59
     * @return The balance as a Float, or null if the balance could not be retrieved or if no transactions are available for the given date.
     */
    suspend fun getBalanceAtGivenDate(
        checkAccountId: Int,
        date: LocalDate
    ): Float? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("CheckAccount/${checkAccountId}/getBalanceAtDate") {
                    parameter("date", date.toString())
                }
            },
            {
                return@requestWithHandling it["objects"]?.jsonPrimitive?.contentOrNull?.toFloat()
            })
    }

    /**
     * Get the balance at a given date.
     *
     * Calculates the balance by summing all transactions known to sevDesk up to and including the specified date.
     * Important: This balance might not reflect the actual bank account balance if sevDesk lacks older transactions.
     *
     * @param checkAccount The check account to get the balance from.
     * @param date Only consider transactions up to this date at 23:59:59.
     * @return The balance as a Float, or null if the balance could not be retrieved or if no transactions are available for the given date.
     */
    suspend fun getBalanceAtGivenDate(
        checkAccount: CheckAccount,
        date: LocalDate
    ): Float? {
        return getBalanceAtGivenDate(checkAccount.id, date)
    }
}

