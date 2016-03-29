package com.gitlab.artismarti.tinbo.timer

import com.gitlab.artismarti.tinbo.Notification
import com.gitlab.artismarti.tinbo.printer.CSVTablePrinter
import com.gitlab.artismarti.tinbo.config.Default
import com.gitlab.artismarti.tinbo.persistence.Category
import com.gitlab.artismarti.tinbo.printer.printInfo
import com.gitlab.artismarti.tinbo.printer.printlnInfo
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * @author artur
 */
class TimerExecutor(val timerDataHolder: TimerDataHolder = Injekt.get()) {

    init {
        timerDataHolder.loadData(Default.DATA_NAME)
    }

    private var currentTimer = Timer.INVALID
    private var running = false

    fun inProgress(): Boolean {
        return !currentTimer.isInvalid()
    }

    fun listData(): List<String> {
        val csv = CSVTablePrinter()

        val data = timerDataHolder.data
        val table = data.categories.flatMap { extractEntriesAsString(it) }.toMutableList()
        table.add(0, "Category;Date;H;M;S;Notice")

        return csv.asTable(table)
    }

    private fun extractEntriesAsString(category: Category): List<String> = category.entries.sorted().map { "${category.name};${it.toString()}" }

    fun loadData(name: String) {
        timerDataHolder.loadData(name)
    }

    fun startPrintingTime(timer: Timer) {
        currentTimer = timer
        running = true
        while (running) {
            if (currentTimer.timerMode == TimerMode.DEFAULT)
                printInfo("\rElapsed time: $timer")
            if (currentTimer.isFinished())
                stop()
            Thread.sleep(1000L)
        }
    }

    fun stop() {
        if (inProgress()) {
            running = false
            saveAndResetCurrentTimer()
        } else {
            printlnInfo("Other timer already in process. Stop the timer before starting a new one.")
        }
    }

    private fun saveAndResetCurrentTimer() {
        notify()
        timerDataHolder.persistEntry(currentTimer.name, createTimeEntry())
        currentTimer = Timer.INVALID
    }

    private fun createTimeEntry(): TimerEntry {
        val (secs, mins, hours) = currentTimer.getTimeTriple()
        return TimerEntry(currentTimer.message, hours, mins, secs, currentTimer.startDateTime.toLocalDate())
    }

    private fun notify() {
        Notification.finished(currentTimer.toString())
    }

}
