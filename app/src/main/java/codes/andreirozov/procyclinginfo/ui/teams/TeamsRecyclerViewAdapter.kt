package codes.andreirozov.procyclinginfo.ui.teams

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import codes.andreirozov.procyclinginfo.R
import codes.andreirozov.procyclinginfo.data.model.Team
import codes.andreirozov.procyclinginfo.databinding.TeamsRvItemBinding

class TeamsRecyclerViewAdapter : RecyclerView.Adapter<TeamsRecyclerViewAdapter.ItemViewHolder>() {

    // Untouchable list with all teams, use it when clear filter
    private var allTeams: List<Team> = listOf()

    // list with teams for display
    private var teams: List<Team> = listOf()

    private var countryCode = mapOf<String, String>()

    class ItemViewHolder(var binding: TeamsRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val binding = TeamsRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val countries = parent.resources.getStringArray(R.array.country_array)
        val codes = parent.resources.getStringArray(R.array.country_code_array)

        countryCode = countries.zip(codes).toMap()

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.binding.nameTeamsTextView.text = teams[position].teamName

        try {

            // Set flag
            val imgStream = holder.binding.flagTeamsImageView.resources.assets.open("flags/" + teams[position].country + ".png")
            val image = Drawable.createFromStream(imgStream, null)
            holder.binding.flagTeamsImageView.setImageDrawable(image)

        } catch (exception: Exception) {

            Log.e("Teams RV unknown flag", teams[position].country)

            // Set basic image for items with incorrect flag's name
            holder.binding.flagTeamsImageView.setImageResource(R.drawable.question)

        }
    }

    override fun getItemCount(): Int {
        return teams.size
    }

    fun setFilterTeams(teamCategories: MutableList<String>, country: String) {

        var filteredTeams = allTeams

        if (teamCategories.isNotEmpty()) filteredTeams = filteredTeams.filter { team -> teamCategories.contains(team.teamCategory) }

        val code = getCodeByName(country)
        if (code != "All") filteredTeams = filteredTeams.filter { team -> team.country == code }

        setTeams(filteredTeams)
    }

    private fun getCodeByName(country: String): String {

        return countryCode.getOrElse(country, { "All" })

    }

    fun setTeams(teams: List<Team>?) {

        if (teams != null) this.teams = teams

    }

    fun setDefaultTeams(teams: List<Team>?) {

        if (teams != null) {
            this.teams = teams
            allTeams = teams
        }

    }
}