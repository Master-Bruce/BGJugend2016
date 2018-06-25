package de.schiewe.volker.bgjugend2016.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.schiewe.volker.bgjugend2016.helper.DatabaseHelper
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.Info

const val EVENT_REFERENCE = "events"
private const val INFO_REFERENCE = "infos"

class EventViewModel : ViewModel() {
    private val TAG: String = this.javaClass.name

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
                                    dataSnapshot.children.forEach {
                                        try {
                                            val event: Event? = it.getValue(Event::class.java)
                                            eventList.add(event!!)
                                        } catch (e: Exception) {
                                            Log.d(TAG, "Event could not be parsed (key: ${it.key}, Exception: $e)")
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
                                    dataSnapshot.children.forEach {
                                        try {
                                            val info: Info? = it.getValue(Info::class.java)
                                            infoList.add(info!!)
                                        } catch (e: Exception) {
                                            Log.d(TAG, "Info could not be parsed (key: ${it.key}, Exception: $e)")
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