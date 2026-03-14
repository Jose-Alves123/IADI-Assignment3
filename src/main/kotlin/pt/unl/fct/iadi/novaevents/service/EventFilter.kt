package pt.unl.fct.iadi.novaevents.service

import java.time.LocalDate
import pt.unl.fct.iadi.novaevents.model.EventType

data class EventFilter(
        val type: EventType? = null,
        val clubId: Long? = null,
        val from: LocalDate? = null,
        val to: LocalDate? = null
)
