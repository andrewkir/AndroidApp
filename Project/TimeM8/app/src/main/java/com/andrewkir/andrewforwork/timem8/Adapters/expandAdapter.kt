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

    private lateinit var data: List<dailyFrog>
    private var context: Context? = null
    private val expandState = SparseBooleanArray()
    private lateinit var db: DBdaily

    constructor(data: List<dailyFrog>){
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db = DBdaily(this.context!!)
        val item = data[position]
        var task = item.tasks.split(";;;")
        var tasks = ArrayList<String>()
        for (i in 0 until item.count){
            tasks.add(task[i])
        }
        holder.setIsRecyclable(false)
        holder.textView.text = item.name
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context!!, item.colorId1))
        holder.expandableLayout.setInRecyclerView(true)
        holder.expandableLayout.setBackgroundColor(ContextCompat.getColor(context!!, item.colorId2))
        holder.nameText.text = item.description
        //настройка анимации expand
        holder.expandableLayout.setInterpolator(Utils.createInterpolator(Utils.DECELERATE_INTERPOLATOR))
        //
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
            var cb = CheckBox(this.context)
            cb.id = i
            cb.text = tasks[i]
            var tmp = cb.paintFlags
            holder.expandLiner.addView(cb)
            if(item.isDone[i]){
                cb.isChecked = true
                cb.paintFlags = cb.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            cb.setOnClickListener {
                item.isDone[i] = cb.isChecked
                if(item.isDone[i]){
                    cb.paintFlags = cb.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    cb.paintFlags = tmp
                }
                db.updateFrog(item)
            }
        }

        holder.buttonLayout.rotation = if (expandState.get(position)) 180f else 0f
        holder.relativeExpand.setOnClickListener { onClickButton(holder.expandableLayout) }
//        holder.buttonLayout.setOnClickListener {  }
    }

    private fun onClickButton(expandableLayout: ExpandableLayout) {
        expandableLayout.toggle()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var textView: TextView
        var buttonLayout: RelativeLayout
        var expandLiner: LinearLayout
        var expandableLayout: ExpandableLinearLayout
        var relativeExpand: RelativeLayout
        var nameText: TextView

        init {
            textView = v.findViewById(R.id.textView)
            buttonLayout = v.findViewById(R.id.button)
            expandableLayout = v.findViewById(R.id.expandableLayout)
            expandLiner = v.findViewById(R.id.linearExpand)
            nameText = v.findViewById(R.id.row_daily_name)
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