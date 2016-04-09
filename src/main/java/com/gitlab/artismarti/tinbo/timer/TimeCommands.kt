package com.gitlab.artismarti.tinbo.timer

import com.gitlab.artismarti.tinbo.config.Default
import com.gitlab.artismarti.tinbo.config.ModeAdvisor
import com.gitlab.artismarti.tinbo.utils.printlnInfo
import org.springframework.shell.core.CommandMarker
import org.springframework.shell.core.annotation.CliAvailabilityIndicator
import org.springframework.shell.core.annotation.CliCommand
import org.springframework.shell.core.annotation.CliOption
import org.springframework.stereotype.Component
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.concurrent.CompletableFuture

/**
 * Commands users can write to interact in timer mode.
 *
 * @author artur
 */
@Component
class TimeCommands(val executor: TimeExecutor = Injekt.get()) : CommandMarker {

    @CliAvailabilityIndicator("listt", "start", "stop", "q", "loadt", "show")
    fun isAvailable(): Boolean {
        return ModeAdvisor.isTimerMode()
    }

    @CliCommand("listTimers", "listt", help = "Lists whole timer data sorted by date. Can be filtered by category name.")
    fun listData(@CliOption(key = arrayOf("category", "cat"), unspecifiedDefaultValue = "", specifiedDefaultValue = "",
            help = "Name to filter only for this specific category.") categoryName: String): String {
        val data = when (categoryName) {
            "" -> executor.listData()
            else -> executor.listDataFilteredBy(categoryName)
        }
        return data
    }

    @CliCommand(value = "start", help = "Starts the timer and waits for you to type 'stop' to finish it if no arguments are specified.")
    fun startTimer(@CliOption(key = arrayOf("minutes", "m", "mins"), specifiedDefaultValue = "0",
            unspecifiedDefaultValue = "0", help = "Duration of timer in minutes.") mins: Int,
                   @CliOption(key = arrayOf("seconds", "s", "mins"), specifiedDefaultValue = "0",
                           unspecifiedDefaultValue = "0", help = "Duration of timer in seconds.") seconds: Int,
                   @CliOption(key = arrayOf("background", "bg"), unspecifiedDefaultValue = "false",
                           specifiedDefaultValue = "true", help = "If the timer should be started in background.") bg: Boolean,
                   @CliOption(key = arrayOf("name", "n"), unspecifiedDefaultValue = Default.MAIN_CATEGORY_NAME,
                           specifiedDefaultValue = Default.MAIN_CATEGORY_NAME, help = "Category in which the time should be saved.") name: String,
                   @CliOption(key = arrayOf("message", "msg"), unspecifiedDefaultValue = "",
                           specifiedDefaultValue = "", help = "Note for this tracking.") message: String) {

        if (inputsAreInvalid(mins, seconds)) {
            printlnInfo("Invalid parameters: minutes and seconds have to be positive and seconds not bigger than 59.")
            return;
        }

        if (!executor.inProgress()) {
            val mode = specifyTimerMode(bg)
            CompletableFuture.runAsync {
                executor.startPrintingTime(Timer(mode, name, message, stopDateTime = Timer.calcStopTime(mins, seconds)))
            }
        } else {
            printlnInfo("Other timer already in process. Stop the timer before starting a new one.")
        }
    }

    private fun specifyTimerMode(bg: Boolean): TimeMode {
        if (bg) return TimeMode.BACKGROUND
        else return TimeMode.DEFAULT
    }

    private fun inputsAreInvalid(mins: Int, seconds: Int): Boolean {
        return !(mins >= 0 && seconds >= 0 && seconds < 60)
    }

    @CliCommand(value = "stop", help = "Stops the timer.")
    fun stopTimer(@CliOption(key = arrayOf("name", "n"),
            unspecifiedDefaultValue = "",
            specifiedDefaultValue = "",
            help = "Category in which the time should be saved.") name: String,
                  @CliOption(key = arrayOf("message", "msg"),
                          unspecifiedDefaultValue = "",
                          specifiedDefaultValue = "",
                          help = "Note for this tracking.") message: String) {
        executor.stop(name, message)
    }

    @CliCommand(value = "q", help = "Stops the timer.")
    fun stopTimerWithQ() {
        executor.stop()
    }

    @CliCommand("loadTimers", "loadt", help = "Changes the complete data set of timers and categories.")
    fun loadData(@CliOption(key = arrayOf("name"), help = "name of the data set to load",
            unspecifiedDefaultValue = Default.DATA_NAME,
            specifiedDefaultValue = Default.DATA_NAME) name: String) {
        executor.loadData(name)
    }

    @CliCommand(value = "show", help = "Shows the current running timer. Useful when in background mode.")
    fun showCurrentTimer(): String {
        return executor.showTimer()
    }
}