package app.grupo.tuavance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.activity.viewModels
import app.grupo.tuavance.model.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    private val sharedViewModel: ViewModel by viewModels()
    private lateinit var imagen:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        //FirebaseAuth.getInstance().signOut()
        imagen = findViewById(R.id.gif)
        sharedViewModel.iniciarGif(this,R.raw.louise_corriendo,imagen)
        sharedViewModel.iniciarConteo(1000,2000,listOf(),listOf {
            otraScreen()
        })
    }

    //Si no hay un usuario activo va a registrarse, si lo hay muestra el home
    private val otraScreen = fun() {
        if (sharedViewModel.usuario != null) {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        } else {
            val intent = Intent(this, IntroActivity::class.java)
            this.startActivity(intent)
        }
    }

}