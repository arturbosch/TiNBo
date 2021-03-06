package io.gitlab.arturbosch.tinbo.api.marker

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
interface PersistableMarker {
	val persistencePath: Path
}
