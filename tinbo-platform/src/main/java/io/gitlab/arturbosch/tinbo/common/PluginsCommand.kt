package io.gitlab.arturbosch.tinbo.common

import io.gitlab.arturbosch.tinbo.PluginRegistry
import io.gitlab.arturbosch.tinbo.api.config.ModeManager
import io.gitlab.arturbosch.tinbo.api.config.TinboMode
import io.gitlab.arturbosch.tinbo.api.marker.Command
import io.gitlab.arturbosch.tinbo.api.model.util.CSVTablePrinter
import io.gitlab.arturbosch.tinbo.api.plusElementAtBeginning
import org.springframework.shell.core.annotation.CliAvailabilityIndicator
import org.springframework.shell.core.annotation.CliCommand
import org.springframework.stereotype.Component

/**
 * @author Artur Bosch
 */
@Component
class PluginsCommand(private val registry: PluginRegistry) : Command {

	override val id: String = "plugins"
	private val csv = CSVTablePrinter()

	@CliAvailabilityIndicator("plugins")
	fun onlyInStartMode() = ModeManager.isCurrentMode(TinboMode.START)

	@CliCommand("plugins", help = "Lists all used plugins with their specified versions.")
	fun plugins(): String {
		val plugins = registry.plugins
		return if (plugins.isEmpty()) {
			"No plugins registered."
		} else {
			val entries = plugins
					.map { "${it.name()};${it.version()};${it.providesMode()?.id ?: " "}" }
					.plusElementAtBeginning("Name;Version;Introduces Mode")
			return csv.asTable(entries).joinToString("\n")
		}
	}

}
