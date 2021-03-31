package codes.andreirozov.procyclinginfo.ui.members

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import codes.andreirozov.procyclinginfo.R
import codes.andreirozov.procyclinginfo.data.model.Member
import codes.andreirozov.procyclinginfo.databinding.MembersRvItemBinding

class MembersRecyclerViewAdapter :
    RecyclerView.Adapter<MembersRecyclerViewAdapter.ItemViewHolder>() {

    // Untouchable list with all members, use it when clear filter
    private var allMembers: List<Member> = listOf()

    // list with members for display
    private var members: List<Member> = listOf()

    private var countryCode = mapOf<String, String>()

    class ItemViewHolder(var binding: MembersRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val binding =
            MembersRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val countries = parent.resources.getStringArray(R.array.country_array)
        val codes = parent.resources.getStringArray(R.array.country_code_array)

        countryCode = countries.zip(codes).toMap()

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.binding.lastNameMembersTextView.text = members[position].LastName
        holder.binding.nameMembersTextView.text = members[position].firstName

        try {

            // Set flag
            val imgStream =
                holder.binding.flagMembersImageView.resources.assets.open("flags/" + members[position].country + ".png")
            val image = Drawable.createFromStream(imgStream, null)
            holder.binding.flagMembersImageView.setImageDrawable(image)

        } catch (exception: Exception) {

            Log.e("Members RV unknown flag", members[position].country)

            // Set basic image for items with incorrect flag's name
            holder.binding.flagMembersImageView.setImageResource(R.drawable.question)
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    fun setFilterMembers(
        teamCategories: MutableList<String>,
        gender: String,
        function: String,
        country: String
    ) {

        var filteredMembers = allMembers

        if (teamCategories.isNotEmpty()) filteredMembers =
            filteredMembers.filter { member -> teamCategories.contains(member.teamCategory) }

        if (gender != "Both") filteredMembers =
            filteredMembers.filter { member -> member.gender == gender }

        if (function != "All") filteredMembers =
            filteredMembers.filter { member -> member.function == function }

        val code = getCodeByName(country)
        if (code != "All") filteredMembers =
            filteredMembers.filter { member -> member.country == code }

        setMembers(filteredMembers)
    }

    private fun getCodeByName(country: String): String {

        return countryCode.getOrElse(country, { "All" })
    }

    private fun setMembers(members: List<Member>?) {

        if (members != null) this.members = members
    }

    fun setDefaultMembers(members: List<Member>?) {

        if (members != null) {
            this.members = members
            allMembers = members
        }
    }
}