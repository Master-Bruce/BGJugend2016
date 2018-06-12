package de.schiewe.volker.bgjugend2016.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.storage.FirebaseStorage
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.helper.GlideApp
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_event.*


class EventFragment : Fragment(), AppBarLayout.OnOffsetChangedListener {
    private var event: Event? = null
    private var menu: Menu? = null
    private var menuItems: List<Int> = listOf(R.id.menu_register, R.id.menu_notification, R.id.menu_calender, R.id.menu_map, R.id.menu_share)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_event, container, false)
        val storageReference: FirebaseStorage = FirebaseStorage.getInstance()

        val sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        sharedViewModel.getSelected().observe(activity!!, Observer { item ->
            event = item
            storageReference.getReference("event_img/${event!!.imagePath}").downloadUrl.addOnSuccessListener { uri ->
                val eventImage = rootView.findViewById<ImageView>(R.id.event_image)
                GlideApp.with(this@EventFragment)
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(2000))
                        .into(eventImage)

            }
        })
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).setSupportActionBar(event_toolbar)
        setHasOptionsMenu(true)
        main_appbar.addOnOffsetChangedListener(this)

        if (event != null) {
            event_toolbar.title = event!!.title
            header_text.text = event!!.header
            place_text.text = event!!.place
            event_text.text = event!!.formattedText()
            age_text.text = event!!.ageString()
            people_text.text = event!!.peopleNumber.toString()
            cost_text.text = event!!.costString()
            deadline_text.text = event!!.deadlineString()
            team_text.text = event!!.team

            contact_name.text = event!!.contact?.name ?: ""
            contact_address.text = event!!.contact?.formattedAddress() ?: ""
            contact_phone.text = event!!.contact?.telephone ?: ""
            contact_mail.text = event!!.contact?.mail ?: ""
        }
        fab_register.setOnClickListener({registerForEvent()})
        button_register.setOnClickListener({registerForEvent()})
        place_text.setOnClickListener({openMap()})
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        this.menu = menu
        inflater?.inflate(R.menu.main_menu, menu)
        changeMenuVisibility(false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_notification -> {
                // TODO set or remove notification
                item.isChecked = !item.isChecked
            }
            R.id.menu_calender -> {
                // Todo start calender
            }
            R.id.menu_map -> {
                this.openMap()
            }
            R.id.menu_register -> {
                this.registerForEvent()
            }
            R.id.menu_share -> {
                // TODO share intent
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val scrollRange = appBarLayout!!.totalScrollRange
        if (scrollRange + verticalOffset == 0) {
            changeMenuVisibility(true)
        } else {
            changeMenuVisibility(false)
        }
    }

    private fun changeMenuVisibility(visibility: Boolean) {
        if (menu != null) {
            for (itemId in this.menuItems) {
                val item = menu!!.findItem(itemId)
                item.isVisible = visibility
            }
        }
    }

    private fun registerForEvent(){
        // TODO open registration mail
    }

    private fun openMap(){
        // TODO open Map
    }

    companion object {
        @JvmStatic
        fun newInstance(): EventFragment = EventFragment()
    }

}
