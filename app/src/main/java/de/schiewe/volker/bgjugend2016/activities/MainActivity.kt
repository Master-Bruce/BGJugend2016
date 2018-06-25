package de.schiewe.volker.bgjugend2016.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.fragments.*
import de.schiewe.volker.bgjugend2016.helper.DatabaseHelper
import de.schiewe.volker.bgjugend2016.isNewVersion
import de.schiewe.volker.bgjugend2016.migrateToCurrentVersion
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), EventListFragment.OnListItemSelectedListener {
    override fun onFilterButtonClicked() {
        FilterModalBottomSheet().show(supportFragmentManager!!, "Sheet")
    }

    override fun onEventSelected() {
        val eventFragment = EventFragment.newInstance()
        openFragment(eventFragment, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intentData = intent.data
        if (intentData is Uri){
            // Deep Link handling
            DatabaseHelper(this).getEvents().observe(this, Observer { events ->
                val event = events?.single { event -> event is Event && Uri.parse(event.url) == intentData }
                if (event != null){
                    val sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
                    sharedViewModel.select(event as Event)
                    openFragment(EventFragment.newInstance())
                    DatabaseHelper(this).getEvents().removeObservers(this@MainActivity)
                }
            })
        }

        if (savedInstanceState == null) {
            // Open default Fragment
            openFragment(EventListFragment.newInstance())
        }

        if (isNewVersion(PreferenceManager.getDefaultSharedPreferences(this), getString(R.string.last_version_key))) {
            migrateToCurrentVersion(this)
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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
}
