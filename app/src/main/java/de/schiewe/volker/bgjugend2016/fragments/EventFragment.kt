package de.schiewe.volker.bgjugend2016.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.stfalcon.frescoimageviewer.ImageViewer
import de.schiewe.volker.bgjugend2016.*
import de.schiewe.volker.bgjugend2016.helper.Analytics
import de.schiewe.volker.bgjugend2016.helper.NotificationHelper
import de.schiewe.volker.bgjugend2016.helper.TopProgressBarDrawable
import de.schiewe.volker.bgjugend2016.interfaces.AppBarStateChangeListener
import de.schiewe.volker.bgjugend2016.interfaces.UserDataSubmitListener
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.UserData
import de.schiewe.volker.bgjugend2016.views.SharedViewModel
import kotlinx.android.synthetic.main.fragment_event.*
import java.util.*


class EventFragment : Fragment(), AppBarStateChangeListener, UserDataSubmitListener {
    private val classTag = this.javaClass.simpleName
    private var event: Event? = null
    private var menu: Menu? = null
    private var menuItems: List<Int> = listOf(R.id.menu_register, R.id.menu_notification, R.id.menu_calender, R.id.menu_map, R.id.menu_share)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_event, container, false)
        if (activity != null) {
            val activity = activity as AppCompatActivity
            val sharedViewModel = ViewModelProvider(activity).get(SharedViewModel::class.java)
            sharedViewModel.getSelected().observe(activity, Observer { item ->
                event = item
            })
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let {
            (it as AppCompatActivity).setSupportActionBar(event_toolbar)
            setHasOptionsMenu(true)
            main_appbar.addOnOffsetChangedListener(this)

            if (event != null) {
                event_toolbar.title = event?.title
                header_text.text = event?.header
                place_date_text.text = it.getString(R.string.date_and_place, event?.dateString(), event?.place)
                event_text.text = event?.formattedText()
                age_text.text = event?.ageString(it)
                people_text.text = event?.peopleNumber.toString()
                cost_text.text = event?.costString()
                deadline_text.text = event?.deadlineString()
                team_text.text = event?.team

                contact_name.text = event?.contact?.name ?: ""
                contact_address.text = event?.contact?.formattedAddress() ?: ""
                contact_phone.text = event?.contact?.telephone ?: ""
                contact_mail.text = event?.contact?.mail ?: ""

                hideEmptyTextViews(listOf(contact_name, contact_address, contact_phone, contact_mail))

                val eventImage = view.findViewById<SimpleDraweeView>(R.id.event_image)
                val progressBarDrawable = TopProgressBarDrawable()
                progressBarDrawable.color = ContextCompat.getColor(it, R.color.white)
                progressBarDrawable.backgroundColor = ContextCompat.getColor(it, R.color.colorAccent)
                progressBarDrawable.setPadding(0)
                eventImage.hierarchy = GenericDraweeHierarchyBuilder.newInstance(resources)
                        .setProgressBarImage(progressBarDrawable)
                        .setPlaceholderImage(getProgressBar(it, 10f, 100f))
                        .setFailureImage(R.drawable.youth_sheep)
                        .setOverlay(ContextCompat.getDrawable(it, R.drawable.image_gradient))
                        .setFadeDuration(300)
                        .build()

                eventImage.setImageURI(event?.imageUrl)

                event_image.setOnClickListener { _: View? ->
                    if (event?.imageUrl == null)
                        return@setOnClickListener
                    Analytics.logEvent(it, "View Image")
                    try {
                        val hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(resources)
                                .setPlaceholderImage(getProgressBar(it, 10f, 100f))
                                .setFadeDuration(300)

                        ImageViewer.Builder(activity, mutableListOf(event?.imageUrl))
                                .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                                .setStartPosition(0)
                                .show()

                    } catch (e: Exception) {
                        Log.e(classTag, "Error on image clicked: $e")
                    }
                }
            }
        }


        fab_register.setOnClickListener { onRegisterClick() }
        button_register.setOnClickListener { onRegisterClick() }
        place_date_text.setOnClickListener { openMap() }
    }

    override fun onAttach(context: Context) {
        activity?.let {
            Analytics.setScreen(it, javaClass.simpleName)
        }
        super.onAttach(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.main_menu, menu)
        changeMenuVisibility(false)
        val activity = activity
        val event = event
        if (event != null && activity != null) {
            val notificationHelper = NotificationHelper(activity)
            val notificationItem = menu.findItem(R.id.menu_notification)
            notificationItem.isChecked = notificationHelper.isNotificationEnabled(event.pk)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val activity = activity
        val event = event
        when (item.itemId) {
            R.id.menu_notification -> {
                if (event != null && activity != null) {
                    val newValue = !item.isChecked
                    Analytics.logEvent(activity, "Notification $newValue")
                    val notifications = NotificationHelper(activity)
                    val snackbarText = if (notifications.setNotification(event, newValue)) {
                        item.isChecked = newValue
                        if (newValue)
                            getString(R.string.notification_created)
                        else
                            getString(R.string.notification_removed)
                    } else
                        getString(R.string.already_too_late)

                    view?.let { Snackbar.make(it, snackbarText, Snackbar.LENGTH_LONG).show() }
                }
            }
            R.id.menu_calender -> {
                activity?.let { context ->
                    Analytics.logEvent(context, "Add to Calendar")
                    event?.let { addEventToCalender(context, it) }
                }
            }
            R.id.menu_map -> {
                this.openMap()
            }
            R.id.menu_register -> {
                this.onRegisterClick()
            }
            R.id.menu_share -> {
                activity?.let { context ->
                    Analytics.logEvent(context, "Share Event")
                    event?.let { shareEvent(context, it) }
                }
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
                val item = menu?.findItem(itemId)
                val showAsAction = if (visibility) MenuItem.SHOW_AS_ACTION_IF_ROOM else MenuItem.SHOW_AS_ACTION_NEVER
                item?.setShowAsAction(showAsAction)

                if (itemId == R.id.menu_register)
                    item?.isVisible = visibility
            }
        }
    }

    private fun openMap() {
        activity?.let { context ->
            Analytics.logEvent(context, "Open Map")
            event?.let { openPlaceOnMap(context, it.place) }
        }
    }

    private fun onRegisterClick() {
        val event = event
        val startDate = event?.startDate
        if (event != null) {
            if (event.isRegistrationDisabled || startDate != null && startDate < Date().time)
                view?.let { Snackbar.make(it, "Keine Anmeldung mehr möglich.", Snackbar.LENGTH_LONG).show() }
            else
                UserDataModalBottomSheet().show(childFragmentManager, USER_DATA_BOTTOM_SHEET)
        }
    }

    override fun onUserDataSubmit() {
        (childFragmentManager.findFragmentByTag(USER_DATA_BOTTOM_SHEET) as UserDataModalBottomSheet).dismiss()
        val event = event
        if (activity != null && event != null && event.contact !== null) {
            val fragmentActivity = activity as FragmentActivity
            val subject = getString(R.string.registration_for, event.title)
            val body = generateMailText(event, UserData(fragmentActivity, PreferenceManager.getDefaultSharedPreferences(activity)))
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:" + event.contact.mail + "?subject=" + subject + "&body=" + body)
                putExtra(Intent.EXTRA_EMAIL, listOf(event.contact.mail).toTypedArray())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.registration_for, event.title))
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.send_mail)))

            Analytics.logEvent(fragmentActivity, "Register")
        }

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
