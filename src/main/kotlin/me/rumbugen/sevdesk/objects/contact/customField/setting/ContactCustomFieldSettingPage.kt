package me.rumbugen.sevdesk.objects.contact.customField.setting

import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.contact.address.ContactAddress
import me.rumbugen.sevdesk.objects.contact.customField.ContactCustomField
import me.rumbugen.sevdesk.requests.ContactAddressRequests
import me.rumbugen.sevdesk.requests.ContactFieldRequests

class ContactCustomFieldSettingPage(
    val contactAddressRequests: ContactFieldRequests,

    override val items: List<ContactCustomFieldSetting>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int?
) : Page<ContactCustomFieldSetting>(items, limit, offset, total) {
    override suspend fun next(): Page<ContactCustomFieldSetting> {
        require(hasNext()) { "No next page available" }

        val newOffset = limit * currentPage
        return makeRequest(newOffset)
    }

    override suspend fun previous(): Page<ContactCustomFieldSetting> {
        require(hasPrevious()) { "No previous page available" }

        val newOffset = limit * (currentPage - 1)
        return makeRequest(newOffset)
    }

    private suspend fun makeRequest(newOffset: Int): Page<ContactCustomFieldSetting> {
        val checkAccounts =
            contactAddressRequests.retrieveContactFieldSettings(
                countAll = true,
                limit = limit,
                offset = newOffset
            )
        return ContactCustomFieldSettingPage(
            contactAddressRequests = contactAddressRequests,

            items = checkAccounts.first,
            limit = limit,
            offset = newOffset,
            total = checkAccounts.second!!
        )
    }
}