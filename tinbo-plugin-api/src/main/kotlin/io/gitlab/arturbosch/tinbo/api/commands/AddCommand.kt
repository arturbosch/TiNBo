package io.gitlab.arturbosch.tinbo.api.commands

import io.gitlab.arturbosch.tinbo.api.marker.Command
import io.gitlab.arturbosch.tinbo.api.config.ModeManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.shell.core.annotation.CliAvailabilityIndicator
import org.springframework.shell.core.annotation.CliCommand
import org.springframework.stereotype.Component

/**
 * @author Artur Bosch
 */
@Component
class AddCommand @Autowired constructor(private val commandChooser: CommandChooser) : Command {

	override val id: String = "share"

	@CliAvailabilityIndicator("add")
	fun basicsAvailable(): Boolean {
		return ModeManager.isEditAllowed()
	}

	@CliCommand("add", help = "Adds a new entry")
	fun add(): String {
		return commandChooser.forAddableMode().add()
	}

}
