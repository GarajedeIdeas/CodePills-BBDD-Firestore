package com.jrinconada.gcpfirestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.initialize

class MainActivity : AppCompatActivity() {

    // Vistas
    private lateinit var newTaskEditText: EditText
    private lateinit var addTasksButton: Button
    private lateinit var tasksListView: ListView

    // Datos
    private lateinit var tasks: MutableList<String>
    private lateinit var tasksAdapter: ArrayAdapter<String>

    // Firestore
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vistas
        newTaskEditText = findViewById(R.id.newTaskEditText)
        addTasksButton = findViewById(R.id.addButton)
        tasksListView = findViewById(R.id.tasksListView)

        // Datos
        tasks = ArrayList()
        tasksAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        tasksListView.adapter = tasksAdapter

        // Eventos
        addTasksButton.setOnClickListener { addTask() }
        tasksListView.setOnItemClickListener { _, _, i, _ ->  removeTask(i) }

        // Firestore
        db = Firebase.firestore
        loadTasks()
    }

    private fun loadTasks() {
        addTasksButton.isEnabled = false
        db.collection("tasks")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val task = document.getString("description")
                    task?.let { tasks.add(it) }
                }
                tasksAdapter.notifyDataSetChanged()
                addTasksButton.isEnabled = true
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                addTasksButton.isEnabled = true
            }
    }

    private fun addTask() {
        val task = newTaskEditText.text.toString()
        if (task.isNotBlank()) {
            addTasksButton.isEnabled = false
            val pair = hashMapOf("description" to task)
            db.collection("tasks")
                .add(pair)
                .addOnSuccessListener {
                    tasks.add(task)
                    tasksAdapter.notifyDataSetChanged()
                    newTaskEditText.text.clear()
                    addTasksButton.isEnabled = true
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                    addTasksButton.isEnabled = true
                }
        }
    }

    private fun removeTask(i: Int) {
        addTasksButton.isEnabled = false
        db.collection("tasks")
            .document("WCHbMjndzi7tMHPmftfr")
            .delete()
            .addOnSuccessListener {
                tasks.removeAt(i)
                tasksAdapter.notifyDataSetChanged()
                addTasksButton.isEnabled = true
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                addTasksButton.isEnabled = true
            }

    }
}