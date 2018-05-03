package de.schiewe.volker.bgjugend2016.helper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.database.*
import de.schiewe.volker.bgjugend2016.models.Event


class EventViewModel : ViewModel() {
    var events: MutableLiveData<List<Event>> = MutableLiveData()

    fun getEvents(): LiveData<List<Event>> {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val yearDbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("2018")
        val eventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //TODO: get Event objects
                    // ...
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
            }
        }
        yearDbRef.addValueEventListener(eventListener)
        return events
    }
}