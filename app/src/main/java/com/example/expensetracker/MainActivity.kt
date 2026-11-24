package com.example.expensetracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val saved = mutableListOf<String>()
    private val shown = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTotal = findViewById(R.id.tvTotal)
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val listView = findViewById<ListView>(R.id.listView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, shown)
        listView.adapter = adapter

        val data = getSharedPreferences("expenses", MODE_PRIVATE).getString("data", "") ?: ""
        if (data.isNotEmpty()) saved.addAll(data.split("\n"))
        refresh()

        btnAdd.setOnClickListener {
            val title = etTitle.text.toString().trim().replace("|", " ").replace("\n", " ")
            val amount = etAmount.text.toString().toDoubleOrNull()
            if (title.isEmpty() || amount == null || amount <= 0) {
                Toast.makeText(this, "Enter a valid title and amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saved.add(0, "$title|$amount")
            etTitle.text.clear()
            etAmount.text.clear()
            refresh()
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            AlertDialog.Builder(this)
                .setTitle("Delete this expense?")
                .setPositiveButton("Delete") { _, _ ->
                    saved.removeAt(position)
                    refresh()
                }
                .setNegativeButton("Cancel", null)
                .show()
            true
        }
    }

    private fun refresh() {
        shown.clear()
        var total = 0.0
        for (line in saved) {
            val parts = line.split("|")
            val amt = parts[1].toDouble()
            total += amt
            shown.add("%s  -  ₹%.2f".format(parts[0], amt))
        }
        adapter.notifyDataSetChanged()
        tvTotal.text = "Total: ₹%.2f".format(total)
        getSharedPreferences("expenses", MODE_PRIVATE)
            .edit().putString("data", saved.joinToString("\n")).apply()
    }
}