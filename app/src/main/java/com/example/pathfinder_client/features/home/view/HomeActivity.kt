// HomeActivity.kt (versión limpia sin status)

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
import androidx.lifecycle.ViewModelProvider
import com.example.pathfinder_client.data.repositories.devices.DeviceRepository
import com.example.pathfinder_client.features.home.viewmodel.HomeViewModel
import com.example.pathfinder_client.features.home.viewmodel.HomeViewModelFactory
import com.example.pathfinder_client.data.local.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var teamsRecyclerView: RecyclerView
    private lateinit var teamsCountTextView: TextView
    private lateinit var userNameTextView: TextView

    private lateinit var viewModel: HomeViewModel
    private val teamsList = mutableListOf<TeamModel>()
    private lateinit var teamsAdapter: TeamsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        teamsRecyclerView = findViewById(R.id.teamsRecyclerView)
        teamsCountTextView = findViewById(R.id.teamsCountTextView)
        userNameTextView = findViewById(R.id.userNameTextView)

        val username = intent.getStringExtra("username") ?: "Usuario"
        userNameTextView.text = "Hola, $username"

        teamsRecyclerView.layoutManager = LinearLayoutManager(this)
        teamsAdapter = TeamsAdapter(teamsList, this::onAddHelmetClicked)
        teamsRecyclerView.adapter = teamsAdapter

        findViewById<ImageButton>(R.id.btnOpenSidebar).setOnClickListener {
            drawerLayout.open()
        }

        findViewById<ImageButton>(R.id.btnCloseSidebar).setOnClickListener {
            drawerLayout.close()
        }

        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<FloatingActionButton>(R.id.btnAddTeam).setOnClickListener {
            showAddTeamDialog()
        }

        updateTeamsCount()

        val deviceRepository = DeviceRepository()
        val viewModelFactory = HomeViewModelFactory(deviceRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        loadTeamsFromApi()
    }

    private fun loadTeamsFromApi() {
        showLoading(true)

        val preferencesManager = PreferencesManager(this)
        val userId = preferencesManager.getUserId()
        val token = preferencesManager.getToken()

        if (userId?.isNotEmpty() == true && token?.isNotEmpty() == true) {
            lifecycleScope.launch {
                try {
                    val response = viewModel.getEquipos(userId, token)
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse?.success == true) {
                            val teams = apiResponse.data.map { teamResponse ->
                                TeamModel(
                                    id = teamResponse.id.hashCode(),
                                    name = teamResponse.name,
                                    helmets = teamResponse.helmets.map { helmet ->
                                        HelmetModel(
                                            id = helmet.device_id,
                                            name = helmet.name,
                                        )
                                    }
                                )
                            }

                            withContext(Dispatchers.Main) {
                                teamsList.clear()
                                teamsList.addAll(teams)
                                teamsAdapter.notifyDataSetChanged()
                                updateTeamsCount()
                                showLoading(false)
                            }
                        } else {
                            showError("Error: ${apiResponse?.message ?: "Desconocido"}")
                        }
                    } else {
                        showError("Error en la petición: ${response.code()}")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showError("Error: ${e.message}")
                        showLoading(false)
                    }
                }
            }
        } else {
            showError("No hay sesión activa")
            redirectToLogin()
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        // Mostrar u ocultar indicador de carga
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        showLoading(false)
    }

    private fun showAddTeamDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_team, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val teamNameEditText = dialogView.findViewById<TextInputEditText>(R.id.teamNameEditText)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelAddTeam)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmAddTeam)

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val teamName = teamNameEditText.text.toString().trim()
            if (teamName.isNotEmpty()) {
                addTeam(teamName)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor ingrese un nombre para el equipo", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }

    private fun addTeam(teamName: String) {
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

        selectedTeamNameTextView.text = team.name

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val helmetId = helmetIdEditText.text.toString().trim()
            if (helmetId.isNotEmpty()) {
                addHelmetToTeam(team, helmetId)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor ingrese el ID del casco", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }

    private fun addHelmetToTeam(team: TeamModel, helmetId: String) {
        val teamIndex = teamsList.indexOf(team)
        if (teamIndex != -1) {
            val helmet = HelmetModel(helmetId, "Nuevo Casco")
            val updatedTeam = team.copy(helmets = team.helmets + helmet)
            teamsList[teamIndex] = updatedTeam
            teamsAdapter.notifyItemChanged(teamIndex)
            Toast.makeText(this, "Casco vinculado al equipo ${team.name}", Toast.LENGTH_SHORT).show()
        }
    }
}

// Modelos de datos sin status
data class TeamModel(
    val id: Int,
    val name: String,
    val helmets: List<HelmetModel>
)

data class HelmetModel(
    val id: String,
    val name: String
)
