package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import me.rumbugen.sevdesk.objects.basic.BookingVersion
import org.junit.jupiter.api.Test

class TestBasicRequests {
    val basicsRequest = TestObjects.sevDeskAPI.basicsRequest()

    /**
     * New Accounts are always v2, thats why I assert that it is VERSION_2
     */
    @Test
    fun testBookingVersion() = runBlocking {
        assert(basicsRequest.retrieveBookingVersion() == BookingVersion.VERSION_2)
    }

    @Test
    fun testCountrys() = runBlocking {
        val retrieveCountryList = basicsRequest.retrieveCountryList()
        assert(retrieveCountryList.size > 250)
    }

    @Test
    fun testCategory() = runBlocking {
        val retrieveCategories = basicsRequest.retrieveCategories()
        assert(retrieveCategories.size >= 27)
    }
}