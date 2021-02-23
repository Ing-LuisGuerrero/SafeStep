package com.equipo5.safestep.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.equipo5.safestep.R
import com.equipo5.safestep.activities.ReportDetailedActivity
import com.equipo5.safestep.adapters.ReportsAdapter
import com.equipo5.safestep.adapters.ReportsListener
import com.equipo5.safestep.models.Report
import com.equipo5.safestep.viewmodels.ReportsViewModel
import kotlinx.android.synthetic.main.fragment_reports_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "operation"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportsListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportsListFragment : Fragment(), ReportsListener {
    // TODO: Rename and change types of parameters
    private var operation: String? = null

    private lateinit var reportsAdapter: ReportsAdapter
    private lateinit var viewModel: ReportsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            operation = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reports_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(operation == getString(R.string.last_reports_title)) {
            getAllReports(true, view)
        } else {
            getAllReports(false, view)
        }

        //tvOperation.text = operation.toString()
    }

    private fun getAllReports(getAll: Boolean, view: View) {
        this.viewModel = ViewModelProvider(this).get(ReportsViewModel()::class.java)
        this.viewModel.getAll = getAll
        this.viewModel.refresh()

        reportsAdapter = context?.let { ReportsAdapter(it,this) }!!

        rvReports.apply {
            layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            adapter = reportsAdapter
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        this.viewModel.listReports.observe(viewLifecycleOwner, { report ->
            reportsAdapter.updateData(report)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, {
            if(it != null) {
                when(it) {
                    true -> rlLoadingAllReports.visibility = View.VISIBLE
                    false -> rlLoadingAllReports.visibility = View.INVISIBLE
                }
            } else {
                rlLoadingAllReports.visibility = View.INVISIBLE
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportsListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(operation: String) =
            ReportsListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, operation)
                }
            }
    }

    override fun onReportClicked(report: Report, position: Int) {
        val intent = Intent(context, ReportDetailedActivity::class.java)
        intent.putExtra("report", report);
        startActivity(intent);
    }
}