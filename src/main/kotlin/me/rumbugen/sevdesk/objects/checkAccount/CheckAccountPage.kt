package me.rumbugen.sevdesk.objects.checkAccount

import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.requests.CheckAccountRequests

/**
 * Represents a page of [CheckAccount] data.
 *
 * This class extends the generic [Page] class and is specific to [CheckAccount] objects.
 * It provides functionality to navigate through pages of check account data.
 *
 * @param checkAccount The [me.rumbugen.sevdesk.requests.CheckAccountRequests] instance used to retrieve check accounts.
 * @param items The list of [CheckAccount] objects contained in this page.
 * @param limit The maximum number of items per page.
 * @param offset The starting index of the items in this page.
 * @param total The total number of check accounts available (optional).
 */
class CheckAccountPage(
    val checkAccount: CheckAccountRequests,
    override val items: List<CheckAccount>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int?
) : Page<CheckAccount>(items, limit, offset, total) {

    override suspend fun next(): Page<CheckAccount> {
        require(hasNext()) { "No next page available" }

        val newOffset = limit * currentPage
        val checkAccounts = checkAccount.retrieveCheckAccounts(true, limit, newOffset)
        return CheckAccountPage(
            checkAccount = checkAccount,
            items = checkAccounts.first,
            limit = limit,
            offset = newOffset,
            total = checkAccounts.second!!
        )
    }

    override suspend fun previous(): Page<CheckAccount> {
        require(hasPrevious()) { "No previous page available" }

        val newOffset = limit * (currentPage - 1)
        val checkAccounts = checkAccount.retrieveCheckAccounts(true, limit, newOffset)
        return CheckAccountPage(
            checkAccount = checkAccount,
            items = checkAccounts.first,
            limit = limit,
            offset = offset,
            total = checkAccounts.second!!
        )
    }
}