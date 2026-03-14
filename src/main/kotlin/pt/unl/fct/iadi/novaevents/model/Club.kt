package pt.unl.fct.iadi.novaevents.model

data class Club(
        val id: Long,
        val name: String,
        val description: String,
        val category: ClubCategory
)
