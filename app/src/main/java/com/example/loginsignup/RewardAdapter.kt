package com.example.loginsignup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignup.data.Reward
import java.text.SimpleDateFormat
import java.util.*

class RewardAdapter(private val rewards: List<Reward>) :
    RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    inner class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.rewardTitle)
        val description: TextView = itemView.findViewById(R.id.rewardDescription)
        val date: TextView = itemView.findViewById(R.id.rewardDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reward, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewards[position]
        holder.title.text = reward.rewardTitle
        holder.description.text = reward.rewardDescription
        holder.date.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(reward.dateEarned))
    }

    override fun getItemCount(): Int = rewards.size
}
