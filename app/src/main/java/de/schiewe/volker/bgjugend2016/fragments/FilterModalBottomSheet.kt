package de.schiewe.volker.bgjugend2016.fragments

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.schiewe.volker.bgjugend2016.R

class FilterModalBottomSheet : BottomSheetDialogFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false)
    }
}