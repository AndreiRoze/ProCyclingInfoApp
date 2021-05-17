package codes.andreirozov.procyclinginfo.ui.members

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
import codes.andreirozov.procyclinginfo.databinding.FragmentOfMembersViewPagerBinding
import codes.andreirozov.procyclinginfo.ui.MainActivity
import codes.andreirozov.procyclinginfo.ui.base.ViewModelFactory
import codes.andreirozov.procyclinginfo.utils.Resource
import codes.andreirozov.procyclinginfo.utils.hideView
import codes.andreirozov.procyclinginfo.utils.showView
import com.google.android.material.chip.Chip

class MembersViewPagerFragment : Fragment() {

    private var binding: FragmentOfMembersViewPagerBinding? = null

    // Init viewModel
    private val viewModel: MembersViewModel by lazy {
        ViewModelProvider(
            viewModelStore,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService), raceType)
        ).get(MembersViewModel::class.java)
    }
    private lateinit var groupType: String
    private lateinit var raceType: String
    private lateinit var rvAdapter: MembersRecyclerViewAdapter

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
        binding = FragmentOfMembersViewPagerBinding.inflate(layoutInflater)

        setUI()

        setObservers()

        return binding!!.root
    }

    private fun setUI() {

        // Create recyclerView adapter
        rvAdapter = MembersRecyclerViewAdapter()

        // Set RecyclerView
        binding!!.membersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        // Fill ChipGroup with years
        if (raceType == "cyclocross") {
            val list = listOf("2017-2018", "2018-2019", "2019-2020", "2020-2021")

            for (i in list) {
                val chip = Chip(context, null, R.attr.customChipChoiceStyle)
                chip.text = i
                binding!!.membersChipGroup.addView(chip)
            }

        } else {

            for (i in 2005..2020) {
                val chip = Chip(context, null, R.attr.customChipChoiceStyle)
                chip.text = i.toString()
                binding!!.membersChipGroup.addView(chip)
            }
        }

        // ChipGroup checked change listener
        binding!!.membersChipGroup.setOnCheckedChangeListener { chipGroup, chipId ->
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
        (binding!!.membersChipGroup[0] as Chip).isChecked = true

        binding!!.retryMembersButton.setOnClickListener {

            // Get text from checked Chip
            val checkedChipId = binding!!.membersChipGroup.checkedChipId
            val year = binding!!.membersChipGroup.findViewById<Chip>(checkedChipId).text.toString()

            viewModel.setYear(year)
        }
    }

    fun setFilterMembers(
        teamCategories: MutableList<String>,
        gender: String,
        function: String,
        country: String
    ) {

        rvAdapter.setFilterMembers(teamCategories, gender, function, country)
        rvAdapter.notifyDataSetChanged()

        if (rvAdapter.itemCount == 0) {

            // UI visibility
            hideView(binding!!.membersRecyclerView)
            hideView(binding!!.membersProgressBar)
            hideView(binding!!.noInternetMembersLinearLayout)
            showView(binding!!.noAvailableMembersTextView)

        } else {

            // UI visibility
            hideView(binding!!.noAvailableMembersTextView)
            hideView(binding!!.membersProgressBar)
            hideView(binding!!.noInternetMembersLinearLayout)
            showView(binding!!.membersRecyclerView)
        }
    }

    private fun setObservers() {

        viewModel.members.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {

                    it.data?.let { response ->

                        rvAdapter.setDefaultMembers(response.body())
                        rvAdapter.notifyDataSetChanged()

                    }

                    // UI visibility
                    hideView(binding!!.noAvailableMembersTextView)
                    hideView(binding!!.membersProgressBar)
                    hideView(binding!!.noInternetMembersLinearLayout)
                    showView(binding!!.membersRecyclerView)

                    // Filter FAB clickable, when success only
                    (activity as MainActivity).enableClickableFab()

                    Log.i("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.ERROR -> {

                    // UI visibility
                    hideView(binding!!.noAvailableMembersTextView)
                    hideView(binding!!.membersProgressBar)
                    hideView(binding!!.membersRecyclerView)
                    showView(binding!!.noInternetMembersLinearLayout)

                    Log.e("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.LOADING -> {

                    // UI visibility
                    hideView(binding!!.noAvailableMembersTextView)
                    hideView(binding!!.membersRecyclerView)
                    hideView(binding!!.noInternetMembersLinearLayout)
                    showView(binding!!.membersProgressBar)

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

        fun newInstance(groupType: String, raceType: String) = MembersViewPagerFragment().apply {
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