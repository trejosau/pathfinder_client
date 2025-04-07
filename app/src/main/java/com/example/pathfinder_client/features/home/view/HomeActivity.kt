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
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
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
import com.example.pathfinder_client.features.sensors.view.SensorsActivity
import org.json.JSONObject

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
        teamsAdapter = TeamsAdapter(
            teamsList,
            this::onAddHelmetClicked,
            this::onHelmetViewClicked
        )
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

    private fun onHelmetViewClicked(helmet: HelmetModel) {
        if (helmet.id == "73283888-21a9-4963-8447-0f239d89b6be") {
            // Navegar a la vista de sensores reales
            val intent = Intent(this, SensorsActivity::class.java)
            intent.putExtra("helmet_id", helmet.id)
            intent.putExtra("helmet_name", helmet.name)
            startActivity(intent)
        } else {
            // Navegar a la vista de sensores simulados (vacía)
            showSimulatedSensorsDialog(helmet)
        }
    }

    private fun showSimulatedSensorsDialog(helmet: HelmetModel) {
        AlertDialog.Builder(this)
            .setTitle("Casco Simulado")
            .setMessage("Este es un casco simulado con ID: ${helmet.id}. No hay datos reales disponibles.")
            .setPositiveButton("Aceptar", null)
            .show()
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
                                    id = teamResponse.id,
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
                            // Mostrar el mensaje de error recibido directamente
                            showError(apiResponse?.message ?: "Error desconocido")
                        }
                    } else {
                        // Extraer el campo "message" del JSON del error
                        val errorMessage = try {
                            val errorBody = response.errorBody()?.string()
                            val jsonError = errorBody?.let { JSONObject(it) }
                            jsonError?.getString("message") ?: "Error desconocido"
                        } catch (e: Exception) {
                            "Error desconocido"
                        }
                        showError(errorMessage)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showError(e.message ?: "Ocurrió un error")
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
        findViewById<ProgressBar>(R.id.progressBar).visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Método para mostrar Toast con el mensaje recibido directamente
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
        val preferencesManager = PreferencesManager(this)
        val userId = preferencesManager.getUserId()
        val token = preferencesManager.getToken()

        if (userId?.isNotEmpty() == true && token?.isNotEmpty() == true) {
            showLoading(true)
            lifecycleScope.launch {
                try {
                    val response = viewModel.createTeam(teamName, userId, token)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            if (apiResponse?.success == true) {
                                // Recargar la lista completa para tener el ID correcto del servidor
                                loadTeamsFromApi()
                                Toast.makeText(this@HomeActivity, "Equipo '$teamName' agregado", Toast.LENGTH_SHORT).show()
                            } else {
                                showError(apiResponse?.message ?: "Error desconocido")
                            }
                        } else {
                            // Extraer el campo "message" del JSON del error
                            val errorMessage = try {
                                val errorBody = response.errorBody()?.string()
                                val jsonError = errorBody?.let { JSONObject(it) }
                                jsonError?.getString("message") ?: "Error desconocido"
                            } catch (e: Exception) {
                                "Error desconocido"
                            }
                            showError(errorMessage)
                        }
                        showLoading(false)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showError(e.message ?: "Ocurrió un error")
                        showLoading(false)
                    }
                }
            }
        } else {
            showError("No hay sesión activa")
            redirectToLogin()
        }
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
                linkHelmetToTeam(team, helmetId)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor ingrese el ID del casco", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }

    private fun linkHelmetToTeam(team: TeamModel, helmetId: String) {
        showLoading(true)
        val preferencesManager = PreferencesManager(this)
        val token = preferencesManager.getToken()

        if (token?.isNotEmpty() == true) {
            lifecycleScope.launch {
                try {
                    val response = viewModel.linkTeam(helmetId, team.id.toString(), token)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            if (apiResponse?.success == true) {
                                // Si es exitoso, recargar los equipos para obtener la información actualizada
                                loadTeamsFromApi()
                                Toast.makeText(
                                    this@HomeActivity,
                                    "Casco vinculado al equipo ${team.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                showError(apiResponse?.message ?: "Error desconocido")
                            }
                        } else {
                            val errorMessage = try {
                                val errorBody = response.errorBody()?.string()
                                val jsonError = errorBody?.let { JSONObject(it) }
                                jsonError?.getString("message") ?: "Error desconocido"
                            } catch (e: Exception) {
                                "Error desconocido"
                            }
                            showError(errorMessage)
                        }
                        showLoading(false)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showError(e.message ?: "Ocurrió un error")
                        showLoading(false)
                    }
                }
            }
        } else {
            showError("No hay sesión activa")
            redirectToLogin()
        }
    }
}

// Modelos de datos sin status
data class TeamModel(
    val id: String,
    val name: String,
    val helmets: List<HelmetModel>
)

data class HelmetModel(
    val id: String,
    val name: String
)
