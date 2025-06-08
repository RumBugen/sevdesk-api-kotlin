package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import me.rumbugen.sevdesk.objects.contact.customField.ContactCustomField
import me.rumbugen.sevdesk.objects.contact.customField.setting.ContactCustomFieldSetting
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class TestContactFieldRequests {
    val requests = TestObjects.sevDeskAPI.contactFieldRequest()

    @Test
    fun retrieveContactFields() = runBlocking {
        createTestContactField()
        createTestContactField()

        val page = requests.retrieveContactFieldsWithPagination(perPage = 1)
        assert(page.items.size == 1)

        val next = page.next()
        assert(next.items.size == 1)
        assert(next.previous().items.size == 1)
    }

    @Test
    fun createContactField() = runBlocking {
        createTestContactField()
        return@runBlocking
    }

    @Test
    fun retrieveContactFieldByID() = runBlocking {
        val contactField = createTestContactField()

        val item = requests.findContactFieldByID(contactField.id)
        assertNotNull(item)
        assert(item == contactField)
    }

    @Test
    fun updateContactField() = runBlocking {
        val contactField = createTestContactField()

        val field: ContactCustomField? = requests.updateContactField(
            contactField.id,
            value = "UpdatedTest"
        )
        assertNotNull(field)
        assert(contactField.id == field.id)
        assert(field.value == "UpdatedTest")
    }

    @Test
    fun deleteContactField() = runBlocking {
        val contactField = createTestContactField()

        requests.deleteContactField(contactField.id)
        assert(requests.findContactFieldSettingByID(contactField.id) == null)
    }

    @Test
    fun retrieveContactFieldSettings() = runBlocking {
        createTestContactFieldSetting()
        createTestContactFieldSetting()

        val page = requests.retrieveContactFieldSettingsWithPagination(perPage = 1)
        assert(page.items.size == 1)

        val next = page.next()
        assert(next.items.size == 1)
        assert(next.previous().items.size == 1)
    }

    @Test
    fun createContactFieldSetting() = runBlocking {
        createTestContactFieldSetting()
        return@runBlocking
    }

    @Test
    fun findContactFieldSettingByID() = runBlocking {
        val setting = createTestContactFieldSetting()

        val byID = requests.findContactFieldSettingByID(setting.id)
        assertNotNull(byID)
        assert(byID == setting)
    }

    @Test
    fun updateContactFieldSetting() = runBlocking {
        val contactFieldSetting = createTestContactFieldSetting()

        val updateContactFieldSetting = requests.updateContactFieldSetting(
            contactFieldSetting.id,
            "Test35",
            "Test25"
        )

        requireNotNull(updateContactFieldSetting)
        assert(updateContactFieldSetting.id == contactFieldSetting.id)
        assert(updateContactFieldSetting.name == "Test35")
        assert(updateContactFieldSetting.description == "Test25")
    }

    @Test
    fun deleteContactFieldSetting() = runBlocking {
        val contactFieldSetting = createTestContactFieldSetting()

        requests.deleteContactFieldSetting(contactFieldSetting.id)
        assert(requests.findContactFieldSettingByID(contactFieldSetting.id) == null)
    }

    @Test
    fun receiveCountReference() = runBlocking {
        val contactFieldSetting = createTestContactFieldSetting()
        assert(requests.receiveCountReference(contactFieldSetting.id) == 0)
    }

    companion object {
        suspend fun createTestContactFieldSetting(): ContactCustomFieldSetting {
            val setting = TestObjects.sevDeskAPI.contactFieldRequest().createContactFieldSetting(
                "Test",
                "Test2"
            )
            requireNotNull(setting)
            assert(setting.name == "Test")
            assert(setting.description == "Test2")

            return setting
        }

        suspend fun createTestContactField(): ContactCustomField {
            val fieldSetting = createTestContactFieldSetting()
            val contact = TestContactRequest.testCreateContact()
            val setting = TestObjects.sevDeskAPI.contactFieldRequest().createContactField(
                contact,
                fieldSetting,
                "Test"
            )

            requireNotNull(setting)
            assert(setting.value == "Test")
            assert(setting.contact!!.id == contact.id)
            assert(setting.contactCustomFieldSetting!!.id == fieldSetting.id)

            return setting
        }
    }

}