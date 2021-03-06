package io.gitlab.arturbosch.tinbo.psp

import io.gitlab.arturbosch.tinbo.api.marker.Command
import io.gitlab.arturbosch.tinbo.api.config.ModeManager
import io.gitlab.arturbosch.tinbo.api.config.TinboMode
import io.gitlab.arturbosch.tinbo.api.utils.printlnInfo
import org.springframework.shell.core.annotation.CliAvailabilityIndicator
import org.springframework.shell.core.annotation.CliCommand
import org.springframework.stereotype.Component

/**
 * @author Artur Bosch
 */
@Component
class StartProjectsModeCommand : Command {

	override val id: String = "start"

	@CliAvailabilityIndicator("projects")
	fun onlyModeCommands(): Boolean {
		return ModeManager.isCurrentMode(TinboMode.START)
	}

	@CliCommand("projects", help = "Switch to projects mode, managing projects like in PSP.")
	fun projectsMode() {
		ModeManager.current = ProjectsMode
		printlnInfo("Entering projects mode...")
	}

}
