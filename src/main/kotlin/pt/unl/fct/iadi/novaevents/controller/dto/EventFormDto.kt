package pt.unl.fct.iadi.novaevents.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import pt.unl.fct.iadi.novaevents.model.EventType
import java.time.LocalDate

data class EventFormDto(
    @field:NotBlank(message = "Name is required")
    var name: String? = null,

    @field:NotNull(message = "Date is required")
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var date: LocalDate? = null,

    var location: String? = null,

    @field:NotNull(message = "Event type is required")
    var type: EventType? = null,

    var description: String? = null
)
