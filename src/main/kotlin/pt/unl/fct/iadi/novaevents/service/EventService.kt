package pt.unl.fct.iadi.novaevents.service

import java.time.LocalDate
import java.util.Locale
import java.util.NoSuchElementException
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType

@Service
class EventService {
    private val events: MutableList<Event> =
            mutableListOf(
                    Event(
                            id = 1,
                            clubId = 1,
                            name = "Beginner's Chess Workshop",
                            date = LocalDate.of(2026, 4, 8),
                            location = "Library Seminar Room",
                            type = EventType.WORKSHOP,
                            description =
                                    "An introductory workshop covering chess rules, basic tactics, and opening principles."
                    ),
                    Event(
                            id = 2,
                            clubId = 1,
                            name = "Spring Chess Open",
                            date = LocalDate.of(2026, 4, 19),
                            location = "Room A101",
                            type = EventType.COMPETITION,
                            description = "Rapid Swiss tournament open to all students."
                    ),
                    Event(
                            id = 3,
                            clubId = 2,
                            name = "Line Follower Workshop",
                            date = LocalDate.of(2026, 4, 12),
                            location = "Engineering Lab 2",
                            type = EventType.WORKSHOP,
                            description = "Build and tune a basic autonomous robot."
                    ),
                    Event(
                            id = 4,
                            clubId = 3,
                            name = "Street Portrait Walk",
                            date = LocalDate.of(2026, 4, 15),
                            location = "City Center",
                            type = EventType.SOCIAL,
                            description = "Guided photo walk focused on portrait composition."
                    ),
                    Event(
                            id = 5,
                            clubId = 4,
                            name = "Serra Sunrise Hike",
                            date = LocalDate.of(2026, 4, 26),
                            location = "North Trailhead",
                            type = EventType.MEETING,
                            description = "Early morning hike with beginner-friendly pace."
                    ),
                    Event(
                            id = 6,
                            clubId = 5,
                            name = "Classic Noir Night",
                            date = LocalDate.of(2026, 4, 22),
                            location = "Auditorium B",
                            type = EventType.TALK,
                            description = "Screening followed by discussion on noir cinema themes."
                    )
            )

    private var nextId: Long = 7

    fun findAll(filter: EventFilter): List<Event> {
        return events.asSequence()
                .filter { filter.type == null || it.type == filter.type }
                .filter { filter.clubId == null || it.clubId == filter.clubId }
                .filter { filter.from == null || !it.date.isBefore(filter.from) }
                .filter { filter.to == null || !it.date.isAfter(filter.to) }
                .sortedBy { it.date }
                .toList()
    }

    fun findByIdAndClubId(clubId: Long, eventId: Long): Event {
        return events.find { it.id == eventId && it.clubId == clubId }
                ?: throw NoSuchElementException(
                        "Event with id $eventId for club $clubId was not found"
                )
    }

    fun findById(eventId: Long): Event {
        return events.find { it.id == eventId }
                ?: throw NoSuchElementException("Event with id $eventId was not found")
    }

    fun findByClubId(clubId: Long): List<Event> =
            events.filter { it.clubId == clubId }.sortedBy { it.date }

    fun create(clubId: Long, form: EventFormDto): Event {
        validateUniqueName(form.name!!, null)

        val event =
                Event(
                        id = nextId++,
                        clubId = clubId,
                        name = form.name!!.trim(),
                        date = form.date!!,
                        location = normalizeOptionalText(form.location),
                        type = form.type!!,
                        description = normalizeOptionalText(form.description)
                )
        events.add(event)
        return event
    }

    fun update(clubId: Long, eventId: Long, form: EventFormDto): Event {
        val event = findByIdAndClubId(clubId, eventId)
        validateUniqueName(form.name!!, eventId)

        event.name = form.name!!.trim()
        event.date = form.date!!
        event.location = normalizeOptionalText(form.location)
        event.type = form.type!!
        event.description = normalizeOptionalText(form.description)

        return event
    }

    fun delete(clubId: Long, eventId: Long) {
        val removed = events.removeIf { it.id == eventId && it.clubId == clubId }
        if (!removed) {
            throw NoSuchElementException("Event with id $eventId for club $clubId was not found")
        }
    }

    private fun validateUniqueName(rawName: String, currentEventId: Long?) {
        val normalized = rawName.trim().lowercase(Locale.getDefault())
        val duplicate =
                events.any {
                    it.name.trim().lowercase(Locale.getDefault()) == normalized &&
                            it.id != currentEventId
                }
        if (duplicate) {
            throw DuplicateEventNameException("An event with this name already exists")
        }
    }

    private fun normalizeOptionalText(value: String?): String? {
        val trimmed = value?.trim().orEmpty()
        return trimmed.ifBlank { null }
    }
}
