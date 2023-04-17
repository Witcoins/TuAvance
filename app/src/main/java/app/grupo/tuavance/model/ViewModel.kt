package app.grupo.tuavance.model

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class ViewModel: ViewModel() {

    private lateinit var efecto: MediaPlayer

    val db = Firebase.firestore
    val usuario = FirebaseAuth.getInstance().currentUser
    var password = ""
    var email = ""

    var objetivo = "Escribe tu objetivo"
    var listaTareas:MutableList<Tarea> = mutableListOf()
    var actualizar = MutableLiveData(false)
    lateinit var prefs:SharedPreferences
    var chuleados = MutableLiveData(0)
    var fechaTarea = ""
    var descripcionTarea = ""
    var editar = false

    fun escribir(tarea:String, fecha:String){
        db.collection(objetivo)
            .document((listaTareas.size+1).toString())
            .set(
                hashMapOf(
                    "descripcion" to tarea,
                    "fecha" to fecha,
                    "chuleada" to false
                )
            )
    }

    fun edicion(campo:String, valor:String){
        db.collection(objetivo)
            .whereEqualTo("descripcion",descripcionTarea)
            .get().addOnCompleteListener {
                var idDocumento = "0"
                for(tarea in it.result.documents){
                    idDocumento = tarea.id
                }
                db.collection(objetivo)
                    .document(idDocumento)
                    .update(campo,valor)
            }
    }

    //Verificar si coincide lo que el niño escribe con el patrón de correo determinado
    fun validarCorreo(email:String, emailEditText: EditText) :Boolean{
        val emailValido = Patterns.EMAIL_ADDRESS
        return if(!emailValido.matcher(email).matches()){
            emailEditText.error = "Correo no válido"
            false
        } else true
    }

    //Crea los patrones de número, mayúscula y minúscula y muestra error si no cumple con las condiciones
    fun confirmacion(editText: EditText) :Boolean{
        return if(password.length < 6) {
            editText.error = "La constraseña debe contener al menos 6 caracteres"
            false
        }else true
    }

    fun iniciarConteo(seg: Long,num:Long, tick:List<()->Unit>, finish:List<()->Unit>){
        val timer = object: CountDownTimer(seg,num){
            override fun onTick(millisUntilFinished: Long) {
                for (element in tick){
                    element()
                }
            }

            override fun onFinish() {
                for (element in finish){
                    element()
                }
            }
        }
        timer.start()
    }

    fun iniciarGif(contexto:Context, gif:Int,image: ImageView){
        Glide.with(contexto)
            .asGif()
            .load(gif)
            .into(image)
    }

    fun cargarImagen(contexto:Context,url:String,image: ImageView){
        Glide.with(contexto)
            .load(url)
            .skipMemoryCache(true)
            .into(image)
            .clearOnDetach()
    }

    fun iniciarEfecto(contexto: Context, sonido: Int, loop: Boolean) {
        efecto = MediaPlayer.create(contexto, sonido)
        efecto.isLooping = loop
        efecto.start()
    }
}