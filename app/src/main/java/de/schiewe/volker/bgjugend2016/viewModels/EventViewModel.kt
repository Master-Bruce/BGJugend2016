package de.schiewe.volker.bgjugend2016.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.database.*
import de.schiewe.volker.bgjugend2016.models.Event


class EventViewModel : ViewModel() {
    var events: MutableLiveData<List<Event>> = MutableLiveData()

    fun getEvents(): LiveData<List<Event>> {
        val yearDbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("2018/events")
        val eventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val list: MutableList<Event> = mutableListOf()
                    dataSnapshot.children.forEach{
                        val event:Event? = it.getValue(Event::class.java)
                        list.add(event!!)
                    }
                    events.value = list
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                events.value = null
                // Failed to read value
            }
        }
        yearDbRef.addValueEventListener(eventListener)
        return events
    }
}