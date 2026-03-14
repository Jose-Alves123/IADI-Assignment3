package pt.unl.fct.iadi.novaevents.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import pt.unl.fct.iadi.novaevents.service.ClubService
import pt.unl.fct.iadi.novaevents.service.EventService

@Controller
class ClubController(private val clubService: ClubService, private val eventService: EventService) {

    @GetMapping("/", "/clubs")
    fun listClubs(model: Model): String {
        model.addAttribute("clubs", clubService.findAll())
        return "clubs/list"
    }

    @GetMapping("/clubs/{clubId}")
    fun clubDetail(@PathVariable clubId: Long, model: Model): String {
        val club = clubService.findById(clubId)
        val clubEvents = eventService.findByClubId(clubId)

        model.addAttribute("club", club)
        model.addAttribute("events", clubEvents)
        return "clubs/detail"
    }
}
