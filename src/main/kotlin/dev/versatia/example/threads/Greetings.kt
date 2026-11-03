package dev.versatia.example.threads

import dev.versatia.di.LocalBean
import dev.versatia.task.PluginScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component.text
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * The coroutines of a `PluginScope` run on the server thread; `withContext(Dispatchers.IO)` moves
 * the file reading to a worker thread and returns to the server thread when it is done, so the
 * Bukkit call after it is safe.
 */
@LocalBean
class Greetings(private val plugin: ThreadsPlugin, private val scope: PluginScope) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        scope.launch {
            val motd = withContext(Dispatchers.IO) {
                plugin.logger.info("Reading motd.txt on ${Thread.currentThread().name}")
                plugin.dataFolder.resolve("motd.txt").readText().trim()
            }
            plugin.logger.info("Sending it to ${player.name} on ${Thread.currentThread().name}")
            player.sendMessage(text(motd))
        }
    }
}
