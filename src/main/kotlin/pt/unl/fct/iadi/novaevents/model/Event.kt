package pt.unl.fct.iadi.novaevents.model

import java.time.LocalDate

data class Event(
        val id: Long,
        val clubId: Long,
        var name: String,
        var date: LocalDate,
        var location: String?,
        var type: EventType,
        var description: String?
)
