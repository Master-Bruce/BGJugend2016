package de.schiewe.volker.bgjugend2016.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.schiewe.volker.bgjugend2016.helper.DatabaseHelper
import de.schiewe.volker.bgjugend2016.helper.NotificationHelper
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.GeneralData
import de.schiewe.volker.bgjugend2016.viewModels.EVENT_REFERENCE

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (!action.equals(Intent.ACTION_BOOT_COMPLETED))
            return
        recreateNotifications(context)
    }

    private fun recreateNotifications(context: Context?) {
        // TODO improve structure
        if (context == null)
            return
        var generalData: GeneralData?
        DatabaseHelper.getDatabase()
                .getReference("generalData")
                .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    generalData = dataSnapshot.getValue(GeneralData::class.java)
                                    val databaseName = generalData?.currentDatabaseName
                                    DatabaseHelper.getDatabase().getReference("$databaseName/$EVENT_REFERENCE")
                                            .addValueEventListener(
                                                    object : ValueEventListener {
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                val eventList: MutableList<Event> = mutableListOf()
                                                                dataSnapshot.children.forEach {
                                                                    val event: Event? = it.getValue(Event::class.java)
                                                                    eventList.add(event!!)
                                                                }
                                                                val notifications = NotificationHelper(context)
                                                                notifications.restoreNotifications(eventList)
                                                            }
                                                        }

                                                        override fun onCancelled(databaseError: DatabaseError) {
                                                            // Failed to read value
                                                        }
                                                    }
                                            )

                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Failed to read value
                            }
                        })

    }
}