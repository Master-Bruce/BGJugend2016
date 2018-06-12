package de.schiewe.volker.bgjugend2016.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import com.google.firebase.database.FirebaseDatabase
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.fragments.*
import de.schiewe.volker.bgjugend2016.isNewVersion
import de.schiewe.volker.bgjugend2016.migrateToCurrentVersion
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), EventListFragment.OnListItemSelectedListener {
    override fun onFilterButtonClicked() {
        FilterModalBottomSheet().show(supportFragmentManager!!, "Sheet")
    }

    override fun onEventSelected() {
        val eventFragment = EventFragment.newInstance()
        openFragment(eventFragment, true)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_events -> {
                val eventFragment = EventListFragment.newInstance()
                openFragment(eventFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_info -> {
                val infoFragment = InfoFragment.newInstance()
                openFragment(infoFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                val preferenceFragment = PreferenceFragment.newInstance()
                openFragment(preferenceFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            openFragment(EventListFragment.newInstance())
        }

        if (isNewVersion(PreferenceManager.getDefaultSharedPreferences(this), getString(R.string.last_version_key))) {
                migrateToCurrentVersion(this)
        }

//        TODO REMOVE ME
//        val notifications = NotificationHelper(this)
//        val event = Event()
//        event.pk = 1
//        event.startDate = Calendar.getInstance().timeInMillis
//        event.title = "Kinderrüsttage"
//        val event2 = Event()
//        event2.pk = 2
//        event2.startDate = Calendar.getInstance().timeInMillis
//        event2.title = "Herrnhaag"
//        notifications.setNotification(event, true)
//        notifications.setNotification(event2, true)
//        notifications.setNotification(event, false)


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
