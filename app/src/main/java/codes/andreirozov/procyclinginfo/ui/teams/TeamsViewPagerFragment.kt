package codes.andreirozov.procyclinginfo.ui.teams

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import codes.andreirozov.procyclinginfo.R
import codes.andreirozov.procyclinginfo.data.api.ApiHelper
import codes.andreirozov.procyclinginfo.data.api.RetrofitBuilder
import codes.andreirozov.procyclinginfo.databinding.FragmentOfTeamsViewPagerBinding
import codes.andreirozov.procyclinginfo.ui.MainActivity
import codes.andreirozov.procyclinginfo.ui.base.ViewModelFactory
import codes.andreirozov.procyclinginfo.utils.Resource
import codes.andreirozov.procyclinginfo.utils.hideView
import codes.andreirozov.procyclinginfo.utils.showView
import com.google.android.material.chip.Chip

class TeamsViewPagerFragment : Fragment() {

    private var binding: FragmentOfTeamsViewPagerBinding? = null

    // Init viewModel
    private val viewModel: TeamsViewModel by lazy {
        ViewModelProvider(
            viewModelStore,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService), raceType)
        ).get(TeamsViewModel::class.java)
    }
    private lateinit var groupType: String
    private lateinit var raceType: String
    private lateinit var rvAdapter: TeamsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // get data from Bundle
        arguments?.let {
            groupType = it.getString("GROUP_TYPE")!!
            raceType = it.getString("RACE_TYPE")!!
        }

        //Binding
        binding = FragmentOfTeamsViewPagerBinding.inflate(layoutInflater)

        setUI()

        setObservers()

        return binding!!.root
    }

    private fun setUI() {

        // Create recyclerView adapter
        rvAdapter = TeamsRecyclerViewAdapter()

        // Set RecyclerView
        binding!!.teamsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        // Fill ChipGroup with years
        if (raceType == "cyclocross") {
            val list = listOf("2017-2018", "2018-2019", "2019-2020", "2020-2021")

            for (i in list) {
                val chip = Chip(context, null, R.attr.customChipChoiceStyle)
                chip.text = i
                binding!!.teamsChipGroup.addView(chip)
            }

        } else {

            for (i in 2005..2020) {
                val chip = Chip(context, null, R.attr.customChipChoiceStyle)
                chip.text = i.toString()
                binding!!.teamsChipGroup.addView(chip)
            }

        }

        binding!!.teamsChipGroup.setOnCheckedChangeListener { chipGroup, chipId ->
            var year = "2005"
            for (i in chipGroup) {
                if ((i as Chip).id == chipId) {
                    year = i.text.toString()
                    break
                }
            }

            // Clear filter
            (activity as MainActivity).clearTeamsFilter()

            (activity as MainActivity).disableClickableFab()

            viewModel.setYear(year)
        }

        // Set first element in ChipGroup as selected
        (binding!!.teamsChipGroup[0] as Chip).isChecked = true

        binding!!.retryTeamsButton.setOnClickListener {

            // Get text from checked Chip
            val checkedChipId = binding!!.teamsChipGroup.checkedChipId
            val year = binding!!.teamsChipGroup.findViewById<Chip>(checkedChipId).text.toString()

            viewModel.setYear(year)
        }

    }

    fun setFilterTeams(teamCategories: MutableList<String>, country: String) {

        rvAdapter.setFilterTeams(teamCategories, country)
        rvAdapter.notifyDataSetChanged()

        if (rvAdapter.itemCount == 0) {

            // UI visibility
            hideView(binding!!.teamsRecyclerView)
            hideView(binding!!.teamsProgressBar)
            hideView(binding!!.noInternetTeamsLinearLayout)
            showView(binding!!.noAvailableTeamsTextView)

        } else {

            // UI visibility
            hideView(binding!!.noAvailableTeamsTextView)
            hideView(binding!!.teamsProgressBar)
            hideView(binding!!.noInternetTeamsLinearLayout)
            showView(binding!!.teamsRecyclerView)
        }
    }

    private fun setObservers() {

        viewModel.teams.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {

                    it.data?.let { response ->

                        rvAdapter.setDefaultTeams(response.body())
                        rvAdapter.notifyDataSetChanged()

                    }

                    // UI visibility
                    hideView(binding!!.noAvailableTeamsTextView)
                    hideView(binding!!.teamsProgressBar)
                    hideView(binding!!.noInternetTeamsLinearLayout)
                    showView(binding!!.teamsRecyclerView)

                    // Filter FAB clickable, when success only
                    (activity as MainActivity).enableClickableFab()

                    Log.i("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.ERROR -> {

                    // UI visibility
                    hideView(binding!!.noAvailableTeamsTextView)
                    hideView(binding!!.teamsProgressBar)
                    hideView(binding!!.teamsRecyclerView)
                    showView(binding!!.noInternetTeamsLinearLayout)

                    Log.e("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.LOADING -> {

                    // UI visibility
                    hideView(binding!!.noAvailableTeamsTextView)
                    hideView(binding!!.teamsRecyclerView)
                    hideView(binding!!.noInternetTeamsLinearLayout)
                    showView(binding!!.teamsProgressBar)

                    Log.i("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.SERVER_ERROR -> {

                    // TODO handle server error

                    Log.e("Data status", "${it.message!!} $raceType")

                }
            }
        })
    }

    companion object {

        fun newInstance(groupType: String, raceType: String) = TeamsViewPagerFragment().apply {
            arguments = Bundle().apply {
                putString("GROUP_TYPE", groupType)
                putString("RACE_TYPE", raceType)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}