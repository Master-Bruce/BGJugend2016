package de.schiewe.volker.bgjugend2016.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.Info

class EventViewModel : ViewModel() {
    private val tag: String = this.javaClass.name

    private var baseEvents: MediatorLiveData<List<BaseEvent>> = MediatorLiveData()
    private var events: MutableLiveData<List<Event>> = MutableLiveData()
    private var infos: MutableLiveData<List<Info>> = MutableLiveData()

    var databaseName: String? = null
        set(value) {
            field = value
            this.initFirebaseListeners()
        }

    init {
        baseEvents.addSource(events) { events ->
            if (infos.value != null)
                baseEvents.value = events as List<BaseEvent> + infos.value as List<BaseEvent>
            else
                baseEvents.value = events
        }

        baseEvents.addSource(infos) { infos ->
            if (events.value != null)
                baseEvents.value = infos as List<BaseEvent> + events.value as List<BaseEvent>
            else
                baseEvents.value = infos
        }
    }

    private fun initFirebaseListeners() {
        DatabaseHelper.getDatabase().getReference("${this.databaseName}/$EVENT_REFERENCE")
                .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val eventList: MutableList<Event> = mutableListOf()
                                    dataSnapshot.children.forEach { snapshot ->
                                        try {
                                            val event: Event? = snapshot.getValue(Event::class.java)
                                            event?.let { eventList.add(it)  }
                                        } catch (e: Exception) {
                                            Log.d(tag, "Event could not be parsed (key: ${snapshot.key}, Exception: $e)")
                                        }
                                    }
                                    events.value = eventList
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                events = MutableLiveData()
                                // Failed to read value
                            }
                        }
                )
        DatabaseHelper.getDatabase().getReference("${this.databaseName}/$INFO_REFERENCE")
                .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val infoList: MutableList<Info> = mutableListOf()
                                    dataSnapshot.children.forEach { snapshot ->
                                        try {
                                            val info: Info? = snapshot.getValue(Info::class.java)
                                            info?.let { infoList.add(it) }
                                        } catch (e: Exception) {
                                            Log.d(tag, "Info could not be parsed (key: ${snapshot.key}, Exception: $e)")
                                        }
                                    }
                                    infos.value = infoList
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                infos = MutableLiveData()
                                // Failed to read value
                            }
                        }
                )
    }

    fun getEvents(): LiveData<List<BaseEvent>> {
        return baseEvents
    }
}