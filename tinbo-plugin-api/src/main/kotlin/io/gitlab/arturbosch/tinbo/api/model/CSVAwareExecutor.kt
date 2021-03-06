package io.gitlab.arturbosch.tinbo.api.model

import io.gitlab.arturbosch.tinbo.api.model.util.CSVTablePrinter
import io.gitlab.arturbosch.tinbo.api.plusElementAtBeginning
import io.gitlab.arturbosch.tinbo.api.withIndexedColumn

/**
 * @author Artur Bosch
 */
abstract class CSVAwareExecutor {

	protected abstract val tableHeader: String
	protected val csv = CSVTablePrinter()

	fun tableAsString(summaries: List<String>, header: String = tableHeader): String {
		return csv.asTable(
				summaries.withIndexedColumn()
						.plusElementAtBeginning(header)
		).joinToString(separator = "\n")
	}
}
