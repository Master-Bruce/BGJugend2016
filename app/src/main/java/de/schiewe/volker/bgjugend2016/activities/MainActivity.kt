package de.schiewe.volker.bgjugend2016.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.MenuItem
import com.facebook.drawee.backends.pipeline.Fresco
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.database.DatabaseHelper
import de.schiewe.volker.bgjugend2016.fragments.*
import de.schiewe.volker.bgjugend2016.interfaces.OnListItemSelectedListener
import de.schiewe.volker.bgjugend2016.isNewVersion
import de.schiewe.volker.bgjugend2016.migrateToCurrentVersion
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.views.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnListItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intentData = intent.data
        if (intentData is Uri) {
            // Deep Link and Notification click handling
            handleIntentData(intentData)
        }

        if (savedInstanceState == null) {
            Fresco.initialize(this)
            // Open default Fragment
            openFragment(EventListFragment.newInstance())
        }

        if (isNewVersion(PreferenceManager.getDefaultSharedPreferences(this), getString(R.string.last_version_key))) {
            migrateToCurrentVersion(this)
        }

        navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        if (intent != null && intent.data is Uri)
            handleIntentData(intent.data)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_events -> {
                val eventFragment = EventListFragment.newInstance()
                openFragment(eventFragment)
                return true
            }
            R.id.navigation_info -> {
                val infoFragment = InfoFragment.newInstance()
                openFragment(infoFragment)
                return true
            }
            R.id.navigation_settings -> {
                val preferenceFragment = PreferenceFragment.newInstance()
                openFragment(preferenceFragment)
                return true
            }
        }
        return false
    }

    override fun onFilterButtonClicked() {
        FilterModalBottomSheet().show(supportFragmentManager!!, "Sheet")
    }

    override fun onEventSelected() {
        val eventFragment = EventFragment.newInstance()
        openFragment(eventFragment, true)
    }

    private fun handleIntentData(uri: Uri) {
        DatabaseHelper(this).getEvents().observe(this, Observer { events ->
            val event = events?.single { event -> event is Event && Uri.parse(event.url) == uri }
            if (event != null) {
                val sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
                sharedViewModel.select(event as Event)
                openFragment(EventFragment.newInstance())
                DatabaseHelper(this).getEvents().removeObservers(this@MainActivity)
            }
        })
    }

    private fun openFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }
}
