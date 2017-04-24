package io.gitlab.arturbosch.tinbo.commands

import io.gitlab.arturbosch.tinbo.api.Command
import io.gitlab.arturbosch.tinbo.api.Editable
import io.gitlab.arturbosch.tinbo.config.ModeManager
import io.gitlab.arturbosch.tinbo.model.AbstractExecutor
import io.gitlab.arturbosch.tinbo.model.Data
import io.gitlab.arturbosch.tinbo.model.DummyEntry
import io.gitlab.arturbosch.tinbo.model.Entry
import io.gitlab.arturbosch.tinbo.utils.parseIndices
import io.gitlab.arturbosch.tinbo.utils.printlnInfo
import jline.console.ConsoleReader
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author artur
 */
abstract class EditableCommands<E : Entry, D : Data<E>, in T : DummyEntry>(
		val executor: AbstractExecutor<E, D, T>) : Command, Editable {

	private val NEED_EDIT_MODE_TEXT = "Before adding or list entries exit edit mode with 'save' or 'cancel'."

	protected var isListMode: Boolean = false
	protected var isEditMode: Boolean = false

	@Autowired
	protected lateinit var console: ConsoleReader

	protected fun withListMode(body: () -> String): String {
		isListMode = true
		return body.invoke()
	}

	protected fun withinListMode(body: () -> String): String {
		if (isListMode) {
			return body.invoke()
		} else {
			return "Before editing entries you have to 'list' them to get indices to work on."
		}
	}

	protected fun enterEditModeWithIndex(index: Int, body: () -> String): String {
		if (executor.indexExists(index)) {
			ModeManager.isBackCommandBlocked = true
			isEditMode = true
			return body.invoke()
		} else {
			return "This index doesn't exist"
		}
	}

	protected fun withinEditMode(command: String, body: () -> String): String {
		if (isEditMode) {
			ModeManager.isBackCommandBlocked = false
			isEditMode = false
			isListMode = false
			return body.invoke()
		} else {
			return "You need to be in edit mode to use $command."
		}
	}

	protected fun whileNotInEditMode(body: () -> String): String {
		if (isEditMode) {
			return NEED_EDIT_MODE_TEXT
		} else {
			return body.invoke()
		}
	}

	override fun load(name: String): String {
		executor.loadData(name)
		return "Successfully loaded data set $name"
	}

	override fun list(categoryName: String, all: Boolean): String {
		return withListMode {
			if (isEditMode) {
				if (categoryName.isNotEmpty())
					printlnInfo("While in edit mode filtering is ignored.")
				executor.listInMemoryEntries(all)
			} else {
				when (categoryName) {
					"" -> executor.listData(all)
					else -> executor.listDataFilteredBy(categoryName, all)
				}
			}
		}
	}

	override fun cancel(): String {
		return withinEditMode("cancel") {
			executor.cancel()
			"Cancelled edit mode."
		}
	}

	override fun save(name: String): String {
		return withinEditMode("save") {
			executor.saveEntries(name)
			"Successfully saved edited data"
		}
	}

	override fun delete(indexPattern: String): String {
		return withinListMode {
			try {
				val indices = if (indexPattern == "-1") setOf(-1) else parseIndices(indexPattern)
				ModeManager.isBackCommandBlocked = true
				isEditMode = true
				executor.deleteEntries(indices)
				"Successfully deleted task(s)."
			} catch (e: IllegalArgumentException) {
				"Could not parse the indices pattern. Use something like '1 2 3-5 6'."
			}
		}
	}

	override fun changeCategory(oldName: String, newName: String): String {
		return whileNotInEditMode {
			executor.changeCategory(oldName, newName)
			"Updated entries of category $oldName to have new category $newName"
		}
	}

	override fun data(): String {
		return "Available data sets: " + executor.getAllDataNames().joinToString()
	}
}
