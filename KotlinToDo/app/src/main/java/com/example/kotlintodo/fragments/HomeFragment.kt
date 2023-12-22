package com.example.kotlintodo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlintodo.R
import com.example.kotlintodo.databinding.FragmentHomeBinding
import com.example.kotlintodo.utils.ToDoAdapter
import com.example.kotlintodo.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment(), AddTodoFragment.DialogNextBtnClickListener,
    ToDoAdapter.ToDoAdapterClicksInterface {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private var popFragment : AddTodoFragment? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var mList: MutableList<ToDoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFireBase()
        registerEvents()
    }

    private fun registerEvents(){



        binding.addButton.setOnClickListener{
            if (popFragment != null)
                childFragmentManager.beginTransaction().remove(popFragment!!).commit()
            popFragment = AddTodoFragment()
            popFragment!!.setListener(this)
            popFragment!!.show(
                childFragmentManager,
                AddTodoFragment.TAG
            )

        }


        binding.logoutBtn.setOnClickListener {
        if(firebaseAuth.currentUser != null){
        firebaseAuth.signOut()
            navController.navigate(R.id.action_homeFragment2_to_signInFragment)


        }
        }
    }


    private fun init(view: View){
        navController = Navigation.findNavController(view)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks").child(firebaseAuth.currentUser?.uid.toString())

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.mainRecyclerView.adapter = adapter
    }

    private fun getDataFromFireBase(){
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               mList.clear()
                for (taskSnapShot in snapshot.children){
                    val todoTask = taskSnapShot.key?.let{
                        ToDoData(it , taskSnapShot.value.toString())
                    }

                    if(todoTask != null){
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText){

        val taskReference = databaseRef.push() // Creates a new unique reference
        taskReference.setValue(todo).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Todo saved successfully!!", Toast.LENGTH_SHORT).show()
                todoEt.text = null
            } else {
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
        val map = HashMap<String , Any>()
        map[toDoData.taskId] = toDoData.task
        databaseRef.updateChildren(map).addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                todoEt.text = null
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()

            }
            todoEt.text = null

            popFragment!!.dismiss()
        }
    }

    override fun onDeleteTaskBtnClicked(toDoData: ToDoData) {
        databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {
        if(popFragment != null)
            childFragmentManager.beginTransaction().remove(popFragment!!).commit()

        popFragment = AddTodoFragment.newInstance(toDoData.taskId, toDoData.task)
        popFragment!!.setListener(this)
        popFragment!!.show(childFragmentManager, AddTodoFragment.TAG)
    }

}