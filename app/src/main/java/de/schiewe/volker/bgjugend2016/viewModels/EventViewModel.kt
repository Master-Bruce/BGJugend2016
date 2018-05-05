package de.schiewe.volker.bgjugend2016.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.database.*
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.Info


class EventViewModel : ViewModel() {
    var baseEvents: MediatorLiveData<List<BaseEvent>> = MediatorLiveData()
    var events: MutableLiveData<List<Event>> = MutableLiveData()
    var infos: MutableLiveData<List<Info>> = MutableLiveData()

    init {
        baseEvents.addSource(events, { events ->
            if (infos.value != null)
                baseEvents.value = events as List<BaseEvent> + infos.value as List<BaseEvent>
            else
                baseEvents.value = events
        })

        baseEvents.addSource(infos, { infos ->
            if (events.value != null)
                baseEvents.value = infos as List<BaseEvent> + events.value as List<BaseEvent>
            else
                baseEvents.value = infos

        })
    }

    fun getEvents(): LiveData<List<BaseEvent>> {
        FirebaseDatabase.getInstance().getReference("2018/events")
                .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val eventList: MutableList<Event> = mutableListOf()
                                    dataSnapshot.children.forEach {
                                        val event: Event? = it.getValue(Event::class.java)
                                        eventList.add(event!!)
                                    }
                                    events.value = eventList
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                events = MutableLiveData()
                                // Failed to read value
                            }
                        })
        FirebaseDatabase.getInstance().getReference("2018/infos")
                .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val infoList: MutableList<Info> = mutableListOf()
                                    dataSnapshot.children.forEach {
                                        val info: Info? = it.getValue(Info::class.java)
                                        infoList.add(info!!)
                                    }
                                    infos.value = infoList
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                infos = MutableLiveData()
                                // Failed to read value
                            }
                        })


        return baseEvents
    }
}