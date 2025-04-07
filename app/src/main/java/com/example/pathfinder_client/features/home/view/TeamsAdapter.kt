// TeamsAdapter.kt
package com.example.pathfinder_client.features.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pathfinder_client.R

class TeamsAdapter(
    private val teams: List<TeamModel>,
    private val onAddHelmetClicked: (TeamModel) -> Unit,
    private val onHelmetViewClicked: (HelmetModel) -> Unit
) : RecyclerView.Adapter<TeamsAdapter.TeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position])
    }

    override fun getItemCount(): Int = teams.size

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val teamNameTextView: TextView = itemView.findViewById(R.id.teamNameTextView)
        private val teamHelmetCountTextView: TextView = itemView.findViewById(R.id.teamHelmetCountTextView)
        private val btnAddHelmet: ImageButton = itemView.findViewById(R.id.btnAddHelmet)
        private val helmetsRecyclerView: RecyclerView = itemView.findViewById(R.id.helmetsRecyclerView)

        fun bind(team: TeamModel) {
            teamNameTextView.text = team.name
            teamHelmetCountTextView.text = "Cascos: ${team.helmets.size}"

            // Configuramos el RecyclerView de cascos pasando también el callback para el clic
            helmetsRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            helmetsRecyclerView.adapter = HelmetsAdapter(team.helmets, onHelmetViewClicked)

            btnAddHelmet.setOnClickListener { onAddHelmetClicked(team) }
        }
    }
}