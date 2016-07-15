package com.gitlab.artismarti.tinbo.common

import com.gitlab.artismarti.tinbo.config.ModeAdvisor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.shell.core.annotation.CliAvailabilityIndicator
import org.springframework.shell.core.annotation.CliCommand
import org.springframework.shell.core.annotation.CliOption
import org.springframework.stereotype.Component

/**
 * @author artur
 */
@Component
open class SummarizableUmbrellaCommands @Autowired constructor(val commandChooser: CommandChooser) : Command {

	override val id: String = "sum"

	@CliAvailabilityIndicator("sum")
	fun isAvailable(): Boolean {
		return ModeAdvisor.isTimerMode() || ModeAdvisor.isFinanceMode()
	}

	@CliCommand(value = "sum", help = "Sums up entries of all or specified categories.")
	fun sumCategories(@CliOption(key = arrayOf("", "categories"),
			help = "Specify categories to show sum for. Default: for all.",
			unspecifiedDefaultValue = "",
			specifiedDefaultValue = "") categories: String): String {

		val filters =
				if (categories.isEmpty()) listOf<String>()
				else categories.split(Regex("[,;. ]+")).map { it.trim().toLowerCase() }

		return commandChooser.forSummarizableMode().sum(filters)
	}
}