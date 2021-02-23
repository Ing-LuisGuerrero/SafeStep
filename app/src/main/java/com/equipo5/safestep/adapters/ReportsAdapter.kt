package com.equipo5.safestep.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.equipo5.safestep.R
import com.equipo5.safestep.models.Report
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReportsAdapter(val context: Context, val reportsListener: ReportsListener): RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    var listReports = ArrayList<Report>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
        R.layout.item_report, parent, false))

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = listReports[position]

        when {
            report.image1 != null -> {
                Picasso.with(context).load(report.image1).into(holder.ivMainImage)
            }
            report.image2 != null -> {
                Picasso.with(context).load(report.image2).into(holder.ivMainImage)
            }
            report.image3 != null -> {
                Picasso.with(context).load(report.image3).into(holder.ivMainImage)
            }
        }

        holder.tvReportPlaceName.text = report.city

        if(report.isValidated == true) {
            holder.tvReportVerified.visibility = View.INVISIBLE
        } else {
            holder.tvReportVerified.text = "No verificado"
        }

        holder.tvReportTitle.text = report.title
        holder.tvReportCategory.text = "Categoria: ${report.category}"

        //val patron = "%02d/%02d/%04d %02d:%02d"

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        holder.tvReportWhenWasItReported.text = "Reportado el: ${simpleDateFormat.format(report.whenWasItReported.toDate()) }"
        holder.tvWhenItHappened.text = "Sucedió el: ${simpleDateFormat.format(report.whenItHappened.toDate())}"

        holder.itemView.setOnClickListener {
            reportsListener.onReportClicked(report, position)
        }
    }

    override fun getItemCount() = listReports.size

    fun updateData(data: List<Report>) {
        listReports.clear()
        listReports.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReportTitle = itemView.findViewById<TextView>(R.id.tvReportTitle)
        val tvReportCategory = itemView.findViewById<TextView>(R.id.tvReportCategory)
        val tvReportWhenWasItReported = itemView.findViewById<TextView>(R.id.tvWhenWasItReported)
        val tvWhenItHappened = itemView.findViewById<TextView>(R.id.tvWhenItHappened)
        val tvReportPlaceName = itemView.findViewById<TextView>(R.id.tvReportPlaceName)
        val ivMainImage = itemView.findViewById<ImageView>(R.id.ivMainImage)
        val tvReportVerified = itemView.findViewById<TextView>(R.id.tvReportVerified)
    }

}