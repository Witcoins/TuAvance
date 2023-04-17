package app.grupo.tuavance

import android.content.Context
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import app.grupo.tuavance.databinding.ActivityMainBinding
import app.grupo.tuavance.model.CuentosAdapter
import app.grupo.tuavance.model.Tarea
import app.grupo.tuavance.model.ViewModel


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel:ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel.prefs = getSharedPreferences("preferencias_audio", Context.MODE_PRIVATE)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment
        val navController = navHostFragment.navController
        navController.enableOnBackPressed(false)
        overridePendingTransition(R.anim.nav_default_enter_anim,R.anim.nav_default_exit_anim)
    }

    override fun onResume() {
        super.onResume()
        val callback = onBackPressedDispatcher.addCallback {  }
        callback.isEnabled
    }
}