package codes.andreirozov.procyclinginfo.ui.teams

import androidx.lifecycle.*
import codes.andreirozov.procyclinginfo.data.repository.TeamRepository
import codes.andreirozov.procyclinginfo.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class TeamsViewModel(
    private val teamRepository: TeamRepository,
    private val raceType: String
) : ViewModel() {

    private val year: MutableLiveData<String> = MutableLiveData()

    val teams = year.switchMap {
        liveData(Dispatchers.IO) {

            emit(Resource.loading(null))

            try {

                val data = teamRepository.getTeams(raceType, it)
                if (data.isSuccessful) {
                    emit(Resource.success(data))
                } else emit(Resource.serverError(data))

            } catch (exception: Exception) {
                emit(Resource.error(null, exception.message ?: "Error occurred."))
            }
        }
    }

    fun setYear(year: String) {
        this.year.value = year
    }

}