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
import com.google.android.material.chip.Chip

class MembersViewPagerFragment : Fragment() {

    private var fragmentOfMembersViewPagerBinding: FragmentOfMembersViewPagerBinding? = null

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
        val binding = FragmentOfMembersViewPagerBinding.inflate(layoutInflater)
        fragmentOfMembersViewPagerBinding = binding

        setUI(binding)

        setObservers()

        return binding.root
    }

    private fun setUI(binding: FragmentOfMembersViewPagerBinding) {

        // Create recyclerView adapter
        rvAdapter = MembersRecyclerViewAdapter()

        // Set RecyclerView
        binding.membersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        // Fill ChipGroup with years
        if (raceType == "cyclocross") {
            val list = listOf("2017-2018", "2018-2019", "2019-2020", "2020-2021")

            for (i in list) {
                val chip = Chip(context, null, R.attr.customChipChoiceStyle)
                chip.text = i
                binding.membersChipGroup.addView(chip)
            }

        } else {

            for (i in 2005..2020) {
                val chip = Chip(context, null, R.attr.customChipChoiceStyle)
                chip.text = i.toString()
                binding.membersChipGroup.addView(chip)
            }
        }

        // ChipGroup checked change listener
        binding.membersChipGroup.setOnCheckedChangeListener { chipGroup, chipId ->
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
        (binding.membersChipGroup[0] as Chip).isChecked = true

        binding.retryMembersButton.setOnClickListener {

            // Get text from checked Chip
            val checkedChipId = binding.membersChipGroup.checkedChipId
            val year = binding.membersChipGroup.findViewById<Chip>(checkedChipId).text.toString()

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
            goneMembersRecyclerView()
            goneMembersProgressBar()
            goneNoInternetMembersLinearLayout()
            visibleNoAvailableMembersTextView()

        } else {

            // UI visibility
            goneNoAvailableMembersTextView()
            goneMembersProgressBar()
            goneNoInternetMembersLinearLayout()
            visibleMembersRecyclerView()
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
                    goneNoAvailableMembersTextView()
                    goneMembersProgressBar()
                    goneNoInternetMembersLinearLayout()
                    visibleMembersRecyclerView()

                    // Filter FAB clickable, when success only
                    (activity as MainActivity).enableClickableFab()

                    Log.e("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.ERROR -> {

                    // UI visibility
                    goneNoAvailableMembersTextView()
                    goneMembersProgressBar()
                    goneMembersRecyclerView()
                    visibleNoInternetMembersLinearLayout()

                    Log.e("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.LOADING -> {

                    // UI visibility
                    goneNoAvailableMembersTextView()
                    goneMembersRecyclerView()
                    goneNoInternetMembersLinearLayout()
                    visibleMembersProgressBar()

                    Log.e("Data status", "${it.message!!} $raceType")

                }
                Resource.Status.SERVER_ERROR -> {

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
        fragmentOfMembersViewPagerBinding = null
        super.onDestroyView()
    }

    //------------------------------ UI VISIBILITY FUNCTIONS BELOW ------------------------------

    private fun goneNoAvailableMembersTextView() {
        fragmentOfMembersViewPagerBinding?.noAvailableMembersTextView?.visibility = View.GONE
    }

    private fun visibleNoAvailableMembersTextView() {
        fragmentOfMembersViewPagerBinding?.noAvailableMembersTextView?.visibility = View.VISIBLE
    }

    private fun goneMembersRecyclerView() {
        fragmentOfMembersViewPagerBinding?.membersRecyclerView?.visibility = View.GONE
    }

    private fun visibleMembersRecyclerView() {
        fragmentOfMembersViewPagerBinding?.membersRecyclerView?.visibility = View.VISIBLE
    }

    private fun goneMembersProgressBar() {
        fragmentOfMembersViewPagerBinding?.membersProgressBar?.visibility = View.GONE
    }

    private fun visibleMembersProgressBar() {
        fragmentOfMembersViewPagerBinding?.membersProgressBar?.visibility = View.VISIBLE
    }

    private fun goneNoInternetMembersLinearLayout() {
        fragmentOfMembersViewPagerBinding?.noInternetMembersLinearLayout?.visibility = View.GONE
    }

    private fun visibleNoInternetMembersLinearLayout() {
        fragmentOfMembersViewPagerBinding?.noInternetMembersLinearLayout?.visibility = View.VISIBLE
    }
}