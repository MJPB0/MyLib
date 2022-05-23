package com.example.mylib.add_entry

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.mylib.Communicator
import com.example.mylib.R
import com.example.mylib.data.Database
import com.example.mylib.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class AddEntryFragment : Fragment() {

    private var communicator: Communicator?= null

    private lateinit var nameText: TextView
    private lateinit var typeText: TextView
    private lateinit var seenCheckBox: CheckBox

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        communicator = ViewModelProvider(requireActivity())[Communicator::class.java]

        return inflater.inflate(R.layout.fragment_add_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.nameText = view.findViewById<TextView>(R.id.add_entry_name_text)
        this.typeText = view.findViewById<TextView>(R.id.add_entry_type_text)
        this.seenCheckBox = view.findViewById<CheckBox>(R.id.add_entry_seen_checkbox)

        view.findViewById<ImageView>(R.id.add_entry_logout_image).apply {
            setOnClickListener {
                attemptLoggingOut()
            }
        }

        view.findViewById<TextView>(R.id.add_entry_logout_text).apply {
            setOnClickListener {
                attemptLoggingOut()
            }
        }

        view.findViewById<ImageView>(R.id.add_entry_display_entries_image).apply {
            setOnClickListener {
                view.findNavController().navigate(R.id.action_addEntryFragment_to_menuFragment)
            }
        }

        view.findViewById<TextView>(R.id.add_entry_display_entries_text).apply {
            setOnClickListener {
                view.findNavController().navigate(R.id.action_addEntryFragment_to_menuFragment)
            }
        }

        view.findViewById<ImageView>(R.id.add_entry_image).apply {
            setOnClickListener {
                attemptAddingNewEntry()
            }
        }

        view.findViewById<TextView>(R.id.add_entry_text).apply {
            setOnClickListener {
                attemptAddingNewEntry()
            }
        }
    }

    private fun attemptLoggingOut() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to logout?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                communicator?.setUserEmail("")
                communicator?.setUserUID("")
                FirebaseAuth.getInstance().signOut()

                Toast.makeText(context, "You successfully logged out", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.action_addEntryFragment_to_loginFragment)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun attemptAddingNewEntry() {
        val providedName = nameText.text.toString()
        val providedType = typeText.text.toString()
        val wasSeen = seenCheckBox.isChecked

        if (providedName == ""){
            Toast.makeText(context, "An entry must have a name", Toast.LENGTH_SHORT).show()
            return
        }

        if (providedType == ""){
            Toast.makeText(context, "An entry must have a type", Toast.LENGTH_SHORT).show()
            return
        }

        Database.ref.get()
            .addOnSuccessListener { task ->
                val isAlreadyInDatabase = task.child(providedName).exists()

                if (isAlreadyInDatabase) {
                    Toast.makeText(context, "An entry with that name already exists", Toast.LENGTH_SHORT).show()
                } else {
                    addNewEntry(providedName, providedType, wasSeen)
                }
            }
    }

    private fun addNewEntry(name: String, type: String, seen: Boolean) {
        val newEntry = Entry(name, type, seen)
        Database.ref.child(auth.currentUser!!.uid).child(name).setValue(newEntry)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Toast.makeText(context, "You successfully added a new entry", Toast.LENGTH_SHORT).show()
                    resetInputs()
                } else {
                    Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetInputs() {
        nameText.text = ""
        typeText.text = ""
        seenCheckBox.isChecked = false
    }

}