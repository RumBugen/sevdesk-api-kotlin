package me.rumbugen.sevdesk.objects.invoice

enum class BookingType {
    FULL_PAYMENT, // Normal booking
    N, // Partial booking (historically used for normal booking)
    CB, // Reduced amount due to discount (skonto)
    CF, // Reduced/Higher amount due to currency fluctuations
    O, // Reduced/Higher amount due to other reasons
    OF, // Higher amount due to reminder charges
    MTC // Reduced amount due to the monetary traffic costs
}