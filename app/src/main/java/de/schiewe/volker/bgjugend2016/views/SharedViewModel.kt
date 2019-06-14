package de.schiewe.volker.bgjugend2016.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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