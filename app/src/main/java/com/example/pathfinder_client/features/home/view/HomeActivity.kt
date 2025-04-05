package com.example.pathfinder_client.features.home.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pathfinder_client.R
import com.example.pathfinder_client.features.login.view.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var teamsRecyclerView: RecyclerView
    private lateinit var teamsCountTextView: TextView
    private lateinit var userNameTextView: TextView

    // Lista temporal para simular equipos
    private val teamsList = mutableListOf<TeamModel>()
    // Aquí deberías usar un adaptador real
    private lateinit var teamsAdapter: TeamsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicializar vistas
        drawerLayout = findViewById(R.id.drawerLayout)
        teamsRecyclerView = findViewById(R.id.teamsRecyclerView)
        teamsCountTextView = findViewById(R.id.teamsCountTextView)
        userNameTextView = findViewById(R.id.userNameTextView)

        // Configurar nombre de usuario desde intent
        val username = intent.getStringExtra("username") ?: "Usuario"
        userNameTextView.text = "Hola, $username"

        // Configurar recyclerView
        teamsRecyclerView.layoutManager = LinearLayoutManager(this)
        teamsAdapter = TeamsAdapter(teamsList, this::onAddHelmetClicked)
        teamsRecyclerView.adapter = teamsAdapter

        // Botón para abrir sidebar
        findViewById<ImageButton>(R.id.btnOpenSidebar).setOnClickListener {
            drawerLayout.open()
        }

        // Botón para cerrar sidebar
        findViewById<ImageButton>(R.id.btnCloseSidebar).setOnClickListener {
            drawerLayout.close()
        }

        // Botón para cerrar sesión
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            // Limpiar sesión si es necesario
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Botón para agregar equipo
        findViewById<FloatingActionButton>(R.id.btnAddTeam).setOnClickListener {
            showAddTeamDialog()
        }

        // Actualizar contador de equipos
        updateTeamsCount()
    }

    private fun showAddTeamDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_team, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // No necesitamos establecer título ya que está en el layout

        val teamNameEditText = dialogView.findViewById<TextInputEditText>(R.id.teamNameEditText)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelAddTeam)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmAddTeam)

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val teamName = teamNameEditText.text.toString().trim()
            if (teamName.isNotEmpty()) {
                // Agregar el equipo (simulado para esta implementación)
                addTeam(teamName)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor ingrese un nombre para el equipo", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }

    private fun addTeam(teamName: String) {
        // En un caso real, esto debería enviar una solicitud al servidor
        // y actualizar la lista cuando se reciba una respuesta exitosa
        val newTeam = TeamModel(teamsList.size + 1, teamName, emptyList())
        teamsList.add(newTeam)
        teamsAdapter.notifyItemInserted(teamsList.size - 1)
        updateTeamsCount()
        Toast.makeText(this, "Equipo '$teamName' agregado", Toast.LENGTH_SHORT).show()
    }

    private fun updateTeamsCount() {
        teamsCountTextView.text = "Equipos: ${teamsList.size}"
    }

    private fun onAddHelmetClicked(team: TeamModel) {
        showAddHelmetDialog(team)
    }

    private fun showAddHelmetDialog(team: TeamModel) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_helmet, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val helmetIdEditText = dialogView.findViewById<TextInputEditText>(R.id.helmetIdEditText)
        val selectedTeamNameTextView = dialogView.findViewById<TextView>(R.id.selectedTeamNameTextView)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelAddHelmet)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmAddHelmet)

        // Establecer el nombre del equipo seleccionado
        selectedTeamNameTextView.text = team.name

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val helmetId = helmetIdEditText.text.toString().trim()
            if (helmetId.isNotEmpty()) {
                // Lógica para vincular el casco (simulada aquí)
                addHelmetToTeam(team, helmetId)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor ingrese el ID del casco", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }

    private fun addHelmetToTeam(team: TeamModel, helmetId: String) {
        // En un caso real, esto enviaría una solicitud al servidor
        // y actualizaría la lista cuando se reciba una respuesta exitosa
        val teamIndex = teamsList.indexOf(team)
        if (teamIndex != -1) {
            val helmet = HelmetModel(helmetId, "Conectado")
            val updatedTeam = team.copy(helmets = team.helmets + helmet)
            teamsList[teamIndex] = updatedTeam
            teamsAdapter.notifyItemChanged(teamIndex)
            Toast.makeText(this, "Casco vinculado al equipo ${team.name}", Toast.LENGTH_SHORT).show()
        }
    }
}

// Modelos de datos para esta implementación
data class TeamModel(
    val id: Int,
    val name: String,
    val helmets: List<HelmetModel>
)

data class HelmetModel(
    val id: String,
    val status: String
)