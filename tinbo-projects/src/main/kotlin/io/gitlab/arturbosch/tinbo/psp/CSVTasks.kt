package io.gitlab.arturbosch.tinbo.psp

import io.gitlab.arturbosch.tinbo.api.model.Task
import io.gitlab.arturbosch.tinbo.api.model.CSVAwareExecutor
import org.springframework.stereotype.Component

/**
 * @author Artur Bosch
 */
@Component
class CSVTasks : CSVAwareExecutor() {

	private val dummy = Task()
	override val tableHeader: String = "No.;" + dummy.csvHeader()

	fun convert(tasks: List<Task>): String {
		return tableAsString(tasks.map(Task::asCSV))
	}
}
