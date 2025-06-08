package me.rumbugen.sevdesk.objects.contact.customField

import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.contact.address.ContactAddress
import me.rumbugen.sevdesk.requests.ContactAddressRequests
import me.rumbugen.sevdesk.requests.ContactFieldRequests

class ContactCustomFieldPage(
    val contactAddressRequests: ContactFieldRequests,

    override val items: List<ContactCustomField>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int?
) : Page<ContactCustomField>(items, limit, offset, total) {
    override suspend fun next(): Page<ContactCustomField> {
        require(hasNext()) { "No next page available" }

        val newOffset = limit * currentPage
        return makeRequest(newOffset)
    }

    override suspend fun previous(): Page<ContactCustomField> {
        require(hasPrevious()) { "No previous page available" }

        val newOffset = limit * (currentPage - 1)
        return makeRequest(newOffset)
    }

    private suspend fun makeRequest(newOffset: Int): Page<ContactCustomField> {
        val checkAccounts =
            contactAddressRequests.retrieveContactFields(
                countAll = true,
                limit = limit,
                offset = newOffset
            )
        return ContactCustomFieldPage(
            contactAddressRequests = contactAddressRequests,

            items = checkAccounts.first,
            limit = limit,
            offset = newOffset,
            total = checkAccounts.second!!
        )
    }
}