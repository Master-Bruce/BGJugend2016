package de.schiewe.volker.bgjugend2016.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.helper.GlideApp
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_event.*


class EventFragment : Fragment() {
    private var event: Event? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_event, container, false)
        val storageReference: FirebaseStorage = FirebaseStorage.getInstance()

        val sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        sharedViewModel.getSelected().observe(activity!!, Observer { item ->
            event = item
            storageReference.getReference("event_img/${event!!.imagePath}").downloadUrl.addOnSuccessListener { uri ->
                GlideApp.with(this@EventFragment)
                        .load(uri)
                        .placeholder(R.drawable.placeholder)
                        .into(event_image)
            }
        })
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (event != null){
            event_title.text = event!!.title
            header_text.text = event!!.header
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

    }


    companion object {
        @JvmStatic
        fun newInstance(): EventFragment = EventFragment()
    }

}
