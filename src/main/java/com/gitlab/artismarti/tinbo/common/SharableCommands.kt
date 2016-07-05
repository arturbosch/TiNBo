package com.gitlab.artismarti.tinbo.common

import com.gitlab.artismarti.tinbo.config.ModeAdvisor
import com.gitlab.artismarti.tinbo.notes.NoteCommands
import jline.console.ConsoleReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.shell.core.annotation.CliAvailabilityIndicator
import org.springframework.shell.core.annotation.CliCommand
import org.springframework.shell.core.annotation.CliOption
import org.springframework.stereotype.Component

/**
 * @author artur
 */
@Component
open class SharableCommands @Autowired constructor(val commandChooser: CommandChooser,
                                                   val consoleReader: ConsoleReader) : Command {

	override val id: String = "edit"

	@CliAvailabilityIndicator("add", "ls", "save", "cancel", "remove", "changeCategory", "data")
	fun basicsAvailable(): Boolean {
		return ModeAdvisor.isTimerMode() || ModeAdvisor.isNotesMode() || ModeAdvisor.isTasksMode()
	}

	@CliCommand("add", help = "Adds a new entry")
	fun add(): String {
		return commandChooser.forCurrentMode().add()
	}

	@CliCommand("ls", "list", help = "Lists all entries.")
	fun list(@CliOption(
			key = arrayOf("category", "cat", "c"),
			unspecifiedDefaultValue = "",
			specifiedDefaultValue = "",
			help = "Name to filter only for this specific category.") categoryName: String): String {

		return commandChooser.forCurrentMode().list(categoryName)
	}

	@CliCommand("cancel", help = "Cancels edit mode.")
	fun cancel(): String {
		return commandChooser.forCurrentMode().cancel()
	}

	@CliCommand("save", help = "Saves current editing if list command was used.")
	fun save(@CliOption(key = arrayOf("name", "n"), help = "Saves notes under a new data set (also a new filename).",
			specifiedDefaultValue = "", unspecifiedDefaultValue = "") name: String): String {

		return commandChooser.forCurrentMode().save(name)
	}

	@CliCommand("remove", "delete", help = "Deletes entries from storage.")
	fun delete(@CliOption(key = arrayOf("indices", "index", "i"), mandatory = true,
			help = "Indices pattern, allowed are numbers with space in between or intervals like 1-5 e.g. '1 2 3-5 6'.")
	           indexPattern: String): String {

		return commandChooser.forCurrentMode().delete(indexPattern)
	}

	@CliCommand("changeCategory", help = "Changes a categories name with the side effect that all entries of this category get updated.")
	fun changeCategory(@CliOption(key = arrayOf("old"), help = "Old category name.",
			unspecifiedDefaultValue = "", specifiedDefaultValue = "") old: String,
	                   @CliOption(key = arrayOf("new"), help = "Old category name.",
			                   unspecifiedDefaultValue = "", specifiedDefaultValue = "") new: String): String {

		val commandsForCurrentMode = commandChooser.forCurrentMode()
		if (commandsForCurrentMode is NoteCommands || commandsForCurrentMode is NoopCommands) {
			return "Changing category is not yet supported for notes."
		}

		var oldName = old
		var newName = new
		if (old.isEmpty()) {
			oldName = consoleReader.readLine("Enter a old category name to replace: ")
		}
		if (new.isEmpty()) {
			newName = consoleReader.readLine("Enter a new category name: ")
		}

		if (oldName.isEmpty() || newName.isEmpty()) {
			return "Specify old and new category name"
		}

		return commandsForCurrentMode.changeCategory(oldName, newName)
	}

	@CliCommand("data", help = "prints all available data sets")
	fun data(): String {
		return commandChooser.forCurrentMode().data()
	}
}
