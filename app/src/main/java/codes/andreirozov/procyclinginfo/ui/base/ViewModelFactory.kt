package codes.andreirozov.procyclinginfo.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import codes.andreirozov.procyclinginfo.data.api.ApiHelper
import codes.andreirozov.procyclinginfo.data.repository.MemberRepository
import codes.andreirozov.procyclinginfo.data.repository.TeamRepository
import codes.andreirozov.procyclinginfo.ui.members.MembersViewModel
import codes.andreirozov.procyclinginfo.ui.teams.TeamsViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(
    private val apiHelper: ApiHelper,
    private val raceType: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MembersViewModel::class.java)) {
            return MembersViewModel(MemberRepository(apiHelper), raceType) as T
        }

        if (modelClass.isAssignableFrom(TeamsViewModel::class.java)) {
            return TeamsViewModel(TeamRepository(apiHelper), raceType) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }
}