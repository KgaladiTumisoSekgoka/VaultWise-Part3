package com.example.loginsignup

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignup.data.Reward
import java.text.SimpleDateFormat
import java.util.*


class RewardAdapter(private val rewards: List<Reward>) : RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reward, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewards[position]
        holder.title.text = reward.rewardTitle
        holder.description.text = reward.rewardDescription

        // Format dateEarned from timestamp to a readable date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(reward.dateEarned))
        holder.date.text = formattedDate

        holder.rewardIcon.setImageResource(reward.iconResId)
        Log.d("RewardAdapter", "Icon resource ID: ${reward.iconResId}")

    }

    override fun getItemCount(): Int = rewards.size

    inner class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rewardIcon: ImageView = itemView.findViewById(R.id.rewardIcon)
        val title: TextView = itemView.findViewById(R.id.rewardTitle)
        val description: TextView = itemView.findViewById(R.id.rewardDescription)
        val date: TextView = itemView.findViewById(R.id.rewardDate)
    }
}
