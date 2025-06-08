package me.rumbugen.sevdesk.objects.communicationWay.key

import me.rumbugen.sevdesk.objects.SevDeskObject
import java.time.OffsetDateTime

data class CommunicationWayKey(
    /**
     * The communication way id
     */
    override val id: Int,

    val name: String? = null,
    val translationCode: String? = null,
): SevDeskObject(id)