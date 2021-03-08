package codes.andreirozov.procyclinginfo.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import codes.andreirozov.procyclinginfo.ui.members.MembersViewPagerFragment
import codes.andreirozov.procyclinginfo.ui.teams.TeamsViewPagerFragment

class ViewPagerFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val itemsCount: Int,
    private val groupType: String
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> if (groupType == "members") MembersViewPagerFragment.newInstance(
                groupType,
                "road"
            ) else TeamsViewPagerFragment.newInstance(groupType, "road")

            1 -> if (groupType == "members") MembersViewPagerFragment.newInstance(
                groupType,
                "mtb"
            ) else TeamsViewPagerFragment.newInstance(groupType, "mtb")

            2 -> if (groupType == "members") MembersViewPagerFragment.newInstance(
                groupType,
                "cyclocross"
            ) else TeamsViewPagerFragment.newInstance(groupType, "cyclocross")

            else -> if (groupType == "members") MembersViewPagerFragment.newInstance(
                groupType,
                "road"
            ) else TeamsViewPagerFragment.newInstance(groupType, "road")
        }
    }
}