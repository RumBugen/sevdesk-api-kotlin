package me.rumbugen.sevdesk.requests

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.Util
import me.rumbugen.sevdesk.Util.setJSONObjectBody
import me.rumbugen.sevdesk.objects.checkAccount.transaction.CheckAccountTransaction
import me.rumbugen.sevdesk.objects.checkAccount.transaction.CheckAccountTransactionPage
import me.rumbugen.sevdesk.objects.checkAccount.transaction.CheckAccountTransactionSerializer
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.Int

/**
 * CheckAccountTransaction
 *
 * A check account transaction is a payment on a check account from or to the customer.
 * They are essential for booking invoices, vouchers (receipts) and credit notes with them to mark them as paid.
 * For a correct bookkeeping, there is always one or multiple transactions linked to an invoice, a voucher or a credit note, until the relevant object is completely paid.
 */
class CheckAccountTransactionRequests internal constructor(private var sevDeskAPI: SevDeskAPI) {

    /**
     * Retrieve transactions
     *
     * Retrieves transactions based on specified filter criteria.
     *
     * @param checkAccountId Optional. Filters transactions to only include those associated with the given check account ID.
     * @param isBooked Optional. Filters transactions based on their booking status. If true, only booked transactions are returned.
     * @param paymtPurpose Optional. Filters transactions to only include those with the given payment purpose.
     * @param startDate Optional. Filters transactions to only include those with a transaction date on or after the given date.
     * @param endDate Optional. Filters transactions to only include those with a transaction date on or before the given date.
     * @param payeePayerName Optional. Filters transactions to only include those where the payee or payer name matches the given value.
     * @param onlyCredit Optional. If true, filters transactions to only include credit transactions. Cannot be used together with onlyDebit.
     * @param onlyDebit Optional. If true, filters transactions to only include debit transactions. Cannot be used together with onlyCredit.
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return A Pair containing:
     *   - A List of CheckAccountTransaction objects that match the specified filter criteria.
     *   - An optional Integer representing the total count of transactions matching the filter criteria, or null if countAll is false.
     */
    suspend fun retrieveTransactions(
        checkAccountId: Int? = null,
        isBooked: Boolean? = null,
        paymtPurpose: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        payeePayerName: String? = null,
        onlyCredit: Boolean? = null,
        onlyDebit: Boolean? = null,
        // Paging
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null,
    ): Pair<List<CheckAccountTransaction>, Int?> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("CheckAccountTransaction") {
                    if (checkAccountId != null) {
                        parameter("checkAccount[id]", checkAccountId)
                        parameter("checkAccount[objectName]", "CheckAccount")
                    }
                    if (isBooked != null) parameter("isBooked", isBooked)
                    if (paymtPurpose != null) parameter("paymtPurpose", paymtPurpose)
                    if (startDate != null) parameter("startDate", startDate.toString())
                    if (endDate != null) parameter("endDate", endDate.toString())
                    if (payeePayerName != null) parameter("payeePayerName", payeePayerName)
                    if (onlyCredit != null) parameter("onlyCredit", onlyCredit)
                    if (onlyDebit != null) parameter("onlyDebit", onlyDebit)

                    // Paging
                    parameter("countAll", countAll)
                    if (limit != null) parameter("limit", limit)
                    if (offset != null) parameter("offset", offset)
                }
            },
            {
                return@requestWithHandling Util.getObjectList(it, CheckAccountTransactionSerializer()).to(it["total"]?.jsonPrimitive?.contentOrNull?.toInt())
            }) ?: mutableListOf<CheckAccountTransaction>().to(null)
    }

    /**
     * Retrieve transactions
     *
     * Retrieves transactions based on specified filter criteria.
     *
     * @param checkAccountId Optional. Filters transactions to only include those associated with the given check account ID.
     * @param isBooked Optional. Filters transactions based on their booking status, if provided.  If true, only booked transactions are returned.
     * @param paymtPurpose Optional. Filters transactions to only include those with the given payment purpose, if provided.
     * @param startDate Optional. Filters transactions to only include those with a transaction date on or after the given date, if provided.
     * @param endDate Optional. Filters transactions to only include those with a transaction date on or before the given date, if provided.
     * @param payeePayerName Optional. Filters transactions to only include those where the payee or payer name matches the given value, if provided.
     * @param onlyCredit Optional. If `true`, filters transactions to only include credit transactions. Cannot be used together with `onlyDebit`.
     * @param onlyDebit Optional. If `true`, filters transactions to only include debit transactions. Cannot be used together with `onlyCredit`.
     * @param limit Optional. Limits the number of transactions returned. This parameter is not used in this method, as pagination is not supported and all matching transactions are returned.
     * @param offset Optional. Specifies the starting position (offset) for retrieving transactions. This parameter is not used in this method, as pagination is not supported.
     * @return A List of CheckAccountTransaction objects that match the specified filter criteria.
     */
    suspend fun retrieveTransactionsWithoutPagination(
        checkAccountId: Int? = null,
        isBooked: Boolean? = null,
        paymtPurpose: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        payeePayerName: String? = null,
        onlyCredit: Boolean? = null,
        onlyDebit: Boolean? = null,
        // Paging
        limit: Int? = null,
        offset: Int? = null,
    ): List<CheckAccountTransaction> {
        return retrieveTransactions(
            checkAccountId,
            isBooked,
            paymtPurpose,
            startDate,
            endDate,
            payeePayerName,
            onlyCredit,
            onlyDebit,
            false,
            limit,
            offset
        ).first
    }

    /**
     * Retrieve transactions
     *
     * Retrieves transactions based on specified filter criteria.
     *
     * @param checkAccountId Optional. Filters transactions to only include those associated with the given check account ID, if provided.
     * @param isBooked Optional. Filters transactions based on their booking status, if provided. If `true`, only booked transactions are returned.
     * @param paymtPurpose Optional. Filters transactions to only include those with the given payment purpose, if provided.
     * @param startDate Optional. Filters transactions to only include those with a transaction date on or after the given date, if provided.
     * @param endDate Optional. Filters transactions to only include those with a transaction date on or before the given date, if provided.
     * @param payeePayerName Optional. Filters transactions to only include those where the payee or payer name matches the given value, if provided.
     * @param onlyCredit Optional. If `true`, filters transactions to only include credit transactions. Cannot be used together with `onlyDebit`.
     * @param onlyDebit Optional. If `true`, filters transactions to only include debit transactions. Cannot be used together with `onlyCredit`.
     *
     * @param perPage The number of objects to retrieve per page. Defaults to 10.
     * @param page The page number to retrieve. Defaults to 1.
     *
     * @return A Page object containing a list of CheckAccountTransaction objects that match the specified filter criteria and pagination information.
     */
    suspend fun retrieveTransactionsWithPagination(
        // Paging
        perPage: Int = 10,
        page: Int = 1,

        checkAccountId: Int? = null,
        isBooked: Boolean? = null,
        paymtPurpose: String? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        payeePayerName: String? = null,
        onlyCredit: Boolean? = null,
        onlyDebit: Boolean? = null,
    ): Page<CheckAccountTransaction> {
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = retrieveTransactions(
            checkAccountId,
            isBooked,
            paymtPurpose,
            startDate,
            endDate,
            payeePayerName,
            onlyCredit,
            onlyDebit,
            true,
            limit,
            offset
        )

        return CheckAccountTransactionPage(
            checkAccountTransactionRequests = this,

            checkAccountId = checkAccountId,
            isBooked = isBooked,
            paymtPurpose = paymtPurpose,
            startDate = startDate,
            endDate = endDate,
            payeePayerName = payeePayerName,
            onlyCredit = onlyCredit,
            onlyDebit = onlyDebit,

            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!
        )
    }

    /**
     * Create a new transaction
     *
     * Creates a new transaction on a check account.
     *
     * @param valueDate Date the check account transaction was booked
     * @param entryDate Date the check account transaction was imported
     * @param paymtPurpose The purpose of the transaction
     * @param amount Amount of the transaction
     * @param payeePayerName Name of the other party
     * @param payeePayerAcctNo IBAN or account number of the other party
     * @param payeePayerBankCode BIC or bank code of the other party
     * @param checkAccount The check account to which the transaction belongs
     * @param status Status of the check account transaction.
     * @param sourceTransaction The check account transaction serving as the source of the rebooking
     * @param targetTransaction The check account transaction serving as the target of the rebooking
     */
    suspend fun createNewTransaction(
        valueDate: OffsetDateTime,
        entryDate: OffsetDateTime? = null,
        paymtPurpose: String? = null,
        amount: Float,
        payeePayerName: String? = null,
        payeePayerAcctNo: String? = null,
        payeePayerBankCode: String? = null,
        checkAccount: Int,
        status: CheckAccountTransaction.Status,
        sourceTransaction: Int? = null,
        targetTransaction: Int? = null
    ): CheckAccountTransaction? {
        return sevDeskAPI.requestWithHandling(

        {
            val jsonObject = buildJsonObject {
                put("valueDate", valueDate.toString())
                put("entryDate", entryDate.toString())
                put("paymtPurpose", paymtPurpose)
                put("amount", amount)
                put("payeePayerName", payeePayerName)
                put("payeePayerAcctNo", payeePayerAcctNo)
                put("payeePayerBankCode", payeePayerBankCode)
                put("checkAccount", buildJsonObject {
                    put("id", checkAccount)
                    put("objectName", "CheckAccount")
                })
                put("status", status.idRepresentation)
                if (sourceTransaction != null) put("sourceTransaction", buildJsonObject {
                    put("id", sourceTransaction)
                    put("objectName", "CheckAccountTransaction")
                })
                if (targetTransaction != null) put("targetTransaction", buildJsonObject {
                    put("id", targetTransaction)
                    put("objectName", "CheckAccountTransaction")
                })
            }

            sevDeskAPI.client.post("CheckAccountTransaction") {
                setJSONObjectBody(jsonObject)
            }
        },
            {
                return@requestWithHandling Json.decodeFromJsonElement(CheckAccountTransactionSerializer(), it["objects"]!!)
            })
    }

    /**
     * Find check account transaction by ID
     *
     * Retrieve an existing check account transaction
     *
     * @param checkAccountTransactionId ID of check account transaction
     */
    suspend fun findCheckAccountTransactionByID(
        checkAccountTransactionId: Int
    ): CheckAccountTransaction? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("CheckAccountTransaction/$checkAccountTransactionId")
            },
            {
                val jsonElements: JsonArray = it["objects"] as JsonArray
                if (jsonElements.isEmpty()) return@requestWithHandling null

                return@requestWithHandling Json.decodeFromJsonElement(CheckAccountTransactionSerializer(),
                    jsonElements[0]
                )
            }
        )
    }

    /**
     * Update an existing check account transaction
     *
     * Update a check account transaction
     *
     * @param checkAccountTransactionId ID of check account to update transaction
     * @param valueDate Date the check account transaction was booked
     * @param entryDate Date the check account transaction was imported
     * @param paymtPurpose The purpose of the transaction
     * @param amount Amount of the transaction
     * @param payeePayerName Name of the payee/payer
     * @param checkAccount The check account to which the transaction belongs
     * @param status Status of the check account transaction.
     * @param sourceTransaction The check account transaction serving as the source of the rebooking
     * @param targetTransaction The check account transaction serving as the target of the rebooking
     */
    suspend fun updateExistingCheckAccountTransaction(
        checkAccountTransactionId: Int,

        valueDate: OffsetDateTime? = null,
        entryDate: OffsetDateTime? = null,
        paymtPurpose: String? = null,
        amount: Float? = null,
        payeePayerName: String? = null,
        checkAccount: Int? = null,
        status: CheckAccountTransaction.Status? = null,
        sourceTransaction: Int? = null,
        targetTransaction: Int? = null
    ): CheckAccountTransaction? {
        return sevDeskAPI.requestWithHandling(
            {
                val jsonObject = buildJsonObject {
                    put("valueDate", valueDate.toString())
                    put("entryDate", entryDate.toString())
                    put("paymtPurpose", paymtPurpose)
                    put("amount", amount)
                    put("payeePayerName", payeePayerName)
                    if (checkAccount != null) put("checkAccount", buildJsonObject {
                        put("id", checkAccount)
                        put("objectName", "CheckAccount")
                    })
                    put("status", status?.idRepresentation)
                    if (sourceTransaction != null) put("sourceTransaction", buildJsonObject {
                        put("id", sourceTransaction)
                        put("objectName", "CheckAccountTransaction")
                    })
                    if (targetTransaction != null) put("targetTransaction", buildJsonObject {
                        put("id", targetTransaction)
                        put("objectName", "CheckAccountTransaction")
                    })
                }

                sevDeskAPI.client.put("CheckAccountTransaction/$checkAccountTransactionId") {
                    setJSONObjectBody(jsonObject)
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(CheckAccountTransactionSerializer(),
                    it["objects"]!!
                )
            }
        )
    }

    /**
     * Update an existing check account transaction.
     *
     * Updates an existing check account transaction with the provided data.
     * Only the following properties of the `checkAccountTransaction` parameter are updated:
     *
     * - valueDate
     * - entryDate
     * - paymtPurpose
     * - amount
     * - payeePayerName
     * - checkAccount
     * - status
     * - sourceTransaction
     * - targetTransaction
     *
     * @param checkAccountTransaction The `CheckAccountTransaction` object containing the updated values.
     * @return The updated `CheckAccountTransaction` object, or `null` if the update fails.
     */
    suspend fun updateExistingCheckAccountTransaction(
        checkAccountTransaction: CheckAccountTransaction,
    ): CheckAccountTransaction? {
        return updateExistingCheckAccountTransaction(
            checkAccountTransaction.id,
            checkAccountTransaction.valueDate,
            checkAccountTransaction.entryDate,
            checkAccountTransaction.paymtPurpose,
            checkAccountTransaction.amount,
            checkAccountTransaction.payeePayerName,
            checkAccountTransaction.checkAccount?.id,
            checkAccountTransaction.status,
            checkAccountTransaction.sourceTransaction?.id,
            checkAccountTransaction.targetTransaction?.id
        )
    }

    /**
     * Deletes a check account transaction.
     *
     * @param checkAccountTransactionId The ID of the check account transaction to delete.
     */
    suspend fun deleteCheckAccountTransaction(
        checkAccountTransactionId: Int
    ) {
        return sevDeskAPI.request {
            sevDeskAPI.client.delete("CheckAccountTransaction/$checkAccountTransactionId")
        }
    }

    /**
     * Deletes a check account transaction.
     *
     * @param accountTransaction The CheckAccountTransaction object to be deleted. The ID of this object is used to identify the transaction for deletion.
     */
    suspend fun deleteCheckAccountTransaction(
        accountTransaction: CheckAccountTransaction
    ) {
        return deleteCheckAccountTransaction(accountTransaction.id)
    }

    /**
     * Enshrine
     *
     * Sets the current date and time as the enshrined value for the specified account transaction.
     * This operation is permitted only if the transaction's status is "Linked" (status code 200) or higher.
     *
     * Once a transaction is enshrined, linked invoices, credit notes, and vouchers become immutable.
     *
     * @param accountTransactionId The ID of the account transaction to enshrine.
     */
    suspend fun enshrine(
        accountTransactionId: Int
    ) {
        return sevDeskAPI.request {
            sevDeskAPI.client.put("CheckAccountTransaction/$accountTransactionId/enshrine")
        }
    }

    /**
     * Enshrine
     *
     * Sets the current date and time as the enshrined value for the specified account transaction.
     * This operation is permitted only if the transaction's status is "Linked" (status code 200) or higher.
     *
     * Once a transaction is enshrined, linked invoices, credit notes, and vouchers become immutable.
     *
     * @param accountTransaction The account transaction to enshrine. The ID of this object is used to identify the transaction for enshrinement.
     */
    suspend fun enshrine(
        accountTransaction: CheckAccountTransaction
    ) {
        return enshrine(accountTransaction.id)
    }
}

