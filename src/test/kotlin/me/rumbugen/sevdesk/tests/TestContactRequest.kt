package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.basic.Category
import me.rumbugen.sevdesk.objects.contact.Contact
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TestContactRequest {

    @Test
    fun testNextFreeCustomerNumber() = runBlocking {
        val number: Int = requests.getNextFreeCustomerNumber()
        assert(number >= 1000)
    }

    @Test
    fun testCreateContact() = runBlocking {
    }

    // TODO Customfields test

    @Test
    fun testRetrieveContacts() = runBlocking {
        testCreateContact()
        testCreateContact()

        val contacts = requests.retrieveContactsWithoutPagination(name = "Test")
        assert(contacts.isNotEmpty())

        var page: Page<Contact> =
            requests.retrieveContactsWithPagination(name = "Test", perPage = 1)
        assert(page.items.size == 1)
        assert(page.hasNext())
        page = page.next()
        assert(page.items.size == 1)
        assert(page.items.first().name == "Test")
    }

    @Test
    fun testFindContactByID() = runBlocking {
        testCreateContact()
        val contact: Contact = requests.retrieveContactsWithoutPagination(limit = 1).first()
        assertNotNull(contact)

        val contactByID: Contact? = requests.findContactByID(contact.id)
        assertNotNull(contactByID)

        assert(contactByID.id == contact.id)
        assert(contactByID.name == contact.name)
    }

    @Test
    fun testUpdateContact() = runBlocking {
        testCreateContact()
        val contact: Contact = requests.retrieveContactsWithoutPagination(limit = 1).first()
        assertNotNull(contact)

        contact.name = "Test2"
        val contactUpdated: Contact? = requests.updateContact(contact)
        assertNotNull(contactUpdated)

        assert(contactUpdated.id == contact.id)
        assert(contactUpdated.name == contact.name)
    }

    @Test
    fun testDeleteContact() = runBlocking {
        testCreateContact()
        val contact: Contact = requests.retrieveContactsWithoutPagination(limit = 1).first()
        assertNotNull(contact)

        requests.deleteContact(contact.id)

        val contactByID: Contact? = requests.findContactByID(contact.id)
        assertNull(contactByID)
    }

    @Test
    fun testGetNumberOfAllItems() = runBlocking {
        testCreateContact()
        val contact: Contact = requests.retrieveContactsWithoutPagination().first()
        assertNotNull(contact)
        assert(requests.getNumberOfAllItems(contact.id).isNotEmpty())
        return@runBlocking
    }

    @Test
    fun testCustomerNumberAvailable() = runBlocking {
        testCreateContact()
        val first: Contact = requests.retrieveContactsWithoutPagination().first()

        assertFalse(requests.customerNumberAvailable(first.customerNumber!!))
        assert(requests.customerNumberAvailable(requests.getNextFreeCustomerNumber().toString()))
    }

    companion object {
        val requests = TestObjects.sevDeskAPI.contactRequest()

        suspend fun testCreateContact(): Contact {
            val first = TestObjects.sevDeskAPI.basicsRequest().retrieveCategories(type = Category.Type.Contact).first()
            val createContact: Contact? = requests.createContact(
                name = "Test",
                categoryId = first.id,
                status = Contact.Status.ACTIVE,
                description = "Something..."
            )
            assertNotNull(createContact)
            assert(createContact.name == "Test")

            return createContact
        }
    }
}