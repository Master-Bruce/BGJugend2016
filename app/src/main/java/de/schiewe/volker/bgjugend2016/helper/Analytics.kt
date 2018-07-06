package de.schiewe.volker.bgjugend2016.helper

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics


class Analytics {
    companion object {
        private var analytics: FirebaseAnalytics? = null
        private fun newInstance(context: Context): FirebaseAnalytics {
            if (analytics == null) {
                analytics = FirebaseAnalytics.getInstance(context)
            }
            return analytics!!
        }

        fun setScreen(activity: Activity, javaClassName: String) {
            newInstance(activity).setCurrentScreen(activity, javaClassName, javaClassName)
        }

        fun logEvent(activity: Activity, id: String) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
            newInstance(activity).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        }
    }
}