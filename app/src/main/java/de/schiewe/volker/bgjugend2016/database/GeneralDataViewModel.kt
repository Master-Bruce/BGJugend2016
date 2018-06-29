package de.schiewe.volker.bgjugend2016.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.schiewe.volker.bgjugend2016.models.GeneralData


class GeneralDataViewModel : ViewModel() {
    private var generalData: MutableLiveData<GeneralData> = MutableLiveData()

    fun getGeneralData(): LiveData<GeneralData> {
        DatabaseHelper.getDatabase().getReference(GENERAL_DATA_REFERENCE)
                .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    generalData.value = dataSnapshot.getValue(GeneralData::class.java)
                                 }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Failed to read value
                            }
                        })


        return generalData
    }
}