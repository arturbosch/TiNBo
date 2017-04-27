package io.gitlab.arturbosch.tinbo.psp

import io.gitlab.arturbosch.tinbo.config.TinboMode

/**
 * @author Artur Bosch
 */
object PSPMode : TinboMode {
	override val id: String = "psp"
	override val editAllowed: Boolean = false
	override val isSummarizable: Boolean = false
}