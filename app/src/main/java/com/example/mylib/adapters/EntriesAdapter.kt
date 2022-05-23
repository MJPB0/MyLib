package com.example.mylib.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.mylib.R
import com.example.mylib.data.Database
import com.example.mylib.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class EntriesAdapter (
    private val entryList: ArrayList<Entry>,
    private val context: Context
) : RecyclerView.Adapter<EntriesAdapter.EntriesListHolder>() {
    var positionEditing: Int = -1

    inner class EntriesListHolder(private val view: View): RecyclerView.ViewHolder(view)
    {
        val entryName = view.findViewById<EditText>(R.id.entry_name_text)
        val entryType = view.findViewById<EditText>(R.id.entry_type_text)
        val entrySeen = view.findViewById<CheckBox>(R.id.entry_check_box)
        val deleteEntryButton = view.findViewById<ImageView>(R.id.entry_delete_image)
        val editEntryButton = view.findViewById<ImageView>(R.id.entry_edit_image)
        var isEditing: Boolean = false
        val auth = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntriesListHolder {
        val view= LayoutInflater.from(parent.context).
        inflate(R.layout.fragment_entries_one_row,parent,false)
        return EntriesListHolder(view)
    }

    override fun onBindViewHolder(holder: EntriesListHolder, position: Int) {

        holder.entryName.isEnabled = false
        holder.entryType.isEnabled = false
        holder.entrySeen.isClickable = false

        holder.entryName.setText(entryList[position].name)
        holder.entryType.setText(entryList[position].type)
        holder.entrySeen.isChecked = entryList[position].seen

        holder.deleteEntryButton.setOnClickListener {
            attemptDeletingEntry(holder.entryName.text.toString(), holder.auth.currentUser!!.uid)
        }

        holder.editEntryButton.setOnClickListener {
            if (positionEditing == position || positionEditing == -1) {
                positionEditing = if (!holder.isEditing) position else -1
                toggleEditMode(holder, !holder.isEditing, position)
            }
        }
    }

    override fun getItemCount()=entryList.size

    private fun toggleEditMode(holder: EntriesListHolder, editModeOn: Boolean, position: Int) {
        holder.entryName.isEnabled = editModeOn
        holder.entryType.isEnabled = editModeOn
        holder.entrySeen.isClickable = editModeOn

        holder.isEditing = editModeOn

        val providedName = holder.entryName.text.toString()
        val providedType = holder.entryType.text.toString()
        val providedSeen = holder.entrySeen.isChecked

        if (editModeOn) {
            holder.editEntryButton.setImageResource(R.drawable.ic_baseline_save_24)
        } else {
            holder.editEntryButton.setImageResource(R.drawable.ic_baseline_edit_24)
            attemptUpdatingEntry(providedName, providedType, providedSeen, holder.auth.currentUser!!.uid)
        }

        val oldName = entryList[position].name
        if (providedName != oldName) {
            attemptDeletingEntry(oldName, holder.auth.currentUser!!.uid)
        }
    }

    private fun attemptUpdatingEntry(name: String, type: String, seen: Boolean, uid: String) {

        val newEntry = mapOf(
            "name" to name,
            "type" to type,
            "seen" to seen,
        )

        Database.ref.child(uid).child(name).updateChildren(newEntry)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Toast.makeText(context, "You successfully updated an entry", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun attemptDeletingEntry(name: String, uid: String) {
        Database.ref.child(uid).child(name).removeValue()
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Toast.makeText(context, "You successfully deleted an entry", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

}