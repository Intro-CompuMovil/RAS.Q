package com.example.rasq.entities

import java.time.LocalDate
import java.time.LocalTime

class EventRequest(val requestorIndex: Int, val title: String, val date: LocalDate, val time: LocalTime, val address: String, val description: String , val bids: ArrayList<Bid> = ArrayList()) : java.io.Serializable
{
}