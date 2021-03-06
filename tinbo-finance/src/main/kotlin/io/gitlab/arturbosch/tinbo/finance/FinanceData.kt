package io.gitlab.arturbosch.tinbo.finance

import io.gitlab.arturbosch.tinbo.api.config.ConfigDefaults
import io.gitlab.arturbosch.tinbo.api.config.Defaults
import io.gitlab.arturbosch.tinbo.api.config.HomeFolder
import io.gitlab.arturbosch.tinbo.api.config.TinboConfig
import io.gitlab.arturbosch.tinbo.api.model.AbstractDataHolder
import io.gitlab.arturbosch.tinbo.api.model.AbstractPersister
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author artur
 */
@Component
class FinanceDataHolder @Autowired constructor(persister: FinancePersister,
											   private val config: TinboConfig) :
		AbstractDataHolder<FinanceEntry, FinanceData>(persister) {

	override val lastUsedData: String
		get() = config.getKey(ConfigDefaults.FINANCE)
				.getOrElse(ConfigDefaults.LAST_USED, { Defaults.FINANCE_NAME })

	override fun newData(name: String, entriesInMemory: List<FinanceEntry>): FinanceData {
		return FinanceData(name, entriesInMemory)
	}

	override fun getEntriesFilteredBy(filter: String): List<FinanceEntry> {
		return getEntries().filter { it.category.equals(filter, ignoreCase = true) }
	}

	override fun changeCategory(oldName: String, newName: String) {
		val updatedEntries = getEntries().map {
			if (it.category.equals(oldName, ignoreCase = true)) {
				it.copy(category = newName)
			} else {
				it
			}
		}
		saveData(data.name, updatedEntries)
	}

}

@Component
class FinancePersister @Autowired constructor(config: TinboConfig) :
		AbstractPersister<FinanceEntry, FinanceData>(HomeFolder.getDirectory(ConfigDefaults.FINANCE), config) {

	override fun restore(name: String): FinanceData {
		return load(name, FinanceData(name), FinanceEntry::class.java)
	}

}
