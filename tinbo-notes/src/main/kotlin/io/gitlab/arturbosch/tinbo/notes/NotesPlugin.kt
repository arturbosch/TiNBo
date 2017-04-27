package io.gitlab.arturbosch.tinbo.notes

import io.gitlab.arturbosch.tinbo.api.Command
import io.gitlab.arturbosch.tinbo.plugins.SpringContext
import io.gitlab.arturbosch.tinbo.plugins.TiNBoPlugin
import jline.console.ConsoleReader
import org.springframework.stereotype.Component

/**
 * @author Artur Bosch
 */
@Component
class NotesPlugin : TiNBoPlugin {

	override fun registerCommands(tinboContext: SpringContext): List<Command> {
		val consoleReader = tinboContext.beanOf<ConsoleReader>()
		val tinboConfig = tinboContext.tinboConfig
		val persister = NotePersister(tinboConfig)
		val dataHolder = NoteDataHolder(persister, tinboConfig)
		val executor = NoteExecutor(dataHolder, tinboConfig)
		val noteCommands = NoteCommands(executor, consoleReader)
		tinboContext.registerSingleton("NoteCommands", noteCommands)

		val notesModeCommand = StartNotesModeCommand()
		tinboContext.registerSingleton("StartNoteModeCommand", notesModeCommand)

		tinboContext.registerSingleton("NotesPersister", persister)
		return listOf(noteCommands, notesModeCommand)
	}

}