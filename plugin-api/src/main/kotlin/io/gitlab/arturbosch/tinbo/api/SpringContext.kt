package io.gitlab.arturbosch.tinbo.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * @author Artur Bosch
 */
@Component
class SpringContext @Autowired constructor(val context: ApplicationContext,
										   val commands: List<Command>) {


}