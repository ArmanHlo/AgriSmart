package com.shoping.agrismartapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CropAdapter(private var crops: List<Crop>) : RecyclerView.Adapter<CropAdapter.CropViewHolder>() {

    class CropViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cropImageView: ImageView = view.findViewById(R.id.cropImageView)
        val nameTextView: TextView = view.findViewById(R.id.cropNameTextView)
        val soilTextView: TextView = view.findViewById(R.id.soilTypeTextView)
        val seasonTextView: TextView = view.findViewById(R.id.seasonTextView)
        val waterTextView: TextView = view.findViewById(R.id.waterTextView)
        val fertilizerTextView: TextView = view.findViewById(R.id.fertilizerTextView)
        val pestsTextView: TextView = view.findViewById(R.id.pestsTextView)
        val tempTextView: TextView = view.findViewById(R.id.tempTextView)
        val phTextView: TextView = view.findViewById(R.id.phTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crop, parent, false)
        return CropViewHolder(view)
    }

    override fun onBindViewHolder(holder: CropViewHolder, position: Int) {
        val crop = crops[position]
        
        Glide.with(holder.itemView.context)
            .load(crop.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .centerCrop()
            .into(holder.cropImageView)

        holder.nameTextView.text = crop.name
        holder.soilTextView.text = "Best Soil: ${crop.soilType}"
        holder.seasonTextView.text = "Season: ${crop.plantingSeason}"
        holder.waterTextView.text = "Water: ${crop.waterRequirement}"
        holder.fertilizerTextView.text = "Fertilizer: ${crop.fertilizer}"
        holder.pestsTextView.text = "Pests: ${crop.commonPests}"
        holder.tempTextView.text = "Temp: ${crop.idealTemp}"
        holder.phTextView.text = "pH: ${crop.phLevel}"
        holder.descriptionTextView.text = crop.description
    }

    override fun getItemCount(): Int = crops.size

    fun updateList(newList: List<Crop>) {
        crops = newList
        notifyDataSetChanged()
    }
}
