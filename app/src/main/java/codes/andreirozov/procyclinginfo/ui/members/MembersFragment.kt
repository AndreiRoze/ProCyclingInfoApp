package codes.andreirozov.procyclinginfo.ui.members

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import codes.andreirozov.procyclinginfo.utils.ViewPagerFragmentAdapter
import codes.andreirozov.procyclinginfo.databinding.FragmentMembersBinding
import codes.andreirozov.procyclinginfo.ui.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class MembersFragment : Fragment() {

    private var fragmentMembersBinding: FragmentMembersBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        //Binding
        val binding = FragmentMembersBinding.inflate(inflater, container, false)
        fragmentMembersBinding = binding

        setUI(binding)

        // Clear filer, when change member's race type
        binding.viewPagerFragmentMembers.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                (activity as MainActivity).clearMembersFilter()
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                (activity as MainActivity).clearMembersFilter()
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                (activity as MainActivity).clearMembersFilter()
                super.onPageScrollStateChanged(state)
            }
        })

        return binding.root
    }

    private fun setUI(binding: FragmentMembersBinding) {

        val viewPagerFragmentAdapter =
                ViewPagerFragmentAdapter(childFragmentManager, viewLifecycleOwner.lifecycle, 3, "members")

        binding.viewPagerFragmentMembers.apply {
            offscreenPageLimit = 2
            adapter = viewPagerFragmentAdapter
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        TabLayoutMediator(
                binding.tabLayoutFragmentMembers,
                binding.viewPagerFragmentMembers
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
    fun setFilterMembers(teamCategories: MutableList<String>, gender: String, function: String, country: String) {
        (childFragmentManager.findFragmentByTag("f" + fragmentMembersBinding?.viewPagerFragmentMembers?.currentItem) as MembersViewPagerFragment).setFilterMembers(teamCategories, gender, function, country)
    }

    override fun onDestroyView() {
        fragmentMembersBinding = null
        super.onDestroyView()
    }
}