package de.schiewe.volker.bgjugend2016.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.schiewe.volker.bgjugend2016.helper.DatabaseHelper
import de.schiewe.volker.bgjugend2016.helper.NotificationHelper
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.GeneralData
import de.schiewe.volker.bgjugend2016.viewModels.EVENT_REFERENCE

class BootReceiver : BroadcastReceiver() {
    private val TAG: String = this.javaClass.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (!action.equals(Intent.ACTION_BOOT_COMPLETED))
            return
        initRecreateNotifications(context)
    }

    private fun initRecreateNotifications(context: Context?) {
        if (context == null)
            return

        DatabaseHelper.getDatabase().getReference("generalData")
                .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val generalData = dataSnapshot.getValue(GeneralData::class.java)
                                    val databaseName = generalData?.currentDatabaseName
                                    if (databaseName != null)
                                        getEvents(databaseName, context)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Failed to read value
                            }
                        })

    }

    fun getEvents(databaseName: String, context: Context?) {
        DatabaseHelper.getDatabase().getReference("$databaseName/$EVENT_REFERENCE")
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
                                            Log.d(TAG, "Could not parse: ${it.key}")
                                        }
                                    }
                                    restoreNotifications(eventList, context)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Failed to read value
                            }
                        }
                )
    }

    private fun restoreNotifications(events: List<Event>, context: Context?) {
        val notifications = NotificationHelper(context!!)
        notifications.restoreNotifications(events)
    }
}