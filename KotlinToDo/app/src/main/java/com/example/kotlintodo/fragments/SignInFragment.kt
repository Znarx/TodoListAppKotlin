package com.example.kotlintodo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.kotlintodo.R
import com.example.kotlintodo.databinding.FragmentSignInBinding
import com.example.kotlintodo.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navControl: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun registerEvents() {

        binding.signupText.setOnClickListener {
            navControl.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {


                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "LogIn Successfully", Toast.LENGTH_SHORT).show()
                            navControl.navigate(R.id.action_signInFragment_to_homeFragment2)
                        } else {
                            Toast.makeText(context, "LogIn Failed", Toast.LENGTH_SHORT).show()
                        }
                    })
            }else{
                Toast.makeText(context, "Enter Email and Password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

