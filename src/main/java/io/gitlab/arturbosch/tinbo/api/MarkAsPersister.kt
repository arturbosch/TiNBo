package io.gitlab.arturbosch.tinbo.api

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
interface MarkAsPersister {
	val SAVE_DIR_PATH: Path
}