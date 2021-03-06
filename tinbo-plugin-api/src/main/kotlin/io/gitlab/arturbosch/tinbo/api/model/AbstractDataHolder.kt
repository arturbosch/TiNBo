package io.gitlab.arturbosch.tinbo.api.model

import io.gitlab.arturbosch.tinbo.api.model.util.lazyData
import java.nio.file.Files
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
abstract class AbstractDataHolder<E : Entry, D : Data<E>>(val persister: AbstractPersister<E, D>) {

	protected abstract val lastUsedData: String

	var data: D by lazyData { persister.restore(lastUsedData) }

	fun loadData(name: String) {
		data = persister.restore(name)
	}

	fun getDataNames(): List<String> {
		return Files.list(persister.persistencePath)
				.map { it.fileName.toString() }
				.collect(Collectors.toList<String>())
				.toList()
	}

	fun persistEntry(entry: E) {
		data.addEntry(entry)
		persister.store(data)
		data = persister.restore(data.name)
	}

	fun saveData(name: String, entriesInMemory: List<E>) {
		data = newData(name, entriesInMemory)
		persister.store(data)
	}

	fun getEntries(): List<E> = data.entries.sorted()

	abstract fun newData(name: String, entriesInMemory: List<E>): D

	abstract fun getEntriesFilteredBy(filter: String): List<E>

	abstract fun changeCategory(oldName: String, newName: String)
}
