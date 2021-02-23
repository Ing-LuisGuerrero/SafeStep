package com.equipo5.safestep.adapters

import com.equipo5.safestep.models.Report

interface ReportsListener {
    fun onReportClicked(report: Report, position: Int)
}