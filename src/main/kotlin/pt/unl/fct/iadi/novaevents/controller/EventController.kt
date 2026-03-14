package pt.unl.fct.iadi.novaevents.controller

import jakarta.validation.Valid
import java.time.LocalDate
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.service.ClubService
import pt.unl.fct.iadi.novaevents.service.DuplicateEventNameException
import pt.unl.fct.iadi.novaevents.service.EventFilter
import pt.unl.fct.iadi.novaevents.service.EventService

@Controller
@RequestMapping
class EventController(
        private val eventService: EventService,
        private val clubService: ClubService
) {

    @GetMapping("/events")
    fun listEvents(
            @RequestParam(required = false) type: EventType?,
            @RequestParam(required = false) clubId: Long?,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            from: LocalDate?,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            to: LocalDate?,
            model: Model
    ): String {
        val filter = EventFilter(type = type, clubId = clubId, from = from, to = to)
        val clubs = clubService.findAll()
        val clubNames = clubs.associate { it.id to it.name }

        model.addAttribute("events", eventService.findAll(filter))
        model.addAttribute("clubs", clubs)
        model.addAttribute("clubNames", clubNames)
        model.addAttribute("types", EventType.entries)
        model.addAttribute("selectedType", type)
        model.addAttribute("selectedClubId", clubId)
        model.addAttribute("selectedFrom", from)
        model.addAttribute("selectedTo", to)

        return "events/list"
    }

    @GetMapping("/clubs/{clubId}/events/{eventId}", "/events/{eventId}")
    fun eventDetail(
            @PathVariable(required = false) clubId: Long?,
            @PathVariable eventId: Long,
            model: Model
    ): String {
        val event = resolveEvent(clubId, eventId)
        val club = clubService.findById(event.clubId)

        model.addAttribute("event", event)
        model.addAttribute("club", club)
        return "events/detail"
    }

    @GetMapping("/clubs/{clubId}/events/new")
    fun newEventForm(@PathVariable clubId: Long, model: Model): String {
        val club = clubService.findById(clubId)
        model.addAttribute("club", club)
        model.addAttribute("eventForm", EventFormDto())
        model.addAttribute("types", EventType.entries)
        model.addAttribute("isEdit", false)
        return "events/form"
    }

    @PostMapping("/clubs/{clubId}/events")
    fun createEvent(
            @PathVariable clubId: Long,
            @Valid @ModelAttribute("eventForm") eventForm: EventFormDto,
            bindingResult: BindingResult,
            model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            return renderFormWithContext(clubId, model, false)
        }

        return try {
            val created = eventService.create(clubId, eventForm)
            "redirect:/clubs/$clubId/events/${created.id}"
        } catch (_: DuplicateEventNameException) {
            bindingResult.rejectValue("name", "duplicate", "An event with this name already exists")
            renderFormWithContext(clubId, model, false)
        }
    }

    @GetMapping("/clubs/{clubId}/events/{eventId}/edit", "/events/{eventId}/edit")
    fun editEventForm(
            @PathVariable(required = false) clubId: Long?,
            @PathVariable eventId: Long,
            model: Model
    ): String {
        val event = resolveEvent(clubId, eventId)
        val form =
                EventFormDto(
                        name = event.name,
                        date = event.date,
                        location = event.location,
                        type = event.type,
                        description = event.description
                )

        model.addAttribute("club", clubService.findById(event.clubId))
        model.addAttribute("event", event)
        model.addAttribute("eventForm", form)
        model.addAttribute("types", EventType.entries)
        model.addAttribute("isEdit", true)
        return "events/form"
    }

    @PutMapping(
            "/clubs/{clubId}/events/{eventId}",
            "/clubs/{clubId}/events/{eventId}/edit",
            "/events/{eventId}",
            "/events/{eventId}/edit"
    )
    fun updateEvent(
            @PathVariable(required = false) clubId: Long?,
            @PathVariable eventId: Long,
            @Valid @ModelAttribute("eventForm") eventForm: EventFormDto,
            bindingResult: BindingResult,
            model: Model
    ): String {
        val event = resolveEvent(clubId, eventId)
        val owningClubId = event.clubId

        if (bindingResult.hasErrors()) {
            model.addAttribute("event", event)
            return renderFormWithContext(owningClubId, model, true)
        }

        return try {
            eventService.update(owningClubId, eventId, eventForm)
            "redirect:/clubs/$owningClubId/events/$eventId"
        } catch (_: DuplicateEventNameException) {
            bindingResult.rejectValue("name", "duplicate", "An event with this name already exists")
            model.addAttribute("event", eventService.findById(eventId))
            renderFormWithContext(owningClubId, model, true)
        }
    }

    @GetMapping("/clubs/{clubId}/events/{eventId}/delete", "/events/{eventId}/delete")
    fun deleteConfirmation(
            @PathVariable(required = false) clubId: Long?,
            @PathVariable eventId: Long,
            model: Model
    ): String {
        val event = resolveEvent(clubId, eventId)
        model.addAttribute("club", clubService.findById(event.clubId))
        model.addAttribute("event", event)
        return "events/delete"
    }

    @DeleteMapping(
            "/clubs/{clubId}/events/{eventId}",
            "/clubs/{clubId}/events/{eventId}/delete",
            "/events/{eventId}",
            "/events/{eventId}/delete"
    )
    fun deleteEvent(
            @PathVariable(required = false) clubId: Long?,
            @PathVariable eventId: Long
    ): String {
        val event = resolveEvent(clubId, eventId)
        eventService.delete(event.clubId, eventId)
        return "redirect:/clubs/${event.clubId}"
    }

    private fun renderFormWithContext(clubId: Long, model: Model, isEdit: Boolean): String {
        model.addAttribute("club", clubService.findById(clubId))
        model.addAttribute("types", EventType.entries)
        model.addAttribute("isEdit", isEdit)
        return "events/form"
    }

    private fun resolveEvent(clubId: Long?, eventId: Long) =
            if (clubId == null) {
                eventService.findById(eventId)
            } else {
                runCatching { eventService.findByIdAndClubId(clubId, eventId) }.getOrElse {
                    eventService.findById(eventId)
                }
            }
}
