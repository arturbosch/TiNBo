package io.gitlab.arturbosch.tinbo.notes

import io.gitlab.arturbosch.tinbo.model.AbstractExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author artur
 */
@Component
open class NoteExecutor @Autowired constructor(noteDataHolder: NoteDataHolder) :
		AbstractExecutor<NoteEntry, NoteData, DummyNote>(noteDataHolder) {

	override val TABLE_HEADER: String
		get() = "No.;Message"

	override fun newEntry(index: Int, dummy: DummyNote): NoteEntry {
		val entry = entriesInMemory[index]
		return entry.copy(dummy.message)
	}

}

