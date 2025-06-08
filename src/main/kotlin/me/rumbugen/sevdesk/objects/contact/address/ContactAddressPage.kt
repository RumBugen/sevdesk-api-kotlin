package me.rumbugen.sevdesk.objects.contact.address

import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.requests.ContactAddressRequests

class ContactAddressPage(
    val contactAddressRequests: ContactAddressRequests,

    override val items: List<ContactAddress>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int?
) : Page<ContactAddress>(items, limit, offset, total) {
    override suspend fun next(): Page<ContactAddress> {
        require(hasNext()) { "No next page available" }

        val newOffset = limit * currentPage
        return makeRequest(newOffset)
    }

    override suspend fun previous(): Page<ContactAddress> {
        require(hasPrevious()) { "No previous page available" }

        val newOffset = limit * (currentPage - 1)
        return makeRequest(newOffset)
    }

    private suspend fun makeRequest(newOffset: Int): Page<ContactAddress> {
        val checkAccounts =
            contactAddressRequests.retrieveContactAddresses(
                countAll = true,
                limit = limit,
                offset = newOffset
            )
        return ContactAddressPage(
            contactAddressRequests = contactAddressRequests,

            items = checkAccounts.first,
            limit = limit,
            offset = newOffset,
            total = checkAccounts.second!!
        )
    }
}