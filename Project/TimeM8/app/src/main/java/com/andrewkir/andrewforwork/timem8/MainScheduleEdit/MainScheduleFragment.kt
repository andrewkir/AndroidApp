package com.andrewkir.andrewforwork.timem8.MainScheduleEdit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.andrewkir.andrewforwork.timem8.DataBase.DBhandler
import com.andrewkir.andrewforwork.timem8.R
import com.andrewkir.andrewforwork.timem8.Models.Sub
import kotlinx.android.synthetic.main.activity_editable.*


class MainScheduleFragment : Fragment() {
    var day = 0
    var date = "1.1.2018"
    internal lateinit var db: DBhandler
    private var lstSubs:List<Sub> = ArrayList()
    lateinit var adapter: com.andrewkir.andrewforwork.timem8.Adapters.MainScheduleAdapter


    internal lateinit var context: Context
    companion object {
        fun newInstance(): MainScheduleFragment {
            return MainScheduleFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        context = inflater.context
        return inflater.inflate(R.layout.activity_editable,container,false)
    }


    override fun onAttach(context1: Context?) {
        super.onAttach(context1)
        context = context1!!
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            day = bundle.getInt("DAY", 0)+1
            date = bundle.getString("DATE","1.1.2018")
        }
        db = DBhandler(context)
        subjectListFragment.addItemDecoration(DividerItemDecoration(context,1))
        if(db.allSubByDay(day).isEmpty()){
            hintToAddSmth.text="Вы ещё не добавили расписание на этот день"
            subjectListFragment.visibility = View.GONE
        } else {
            subjectListFragment.visibility = View.VISIBLE
            adapter = com.andrewkir.andrewforwork.timem8.Adapters.MainScheduleAdapter(date, context, lstSubs) { subject ->
                val detailActivity = Intent(context, MainScheduleDetail::class.java)
                detailActivity.putExtra("NAME_SUB", subject.name)
                detailActivity.putExtra("DATE", date)
                detailActivity.putExtra("COUNT_SUB", subject.count)
                detailActivity.putExtra("DAY", subject.day)
                startActivity(detailActivity)
            }
            subjectListFragment.adapter = adapter
            val layoutManager = LinearLayoutManager(context)
            subjectListFragment.layoutManager = layoutManager
            subjectListFragment.setHasFixedSize(true)
        }
    }


    override fun onResume() {
        super.onResume()
        refreshData()
    }


    private fun refreshData(){
        val dbTmp = DBhandler(context)
        lstSubs = dbTmp.allSubByDay(day)
        if(lstSubs.isEmpty()){
            hintToAddSmth.text="Вы ещё не добавили расписание на этот день"
            subjectListFragment.visibility = View.GONE
        } else {
            subjectListFragment.visibility = View.VISIBLE
            hintToAddSmth.text=""
            val adapter = com.andrewkir.andrewforwork.timem8.Adapters.MainScheduleAdapter(date, context, lstSubs) { subject ->
                val detailActivity = Intent(context, MainScheduleDetail::class.java)
                detailActivity.putExtra("NAME_SUB", subject.name)
                detailActivity.putExtra("DATE", date)
                detailActivity.putExtra("COUNT_SUB", subject.count)
                detailActivity.putExtra("DAY", subject.day)
                startActivity(detailActivity)
            }
            subjectListFragment.adapter = adapter
            val layoutManager = LinearLayoutManager(context)
            subjectListFragment.layoutManager = layoutManager
            subjectListFragment.setHasFixedSize(true)
        }
    }
}