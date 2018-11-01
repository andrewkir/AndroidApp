package com.andrewkir.andrewforwork.timem8.Adapters

import android.animation.ObjectAnimator
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import android.support.v7.widget.RecyclerView
import com.github.aakira.expandablelayout.ExpandableLayout
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter
import android.support.v4.content.ContextCompat
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.util.SparseBooleanArray
import android.view.View
import android.widget.*
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.Models.dailyFrog
import com.andrewkir.andrewforwork.timem8.R
import com.github.aakira.expandablelayout.Utils


class expandAdapter: RecyclerView.Adapter<expandAdapter.ViewHolder>{

    private lateinit var data: ArrayList<dailyFrog>
    private var context: Context? = null
    private val expandState = SparseBooleanArray()
    private lateinit var db: DBdaily

    constructor(data: ArrayList<dailyFrog>){
        this.data = data
        for (i in data.indices) {
            expandState.append(i, false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.row_recycler_expand, parent, false))
    }
    fun removeAt(position: Int) {
        db.deleteFrog(data[position])
        data.removeAt(position)
        notifyItemRemoved(position)
    }
    fun doneAt(position: Int){
        var frog = data[position]
        frog.done = true
        db.updateFrog(frog)
        data.removeAt(position)
        notifyItemRemoved(position)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db = DBdaily(this.context!!)
        val item = data[position]
        var task = item.tasks.split(";;;")
        var tasks = ArrayList<String>()
        for (i in 0 until item.count){
            tasks.add(task[i])
        }
        holder.setIsRecyclable(false)
        holder.textName.text = item.name
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context!!, item.colorId1))
        holder.expandableLayout.setInRecyclerView(true)
        holder.expandableLayout.setBackgroundColor(ContextCompat.getColor(context!!, item.colorId2))
        holder.textDesc.text = item.description
        //настройка анимации
        holder.expandableLayout.setInterpolator(Utils.createInterpolator(Utils.DECELERATE_INTERPOLATOR))
        holder.expandableLayout.isExpanded = expandState.get(position)
        holder.expandableLayout.setListener(object : ExpandableLayoutListenerAdapter() {
            override fun onPreOpen() {
                createRotateAnimator(holder.buttonLayout, 0f, 180f).start()
                expandState.put(position, true)

            }

            override fun onPreClose() {
                createRotateAnimator(holder.buttonLayout, 180f, 0f).start()
                expandState.put(position, false)
                db.updateFrog(item)
            }
        })

        for(i in 0 until item.count) {
            var checkBox = CheckBox(this.context)
            checkBox.id = i
            checkBox.text = tasks[i]
            var tmp = checkBox.paintFlags
            holder.expandLiner.addView(checkBox)
            if(item.isDone[i]){
                checkBox.isChecked = true
                checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            checkBox.setOnClickListener {
                item.isDone[i] = checkBox.isChecked
                if(item.isDone[i]){
                    checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    checkBox.paintFlags = tmp
                }
                db.updateFrog(item)
            }
        }

        holder.buttonLayout.rotation = if (expandState.get(position)) 180f else 0f
        holder.relativeExpand.setOnClickListener { onClickButton(holder.expandableLayout) }
    }

    private fun onClickButton(expandableLayout: ExpandableLayout) {
        expandableLayout.toggle()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var textName: TextView
        var buttonLayout: RelativeLayout
        var expandLiner: LinearLayout
        var expandableLayout: ExpandableLinearLayout
        var relativeExpand: RelativeLayout
        var textDesc: TextView

        init {
            textName = v.findViewById(R.id.rowDailyName)
            buttonLayout = v.findViewById(R.id.financeButton)
            expandableLayout = v.findViewById(R.id.expandableLayout)
            expandLiner = v.findViewById(R.id.linearExpand)
            textDesc = v.findViewById(R.id.rowDailyDesc)
            relativeExpand = v.findViewById(R.id.relativeExpand)
        }
    }

    fun createRotateAnimator(target: View, from: Float, to: Float): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(target, "rotation", from, to)
        animator.duration = 300
        animator.interpolator = Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR)
        return animator
    }
}