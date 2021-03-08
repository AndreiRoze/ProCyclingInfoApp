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
import com.google.android.material.chip.Chip

class TeamsViewPagerFragment : Fragment() {

    private var fragmentOfTeamsViewPagerBinding: FragmentOfTeamsViewPagerBinding? = null

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
        val binding = FragmentOfTeamsViewPagerBinding.inflate(layoutInflater)
        fragmentOfTeamsViewPagerBinding = binding

        setUI(binding)

        setObservers()

        return binding.root
    }

    private fun setUI(binding: FragmentOfTeamsViewPagerBinding) {

        // Create recyclerView adapter
        rvAdapter = TeamsRecyclerViewAdapter()

        // Set RecyclerView
        binding.teamsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        // Fill ChipGroup with years
        if (raceType == "cyclocross") {
            val list = listOf("2017-2018", "2018-2019", "2019-2020", "2020-2021")

            for (i in list) {
                val chip = Chip(context, null, R.attr.customChipChoiceStyle)
                chip.text = i
                binding.teamsChipGroup.addView(chip)
            }

        } else {

            for (i in 2005..2020) {
                val chip = Chip(context, null, R.attr.customChipChoiceStyle)
                chip.text = i.toString()
                binding.teamsChipGroup.addView(chip)
            }

        }

        binding.teamsChipGroup.setOnCheckedChangeListener { chipGroup, chipId ->
            var year = "2005"
            for (i in chipGroup) {
                if ((i as Chip).id == chipId) {
                    year = i.text.toString()
                    break
                }
            }

            // Clear filter
            (activity as MainActivity).clearMembersFilter()

            (activity as MainActivity).disableClickableFab()

            viewModel.setYear(year)
        }

        // Set first element in ChipGroup as selected
        (binding.teamsChipGroup[0] as Chip).isChecked = true

        binding.retryTeamsButton.setOnClickListener {

            // Get text from checked Chip
            val checkedChipId = binding.teamsChipGroup.checkedChipId
            val year = binding.teamsChipGroup.findViewById<Chip>(checkedChipId).text.toString()

            viewModel.setYear(year)
        }

    }

    fun setFilterTeams(teamCategories: MutableList<String>, country: String) {

        rvAdapter.setFilterTeams(teamCategories, country)
        rvAdapter.notifyDataSetChanged()

        if (rvAdapter.itemCount == 0) {

            // UI visibility
            goneTeamsRecyclerView()
            goneTeamsProgressBar()
            goneNoInternetTeamsLinearLayout()
            visibleNoAvailableTeamsTextView()

        } else {

            // UI visibility
            goneNoAvailableTeamsTextView()
            goneTeamsProgressBar()
            goneNoInternetTeamsLinearLayout()
            visibleTeamsRecyclerView()

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
                    goneNoAvailableTeamsTextView()
                    goneTeamsProgressBar()
                    goneNoInternetTeamsLinearLayout()
                    visibleTeamsRecyclerView()

                    // Filter FAB clickable, when success only
                    (activity as MainActivity).enableClickableFab()

                    Log.e("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.ERROR -> {

                    // UI visibility
                    goneNoAvailableTeamsTextView()
                    goneTeamsProgressBar()
                    goneTeamsRecyclerView()
                    visibleNoInternetTeamsLinearLayout()

                    Log.e("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.LOADING -> {

                    // UI visibility
                    goneNoAvailableTeamsTextView()
                    goneTeamsRecyclerView()
                    goneNoInternetTeamsLinearLayout()
                    visibleTeamsProgressBar()

                    Log.e("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.SERVER_ERROR -> {

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
        fragmentOfTeamsViewPagerBinding = null
        super.onDestroyView()
    }

    //------------------------------ UI VISIBILITY FUNCTIONS BELOW ------------------------------

    private fun goneNoAvailableTeamsTextView() {
        fragmentOfTeamsViewPagerBinding?.noAvailableTeamsTextView?.visibility = View.GONE
    }

    private fun visibleNoAvailableTeamsTextView() {
        fragmentOfTeamsViewPagerBinding?.noAvailableTeamsTextView?.visibility = View.VISIBLE
    }

    private fun goneTeamsRecyclerView() {
        fragmentOfTeamsViewPagerBinding?.teamsRecyclerView?.visibility = View.GONE
    }

    private fun visibleTeamsRecyclerView() {
        fragmentOfTeamsViewPagerBinding?.teamsRecyclerView?.visibility = View.VISIBLE
    }

    private fun goneTeamsProgressBar() {
        fragmentOfTeamsViewPagerBinding?.teamsProgressBar?.visibility = View.GONE
    }

    private fun visibleTeamsProgressBar() {
        fragmentOfTeamsViewPagerBinding?.teamsProgressBar?.visibility = View.VISIBLE
    }

    private fun goneNoInternetTeamsLinearLayout() {
        fragmentOfTeamsViewPagerBinding?.noInternetTeamsLinearLayout?.visibility = View.GONE
    }

    private fun visibleNoInternetTeamsLinearLayout() {
        fragmentOfTeamsViewPagerBinding?.noInternetTeamsLinearLayout?.visibility = View.VISIBLE
    }

}