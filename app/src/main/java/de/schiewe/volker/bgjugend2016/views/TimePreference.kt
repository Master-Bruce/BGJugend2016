package de.schiewe.volker.bgjugend2016.views


import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet

class TimePreference(ctxt: Context, attrs: AttributeSet) : DialogPreference(ctxt, attrs) {
    private var lastHour = 0
    private var lastMinute = 0

    fun setTime(hour: Int, minute: Int) {
        val newValue = "${getTimeString(hour)}:${getTimeString(minute)}"
        this.persistString(newValue)
        this.onPreferenceChangeListener.onPreferenceChange(this, newValue)
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        lastHour = getHour()
        lastMinute = getMinute()
    }

    fun getHour(): Int {
        val value = this.getPersistedString("00:00")
        val pieces = value.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        return Integer.parseInt(pieces[0])
    }

    fun getMinute(): Int {
        val value = this.getPersistedString("00:00")
        val pieces = value.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        return Integer.parseInt(pieces[1])
    }

    private fun getTimeString(time: Int): String {
        if (time < 10)
            return "0$time"
        return "$time"
    }
}