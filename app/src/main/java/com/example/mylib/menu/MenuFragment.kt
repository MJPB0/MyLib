package com.example.mylib.menu

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mylib.Communicator
import com.example.mylib.R
import com.example.mylib.adapters.EntriesAdapter
import com.example.mylib.data.Database
import com.example.mylib.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MenuFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()

    lateinit var entriesList: ArrayList<Entry>
    lateinit var entriesListAdapter: EntriesAdapter

    lateinit var searchedText: EditText

    private var communicator: Communicator?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        entriesList = arrayListOf()

        getEntries("")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        communicator = ViewModelProvider(requireActivity())[Communicator::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            attemptLoggingOut()
        }

        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchedText = view.findViewById<EditText>(R.id.menu_search_text)

        applyRecyclerAdapter(entriesList)

        view.findViewById<ImageView>(R.id.menu_logout_image).apply {
            setOnClickListener {
                attemptLoggingOut()
            }
        }

        view.findViewById<TextView>(R.id.menu_logout_text).apply {
            setOnClickListener {
                attemptLoggingOut()
            }
        }

        view.findViewById<ImageView>(R.id.menu_add_image).apply {
            setOnClickListener {
                view.findNavController().navigate(R.id.action_menuFragment_to_addEntryFragment)
            }
        }

        view.findViewById<TextView>(R.id.menu_add_text).apply {
            setOnClickListener {
                view.findNavController().navigate(R.id.action_menuFragment_to_addEntryFragment)
            }
        }

        view.findViewById<ImageView>(R.id.menu_search_image).apply {
            setOnClickListener {
                getEntries(searchedText.text.toString().trim())
            }
        }
    }

    private fun applyRecyclerAdapter(list: ArrayList<Entry>) {
        entriesListAdapter = EntriesAdapter(list, view!!.context)

        val recycler = view!!.findViewById<RecyclerView>(R.id.menu_entries_list)
        val layoutManager= LinearLayoutManager(view!!.context)
        recycler.let {
            it!!.adapter=entriesListAdapter
            it.layoutManager=layoutManager
        }

    }

    private fun getEntries(filter: String) {
        Database.ref.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists())  return

                entriesList.clear()
                for (entrySnapshot in dataSnapshot.children) {
                    val entry = entrySnapshot.getValue(Entry::class.java)

                    if (entry!!.name.contains(filter))
                        entriesList.add(entry)
                }

                applyRecyclerAdapter(entriesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
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
                view?.findNavController()?.navigate(R.id.action_menuFragment_to_loginFragment)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

}