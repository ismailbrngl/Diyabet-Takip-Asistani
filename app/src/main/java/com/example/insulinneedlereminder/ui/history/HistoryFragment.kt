package com.example.insulinneedlereminder.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.insulinneedlereminder.data.db.AppDatabase
import com.example.insulinneedlereminder.data.entity.GlucoseRecord
import com.example.insulinneedlereminder.data.repository.GlucoseRepository
import com.example.insulinneedlereminder.databinding.FragmentHistoryBinding
import com.example.insulinneedlereminder.ui.glucose.GlucoseAdapter
import com.example.insulinneedlereminder.ui.glucose.GlucoseViewModel
import com.example.insulinneedlereminder.ui.glucose.GlucoseViewModelFactory
import com.example.insulinneedlereminder.ui.widget.GlucoseWidget
import com.example.insulinneedlereminder.util.GlucoseStatus
import com.example.insulinneedlereminder.util.PrefsManager

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GlucoseViewModel by activityViewModels {
        val db = AppDatabase.getInstance(requireContext())
        GlucoseViewModelFactory(GlucoseRepository(db.glucoseDao()))
    }

    private lateinit var adapter: GlucoseAdapter
    private var allRecords: List<GlucoseRecord> = emptyList()
    private var currentFilter: String = "ALL" // Seçili filtreyi hafızada tutuyoruz

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyGlucoseThresholds()

        // --- GERİ BUTONU AYARI ---
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        observeRecords()
        setupFilters()
    }

    private fun setupRecyclerView() {
        adapter = GlucoseAdapter { record ->
            viewModel.delete(record)
            GlucoseWidget.sendRefreshBroadcast(requireContext())
        }
        binding.rvHistory.apply {
            adapter = this@HistoryFragment.adapter
            setHasFixedSize(true) // Ekran çizim performansını artırır
        }
    }

    private fun observeRecords() {
        viewModel.allRecords.observe(viewLifecycleOwner) { records ->
            allRecords = records
            applyFilter(currentFilter) // Kayıt silindiğinde veya güncellendiğinde aktif filtreyi koru
        }
    }

    private fun setupFilters() {
        binding.btnAll.setOnClickListener { applyFilter("ALL") }
        binding.btnLow.setOnClickListener { applyFilter("LOW") }
        binding.btnNormal.setOnClickListener { applyFilter("NORMAL") }
        binding.btnHigh.setOnClickListener { applyFilter("HIGH") }
    }

    private fun applyFilter(filterType: String) {
        currentFilter = filterType
        val filteredList = when (filterType) {
            "LOW" -> allRecords.filter { viewModel.getStatus(it.value) == GlucoseStatus.LOW }
            "NORMAL" -> allRecords.filter { viewModel.getStatus(it.value) == GlucoseStatus.NORMAL }
            "HIGH" -> allRecords.filter { viewModel.getStatus(it.value) == GlucoseStatus.HIGH }
            else -> allRecords
        }

        adapter.submitList(filteredList)
        binding.tvEmpty.isVisible = filteredList.isEmpty() // Liste boşsa yazıyı göster
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyGlucoseThresholds() {
        val prefs = PrefsManager(requireContext())
        viewModel.setThresholds(
            low = prefs.glucoseLowThreshold,
            high = prefs.glucoseHighThreshold
        )
    }
}