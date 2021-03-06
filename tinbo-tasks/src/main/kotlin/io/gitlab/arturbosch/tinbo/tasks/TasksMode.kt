package io.gitlab.arturbosch.tinbo.tasks

import io.gitlab.arturbosch.tinbo.api.config.TinboMode

/**
 * @author Artur Bosch
 */
object TasksMode : TinboMode {
	override val id: String = "task"
	override val helpIds: Array<String> = arrayOf(id, "edit", "share", "mode")
	override val editAllowed: Boolean = true
	override val isSummarizable: Boolean = false
}
