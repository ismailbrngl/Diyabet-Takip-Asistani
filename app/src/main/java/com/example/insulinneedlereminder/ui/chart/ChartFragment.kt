package com.example.insulinneedlereminder.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.insulinneedlereminder.R
import com.example.insulinneedlereminder.data.db.AppDatabase
import com.example.insulinneedlereminder.data.entity.GlucoseRecord
import com.example.insulinneedlereminder.data.entity.InsulinRecord
import com.example.insulinneedlereminder.data.repository.GlucoseRepository
import com.example.insulinneedlereminder.data.repository.InsulinRepository
import com.example.insulinneedlereminder.databinding.FragmentChartBinding
import com.example.insulinneedlereminder.ui.glucose.GlucoseViewModel
import com.example.insulinneedlereminder.ui.glucose.GlucoseViewModelFactory
import com.example.insulinneedlereminder.ui.insulin.InsulinViewModel
import com.example.insulinneedlereminder.ui.insulin.InsulinViewModelFactory
import com.example.insulinneedlereminder.util.DateUtils
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

class ChartFragment : Fragment() {

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private val glucoseViewModel: GlucoseViewModel by activityViewModels {
        val db = AppDatabase.getInstance(requireContext())
        GlucoseViewModelFactory(GlucoseRepository(db.glucoseDao()))
    }

    private val insulinViewModel: InsulinViewModel by activityViewModels {
        val db = AppDatabase.getInstance(requireContext())
        InsulinViewModelFactory(InsulinRepository(db.insulinDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNoDataTexts()
        observeGlucose()
        observeInsulin()
    }

    private fun setupNoDataTexts() {
        val noDataColor = themeColor(R.color.text_secondary)
        binding.glucoseChart.apply {
            setNoDataText("Grafik verisi bulunamadı")
            setNoDataTextColor(noDataColor)
        }
        binding.insulinChart.apply {
            setNoDataText("Grafik verisi bulunamadı")
            setNoDataTextColor(noDataColor)
        }
    }

    private fun themeColor(resId: Int): Int =
        ContextCompat.getColor(requireContext(), resId)

    private fun styleChartAxes(chart: LineChart) {
        val axisColor = themeColor(R.color.text_secondary)
        val gridLineColor = themeColor(R.color.outline)
        chart.xAxis.apply {
            textColor = axisColor
            axisLineColor = gridLineColor
        }
        chart.axisLeft.apply {
            textColor = axisColor
            axisLineColor = gridLineColor
            this.gridColor = gridLineColor
        }
        chart.legend.textColor = themeColor(R.color.text_primary)
    }

    private fun styleChartAxes(chart: BarChart) {
        val axisColor = themeColor(R.color.text_secondary)
        val gridLineColor = themeColor(R.color.outline)
        chart.xAxis.apply {
            textColor = axisColor
            axisLineColor = gridLineColor
        }
        chart.axisLeft.apply {
            textColor = axisColor
            axisLineColor = gridLineColor
            this.gridColor = gridLineColor
        }
        chart.legend.textColor = themeColor(R.color.text_primary)
    }

    private fun observeGlucose() {
        glucoseViewModel.lastRecords.observe(viewLifecycleOwner) { records ->
            if (records.isEmpty()) {
                binding.glucoseChart.clear()
                binding.glucoseChart.invalidate()
                binding.tvAverage.text = "-"
                binding.tvMin.text = "-"
                binding.tvMax.text = "-"
                return@observe
            }
            // Grafik tamamen çizildikten sonra setup et
            binding.glucoseChart.post {
                setupGlucoseChart(records)
                setupStats(records)
            }
        }
    }

    private fun observeInsulin() {
        insulinViewModel.allRecords.observe(viewLifecycleOwner) { records ->
            if (records.isEmpty()) {
                binding.insulinChart.clear()
                binding.insulinChart.invalidate()
                return@observe
            }
            binding.insulinChart.post {
                setupInsulinChart(records)
            }
        }
    }

    private fun setupGlucoseChart(records: List<GlucoseRecord>) {
        val entries = records.reversed().mapIndexed { index, record ->
            Entry(index.toFloat(), record.value.toFloat())
        }

        val dataSet = LineDataSet(entries, "Kan Şekeri (mg/dL)").apply {
            color = themeColor(R.color.primary)
            valueTextColor = themeColor(R.color.text_primary)
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(themeColor(R.color.primary))
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        binding.glucoseChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            setPinchZoom(true)
            styleChartAxes(this)

            axisLeft.removeAllLimitLines()
            axisLeft.addLimitLine(
                LimitLine(70f, "Düşük").apply {
                    lineColor = themeColor(R.color.glucose_low)
                    lineWidth = 1f
                    textColor = themeColor(R.color.glucose_low)
                }
            )
            axisLeft.addLimitLine(
                LimitLine(180f, "Yüksek").apply {
                    lineColor = themeColor(R.color.glucose_high)
                    lineWidth = 1f
                    textColor = themeColor(R.color.glucose_high)
                }
            )

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index >= 0 && index < records.reversed().size)
                        DateUtils.formatDate(records.reversed()[index].date)
                    else ""
                }
            }
            xAxis.labelRotationAngle = -45f
            axisRight.isEnabled = false
            notifyDataSetChanged()
            invalidate()
        }
    }

    private fun setupStats(records: List<GlucoseRecord>) {
        if (records.isEmpty()) return
        val values = records.map { it.value }
        val avg = values.average().toInt()
        val min = values.min()
        val max = values.max()

        binding.tvAverage.text = avg.toString()
        binding.tvMin.text = min.toString()
        binding.tvMax.text = max.toString()
    }

    private fun setupInsulinChart(records: List<InsulinRecord>) {
        val grouped = records.reversed()
            .takeLast(14)
            .groupBy { DateUtils.formatDate(it.date) }

        val entries = grouped.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.sumOf { it.units }.toFloat())
        }

        val labels = grouped.keys.toList()

        val dataSet = BarDataSet(entries, "Günlük İnsülin (ünite)").apply {
            color = themeColor(R.color.accent)
            valueTextColor = themeColor(R.color.text_primary)
        }

        binding.insulinChart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            styleChartAxes(this)

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index >= 0 && index < labels.size) labels[index] else ""
                }
            }
            xAxis.labelRotationAngle = -45f
            axisRight.isEnabled = false
            notifyDataSetChanged()
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}