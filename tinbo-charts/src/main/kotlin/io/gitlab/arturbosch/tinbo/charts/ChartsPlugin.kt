package io.gitlab.arturbosch.tinbo.charts

import io.gitlab.arturbosch.tinbo.api.Command
import io.gitlab.arturbosch.tinbo.plugins.TinboContext
import io.gitlab.arturbosch.tinbo.plugins.TinboPlugin
import org.springframework.stereotype.Component

/**
 * @author Artur Bosch
 */
@Component
class ChartsPlugin : TinboPlugin {

	override fun registerCommands(tinboContext: TinboContext): List<Command> {
		return listOf(BurndownChart(), WeekMonthChart())
	}
}