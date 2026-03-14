package pt.unl.fct.iadi.novaevents.service

import java.util.NoSuchElementException
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.model.ClubCategory

@Service
class ClubService {
    private val clubs: List<Club> =
            listOf(
                    Club(
                            id = 1,
                            name = "Chess Club",
                            description =
                                    "A community for strategy lovers, from beginners to tournament players.",
                            category = ClubCategory.ACADEMIC
                    ),
                    Club(
                            id = 2,
                            name = "Robotics Club",
                            description =
                                    "The Robotics Club is the place to turn ideas into machines, build prototypes, and compete in engineering challenges.",
                            category = ClubCategory.TECHNOLOGY
                    ),
                    Club(
                            id = 3,
                            name = "Photography Club",
                            description =
                                    "Explore visual storytelling through portraits, street photography, and editing workshops.",
                            category = ClubCategory.ARTS
                    ),
                    Club(
                            id = 4,
                            name = "Hiking & Outdoors Club",
                            description =
                                    "Weekend trails, outdoor skill sessions, and nature escapes for all experience levels.",
                            category = ClubCategory.SPORTS
                    ),
                    Club(
                            id = 5,
                            name = "Film Society",
                            description =
                                    "Weekly screenings, director spotlights, and conversations about cinema from around the world.",
                            category = ClubCategory.CULTURAL
                    )
            )

    fun findAll(): List<Club> = clubs

    fun findById(id: Long): Club =
            clubs.find { it.id == id }
                    ?: throw NoSuchElementException("Club with id $id was not found")
}
