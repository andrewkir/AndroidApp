package com.example.andrewforwork.timem8

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.example.andrewforwork.timem8.Adapters.MainScheduleAdapter
import com.example.andrewforwork.timem8.DataBase.DBHandler
import com.example.andrewforwork.timem8.Subject.Sub
import kotlinx.android.synthetic.main.activity_editable.*


class MainScheduleFragment(): Fragment() {
    var day = 0
    var date = "1.1.2018"
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
        db = DBHandler(context)
        adapter = MainScheduleAdapter(date,context,db.allSubByDay(day)){
            subject ->
        }
        if(db.allSubByDay(day).isEmpty()){
            hintToAddSmth.text="Вы ещё не добавили расписание на этот день"
        } else {
            subjectListFragment.adapter = adapter
            val layoutManager = LinearLayoutManager(context)
            subjectListFragment.layoutManager = layoutManager
            subjectListFragment.addItemDecoration(DividerItemDecoration(context,1))
            subjectListFragment.setHasFixedSize(true)
        }
    }
    internal lateinit var db: DBHandler
    internal var lstSubs:List<Sub> = ArrayList<Sub>()
    lateinit var adapter: MainScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        refreshData()
        super.onResume()
    }

    private fun refreshData(){
        lstSubs = db.allSubByDay(day)
        if(lstSubs.isEmpty()){
            hintToAddSmth.text="Вы ещё не добавили расписание на этот день"
        } else {
            val adapter = MainScheduleAdapter(date,context, db.allSubByDay(day)) { subject ->
                val DetailActivity = Intent(context,MainScheduleDetail::class.java)
                DetailActivity.putExtra("NAME_SUB",subject.name)
                DetailActivity.putExtra("DATE",date)
                DetailActivity.putExtra("COUNT_SUB",subject.count)
                startActivity(DetailActivity)
            }
            subjectListFragment.adapter = adapter
        }
    }

}