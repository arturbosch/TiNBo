package io.gitlab.arturbosch.tinbo.finance

import io.gitlab.arturbosch.tinbo.api.Command
import io.gitlab.arturbosch.tinbo.plugins.TinboContext
import io.gitlab.arturbosch.tinbo.plugins.TinboPlugin
import jline.console.ConsoleReader
import org.springframework.stereotype.Component

/**
 * @author Artur Bosch
 */
@Component
class FinancePlugin : TinboPlugin {

	override val version: String = "1.0.1"

	override fun registerCommands(tinboContext: TinboContext): List<Command> {
		val consoleReader = tinboContext.beanOf<ConsoleReader>()
		val tinboConfig = tinboContext.tinboConfig
		val configProvider = ConfigProvider(tinboConfig)
		val persister = FinancePersister(tinboConfig)
		val dataHolder = FinanceDataHolder(persister, tinboConfig)
		val executor = FinanceExecutor(dataHolder, configProvider, tinboConfig)
		val financeCommands = FinanceCommands(executor, configProvider, consoleReader)
		tinboContext.registerSingleton("FinanceCommands", financeCommands)

		val financeModeCommand = StartFinanceModeCommand()
		tinboContext.registerSingleton("StartFinanceModeCommand", financeModeCommand)

		val listAllSums = ListAllSums(executor, dataHolder, configProvider)
		tinboContext.registerSingleton("ListAllSums", listAllSums)
		tinboContext.registerSingleton("FinancePersister", persister)
		return listOf(financeCommands, financeModeCommand, listAllSums)
	}

}
