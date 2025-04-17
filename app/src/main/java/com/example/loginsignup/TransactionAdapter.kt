package com.example.loginsignup

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignup.data.ExpenseWithCategory

class TransactionAdapter(
    private var expenses: List<ExpenseWithCategory>,
    private val onItemClick: (ExpenseWithCategory) -> Unit,
    private val onEditClick: (ExpenseWithCategory) -> Unit,
    private val onDeleteClick: (ExpenseWithCategory) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val title: TextView = itemView.findViewById(R.id.txtTitle)
        val description: TextView = itemView.findViewById(R.id.txtDescription)
        val amount: TextView = itemView.findViewById(R.id.txtAmount)
        val dateTime: TextView = itemView.findViewById(R.id.txtDateTime)
        val category: TextView = itemView.findViewById(R.id.txtCategory)
        val btnEdit: ImageButton = itemView.findViewById(R.id.editImageButton)
        val btnDelete: ImageButton = itemView.findViewById(R.id.deleteImageButton)

        fun bind(expense: ExpenseWithCategory) {
            title.text = expense.expense.title // Updated to reference the correct field
            description.text = expense.expense.description
            amount.text = "R${expense.expense.amount}" // Updated to reference the correct field
            dateTime.text = "${expense.expense.date} at ${expense.expense.startTime}" // Updated to reference the correct field
            category.text = expense.category.category_name // Updated to reference the correct field

            // Set up click listeners
            itemView.setOnClickListener {
                onItemClick(expense)
            }

            btnEdit.setOnClickListener {
                onEditClick(expense)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense) // Reuse the bind method to update the view
    }

    override fun getItemCount(): Int = expenses.size
    fun updateData(newData: List<ExpenseWithCategory>) {
        expenses = newData
        notifyDataSetChanged()
    }

    fun getExpenseAt(position: Int): ExpenseWithCategory {
        return expenses[position]
    }

}
