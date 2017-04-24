package io.gitlab.arturbosch.tinbo.psp

import io.gitlab.arturbosch.tinbo.config.ModeListener
import io.gitlab.arturbosch.tinbo.config.ModeManager
import io.gitlab.arturbosch.tinbo.config.TinboMode
import io.gitlab.arturbosch.tinbo.publish

/**
 * @author Artur Bosch
 */
class UnspecifyProjectModeListener : ModeListener {

	override fun change(mode: TinboMode) {
		val old = ModeManager.current
		if (old == ProjectsMode && mode != ProjectsMode) {
			publish(UnspecifyProject)
		}
	}

}

object UnspecifyProject