package com.sommerengineering.baraudio.alerts

class Alert(
    var name: String,
    var sound: Int,
    var voice: String,
    var speed: Float,
    var queueBehavior: QueueBehavior,
    var webhook: String
) {

}

sealed class QueueBehavior {
    object AddToQueue : QueueBehavior()
    object ReplaceQueue : QueueBehavior()
}

fun getAlerts() : List<Alert> {
    return listOf(
        Alert("NQ", 1, "English", 1.0f, QueueBehavior.AddToQueue, "https://webhook.123"),
        Alert("ES", 2, "Spanish", 1.1f, QueueBehavior.ReplaceQueue, "https://webhook.234"),
        Alert("RTY", 3, "Swedish", 1.2f, QueueBehavior.AddToQueue, "https://webhook.345"),
        Alert("FESX", 4, "Catalan", 1.3f, QueueBehavior.ReplaceQueue, "https://webhook.456")
    )
}
