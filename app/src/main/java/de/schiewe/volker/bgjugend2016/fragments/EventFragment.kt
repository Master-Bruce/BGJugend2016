package de.schiewe.volker.bgjugend2016.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.google.firebase.storage.FirebaseStorage
import com.stfalcon.frescoimageviewer.ImageViewer
import de.schiewe.volker.bgjugend2016.*
import de.schiewe.volker.bgjugend2016.helper.GlideApp
import de.schiewe.volker.bgjugend2016.helper.NotificationHelper
import de.schiewe.volker.bgjugend2016.interfaces.AppBarStateChangeListener
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.views.SharedViewModel
import kotlinx.android.synthetic.main.fragment_event.*


class EventFragment : Fragment(), AppBarStateChangeListener, UserDataModalBottomSheet.UserDataSubmitListener {
    private val TAG = this.javaClass.simpleName
    private var event: Event? = null
    private var menu: Menu? = null
    private var imageUrl: Uri? = null
    private var menuItems: List<Int> = listOf(R.id.menu_register, R.id.menu_notification, R.id.menu_calender, R.id.menu_map, R.id.menu_share)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_event, container, false)

        val sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        sharedViewModel.getSelected().observe(activity!!, Observer { item ->
            event = item
            val storage: FirebaseStorage = FirebaseStorage.getInstance()
            val imageReference = storage.getReference("event_img/${event!!.imagePath}")
            imageReference.downloadUrl.addOnSuccessListener { url ->
                this.imageUrl = url
            }
        })

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).setSupportActionBar(event_toolbar)
        setHasOptionsMenu(true)
        main_appbar.addOnOffsetChangedListener(this)
        val storage: FirebaseStorage = FirebaseStorage.getInstance()

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

            hideEmptyTextViews(listOf(contact_name, contact_address, contact_phone, contact_mail))

            val imageReference = storage.getReference("event_img/${event!!.imagePath}")
            val eventImage = view.findViewById<ImageView>(R.id.event_image)

            GlideApp.with(this@EventFragment)
                    .load(imageReference)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(getProgressBar(activity!!, 10f, 100f))
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .error(R.drawable.youth_sheep)
                    .into(eventImage)

            event_image.setOnClickListener { _: View? ->
                if (this.imageUrl == null)
                    return@setOnClickListener

                try {
                    val hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(resources)
                            .setProgressBarImage(getProgressBar(activity!!, 10f, 100f))

                    ImageViewer.Builder(activity, mutableListOf(this.imageUrl!!))
                            .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                            .setStartPosition(0)
                            .show()

                }
                catch (e: Exception){
                    Log.e(TAG, "Error on image clicked: $e")
                }
            }
        }

        fab_register.setOnClickListener { onRegisterClick() }
        button_register.setOnClickListener { onRegisterClick() }
        place_text.setOnClickListener { openMap() }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        this.menu = menu
        inflater?.inflate(R.menu.main_menu, menu)
        changeMenuVisibility(false)

        if (menu != null && event != null && activity != null) {
            val notificationHelper = NotificationHelper(activity!!)
            val notificationItem = menu.findItem(R.id.menu_notification)
            notificationItem.isChecked = notificationHelper.isNotificationEnabled(event!!.pk)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_notification -> {
                if (event != null && activity != null) {
                    val newValue = !item.isChecked
                    val notifications = NotificationHelper(activity!!)
                    val snackbarText = if (notifications.setNotification(event!!, newValue)) {
                        item.isChecked = newValue
                        if (newValue)
                            "Benachrichtigung erstellt."
                        else
                            "Benachrichtigung entfernt"
                    } else
                        "Schon zu spät"

                    view?.let { Snackbar.make(it, snackbarText, Snackbar.LENGTH_LONG).show() }
                }
            }
            R.id.menu_calender -> {
                if (event != null && activity != null)
                    addEventToCalender(activity!!, event!!)
            }
            R.id.menu_map -> {
                this.openMap()
            }
            R.id.menu_register -> {
                this.onRegisterClick()
            }
            R.id.menu_share -> {
                if (event != null && activity != null)
                    shareEvent(activity!!, event!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override var mCurrentState: AppBarStateChangeListener.State = AppBarStateChangeListener.State.IDLE
    override fun onStateChanged(appBarLayout: AppBarLayout?, state: AppBarStateChangeListener.State) {
        if (state == AppBarStateChangeListener.State.COLLAPSED) {
            changeMenuVisibility(true)
        } else {
            changeMenuVisibility(false)
        }
    }

    private fun changeMenuVisibility(visibility: Boolean) {
        if (menu != null) {
            for (itemId in this.menuItems) {
                val item = menu!!.findItem(itemId)
                val showAsAction = if (visibility) MenuItem.SHOW_AS_ACTION_IF_ROOM else MenuItem.SHOW_AS_ACTION_NEVER
                item.setShowAsAction(showAsAction)

                if (itemId == R.id.menu_register)
                    item.isVisible = visibility
            }
        }
    }

    private fun openMap() {
        if (event != null && activity != null)
            openPlaceOnMap(activity!!, event!!.place)
    }

    private fun onRegisterClick() {
        UserDataModalBottomSheet().show(childFragmentManager, USER_DATA_BOTTOM_SHEET)
    }

    override fun onUserDataSubmit() {
        (childFragmentManager.findFragmentByTag(USER_DATA_BOTTOM_SHEET) as UserDataModalBottomSheet).dismiss()
        if (event == null)
            return
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity)

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", event?.contact?.mail, null))
        // TODO correct grammar
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Anmeldung für ${event?.title}")
        emailIntent.putExtra(Intent.EXTRA_TEXT, generateMailText(activity!!, event!!, sharedPrefs))
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)))
    }

    private fun hideEmptyTextViews(textViews: List<TextView>) {
        for (textView in textViews) {
            if (textView.text.trim() == "")
                textView.visibility = View.GONE
            else
                textView.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): EventFragment = EventFragment()
    }

}
