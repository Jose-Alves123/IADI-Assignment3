package pt.unl.fct.iadi.novaevents

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
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
}
