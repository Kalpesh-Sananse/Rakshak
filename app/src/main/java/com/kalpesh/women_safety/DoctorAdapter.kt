package com.kalpesh.women_safety

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class DoctorAdapter(
    private val doctorList: List<Doctor>,
    private val onLocateClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.doctorName)
        val distance: TextView = view.findViewById(R.id.distanceText)
        val locateButton: Button = view.findViewById(R.id.locateButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.name.text = doctor.name
        holder.distance.text = "Distance: ${"%.2f".format(doctor.distance)} km"
        holder.locateButton.setOnClickListener {
            onLocateClick(doctor)
        }
    }

    override fun getItemCount(): Int = doctorList.size
}
