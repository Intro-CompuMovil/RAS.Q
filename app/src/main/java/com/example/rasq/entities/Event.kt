package com.example.rasq.entities

import KeyMarker
import Payment
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.LocalDate
import java.time.LocalTime



class Event(
    var id: String = "",
    var requestorIndex: Int = 0,
    var title: String = "",
    var date: String = "",
    var time: String = "",
    var address: String = "",
    var description: String = "",
    var selectedBid: Bid = Bid(),
    var payment: Payment = Payment(),
    var marker: KeyMarker?=null
) : java.io.Serializable {

}
