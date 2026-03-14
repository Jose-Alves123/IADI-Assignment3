package pt.unl.fct.iadi.novaevents

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class Assignment3ApplicationTests {

    @Autowired lateinit var mockMvc: MockMvc

    @Test fun contextLoads() {}

    @Test
    fun clubsPageRendersTableAndNavbar() {
        mockMvc.perform(get("/clubs"))
                .andExpect(status().isOk)
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<tbody>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nova Events")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(">Clubs<")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(">Events<")))
    }

    @Test
    fun eventsPageRendersActionLinks() {
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk)
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/edit")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/delete")))
    }

    @Test
    fun unknownClubReturns404Page() {
        mockMvc.perform(get("/clubs/9999"))
                .andExpect(status().isNotFound)
                .andExpect(
                        content()
                                .string(
                                        org.hamcrest.Matchers.containsString(
                                                "404 - Resource Not Found"
                                        )
                                )
                )
    }

    @Test
    fun eventsFilteringByTypeAndClubIncludesExpectedSeededEvent() {
        mockMvc.perform(get("/events").param("type", "WORKSHOP").param("clubId", "1"))
                .andExpect(status().isOk)
                .andExpect(
                        content()
                                .string(
                                        org.hamcrest.Matchers.containsString(
                                                "Beginner&#39;s Chess Workshop"
                                        )
                                )
                )
    }

    @Test
    fun eventDetailPageShowsSeededEvent() {
        mockMvc.perform(get("/clubs/1/events/1"))
                .andExpect(status().isOk)
                .andExpect(
                        content()
                                .string(
                                        org.hamcrest.Matchers.containsString(
                                                "Beginner&#39;s Chess Workshop"
                                        )
                                )
                )
                .andExpect(
                        content()
                                .string(
                                        org.hamcrest.Matchers.containsString("Library Seminar Room")
                                )
                )
    }

    @Test
    fun editingEventWithValidDataRedirectsToClubPage() {
        mockMvc.perform(
                        put("/clubs/1/events/2/edit")
                                .contentType("application/x-www-form-urlencoded")
                                .param("name", "Updated Spring Chess Open")
                                .param("date", "2026-04-21")
                                .param("type", "COMPETITION")
                                .param("location", "Main Hall")
                                .param("description", "Updated description")
                )
                .andExpect(status().is3xxRedirection)
                .andExpect(header().string("Location", "/clubs/1"))
    }

    @Test
    fun editingEventWithDuplicateNameShowsValidationError() {
        mockMvc.perform(
                        put("/clubs/1/events/2/edit")
                                .contentType("application/x-www-form-urlencoded")
                                .param("name", "Beginner's Chess Workshop")
                                .param("date", "2026-04-21")
                                .param("type", "COMPETITION")
                                .param("location", "Main Hall")
                                .param("description", "Updated description")
                )
                .andExpect(status().isOk)
                .andExpect(
                        content()
                                .string(
                                        org.hamcrest.Matchers.containsString(
                                                "An event with this name already exists"
                                        )
                                )
                )
    }
}
