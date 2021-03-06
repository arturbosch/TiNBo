package io.gitlab.arturbosch.tinbo.tasks

import io.gitlab.arturbosch.tinbo.api.TinboTerminal
import io.gitlab.arturbosch.tinbo.api.commands.EditableCommands
import io.gitlab.arturbosch.tinbo.api.config.Defaults
import io.gitlab.arturbosch.tinbo.api.config.ModeManager
import io.gitlab.arturbosch.tinbo.api.config.TinboConfig
import io.gitlab.arturbosch.tinbo.api.nullIfEmpty
import io.gitlab.arturbosch.tinbo.api.orDefault
import io.gitlab.arturbosch.tinbo.api.utils.DateTimeFormatters
import io.gitlab.arturbosch.tinbo.api.utils.dateTimeFormatter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.shell.core.annotation.CliAvailabilityIndicator
import org.springframework.shell.core.annotation.CliCommand
import org.springframework.shell.core.annotation.CliOption
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

/**
 * @author artur
 */
@Component
open class TaskCommands @Autowired constructor(executor: TaskExecutor,
											   private val config: TinboConfig,
											   terminal: TinboTerminal) :
		EditableCommands<TaskEntry, TaskData, DummyTask>(executor, terminal) {

	override val id: String = "task"

	private val successMessage = "Successfully added a task."

	@CliAvailabilityIndicator("task", "loadTasks", "editTasks")
	fun isAvailable(): Boolean {
		return ModeManager.isCurrentMode(TasksMode)
	}

	override fun add(): String {
		return whileNotInEditMode {
			val category = console.readLine("Enter a category: ").orDefault(config.getCategoryName())
			val message = console.readLine("Enter a message: ")
			val location = console.readLine("Enter a location: ")
			val description = console.readLine("Enter a description: ")
			val startTime = console.readLine("Enter a start time (yyyy-MM-dd HH:mm): ")
					.orDefault(LocalDateTime.now().format(dateTimeFormatter))
			val endTime = console.readLine("Enter a end time (yyyy-MM-dd HH:mm): ")
			addTask(message, category, location, description, startTime, endTime)
		}
	}

	@CliCommand(value = ["task"], help = "Adds a new task.")
	fun addTask(@CliOption(key = ["message", "msg", "m"], mandatory = true, help = "Summary of the task.",
			specifiedDefaultValue = "", unspecifiedDefaultValue = "") message: String,
				@CliOption(key = ["category", "c", ""], help = "Category for the task",
						specifiedDefaultValue = Defaults.MAIN_CATEGORY_NAME,
						unspecifiedDefaultValue = Defaults.MAIN_CATEGORY_NAME) category: String,
				@CliOption(key = ["location", "loc", "l"], help = "Specify a location for this task.",
						specifiedDefaultValue = "", unspecifiedDefaultValue = "") location: String,
				@CliOption(key = ["description", "des", "d"], help = "Specify a description for this task.",
						specifiedDefaultValue = "", unspecifiedDefaultValue = "") description: String,
				@CliOption(key = ["start", "s"], help = "Specify a end time for this task. Format: yyyy-MM-dd HH:mm",
						specifiedDefaultValue = "", unspecifiedDefaultValue = "") startTime: String,
				@CliOption(key = ["end", "e"], help = "Specify a start time for this task. Format: yyyy-MM-dd HH:mm",
						specifiedDefaultValue = "", unspecifiedDefaultValue = "") endTime: String): String {

		return whileNotInEditMode {

			if (startTime.isNotEmpty()) {
				try {
					val pair = DateTimeFormatters.parseDateTime(endTime, startTime)
					val formattedStartTime = pair.first
					val formattedEndTime = pair.second
					executor.addEntry(TaskEntry(message, description, location, category, formattedStartTime, formattedEndTime))
					successMessage
				} catch (e: DateTimeParseException) {
					"Could not parse date, use format: yyyy-MM-dd HH:mm"
				}
			} else {
				executor.addEntry(TaskEntry(message, description, location, category))
				successMessage
			}

		}
	}

	@CliCommand("editTask", "editTasks", help = "Edits the task entry with given index")
	fun editTask(@CliOption(key = ["index", "i"], mandatory = true,
			help = "Index of the task to edit.") index: Int,
				 @CliOption(key = ["message", "msg", "m"],
						 help = "Summary of the task.") message: String?,
				 @CliOption(key = ["category", "c", ""],
						 help = "Category for the task") category: String?,
				 @CliOption(key = ["location", "loc", "l"],
						 help = "Specify a location for this task.") location: String?,
				 @CliOption(key = ["description", "des", "d"],
						 help = "Specify a description for this task.") description: String?,
				 @CliOption(key = ["start", "s"], unspecifiedDefaultValue = "", specifiedDefaultValue = "",
						 help = "Specify a end time for this task. Format: yyyy-MM-dd HH:mm") startTime: String,
				 @CliOption(key = ["end", "e"], unspecifiedDefaultValue = "", specifiedDefaultValue = "",
						 help = "Specify a start time for this task. Format: yyyy-MM-dd HH:mm") endTime: String): String {

		return withinListMode {
			val i = index - 1
			enterEditModeWithIndex(i) {
				val pair = DateTimeFormatters.parseDateTimeOrDefault(endTime, startTime)
				executor.editEntry(i, DummyTask(message, category, location, description, pair.first, pair.second))
				"Successfully edited a task."
			}
		}
	}

	override fun edit(index: Int): String {
		return withinListMode {
			val i = index - 1
			enterEditModeWithIndex(i) {
				val category = console.readLine("Enter a category (leave empty if unchanged): ").nullIfEmpty()
				val message = console.readLine("Enter a message (leave empty if unchanged): ").nullIfEmpty()
				val location = console.readLine("Enter a location (leave empty if unchanged): ").nullIfEmpty()
				val description = console.readLine("Enter a description (leave empty if unchanged): ").nullIfEmpty()
				val startTime = console.readLine("Enter a start time (yyyy-MM-dd HH:mm) (leave empty if unchanged): ")
				val endTime = console.readLine("Enter a end time (yyyy-MM-dd HH:mm) (leave empty if unchanged): ")

				val pair = DateTimeFormatters.parseDateTimeOrDefault(endTime, startTime)
				executor.editEntry(i, DummyTask(message, category, location, description, pair.first, pair.second))
				"Successfully edited a task."
			}
		}
	}
}
