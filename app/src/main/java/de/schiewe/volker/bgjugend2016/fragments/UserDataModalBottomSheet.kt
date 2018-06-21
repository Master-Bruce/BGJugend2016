package de.schiewe.volker.bgjugend2016.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.validateDateString
import kotlinx.android.synthetic.main.user_data_modal_bottom_sheet.*


const val USER_DATA_BOTTOM_SHEET: String = "USER_DATA_BOTTOM_SHEET"

class UserDataModalBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    private var mUserDataSubmitListener: UserDataSubmitListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_data_modal_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity)
        user_name.setText(sharedPrefs.getString(getString(R.string.name_key), ""), TextView.BufferType.EDITABLE)
        user_street.setText(sharedPrefs.getString(getString(R.string.street_key), ""), TextView.BufferType.EDITABLE)
        user_place.setText(sharedPrefs.getString(getString(R.string.place_key), ""), TextView.BufferType.EDITABLE)
        user_birthday.setText(sharedPrefs.getString(getString(R.string.birthday_key), ""), TextView.BufferType.EDITABLE)
        user_telephone.setText(sharedPrefs.getString(getString(R.string.telephone_key), ""), TextView.BufferType.EDITABLE)
        user_data_register.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var formIsValid = true

        if (!validateName(user_name))
            formIsValid = false
        if (!validateStreet(user_street))
            formIsValid = false
        if (!validatePlace(user_place))
            formIsValid = false
        if (!validateBirthday(user_birthday))
            formIsValid = false
        if (!validateTelephone(user_telephone))
            formIsValid = false

        if (!formIsValid)
            return

        saveAndSubmitData()
    }

    private fun validateName(name: EditText): Boolean {
        return if (name.text.toString() == "") {
            name.error = getString(R.string.input_name)
            false
        } else
            true
    }

    private fun validateStreet(street: EditText): Boolean {
        return if (street.text.toString() == "") {
            street.error = getString(R.string.input_street)
            false
        } else
            true
    }

    private fun validatePlace(place: EditText): Boolean {
        return if (place.text.toString() == "") {
            place.error = getString(R.string.input_plz_and_place)
            false
        } else {
            val regex = """^\d+\s\w+$""".toRegex()
            return if (regex matches place.text.toString())
                true
            else {
                place.error = getString(R.string.check_input)
                false
            }
        }
    }

    private fun validateBirthday(birthday: EditText): Boolean {
        return if (birthday.text.toString() == "")
            true
        else {
            return if (!validateDateString(birthday.text.toString())) {
                birthday.error = getString(R.string.wrong_date_format)
                false
            } else true
        }
    }

    private fun validateTelephone(telephone: EditText): Boolean {
        return if (telephone.text.toString() == "")
            true
        else {
            val regex = """^[\d/\s+-]+$""".toRegex()
            return if (regex matches telephone.text.toString())
                true
            else {
                telephone.error = getString(R.string.use_correct_phonenumber)
                false
            }
        }
    }

    private fun saveAndSubmitData() {
        val name = user_name.text.toString()
        val street = user_street.text.toString()
        val place = user_place.text.toString()
        val birthday = user_birthday.text.toString()
        val telephone = user_telephone.text.toString()

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity)
        with(sharedPrefs.edit()) {
            putString(getString(R.string.name_key), name)
            putString(getString(R.string.street_key), street)
            putString(getString(R.string.place_key), place)
            putString(getString(R.string.birthday_key), birthday)
            putString(getString(R.string.telephone_key), telephone)
            apply()
        }

        mUserDataSubmitListener?.onUserDataSubmit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mUserDataSubmitListener = parent as UserDataSubmitListener
        }
    }

    override fun onDetach() {
        mUserDataSubmitListener = null
        super.onDetach()
    }

    interface UserDataSubmitListener {
        fun onUserDataSubmit()
    }
}
