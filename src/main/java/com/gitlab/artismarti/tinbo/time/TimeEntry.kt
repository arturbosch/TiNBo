package com.gitlab.artismarti.tinbo.time

import com.gitlab.artismarti.tinbo.common.Entry
import com.gitlab.artismarti.tinbo.spaceIfEmpty
import com.gitlab.artismarti.tinbo.toTimeString
import com.gitlab.artismarti.tinbo.utils.dateFormatter
import java.time.LocalDate

/**
 * @author artur
 */
class TimeEntry(var category: String = "Main",
				var message: String = "invalid",
				var hours: Long = -1L,
				var minutes: Long = -1L,
				var seconds: Long = -1L,
				var date: LocalDate = LocalDate.now()) : Entry() {

	override fun toString(): String {
		return "$category;${date.format(dateFormatter)};${hours.toTimeString()}:${minutes.toTimeString()}" +
				":${seconds.toTimeString()};${message.spaceIfEmpty()}"
	}

	override fun compareTo(other: Entry): Int {
		if (other !is TimeEntry) return -1
		return this.date.compareTo(other.date)
	}
}

