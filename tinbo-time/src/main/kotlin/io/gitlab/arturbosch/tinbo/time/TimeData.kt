package io.gitlab.arturbosch.tinbo.time

import io.gitlab.arturbosch.tinbo.api.model.Data
import io.gitlab.arturbosch.tinbo.api.config.Defaults
import org.springframework.stereotype.Component

/**
 * @author artur
 */
@Component
open class TimeData(name: String = Defaults.TIME_NAME,
					entries: List<TimeEntry> = listOf()) : Data<TimeEntry>(name, entries)
