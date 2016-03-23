package com.gitlab.artismarti.tinbo.timer

import java.time.Duration
import java.time.LocalDateTime

/**
 * Domain model for timers. Specifies by a mode, start and end time.
 * Methods are provided to test if the timer is invalid or finished.
 * A convenient static calc end time method is provided.
 *
 * @author artur
 */
class Timer {

    constructor(timerMode: TimerMode = TimerMode.INVALID,
                startDateTime: LocalDateTime = LocalDateTime.now(),
                stopDateTime: LocalDateTime? = null) {
        this.timerMode = timerMode
        this.startDateTime = startDateTime
        this.stopDateTime = stopDateTime
    }

    val timerMode: TimerMode
    private val startDateTime: LocalDateTime
    private val stopDateTime: LocalDateTime?

    override fun toString(): String {
        val now = LocalDateTime.now()
        val diffSecs = Duration.between(startDateTime, now).seconds.toNumberString()
        val diffMins = Duration.between(startDateTime, now).toMinutes().toNumberString()
        val diffHours = Duration.between(startDateTime, now).toHours().toNumberString()
        return "$diffHours:$diffMins:$diffSecs"
    }

    fun isInvalid(): Boolean {
        return this.equals(INVALID)
    }

    fun isFinished(): Boolean {
        if (stopDateTime == null) {
            return false
        }
        return LocalDateTime.now().compareTo(stopDateTime) >= 0
    }

    companion object {

        val INVALID = Timer(TimerMode.INVALID)
        fun calcStopTime(mins: Int, seconds: Int): LocalDateTime? {
            var stop: LocalDateTime? = null
            if (mins >= 0 && seconds > 0) {
                stop = LocalDateTime.now()
                        .plusMinutes(mins.toLong())
                        .plusSeconds(seconds.toLong())
            }
            return stop
        }

    }
}
