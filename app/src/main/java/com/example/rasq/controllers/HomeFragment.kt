package com.example.rasq.controllers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import com.example.rasq.R
import com.example.rasq.entities.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var uri: Uri
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private val storage = Firebase.storage
    private lateinit var eventRequestsStr: ArrayList<String>
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var eventRequestsSnapshot: DataSnapshot


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val btnProfile: ImageButton = view.findViewById(R.id.btn_profile)
        val eventRequestList: ListView = view.findViewById(R.id.events_list)
        val btnNewEventRequest: ImageButton = view.findViewById(R.id.btn_new_event_request)

        eventRequestsStr = ArrayList()
        arrayAdapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, eventRequestsStr)
        eventRequestList.adapter = arrayAdapter
        createOnSelectListener(eventRequestList)

        btnProfile.setOnClickListener {
            val intentProfile = Intent(activity, ProfileActivity::class.java)
            startActivity(intentProfile)
        }

        btnNewEventRequest.setOnClickListener {
            val intentNewEventRequest = Intent(activity, NewEventRequestActivity::class.java)
            startActivity(intentNewEventRequest)
        }

        fetchEventRequests()

        return view
    }

    private fun fetchEventRequests() {
        val eventRequestsRef = database.reference.child(PATH_EVENTS)

        eventRequestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                eventRequestsSnapshot = dataSnapshot // Store the dataSnapshot

                eventRequestsStr.clear()

                for (eventSnapshot in dataSnapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    event?.let {
                        eventRequestsStr.add(event.title)
                    }
                }

                arrayAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }


    private fun createOnSelectListener(pEventRequestList: ListView) {
        pEventRequestList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedEventId = eventRequestsSnapshot.children.elementAt(position).key
                val intentOwnEventRequest = Intent(activity, ViewEventRequestActivity::class.java)
                intentOwnEventRequest.putExtra("selectedEventId", selectedEventId)
                startActivity(intentOwnEventRequest)
            }
    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
