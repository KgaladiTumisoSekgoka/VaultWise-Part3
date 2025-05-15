package com.example.loginsignup

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import java.util.Locale

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
    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
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
            // Add your Help & Support preference click listener here:
            val helpSupportPref = findPreference<Preference>("help")
            helpSupportPref?.setOnPreferenceClickListener {
                val intent = Intent(requireContext(), HelpandSupport::class.java)
                startActivity(intent)
                true
            }
        }
        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key == "language") {
                val languageCode = sharedPreferences?.getString("language", "en") ?: "en"
                setLocale(languageCode)
                activity?.recreate()
            }
        }
        
        private fun setLocale(languageCode: String) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = resources.configuration
            config.setLocale(locale)

            requireContext().resources.updateConfiguration(
                config,
                requireContext().resources.displayMetrics
            )
        }
    }
}
