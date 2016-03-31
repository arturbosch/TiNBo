package com.gitlab.artismarti.tinbo.notes

import com.gitlab.artismarti.tinbo.config.Default
import com.gitlab.artismarti.tinbo.persistence.Data

/**
 * @author artur
 */
class NotesData(name: String = Default.NOTES_NAME, entries: List<NoteEntry> = listOf()) : Data(name, entries)
