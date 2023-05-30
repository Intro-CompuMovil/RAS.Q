package com.example.rasq.controllers

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.rasq.R


class EventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_event, container, false)

        val eventList : ListView = view.findViewById(R.id.events_list)
        val eventStr = arrayOf<String>() // Reemplaza esto con la lógica apropiada para obtener los títulos de los eventos
        val arrayAdapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, eventStr)
        eventList.adapter = arrayAdapter
        createOnSelectListener(eventList)
        return view
    }

    private fun createOnSelectListener(pEventRequestList : ListView)
    {
        pEventRequestList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val intentOwnEventRequest = Intent(activity, ViewEventActivity::class.java)
                val sendBundle = Bundle()
                sendBundle.putInt("selectedIndex", position)
                intentOwnEventRequest.putExtra("sendBundle", sendBundle)
                startActivity(intentOwnEventRequest)
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment EventFragment.
         */
        @JvmStatic
        fun newInstance() =
            EventFragment()
    }
}
