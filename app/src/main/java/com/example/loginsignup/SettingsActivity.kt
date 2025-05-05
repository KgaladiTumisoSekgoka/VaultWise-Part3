package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        // Load settings fragment
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.settings_container, SettingsFragment())
            }
        }

        // Back button functionality
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // Inner class for preference settings
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            // Profile preference click
            val profilePref = findPreference<Preference>("profile")
            profilePref?.isEnabled = true
            profilePref?.setOnPreferenceClickListener {
                val intent = Intent(requireContext(), Profile::class.java)
                startActivity(intent)
                true
            }

            // Dark mode toggle
            val darkModePref = findPreference<SwitchPreferenceCompat>("dark_mode")
            darkModePref?.setOnPreferenceChangeListener { _, newValue ->
                val isDarkMode = newValue as Boolean
                val mode = if (isDarkMode) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(mode)
                requireActivity().recreate()
                true
            }
        }
    }
}
