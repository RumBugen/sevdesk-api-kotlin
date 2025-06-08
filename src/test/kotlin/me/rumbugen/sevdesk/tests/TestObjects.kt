package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.objects.basic.Category
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.communicationWay.CommunicationWay
import me.rumbugen.sevdesk.objects.sevDeskFile.SevDeskFile

import java.io.File
import kotlin.test.assertNotNull

object TestObjects {

    private const val API_KEY_FILE = "api_key"

    val sevDeskAPI: SevDeskAPI by lazy {
        val apiKey = loadApiKeyFromFile()
        SevDeskAPI(apiKey)
    }

    private fun loadApiKeyFromFile(): String {
        val file = File(API_KEY_FILE)

        if (!file.exists()) {
            throw IllegalStateException("File not found")
        }

        return file.readLines()[0].trim()
    }

    fun createTestContact(): Contact = runBlocking {
        val first = sevDeskAPI.basicsRequest().retrieveCategories(type = Category.Type.Contact).first()
        val createContact: Contact? = sevDeskAPI.contactRequest().createContact(
            name = "Test",
            categoryId = first.id,
            status = Contact.Status.ACTIVE,
            description = "Something..."
        )
        assertNotNull(createContact)
        assert(createContact.name == "Test")

        return@runBlocking createContact
    }


    fun createTestCommunicationWay(): CommunicationWay = runBlocking {
        val contact: Contact = createTestContact()
        val communicationWay: CommunicationWay? = sevDeskAPI.communicationWayRequest().createNewContactCommunicationWay(
            contact.id,
            CommunicationWay.Type.MOBILE,
            "123456782",
            sevDeskAPI.communicationWayRequest().retrieveCommunicationWayKeys().first()
        )

        assertNotNull(communicationWay)
        assert(communicationWay.type == CommunicationWay.Type.MOBILE)
        assert(communicationWay.value == "123456782")

        return@runBlocking communicationWay
    }

    fun testSevDeskFile(sevDeskFile: SevDeskFile?) {
        requireNotNull(sevDeskFile)
        requireNotNull(sevDeskFile.base64Encoded)
        requireNotNull(sevDeskFile.mimetype)
        requireNotNull(sevDeskFile.content)
        requireNotNull(sevDeskFile.filename)
        requireNotNull(sevDeskFile.getStream().readAllBytes().isNotEmpty())
    }
}