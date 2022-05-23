package com.example.mylib.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.mylib.Communicator
import com.example.mylib.R
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var communicator: Communicator?= null

    private lateinit var emailText: TextView
    private lateinit var passwordText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        communicator = ViewModelProvider(requireActivity())[Communicator::class.java]

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.emailText = view.findViewById<TextView>(R.id.login_email_content)
        this.passwordText = view.findViewById<TextView>(R.id.login_password_content)

        emailText.text = ""
        passwordText.text = ""

        if (communicator?.UserEmail?.value != ""){
            emailText.text = communicator?.UserEmail?.value
            communicator?.setUserEmail("")
        }

        view.findViewById<TextView>(R.id.login_create_account).apply {
            setOnClickListener {
                val providedEmail = emailText.text.toString()
                if (providedEmail != "")
                    communicator?.setUserEmail(providedEmail)

                view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
        view.findViewById<Button>(R.id.login_button).apply {
            setOnClickListener {
                login(view)
            }
        }
    }

    private fun login(view: View) {
        val providedEmail = emailText.text.toString().trim()
        val providedPassword = passwordText.text.toString().trim()

        if (providedEmail == ""){
            Toast.makeText(context, "Please enter an email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (providedPassword == ""){
            Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(providedEmail, providedPassword)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){
                    val instance = FirebaseAuth.getInstance()
                    communicator?.setUserUID(instance.currentUser!!.uid)
                    communicator?.setUserEmail(instance.currentUser!!.email.toString())

                    Toast.makeText(context, "You successfully logged in", Toast.LENGTH_SHORT).show()
                    view.findNavController().navigate(R.id.action_loginFragment_to_menuFragment)
                } else{
                    Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
}