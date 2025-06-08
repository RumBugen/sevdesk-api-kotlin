package me.rumbugen.sevdesk.objects.basic

import kotlinx.serialization.Serializable
import me.rumbugen.sevdesk.objects.SevDeskObject
import java.time.OffsetDateTime


/**
 * Represents a client with a unique identifier.
 *
 * This class holds information about a client within the system.
 */
@Serializable(with = SevClientSerializer::class)
data class SevClient(
    /**
     * Unique identifier of the client
     */
    override var id: Int,

    // objectName is not included because the class does the job

    // the following I found with Reverse Engineering and it's not official documented

    /**
     * Additional information.
     *
     * Note: This field is undocumented in the SevDesk API, so its usage and requirements may be subject to change.
     * TODO: Found out if this is
     */
    var additionalInformation: String? = null,

    override var create: OffsetDateTime? = null,
    override var update: OffsetDateTime? = null,
    var name: String? = null,
    var templateMainColor: String? = null,
    var templateSubColor: String? = null,
    var status: Int? = null,
) : SevDeskObject(id, create, update)