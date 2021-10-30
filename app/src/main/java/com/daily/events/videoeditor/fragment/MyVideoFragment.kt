package com.daily.events.videoeditor.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.daily.events.videoeditor.CreateVideoActivity
import com.daily.events.videoeditor.R
import com.daily.events.videoeditor.databinding.FragmentMyVideoBinding

class MyVideoFragment : Fragment() {

    lateinit var myVideoBinding: FragmentMyVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        myVideoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_video, container, false)

        myVideoBinding.imgCreate.setOnClickListener {
            val intent = Intent(requireActivity(), CreateVideoActivity::class.java)
            startActivity(intent)
        }

        return myVideoBinding.root
    }


    companion object
}