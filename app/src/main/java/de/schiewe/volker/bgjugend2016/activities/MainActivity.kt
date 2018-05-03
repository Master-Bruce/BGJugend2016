package de.schiewe.volker.bgjugend2016.activities

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.fragments.EventFragment
import de.schiewe.volker.bgjugend2016.fragments.EventListFragment
import de.schiewe.volker.bgjugend2016.fragments.InfoFragment
import de.schiewe.volker.bgjugend2016.fragments.PreferenceFragment
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.Info
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), EventListFragment.OnListItemSelectedListener, EventFragment.GetEventListener {
    override fun getEventById(event_id: Int): Event {

        throw NotImplementedError("Item should be of type event")
    }

    override fun onListItemSelected(item: BaseEvent) {
        if (item is Info)
            return
        val eventFragment = EventFragment.newInstance(1)
        openFragment(eventFragment, true)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_events -> {
//                toolbar.title = "Songs"
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

    private fun openFragment(fragment: Fragment, addToBackStack:Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.title = "Jugendarbeit EBU"
        openFragment(EventListFragment.newInstance())
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}
