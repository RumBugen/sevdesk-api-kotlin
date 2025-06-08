package me.rumbugen.sevdesk.objects.checkAccount.transaction

import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.requests.CheckAccountTransactionRequests
import java.time.LocalDateTime

class CheckAccountTransactionPage(
    val checkAccountTransactionRequests: CheckAccountTransactionRequests,
    
    val checkAccountId: Int?,
    val isBooked: Boolean?,
    val paymtPurpose: String?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val payeePayerName: String?,
    val onlyCredit: Boolean?,
    val onlyDebit: Boolean?,
    
    override val items: List<CheckAccountTransaction>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int?
) : Page<CheckAccountTransaction>(items, limit, offset, total) {
    override suspend fun next(): Page<CheckAccountTransaction> {
        require(hasNext()) { "No next page available" }

        val newOffset = limit * currentPage
        val checkAccounts =
            checkAccountTransactionRequests.retrieveTransactions(countAll = true, limit = limit, offset = newOffset)
        return CheckAccountTransactionPage(
            checkAccountTransactionRequests = checkAccountTransactionRequests,

            checkAccountId = checkAccountId,
            isBooked = isBooked,
            paymtPurpose = paymtPurpose,
            startDate = startDate,
            endDate = endDate,
            payeePayerName = payeePayerName,
            onlyCredit = onlyCredit,
            onlyDebit = onlyDebit,

            items = checkAccounts.first,
            limit = limit,
            offset = newOffset,
            total = checkAccounts.second!!
        )
    }

    override suspend fun previous(): Page<CheckAccountTransaction> {
        require(hasPrevious()) { "No previous page available" }

        val newOffset = limit * (currentPage - 1)
        val checkAccounts =
            checkAccountTransactionRequests.retrieveTransactions(countAll = true, limit = limit, offset = newOffset)
        return CheckAccountTransactionPage(
            checkAccountTransactionRequests = checkAccountTransactionRequests,

            checkAccountId = checkAccountId,
            isBooked = isBooked,
            paymtPurpose = paymtPurpose,
            startDate = startDate,
            endDate = endDate,
            payeePayerName = payeePayerName,
            onlyCredit = onlyCredit,
            onlyDebit = onlyDebit,

            items = checkAccounts.first,
            limit = limit,
            offset = offset,
            total = checkAccounts.second!!
        )
    }
}