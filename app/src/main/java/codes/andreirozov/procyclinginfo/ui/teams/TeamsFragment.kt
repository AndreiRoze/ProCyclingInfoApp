package codes.andreirozov.procyclinginfo.ui.teams

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import codes.andreirozov.procyclinginfo.utils.ViewPagerFragmentAdapter
import codes.andreirozov.procyclinginfo.databinding.FragmentTeamsBinding
import codes.andreirozov.procyclinginfo.ui.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class TeamsFragment : Fragment() {

    private var fragmentTeamsBinding: FragmentTeamsBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Binding
        val binding = FragmentTeamsBinding.inflate(inflater, container, false)
        fragmentTeamsBinding = binding

        setUI(binding)

        // Clear filer, when change member's race type
        binding.viewPagerFragmentTeams.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                (activity as MainActivity).clearTeamsFilter()
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                (activity as MainActivity).clearTeamsFilter()
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                (activity as MainActivity).clearTeamsFilter()
                super.onPageScrollStateChanged(state)
            }
        })

        return binding.root
    }

    private fun setUI(binding: FragmentTeamsBinding) {

        val viewPagerFragmentAdapter =
                ViewPagerFragmentAdapter(childFragmentManager, viewLifecycleOwner.lifecycle, 3, "teams")

        binding.viewPagerFragmentTeams.apply {
            offscreenPageLimit = 2
            adapter = viewPagerFragmentAdapter
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        TabLayoutMediator(
                binding.tabLayoutFragmentTeams,
                binding.viewPagerFragmentTeams
        ) { tab, position ->
            tab.text = when (position) {
                0 -> "Road"
                1 -> "MTB"
                2 -> "Cyclo-cross"
                else -> "Road"
            }
        }.attach()
    }

    // Use filter for RecyclerView from FAB button
    fun setFilterTeams(teamCategories: MutableList<String>, country: String) {
        (childFragmentManager.findFragmentByTag("f" + fragmentTeamsBinding?.viewPagerFragmentTeams?.currentItem) as TeamsViewPagerFragment).setFilterTeams(teamCategories, country)
    }

    override fun onDestroyView() {
        fragmentTeamsBinding = null
        super.onDestroyView()
    }
}