package de.schiewe.volker.bgjugend2016.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.google.firebase.database.FirebaseDatabase
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.GeneralData

const val EVENT_REFERENCE = "events"
const val GENERAL_DATA_REFERENCE = "generalData"
const val INFO_REFERENCE = "infos"

class DatabaseHelper(private val context: FragmentActivity) {
    fun getEvents(): LiveData<List<BaseEvent>> {
        val eventViewModel = ViewModelProviders.of(context).get(EventViewModel::class.java)
        val generalDataViewModel = ViewModelProviders.of(context).get(GeneralDataViewModel::class.java)
        generalDataViewModel.getGeneralData().observe(context, Observer { generalData: GeneralData? ->
            if (generalData != null) {
                eventViewModel.databaseName = generalData.currentDatabaseName
            }
        })
        return eventViewModel.getEvents()
    }

    fun getGeneralData(): LiveData<GeneralData> {
        val generalDataViewModel = ViewModelProviders.of(context).get(GeneralDataViewModel::class.java)
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

