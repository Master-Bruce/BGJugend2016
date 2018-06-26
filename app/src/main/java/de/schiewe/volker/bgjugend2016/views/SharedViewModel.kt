package de.schiewe.volker.bgjugend2016.views

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import de.schiewe.volker.bgjugend2016.models.Event


class SharedViewModel : ViewModel() {
    private val selected = MutableLiveData<Event>()

    fun select(item: Event) {
        selected.value = item
    }

    fun getSelected(): LiveData<Event> {
        return selected
    }
}