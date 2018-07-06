package de.schiewe.volker.bgjugend2016.fragments

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.helper.Analytics
import de.schiewe.volker.bgjugend2016.interfaces.UserDataSubmitListener
import de.schiewe.volker.bgjugend2016.models.UserData
import kotlinx.android.synthetic.main.user_data_modal_bottom_sheet.*


const val USER_DATA_BOTTOM_SHEET: String = "USER_DATA_BOTTOM_SHEET"

class UserDataModalBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    private var mUserDataSubmitListener: UserDataSubmitListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_data_modal_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (activity != null) {
            val user = UserData(activity!!, PreferenceManager.getDefaultSharedPreferences(activity))
            user_name.setText(user.name, TextView.BufferType.EDITABLE)
            user_street.setText(user.street, TextView.BufferType.EDITABLE)
            user_place.setText(user.place, TextView.BufferType.EDITABLE)
            user_birthday.setText(user.birthday, TextView.BufferType.EDITABLE)
            user_telephone.setText(user.telephone, TextView.BufferType.EDITABLE)
            user_data_register.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        var formIsValid = true
        if (!UserData.validateName(user_name.text.toString())) {
            user_name.error = getString(R.string.input_name)
            formIsValid = false
        }
        if (!UserData.validateStreet(user_street.text.toString())) {
            user_street.error = getString(R.string.input_street)
            formIsValid = false
        }
        if (!UserData.validatePlace(user_place.text.toString())) {
            user_place.error = getString(R.string.input_plz_and_place)
            formIsValid = false
        }
        if (!UserData.validateBirthday(user_birthday.text.toString())) {
            user_birthday.error = getString(R.string.wrong_date_format)
            formIsValid = false
        }
        if (!UserData.validateTelephone(user_telephone.text.toString())) {
            user_telephone.error = getString(R.string.use_correct_phonenumber)
            formIsValid = false
        }

        if (!formIsValid)
            return

        saveAndSubmitData()
    }

    private fun saveAndSubmitData() {
        if (activity != null) {
            val user = UserData(activity!!, PreferenceManager.getDefaultSharedPreferences(activity))
            user.name = user_name.text.toString()
            user.street = user_street.text.toString()
            user.place = user_place.text.toString()
            user.birthday = user_birthday.text.toString()
            user.telephone = user_telephone.text.toString()
            mUserDataSubmitListener?.onUserDataSubmit()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mUserDataSubmitListener = parent as UserDataSubmitListener
        }
        if (activity != null) {
            Analytics.setScreen(activity!!, javaClass.simpleName)
        }
    }

    override fun onDetach() {
        mUserDataSubmitListener = null
        super.onDetach()
    }
}
