package io.gitlab.arturbosch.tinbo.api.marker

/**
 * @author Artur Bosch
 */
interface Cancelable {
	fun cancel(): String
}
