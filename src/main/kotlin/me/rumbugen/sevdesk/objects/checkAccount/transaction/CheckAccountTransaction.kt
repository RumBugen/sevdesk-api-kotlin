package me.rumbugen.sevdesk.objects.checkAccount.transaction

import me.rumbugen.sevdesk.objects.SevDeskObject
import me.rumbugen.sevdesk.objects.basic.SevClient
import me.rumbugen.sevdesk.objects.basic.SevClientSerializer
import me.rumbugen.sevdesk.objects.checkAccount.CheckAccount
import java.time.OffsetDateTime

data class CheckAccountTransaction(
    /**
     * The check account transaction id
     */
    override val id: Int,

    // objectName is not included because the class does the job

    /**
     * Date of check account transaction creation
     */
    override var create: OffsetDateTime? = null,

    /**
     * Date of last check account transaction update
     */
    override var update: OffsetDateTime? = null,

    /**
     * Client to which check account transaction belongs. Will be filled automatically
     */
    override var sevClient: SevClient? = null,

    /**
     * Date the check account transaction was imported
     */
    var valueDate: OffsetDateTime? = null,

    /**
     * Date the check account transaction was booked
     */
    var entryDate: OffsetDateTime? = null,

    /**
     * The purpose of the transaction
     */
    var paymtPurpose: String? = null,

    /**
     * Amount of the transaction
     */
    var amount: Float? = null,

    /**
     * Name of the other party
     */
    var payeePayerName: String? = null,

    /**
     * IBAN or account number of the other party
     */
    var payeePayerAcctNo: String? = null,

    /**
     * BIC or bank code of the other party
     */
    var payeePayerBankCode: String? = null,

    /**
     * ZKA business transaction code. This can be given for finAPI accounts.
     */
    var gvCode: String? = null,

    /**
     * Transaction type, according to the bank. This can be given for finAPI accounts.
     */
    var entryText: String? = null,

    /**
     * Transaction primanota. This can be given for finAPI accounts.
     */
    var primaNotaNo: String? = null,

    /**
     * The check account to which the transaction belongs
     */
    var checkAccount: CheckAccount? = null,

    /**
     * Status of the check account transaction.
     */
    var status: Status?,

    /**
     * The check account transaction serving as the source of a money transit
     */
    var sourceTransaction: CheckAccountTransaction? = null,

    /**
     * The check account transaction serving as the target of a money transit
     */
    var targetTransaction: CheckAccountTransaction? = null,

    /**
     * Timepoint when the transaction was enshrined.
     */
    var enshrined: OffsetDateTime? = null
): SevDeskObject(id, create, update, sevClient) {
    enum class Status(var idRepresentation: Int) {
        CREATED(100),
        LINKED(200),
        PRIVATE(300),
        AUTO_BOOKED(350),
        BOOKED(400);

        companion object {
            fun valueOf(idRepresentation: Int): Status {
                return entries.first { it.idRepresentation == idRepresentation }
            }
        }
    }
}