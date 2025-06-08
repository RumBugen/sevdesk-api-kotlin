package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.checkAccount.CheckAccount
import me.rumbugen.sevdesk.objects.checkAccount.CheckAccountSerializer
import me.rumbugen.sevdesk.objects.invoice.Invoice
import me.rumbugen.sevdesk.requests.CheckAccountRequests
import org.junit.jupiter.api.Test
import java.time.LocalDate

class TestCheckAccountRequests {
    val checkAccountRequest: CheckAccountRequests = TestObjects.sevDeskAPI.checkAccountRequest()

    @Test
    fun testCheckAccounts() = runBlocking {
        val items = mutableListOf<Int>()

        for (account in checkAccountRequest.retrieveCheckAccountsWithoutPagination()) {
            assert(!items.contains(account.id)) { "Item must not contain the same id" }
            items.add(account.id)
        }
    }

    @Test
    fun createFileImportAccount() = runBlocking {
        val accountingNumbers: MutableList<Int> = mutableListOf()

        for (account in checkAccountRequest.retrieveCheckAccountsWithoutPagination()) {
            accountingNumbers.add(account.accountingNumber!!)
        }

        val accountingNumber = (1805..1850).firstOrNull { it !in accountingNumbers }!!
        val checkAccount: CheckAccount = checkAccountRequest.createFileImportAccount(
            "Test",
            CheckAccount.ImportType.CSV,
            accountingNumber,
            "DE12345678910"
        )!!

        assert(checkAccount.name == "Test")
        assert(checkAccount.importType == CheckAccount.ImportType.CSV)
        assert(checkAccount.accountingNumber == accountingNumber)
        assert(checkAccount.iban == "DE12345678910")

        return@runBlocking
    }

    @Test
    fun testCheckAccountsPagination() = runBlocking {
        val pagination: Page<CheckAccount> =
            checkAccountRequest.retrieveCheckAccountsWithPagination(perPage = 1)
        assert(pagination.items.size == 1)

        val next: Page<CheckAccount> = pagination.next()
        assert(next.items.size == 1)
        assert(next.previous().items.size == 1)
    }

    @Test
    fun createFileClearingAccountAndDeletion() = runBlocking {
        val checkAccount: CheckAccount = checkAccountRequest.createFileClearingAccount(
            "Test-Clearing"
        )!!

        assert(checkAccount.name == "Test-Clearing")
        return@runBlocking
    }

    @Test
    fun findCheckAccountById() = runBlocking {
        // Test Valid
        for (account in checkAccountRequest.retrieveCheckAccountsWithoutPagination()) {
            val findCheckAccountById: CheckAccount? = checkAccountRequest.findCheckAccountById(account.id)
            assert(findCheckAccountById != null)
            assert(findCheckAccountById!!.id == account.id)
            break
        }

        // Test Invalid
        val findCheckAccountById: CheckAccount? = checkAccountRequest.findCheckAccountById(0)
        assert(findCheckAccountById == null)

        return@runBlocking
    }

    @Test
    fun updateExistingCheckAccount() = runBlocking {
        // Test Valid
        var checkAccount: CheckAccount? = null
        val accountingNumbers: MutableList<Int> = mutableListOf()

        for (account in checkAccountRequest.retrieveCheckAccountsWithoutPagination()) {
            accountingNumbers.add(account.accountingNumber!!)

            checkAccount = account;
        }
        assert(checkAccount != null)

        checkAccount!!.name = "${checkAccount.name}+"
        var updatedAccount = checkAccountRequest.updateExistingCheckAccount(checkAccount) // Method 1
        assert(updatedAccount != null)
        assert(checkAccount.id == updatedAccount!!.id)
        assert(checkAccount.name == updatedAccount.name)

        updatedAccount = checkAccountRequest.updateExistingCheckAccount( // Method 2
            checkAccount.id,
            "renamed",
            checkAccount.type!!,
            "USD",
            (1805..1850).firstOrNull { it !in accountingNumbers }!!, // 1801 to 1859 is the range
            importType = CheckAccount.ImportType.CSV
        )

        assert(updatedAccount != null)
        assert(checkAccount.id == updatedAccount!!.id)
        assert("USD" == updatedAccount.currency)
        assert("renamed" == updatedAccount.name)

        // Test Invalid

        updatedAccount = updatedAccount.copy(id = 1)

        assert(checkAccountRequest.updateExistingCheckAccount(updatedAccount) == null) // Method 1
        assert(
            checkAccountRequest.updateExistingCheckAccount(
                updatedAccount.id,
                "test",
                updatedAccount.type!!,
                updatedAccount.currency!!,
                updatedAccount.accountingNumber!!
            ) == null
        ) // Method 2
    }

    @Test
    fun deleteCheckAccount() = runBlocking {
        val checkAccount = checkAccountRequest.createFileClearingAccount("Test-Deletion")!!
        assert(checkAccount.name == "Test-Deletion")

        checkAccountRequest.deleteCheckAccount(checkAccount.id)

        val accountById = checkAccountRequest.findCheckAccountById(checkAccount.id)
        assert(accountById != null)
        assert(accountById!!.status == CheckAccount.Status.ARCHIVED)
    }

    @Test
    fun getBalanceAtGivenDate() = runBlocking {
        for (account in checkAccountRequest.retrieveCheckAccountsWithoutPagination()) {
            val balance = checkAccountRequest.getBalanceAtGivenDate(account, LocalDate.now())
            assert(balance != null)
            break
        }

        val balance = checkAccountRequest.getBalanceAtGivenDate(1, LocalDate.now())
        assert(balance == null)
    }

}