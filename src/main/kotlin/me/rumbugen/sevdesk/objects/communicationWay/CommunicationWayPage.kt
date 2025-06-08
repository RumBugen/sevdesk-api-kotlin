package me.rumbugen.sevdesk.objects.communicationWay

import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.requests.CommunicationWayRequests

class CommunicationWayPage(
    val communicationWayRequests: CommunicationWayRequests,

    val contactId: Int? = null,
    val type: CommunicationWay.Type? = null,
    val main: Boolean? = null,

    override val items: List<CommunicationWay>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int?
) : Page<CommunicationWay>(items, limit, offset, total) {
    override suspend fun next(): Page<CommunicationWay> {
        require(hasNext()) { "No next page available" }

        val newOffset = limit * currentPage
        return makeRequest(newOffset)
    }

    override suspend fun previous(): Page<CommunicationWay> {
        require(hasPrevious()) { "No previous page available" }

        val newOffset = limit * (currentPage - 1)
        return makeRequest(newOffset)
    }

    private suspend fun makeRequest(newOffset: Int): Page<CommunicationWay> {
        val communicationWays =
            communicationWayRequests.retrieveCommunicationWays(
                contactId,
                type,
                main,
                countAll = true,
                limit = limit,
                offset = newOffset
            )
        return CommunicationWayPage(
            communicationWayRequests = communicationWayRequests,

            contactId,
            type,
            main,

            items = communicationWays.first,
            limit = limit,
            offset = newOffset,
            total = communicationWays.second!!
        )
    }
}