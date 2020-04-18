package de.schiewe.volker.bgjugend2016.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.FirebaseDatabase
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.GeneralData

const val EVENT_REFERENCE = "events"
const val GENERAL_DATA_REFERENCE = "generalData"
const val INFO_REFERENCE = "infos"

class DatabaseHelper(private val context: FragmentActivity) {
    fun getEvents(): LiveData<List<BaseEvent>> {
        val eventViewModel = ViewModelProvider(context).get(EventViewModel::class.java)
        val generalDataViewModel = ViewModelProvider(context).get(GeneralDataViewModel::class.java)
        generalDataViewModel.getGeneralData().observe(context, Observer { generalData: GeneralData? ->
            if (generalData != null) {
                eventViewModel.databaseName = generalData.currentDatabaseName
            }
        })
        return eventViewModel.getEvents()
    }

    fun getGeneralData(): LiveData<GeneralData> {
        val generalDataViewModel = ViewModelProvider(context).get(GeneralDataViewModel::class.java)
        return generalDataViewModel.getGeneralData()
    }

    companion object {
        private var mDatabase: FirebaseDatabase? = null
        fun getDatabase(): FirebaseDatabase {
            if (mDatabase == null) {
                mDatabase = FirebaseDatabase.getInstance()
                mDatabase?.setPersistenceEnabled(true)
            }
            return mDatabase!!
        }
    }
}

