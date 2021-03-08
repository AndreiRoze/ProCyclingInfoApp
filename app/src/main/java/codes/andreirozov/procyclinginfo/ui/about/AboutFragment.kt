package codes.andreirozov.procyclinginfo.ui.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import codes.andreirozov.procyclinginfo.BuildConfig
import codes.andreirozov.procyclinginfo.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var fragmentAboutBinding: FragmentAboutBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        //Binding
        val binding = FragmentAboutBinding.inflate(inflater, container, false)
        fragmentAboutBinding = binding

        binding.versionTextViewFragmentAbout.text = BuildConfig.VERSION_NAME

        return binding.root
    }

    override fun onDestroyView() {
        fragmentAboutBinding = null
        super.onDestroyView()
    }
}