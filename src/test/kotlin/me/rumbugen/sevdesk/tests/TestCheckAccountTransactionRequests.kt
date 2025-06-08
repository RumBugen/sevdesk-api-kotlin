package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.rumbugen.sevdesk.objects.checkAccount.transaction.CheckAccountTransaction
import me.rumbugen.sevdesk.objects.checkAccount.transaction.CheckAccountTransactionSerializer
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class TestCheckAccountTransactionRequests {
    val checkAccountRequests = TestObjects.sevDeskAPI.checkAccountRequest()
    val checkAccountTransactionRequest = TestObjects.sevDeskAPI.checkAccountTransactionRequest()

    @Test
    fun testCheckAccountTransaction() = runBlocking {
        val items = mutableListOf<Int>()

        for (account in checkAccountTransactionRequest.retrieveTransactionsWithoutPagination()) {
            assert(!items.contains(account.id))
            items.add(account.id)
        }
    }

    @Test
    fun testCheckAccountTransactionPagination() = runBlocking {
        var page = checkAccountTransactionRequest.retrieveTransactionsWithPagination(1, 1)
        val items = mutableListOf<Int>()

        val total: Int = page.total!!
        for (i in 1..total) {
            assert(page.items.size == 1) { "Item size must be 1" }
            assert(!items.contains(page.items[0].id)) { "Item must not contain the same id" }
            items.add(page.items[0].id)

            if (i < total) {
                assert(page.hasNext()) { "Next page must be not empty" }
                page = page.next()
            } else {
                assert(!page.hasNext()) { "Next page must be empty" }
            }
        }

        page = page.previous()
        assert(items.contains(page.items[0].id))
    }

    @Test
    fun testCreateNewTransaction() = runBlocking {
        val accounts = checkAccountRequests.retrieveCheckAccountsWithoutPagination()
        assert(accounts.isNotEmpty())

        val createNewTransaction = checkAccountTransactionRequest.createNewTransaction(
            OffsetDateTime.now(),
            amount = 1.0F,
            checkAccount = accounts[0].id,
            status = CheckAccountTransaction.Status.PRIVATE
        )
        assert(createNewTransaction != null)
    }

    @Test
    fun testAccountById() = runBlocking {
        val accountTransactions =
            checkAccountTransactionRequest.retrieveTransactionsWithoutPagination()
        assert(accountTransactions.isNotEmpty())

        val transaction: CheckAccountTransaction? =
            checkAccountTransactionRequest.findCheckAccountTransactionByID(accountTransactions[0].id)
        assert(transaction != null)
        assert(transaction!!.id == accountTransactions[0].id)
    }

    @Test
    fun testUpdateExistingCheckAccountTransaction() = runBlocking {
        val accountTransactions =
            checkAccountTransactionRequest.retrieveTransactionsWithoutPagination()

        assert(accountTransactions.isNotEmpty())
        val transaction: CheckAccountTransaction = accountTransactions[0]
        transaction.amount = 5.0F
        transaction.paymtPurpose = "${transaction.paymtPurpose}+"
        transaction.status = CheckAccountTransaction.Status.BOOKED

        val accountTransaction: CheckAccountTransaction? =
            checkAccountTransactionRequest.updateExistingCheckAccountTransaction(transaction)

        assert(accountTransaction != null)
        assert(accountTransaction!!.id == transaction.id)
        assert(accountTransaction.status == CheckAccountTransaction.Status.BOOKED)
        assert(accountTransaction.paymtPurpose == transaction.paymtPurpose)
        assert(accountTransaction.amount == 5.0F)
    }

    @Test
    fun testDeleteCheckAccountTransaction() = runBlocking {
        val accounts = checkAccountRequests.retrieveCheckAccountsWithoutPagination()
        assert(accounts.isNotEmpty())

        val createNewTransaction = checkAccountTransactionRequest.createNewTransaction(
            OffsetDateTime.now(),
            amount = 100.0F,
            checkAccount = accounts[0].id,
            status = CheckAccountTransaction.Status.CREATED
        )
        assert(createNewTransaction != null)

        checkAccountTransactionRequest.deleteCheckAccountTransaction(createNewTransaction!!.id)
    }

    @Test
    fun testEnshrine() = runBlocking {
        val accounts = checkAccountRequests.retrieveCheckAccountsWithoutPagination()
        assert(accounts.isNotEmpty())

        val createNewTransaction = checkAccountTransactionRequest.createNewTransaction(
            OffsetDateTime.now(),
            amount = 123.52F,
            checkAccount = accounts[0].id,
            status = CheckAccountTransaction.Status.LINKED,
        )
        assert(createNewTransaction != null)

        checkAccountTransactionRequest.enshrine(createNewTransaction!!.id)
    }

}