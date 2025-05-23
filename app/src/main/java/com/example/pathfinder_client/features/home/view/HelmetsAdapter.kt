// HelmetsAdapter.kt
package com.example.pathfinder_client.features.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pathfinder_client.R

class HelmetsAdapter(
    private val helmets: List<HelmetModel>,
    private val onHelmetViewClicked: (HelmetModel) -> Unit // Añadimos callback para el clic
) : RecyclerView.Adapter<HelmetsAdapter.HelmetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelmetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_helmet, parent, false)
        return HelmetViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelmetViewHolder, position: Int) {
        holder.bind(helmets[position])
    }

    override fun getItemCount(): Int = helmets.size

    inner class HelmetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val helmetIdTextView: TextView = itemView.findViewById(R.id.helmetIdTextView)
        private val helmetStatusTextView: TextView = itemView.findViewById(R.id.helmetStatusTextView)
        private val btnShowHelmetData: ImageButton = itemView.findViewById(R.id.btnShowHelmetData)

        fun bind(helmet: HelmetModel) {
            helmetIdTextView.text = helmet.name

            // Mostramos si está conectado o es simulado según el ID
            helmetStatusTextView.text = "Estado: ${if (helmet.id == "73283888-21a9-4963-8447-0f239d89b6be") "Conectado" else "Simulado"}"

            // Configuramos el listener para el botón del ojo
            btnShowHelmetData.setOnClickListener {
                onHelmetViewClicked(helmet)
            }
        }
    }
}