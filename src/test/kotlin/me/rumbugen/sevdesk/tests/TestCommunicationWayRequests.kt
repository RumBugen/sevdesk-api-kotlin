package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.communicationWay.CommunicationWay
import me.rumbugen.sevdesk.objects.communicationWay.key.CommunicationWayKey
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TestCommunicationWayRequests {
    val requests = TestObjects.sevDeskAPI.communicationWayRequest()

    @Test
    @Order(10)
    fun testCommunicationWayKeys() = runBlocking {
        assert(requests.retrieveCommunicationWayKeys().isNotEmpty())
    }

    @Test
    @Order(20)
    fun testCreateNewContactCommunicationWay() = runBlocking {
        val contact: Contact = TestObjects.createTestContact()
        val communicationWay: CommunicationWay? = requests.createNewContactCommunicationWay(
            contact.id,
            CommunicationWay.Type.MOBILE,
            "123456782",
            getCommunicationWayKeys()
        )

        assert(communicationWay?.value == "123456782")
        assert(communicationWay?.type == CommunicationWay.Type.MOBILE)
        assert(communicationWay!!.contact!!.id == contact.id)
    }

    @Test
    @Order(30)
    fun testCommunicationWays() = runBlocking {
        TestObjects.createTestCommunicationWay()
        TestObjects.createTestCommunicationWay()

        val pagination: Page<CommunicationWay> =
            requests.retrieveCommunicationWaysWithPagination(perPage = 1)
        assert(pagination.items.size == 1)

        val next: Page<CommunicationWay> = pagination.next()
        assert(next.items.size == 1)
        assert(next.previous().items.size == 1)
    }

    @Test
    @Order(40)
    fun testFindCommunicationWayByID() = runBlocking {
        val communicationWay: CommunicationWay = TestObjects.createTestCommunicationWay()

        val way: CommunicationWay? = requests.findCommunicationWayByID(communicationWay.id)
        assertNotNull(way)

        assert(way.id == communicationWay.id)
    }

    @Test
    @Order(50)
    fun testDeleteCommunicationWay() = runBlocking {
        val communicationWay: CommunicationWay = TestObjects.createTestCommunicationWay()
        requests.deleteCommunicationWay(communicationWay.id)

        assertNull(requests.findCommunicationWayByID(communicationWay.id))
    }

    @Test
    @Order(60)
    fun testUpdateExistingCommunicationWay() = runBlocking {
        val communicationWay: CommunicationWay = TestObjects.createTestCommunicationWay()

        val newValue = "${communicationWay.value}+"
        communicationWay.value = newValue

        val newWay: CommunicationWay? =
            requests.updateExistingCommunicationWay(communicationWay)

        assertNotNull(newWay)
        assert(newWay.value == newValue)
    }

    // ----------------

    suspend fun getCommunicationWayKeys(): CommunicationWayKey {
        return requests.retrieveCommunicationWayKeys()[0]
    }

}