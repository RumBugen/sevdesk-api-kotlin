package me.rumbugen.sevdesk.objects.contact

import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.Sorting
import me.rumbugen.sevdesk.requests.ContactRequests
import java.time.OffsetDateTime

class ContactPage(
    val contactRequests: ContactRequests,

    val depth: Boolean = false,
    val categoryId: Int? = null,
    val city: String? = null,
    val tags: List<Int>? = null,
    val customerNumber: Int? = null,
    val parentId: Int? = null,
    val name: String? = null,
    val zip: Int? = null,
    val countryId: Int? = null,
    val createBefore: OffsetDateTime? = null,
    val createAfter: OffsetDateTime? = null,
    val updateBefore: OffsetDateTime? = null,
    val updateAfter: OffsetDateTime? = null,
    val orderByCustomerNumber: Sorting? = null,

    override val items: List<Contact>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int?
) : Page<Contact>(items, limit, offset, total) {
    override suspend fun next(): Page<Contact> {
        require(hasNext()) { "No next page available" }

        val newOffset = limit * currentPage
        return makeRequest(newOffset)
    }

    override suspend fun previous(): Page<Contact> {
        require(hasPrevious()) { "No previous page available" }

        val newOffset = limit * (currentPage - 1)
        return makeRequest(newOffset)
    }

    private suspend fun makeRequest(newOffset: Int): Page<Contact> {
        val checkAccounts =
            contactRequests.retrieveContacts(
                depth,
                categoryId,
                city,
                tags,
                customerNumber,
                parentId,
                name,
                zip,
                countryId,
                createBefore,
                createAfter,
                updateBefore,
                updateAfter,
                orderByCustomerNumber,
                countAll = true,
                limit = limit,
                offset = newOffset
            )
        return ContactPage(
            contactRequests = contactRequests,

            depth,
            categoryId,
            city,
            tags,
            customerNumber,
            parentId,
            name,
            zip,
            countryId,
            createBefore,
            createAfter,
            updateBefore,
            updateAfter,
            orderByCustomerNumber,

            items = checkAccounts.first,
            limit = limit,
            offset = newOffset,
            total = checkAccounts.second!!
        )
    }
}