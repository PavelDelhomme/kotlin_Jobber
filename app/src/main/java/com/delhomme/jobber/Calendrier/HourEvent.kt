package com.delhomme.jobber.Calendrier

data class HourEvent(
    val hour: String,
    val events: List<Event>
) {
    companion object {
        val events: List<Event> = listOf()
    }
}
