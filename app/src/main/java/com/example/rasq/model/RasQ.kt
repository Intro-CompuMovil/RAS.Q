package com.example.rasq.model
import User
import android.content.Context
import com.example.rasq.entities.*
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.LocalDate
import java.time.LocalTime

object RasQ : java.io.Serializable
{
    val users = ArrayList<User>()
    val events = ArrayList<Event>()
    val eventRequests = ArrayList<EventRequest>()
    val invites = ArrayList<Invite>()
    var user: User? = null
    var userIndex: Int = -1

    fun load(pContext: Context)
    {
        var file = File(pContext.filesDir, "users.bin")
        if (file.exists())
        {
            //file.delete() // USE TO DELETE FILE
            users.clear()
            ObjectInputStream(file.inputStream()).use { users.addAll(it.readObject() as ArrayList<User>) }
        }

        file = File(pContext.filesDir, "events.bin")
        if (file.exists())
        {
            //file.delete() // USE TO DELETE FILE
            events.clear()
            ObjectInputStream(file.inputStream()).use { events.addAll(it.readObject() as ArrayList<Event>) }
        }

        file = File(pContext.filesDir, "eventRequests.bin")
        if (file.exists())
        {
            //file.delete() // USE TO DELETE FILE
            eventRequests.clear()
            ObjectInputStream(file.inputStream()).use { eventRequests.addAll(it.readObject() as ArrayList<EventRequest>) }
        }

        file = File(pContext.filesDir, "invites.bin")
        if (file.exists())
        {
            //file.delete() // USE TO DELETE FILE
            invites.clear()
            ObjectInputStream(file.inputStream()).use { invites.addAll(it.readObject() as ArrayList<Invite>) }
        }
    }

    fun save(pContext: Context)
    {
        var file = File(pContext.filesDir, "users.bin")
        if (!file.exists())
        {
            file.createNewFile()
        }
        ObjectOutputStream(file.outputStream()).use { it.writeObject(users) }

        file = File(pContext.filesDir, "events.bin")
        if (!file.exists())
        {
            file.createNewFile()
        }
        ObjectOutputStream(file.outputStream()).use { it.writeObject(events) }

        file = File(pContext.filesDir, "eventRequests.bin")
        if (!file.exists())
        {
            file.createNewFile()
        }
        ObjectOutputStream(file.outputStream()).use { it.writeObject(eventRequests) }

        file = File(pContext.filesDir, "invites.bin")
        if (!file.exists())
        {
            file.createNewFile()
        }
        ObjectOutputStream(file.outputStream()).use { it.writeObject(invites) }
    }

    /*fun createUser(pContext: Context, pName: String, pEmail: String, pPassword: String, pPhone: String): Boolean
    {
        if (users.any { it.email == pEmail })
        {
            return false
        }
        else
        {
            val newUser = User(pName, pEmail, pPassword, pPhone)
            users.add(newUser)
            this.save(pContext)
            return true
        }
    }*/

    fun createEventRequest(pContext: Context, title: String, date: LocalDate, time: LocalTime, address: String, description: String): Boolean {
        val existingRequest = eventRequests.any { it.requestorIndex == userIndex && it.date == date }
        if (existingRequest) {
            // An event request with the same requestor and date already exists
            return false
        } else {
            val newRequest = EventRequest(userIndex, title, date, time, address, description)
            eventRequests.add(newRequest)
            this.save(pContext)
            return true
        }
    }

    fun logIn(pEmail: String, pPassword: String): Boolean
    {
        val foundUser = users.find { it.email == pEmail && it.password == pPassword }
        if(foundUser != null)
        {
            user = users.find { it.email == pEmail && it.password == pPassword }
            userIndex = users.indexOfFirst { it.email == pEmail && it.password == pPassword }
            return true
        }
        else
        {
            return false
        }
    }


}