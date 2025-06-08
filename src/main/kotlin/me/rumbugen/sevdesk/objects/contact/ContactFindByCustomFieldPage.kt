package me.rumbugen.sevdesk.objects.contact

import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.requests.ContactRequests

class ContactFindByCustomFieldPage(
    val contactRequests: ContactRequests,

    val value: String,
    val customFieldId: Int?,
    val customFieldName: String?,

    override val items: List<Contact>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int?
) : Page<Contact>(items, limit, offset, total) {
    override suspend fun next(): Page<Contact> {
        require(hasNext()) { "No next page available" }

        val newOffset = limit * currentPage
        val checkAccounts =
            contactRequests.findContactsByCustomFieldValue(value, customFieldId, customFieldName, countAll = true, limit = limit, offset = newOffset)
        return ContactFindByCustomFieldPage(
            contactRequests = contactRequests,

            value = value,
            customFieldId = customFieldId,
            customFieldName = customFieldName,

            items = checkAccounts.first,
            limit = limit,
            offset = newOffset,
            total = checkAccounts.second!!
        )
    }

    override suspend fun previous(): Page<Contact> {
        require(hasPrevious()) { "No previous page available" }

        val newOffset = limit * (currentPage - 1)
        val checkAccounts =
            contactRequests.findContactsByCustomFieldValue(value, customFieldId, customFieldName, countAll = true, limit = limit, offset = newOffset)
        return ContactFindByCustomFieldPage(
            contactRequests = contactRequests,

            value = value,
            customFieldId = customFieldId,
            customFieldName = customFieldName,

            items = checkAccounts.first,
            limit = limit,
            offset = offset,
            total = checkAccounts.second!!
        )
    }
}