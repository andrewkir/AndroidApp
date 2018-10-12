package com.andrewkir.andrewforwork.timem8.Adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.andrewkir.andrewforwork.timem8.DataBase.DBfinance
import com.andrewkir.andrewforwork.timem8.Models.FinancialOperation
import com.andrewkir.andrewforwork.timem8.R
import kotlin.math.abs

class FinancialAdapter(val context: Context, val itemClick: (FinancialOperation) -> Unit): RecyclerView.Adapter<FinancialAdapter.Holder>() {
    var db = DBfinance(context)
    var operations = db.allOperations()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinancialAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_edit_finance, parent,false)
        return Holder(view,itemClick)
    }

    override fun getItemCount(): Int {
        return operations.size
    }

    override fun onBindViewHolder(holder: FinancialAdapter.Holder, position: Int) {
        holder.bindFrog(operations[position])
    }
    inner class Holder(itemView: View?, val itemClick: (FinancialOperation) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val finName = itemView?.findViewById<TextView>(R.id.finName)
        val finValue = itemView?.findViewById<TextView>(R.id.finValue)
        val finType = itemView?.findViewById<TextView>(R.id.finType)

        fun bindFrog(operation: FinancialOperation){

            finName!!.text = operation.name
            if(operation.amount<0) {
                itemView.setBackgroundColor(Color.parseColor("#ff81c784"))
                finValue!!.text = "+${abs(operation.amount)} \u20BD"
            } else {
                finValue!!.text = "-${operation.amount} \u20BD"
            }
            finType!!.text = operation.type
            itemView.setOnClickListener { itemClick(operation) }
        }
    }
}