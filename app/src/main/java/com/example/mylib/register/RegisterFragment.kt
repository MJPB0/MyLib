package com.example.mylib.register

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
import com.google.firebase.auth.FirebaseUser

class RegisterFragment : Fragment() {
    private var communicator: Communicator?= null

    private lateinit var emailText: TextView
    private lateinit var passwordText: TextView
    private lateinit var confirmPasswordText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        communicator = ViewModelProvider(requireActivity())[Communicator::class.java]

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.emailText = view.findViewById<TextView>(R.id.register_email_content)
        this.passwordText = view.findViewById<TextView>(R.id.register_password_content)
        this.confirmPasswordText = view.findViewById<TextView>(R.id.register_confirm_password_content)

        emailText.text = ""
        passwordText.text = ""
        confirmPasswordText.text = ""

        if (communicator?.UserEmail?.value != ""){
            emailText.text = communicator!!.UserEmail.value
            communicator?.setUserEmail("")
        }

        view.findViewById<Button>(R.id.register_button).apply {
            setOnClickListener {
                register(view)
            }
        }
        view.findViewById<TextView>(R.id.register_login).apply {
            setOnClickListener {
                view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun register(view: View) {
        val providedEmail = emailText.text.toString().trim()
        val providedPassword = passwordText.text.toString().trim()
        val providedConfirmPassword = confirmPasswordText.text.toString().trim()

        if (providedEmail == ""){
            Toast.makeText(context, "Please enter an email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (providedPassword == ""){
            Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
            return
        }

        if (providedPassword != providedConfirmPassword){
            Toast.makeText(context, "Provided passwords should be the same", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(providedEmail, providedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    communicator?.setUserEmail(firebaseUser.email.toString())

                    Toast.makeText(
                        context,
                        "You successfully registered your account",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    Toast.makeText(
                        context,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

}