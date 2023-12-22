package com.example.kotlintodo.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.kotlintodo.R
import com.google.firebase.auth.FirebaseAuth


class SplashFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun init(view: View) {
        firebaseAuth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        val isLogin: Boolean = firebaseAuth.currentUser != null

        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({

            if (isLogin)
                navController.navigate(R.id.action_splashFragment_to_homeFragment2)
            else
                navController.navigate(R.id.action_splashFragment_to_signInFragment)

        }, 2000)
    }
}