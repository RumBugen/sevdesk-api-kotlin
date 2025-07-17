package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import me.rumbugen.sevdesk.objects.basic.Category
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.contact.address.ContactAddress
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountry
import me.rumbugen.sevdesk.tests.TestObjects.createTestContact
import me.rumbugen.sevdesk.tests.TestObjects.sevDeskAPI
import org.junit.jupiter.api.Test
import java.util.Locale
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TestContactAddressRequests {
    @Test
    fun createNewContactAddress() = runBlocking {
        createTestContactAddress()
        return@runBlocking
    }

    @Test
    fun retrieveContactAddresses() = runBlocking {
        createTestContactAddress()
        createTestContactAddress()

        val page = requests.retrieveContactAddressesWithPagination(perPage = 1)
        assert(page.items.size == 1)

        val next = page.next()
        assert(next.items.size == 1)
        assert(next.previous().items.size == 1)
    }

    @Test
    fun findContactAddressByID() = runBlocking {
        val address: ContactAddress = createTestContactAddress()

        val addressByID = assertNotNull(requests.findContactAddressByID(address.id))
        assert(addressByID.id == address.id)
    }

    @Test
    fun updateExistingContactAddress() = runBlocking {
        val address: ContactAddress = createTestContactAddress()

        val name = "${address.name}+"
        address.name = name

        val updatedAddress = assertNotNull(requests.updateExistingContactAddress(address))
        assert(updatedAddress.id == address.id)
        assert(updatedAddress.name == address.name)
    }

    @Test
    fun deleteContactAddress() = runBlocking {
        val address: ContactAddress = createTestContactAddress()

        requests.deleteContactAddress(address.id)

        assertNull(requests.findContactAddressByID(address.id))
    }

    companion object {
        val requests = sevDeskAPI.contactAddressRequest()

        suspend fun createTestContactAddress(): ContactAddress {
            val contact: Contact = createTestContact()

            val country: StaticCountry? = sevDeskAPI.basicsRequest().retrieveCountryList(Locale.GERMANY)
            assertNotNull(country)

            val categories: List<Category> =
                sevDeskAPI.basicsRequest().retrieveCategories(type = Category.Type.ContactAddress)
            assert(categories.isNotEmpty())

            val address = assertNotNull(sevDeskAPI.contactAddressRequest().createNewContactAddress(
                contact,
                "TestStreet",
                "123456",
                "Berlin",
                country,
                categories[0],
                "Test",
                "Test2",
                "Test3",
                "Test4"
            ))
            assert(address.city == "Berlin")
            assert(address.name == "Test")

            return address
        }
    }
}