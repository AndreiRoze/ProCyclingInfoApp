package codes.andreirozov.procyclinginfo.ui

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import codes.andreirozov.procyclinginfo.R
import codes.andreirozov.procyclinginfo.databinding.ActivityMainBinding
import codes.andreirozov.procyclinginfo.ui.about.AboutFragment
import codes.andreirozov.procyclinginfo.ui.members.MembersFragment
import codes.andreirozov.procyclinginfo.ui.teams.TeamsFragment
import com.google.android.material.chip.Chip

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val membersFragment = MembersFragment()
    private val teamsFragment = TeamsFragment()
    private val aboutFragment = AboutFragment()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = membersFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        setUI()

        // Show filter card
        binding.filterFabActivityMain.setOnClickListener {

            when (binding.bottomNavigation.selectedItemId) {
                // Show filter-card for members
                R.id.membersFragment -> binding.motionLayoutActivityMain.transitionToState(R.id.endMembers)
                // Show filter-card for teams
                R.id.teamsFragment -> binding.motionLayoutActivityMain.transitionToState(R.id.endTeams)
            }
        }

        // Catch touch on members filter-card
        binding.filterMembersCardActivityMain.setOnClickListener {

        }

        // Catch touch on teams filter-card
        binding.filterTeamsCardActivityMain.setOnClickListener {

        }

        // Apply filter for members
        binding.contentFilterMembersCardActivityMain.applyFilterMembersButtonActivityMain.setOnClickListener {

            // Get strings of team categories from ChipGroup
            val chips =
                binding.contentFilterMembersCardActivityMain.teamCategoryFilterMembersChipGroupActivityMain.checkedChipIds
            val teamCategories = mutableListOf<String>()
            for (chip in chips) {
                teamCategories.add(findViewById<Chip>(chip).text.toString())
            }

            val gender =
                findViewById<RadioButton>(binding.contentFilterMembersCardActivityMain.genderFilterMembersRadioGroupActivityMain.checkedRadioButtonId).text.toString()
            val function =
                binding.contentFilterMembersCardActivityMain.functionFilterMembersAutoCompleteTextViewActivityMain.text.toString()
            val country =
                binding.contentFilterMembersCardActivityMain.countryFilterMembersAutoCompleteTextViewActivityMain.text.toString()

            Log.e("Filter data", "$teamCategories $gender $function $country")

            //set filter for members
            (fragmentManager.findFragmentByTag("Members") as MembersFragment).setFilterMembers(
                teamCategories,
                gender,
                function,
                country
            )

            binding.motionLayoutActivityMain.transitionToStart()
        }

        // Apply filter for teams
        binding.contentFilterTeamsCardActivityMain.applyFilterTeamsButtonActivityMain.setOnClickListener {

            // Get strings of team categories from ChipGroup
            val chips =
                binding.contentFilterTeamsCardActivityMain.teamCategoryFilterTeamsChipGroupActivityMain.checkedChipIds
            val teamCategories = mutableListOf<String>()
            for (chip in chips) {
                teamCategories.add(findViewById<Chip>(chip).text.toString())
            }

            val country =
                binding.contentFilterTeamsCardActivityMain.countryFilterTeamsAutoCompleteTextViewActivityMain.text.toString()

            Log.e("Filter data", "$teamCategories $country")

            //set filter for teams
            (fragmentManager.findFragmentByTag("Teams") as TeamsFragment).setFilterTeams(
                teamCategories,
                country
            )

            binding.motionLayoutActivityMain.transitionToStart()
        }

        // Clear filter members
        binding.contentFilterMembersCardActivityMain.clearFilterMembersButtonActivityMain.setOnClickListener {

            clearMembersFilter()

            (fragmentManager.findFragmentByTag("Members") as MembersFragment).setFilterMembers(
                mutableListOf(),
                "Both",
                "All",
                "All"
            )
        }

        // Clear filter teams
        binding.contentFilterTeamsCardActivityMain.clearFilterTeamsButtonActivityMain.setOnClickListener {

            clearTeamsFilter()

            (fragmentManager.findFragmentByTag("Teams") as TeamsFragment).setFilterTeams(
                mutableListOf(),
                "All"
            )
        }
    }

    private fun setUI() {

        setBottomNavigation()

        // Set members card
        setTeamCategoryMembersChipGroup()
        setFunctionMembersSpinner()
        setCountryMembersSpinner()

        // Set teams card
        setTeamCategoryTeamsChipGroup()
        setCountryTeamsSpinner()
    }

    private fun setBottomNavigation() {

        fragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, membersFragment, getString(R.string.bottom_nav_members))
            add(R.id.fragmentContainer, teamsFragment, getString(R.string.bottom_nav_teams)).hide(
                teamsFragment
            )
            add(R.id.fragmentContainer, aboutFragment, getString(R.string.bottom_nav_about)).hide(
                aboutFragment
            )
        }.commit()

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.membersFragment -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(membersFragment)
                        .commit()
                    activeFragment = membersFragment

                    // Show fab when select membersFragment
                    binding.filterFabActivityMain.show()

                    true
                }
                R.id.teamsFragment -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(teamsFragment)
                        .commit()
                    activeFragment = teamsFragment

                    // Show fab when select teamsFragment
                    binding.filterFabActivityMain.show()

                    true
                }
                R.id.aboutFragment -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(aboutFragment)
                        .commit()
                    activeFragment = aboutFragment

                    // Hide fab when select aboutFragment
                    binding.filterFabActivityMain.hide()

                    true
                }
                else -> false
            }
        }
    }

    private fun setTeamCategoryMembersChipGroup() {

        val list =
            listOf("CRO", "CTM", "CTW", "E-MTB", "MTB", "P-CRO", "PCT", "PRO", "PRT", "WTT", "WTW")

        for (i in list) {
            val chip = Chip(this, null, R.attr.customChipChoiceStyle)
            chip.text = i
            binding.contentFilterMembersCardActivityMain.teamCategoryFilterMembersChipGroupActivityMain.addView(
                chip
            )
        }
    }

    private fun setFunctionMembersSpinner() {

        val functionSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.members_function_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        (binding.contentFilterMembersCardActivityMain.functionFilterMembersTextInputLayoutActivityMain.editText as? AutoCompleteTextView)?.setAdapter(
            functionSpinnerAdapter
        )
    }

    private fun setCountryMembersSpinner() {

        val countrySpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.country_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        (binding.contentFilterMembersCardActivityMain.countryFilterMembersTextInputLayoutActivityMain.editText as? AutoCompleteTextView)?.setAdapter(
            countrySpinnerAdapter
        )
    }

    private fun setTeamCategoryTeamsChipGroup() {

        val list =
            listOf("CRO", "CTM", "CTW", "E-MTB", "MTB", "P-CRO", "PCT", "PRO", "PRT", "WTT", "WTW")

        for (i in list) {
            val chip = Chip(this, null, R.attr.customChipChoiceStyle)
            chip.text = i
            binding.contentFilterTeamsCardActivityMain.teamCategoryFilterTeamsChipGroupActivityMain.addView(
                chip
            )
        }
    }

    private fun setCountryTeamsSpinner() {

        val countrySpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.country_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        (binding.contentFilterTeamsCardActivityMain.countryFilterTeamsTextInputLayoutActivityMain.editText as? AutoCompleteTextView)?.setAdapter(
            countrySpinnerAdapter
        )
    }

    fun clearMembersFilter() {

        // Clear ChipGroup
        binding.contentFilterMembersCardActivityMain.teamCategoryFilterMembersChipGroupActivityMain.clearCheck()

        // Clear Gender RadioGroup
        binding.contentFilterMembersCardActivityMain.genderFilterMembersRadioGroupActivityMain.check(
            R.id.bothFilterMembersRadioButtonActivityMain
        )

        // Clear function Spinner
        binding.contentFilterMembersCardActivityMain.functionFilterMembersAutoCompleteTextViewActivityMain.setText(
            getString(R.string.all),
            false
        )

        // Clear country Spinner
        binding.contentFilterMembersCardActivityMain.countryFilterMembersAutoCompleteTextViewActivityMain.setText(
            getString(R.string.all),
            false
        )
    }

    fun clearTeamsFilter() {

        // Clear ChipGroup
        binding.contentFilterTeamsCardActivityMain.teamCategoryFilterTeamsChipGroupActivityMain.clearCheck()

        // Clear country Spinner
        binding.contentFilterTeamsCardActivityMain.countryFilterTeamsAutoCompleteTextViewActivityMain.setText(
            getString(R.string.all),
            false
        )
    }

    fun disableClickableFab() {
        binding.filterFabActivityMain.isClickable = false
    }

    fun enableClickableFab() {
        binding.filterFabActivityMain.isClickable = true
    }

    override fun onBackPressed() {

        // Hide filter's card
        if (binding.motionLayoutActivityMain.currentState == R.id.endMembers || binding.motionLayoutActivityMain.currentState == R.id.endTeams) {
            binding.motionLayoutActivityMain.transitionToStart()
        } else {
            // Return to start fragment(MembersFragment), if current fragment is MembersFragment, then exit from app.
            if (activeFragment == membersFragment) {
                super.onBackPressed()
            } else {
                binding.bottomNavigation.selectedItemId = R.id.membersFragment
            }
        }
    }
}