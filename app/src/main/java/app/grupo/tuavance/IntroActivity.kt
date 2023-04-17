package app.grupo.tuavance

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import app.grupo.tuavance.databinding.ActivityIntroBinding
import app.grupo.tuavance.databinding.ActivityMainBinding
import app.grupo.tuavance.model.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class IntroActivity : AppCompatActivity() {

    private var _binding: ActivityIntroBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ViewModel by viewModels()
    private var dialogo: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.entrar.setOnClickListener { verificarCorreo()  }
    }

    private fun verificarCorreo() { //Iniciar sesión con su correo y contraseña
        if (binding.email.text!!.isNotEmpty() && binding.contrasena.text!!.isNotEmpty()) {
            sharedViewModel.email = binding.email.text.toString().trim()
            sharedViewModel.password = binding.contrasena.text.toString()
            if (sharedViewModel.validarCorreo(
                    sharedViewModel.email,
                    binding.email
                )&& sharedViewModel.confirmacion(binding.contrasena)
            ) {//Si el correo que ingresaron es válido y la contraseña es buena entonces intenta buscar en Firebase el usuario y contraseña
                dialogo()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    sharedViewModel.email,
                    sharedViewModel.password
                ).addOnSuccessListener {
                        showHome()
                        dialogo?.dismiss()
                    }.addOnFailureListener {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        sharedViewModel.email,
                        sharedViewModel.password
                    ).addOnSuccessListener {
                        //Si no estaba registrado entonces ya inicia sesión
                        sharedViewModel.db.collection(sharedViewModel.email)
                            .document("info")
                            .set(
                                hashMapOf(
                                    "contraseña" to sharedViewModel.password
                                )
                            )
                        showHome()
                        dialogo?.dismiss()
                    }.addOnFailureListener {
                        Toast.makeText(this,"Correo y contraseña no coinciden", Toast.LENGTH_LONG).show()
                        dialogo?.dismiss()
                        Log.i("prueba","Error: $it")
                    }
                }
            } else {
                binding.email.error = "Ingrese un correo válido"
            }
        } else {
            binding.email.error = "Ingrese su correo y su contraseña"
        }
    }

    private fun showHome() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }
    private fun dialogo() {
        dialogo = MaterialAlertDialogBuilder(this)
            .setBackground(resources.getDrawable(R.color.colorTransparent2))
            .setMessage("Espera un momento")
            .setCancelable(false)
            .create()
        dialogo?.show()
    }

    override fun onResume() {
        super.onResume()
        val callback = onBackPressedDispatcher.addCallback {  }
        callback.isEnabled
    }
}