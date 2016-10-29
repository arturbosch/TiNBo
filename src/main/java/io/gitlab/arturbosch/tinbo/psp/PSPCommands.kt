package io.gitlab.arturbosch.tinbo.psp

import io.gitlab.arturbosch.tinbo.Project
import io.gitlab.arturbosch.tinbo.api.Command
import io.gitlab.arturbosch.tinbo.config.ModeAdvisor
import io.gitlab.arturbosch.tinbo.providers.PromptProvider
import io.gitlab.arturbosch.tinbo.utils.dateFormatter
import jline.console.ConsoleReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.shell.core.annotation.CliAvailabilityIndicator
import org.springframework.shell.core.annotation.CliCommand
import org.springframework.shell.core.annotation.CliOption
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * @author Artur Bosch
 */
@Component
class PSPCommands @Autowired constructor(val console: ConsoleReader,
										 val prompt: PromptProvider,
										 val currentProject: CurrentProject,
										 val csvProjects: CSVProjects) : Command {
	override val id: String = "psp"

	@CliAvailabilityIndicator("showAll", "show-project", "new-project", "open-project")
	fun available() = ModeAdvisor.isProjectsMode()

	@CliCommand("showAll", help = "Shows all running projects.")
	fun projects(): String {
		return csvProjects.convert()
	}

	@CliCommand("show-project")
	fun showProject(@CliOption(key = arrayOf(""), help = "Name the project must start with.") name: String?): String {
		return name?.let { currentProject.showProject(it) } ?: "Specify a project by its name!"
	}

	@CliCommand("new-project", help = "Creates a new project.")
	fun newProject(@CliOption(key = arrayOf(""), help = "Name the project must start with.") name: String?): String {
		val wrong = "Enter a valid and non existing name..."
		return name?.let {
			if (currentProject.projectWithNameExists(name)) return wrong
			try {
				val description = console.readLine("Enter project description: ").orEmpty()
				val start = console.readLine("Enter start date of project (empty if today): ")
				val end = console.readLine("Enter end date of project(empty if not yet known): ")
				val startDate = if (start.isEmpty()) LocalDate.now() else LocalDate.parse(start, dateFormatter)
				val endDate = if (end.isEmpty()) null else LocalDate.parse(end, dateFormatter)
				currentProject.newProject(Project(name, description, startDate, endDate))
				return "Successfully added project $name"
			} catch(e: Exception) {
				return e.message ?: throw e
			}
		} ?: wrong
	}

	@CliCommand("open-project", help = "Opens project with given name.")
	fun openProject(@CliOption(key = arrayOf(""), help = "Name the project must start with.") name: String?): String {
		val wrong = "No such project, enter an existing name..."
		return name?.let {
			if (!currentProject.projectWithNameExists(name)) return wrong
			val realName = currentProject.enter(name)
			prompt.promptText = realName
			return "Opening project $realName..."
		} ?: wrong
	}

}