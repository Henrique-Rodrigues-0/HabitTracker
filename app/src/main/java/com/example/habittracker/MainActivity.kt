package com.example.habittracker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val habitList = ArrayList<String>() // Lista de hábitos
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("HabitTrackerPrefs", Context.MODE_PRIVATE)

        // Carregar hábitos salvos
        loadHabits()

        // Configurar ListView e Adapter
        val listView: ListView = findViewById(R.id.listView)
        adapter = object : ArrayAdapter<String>(this, R.layout.list_item, R.id.item_text, habitList) {}
        listView.adapter = adapter

        // Configurar campo de entrada e botão
        val habitInput: EditText = findViewById(R.id.habitInput)
        val addButton: Button = findViewById(R.id.addButton)

        addButton.setOnClickListener {
            val newHabit = habitInput.text.toString().trim()
            if (newHabit.isNotEmpty()) {
                habitList.add(newHabit) // Adicionar hábito à lista
                adapter.notifyDataSetChanged() // Atualizar ListView
                saveHabits() // Salvar hábitos
                habitInput.text.clear() // Limpar campo de entrada
            } else {
                Toast.makeText(this, "Por favor, insira um hábito.", Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            // Editar hábito
            val selectedHabit = habitList[position]
            habitInput.setText(selectedHabit)
            habitList.removeAt(position)
            adapter.notifyDataSetChanged()
            saveHabits() // Salvar após editar
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            // Remover hábito
            habitList.removeAt(position)
            adapter.notifyDataSetChanged()
            saveHabits() // Salvar após remover
            Toast.makeText(this, "Hábito removido.", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun loadHabits() {
        val savedHabits = sharedPreferences.getStringSet("habits", emptySet()) ?: emptySet()

        // Atualizar a lista sem criar dependência mutável com o Set
        habitList.clear()
        habitList.addAll(savedHabits.toList()) // Converte o Set em uma Lista
    }

    private fun saveHabits() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("habits", habitList.toSet()) // Salvando como um Set de String
        editor.apply()
    }
}
