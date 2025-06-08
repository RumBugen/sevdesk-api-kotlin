package me.rumbugen.sevdesk.objects.basic

import me.rumbugen.sevdesk.objects.SevDeskObject
import java.time.OffsetDateTime
import kotlin.hashCode

/**
 * Category's
 *
 * All this is undocumented in SevDesk
 */
data class Category(
    override val id: Int,
    override var create: OffsetDateTime? = null,
    override var update: OffsetDateTime? = null,
    override var sevClient: SevClient? = null,
    var name: String? = null,
    var objectType: Type? = null,
    var priority: Int? = null,
    var code: String? = null,
    var color: String? = null,
    var postingAccount: String? = null,
    var type: String? = null,
    var translationCode: String? = null,
): SevDeskObject(id, create, update, sevClient) {
    enum class Type {
        Document,
        ContactAddress,
        Task,
        Contact,
        ProjectTime,
        Project,
        Part
    }
}