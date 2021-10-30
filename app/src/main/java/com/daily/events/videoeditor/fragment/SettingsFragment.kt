package com.daily.events.videoeditor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.daily.events.videoeditor.R
import com.daily.events.videoeditor.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    lateinit var settingsBinding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return settingsBinding.root
    }

}