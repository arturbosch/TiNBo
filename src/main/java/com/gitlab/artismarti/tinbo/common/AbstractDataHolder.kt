package com.gitlab.artismarti.tinbo.common

import com.gitlab.artismarti.tinbo.utils.DelegateExt
import java.nio.file.Files
import java.util.stream.Collectors

/**
 * @author artur
 */
abstract class AbstractDataHolder<E : Entry, D : Data<E>>(val persister: AbstractPersister<E, D>) {

	protected abstract val last_used_data: String

	var data: D by DelegateExt.lazyData { persister.restore(last_used_data) }

	fun loadData(name: String) {
		data = persister.restore(name)
	}

	fun getDataNames(): List<String> {
		return Files.list(persister.SAVE_DIR_PATH)
				.map { it.fileName.toString() }
				.collect(Collectors.toList<String>())
				.toList()
	}

	fun persistEntry(entry: E) {
		data.addEntry(entry)
		persister.store(data)
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