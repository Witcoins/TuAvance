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
import java.util.*
import java.util.regex.Pattern

class ViewModel: ViewModel() {

    private lateinit var efecto: MediaPlayer

    val db = Firebase.firestore
    val usuario = FirebaseAuth.getInstance().currentUser
    var password = ""
    var email = ""

    var calendar: Calendar = Calendar.getInstance()
    val hoyDia = calendar[Calendar.DAY_OF_MONTH]
    val hoyMes = calendar[Calendar.MONTH]+1

    var listaObjetivos:MutableList<Objetivo> = mutableListOf()
    var objetivo = "Escribe tu objetivo"
    lateinit var dataObjetivo:Objetivo
    var idObjetivo = ""
    var listaTareas:MutableList<Tarea> = mutableListOf()
    var listaIds:MutableList<String> = mutableListOf()
    var editar = false

    fun escribirTarea(tarea:String, fecha:String, coment:String, id:String){
        db.collection(usuario!!.email!!)
            .document(idObjetivo)
            .collection("Tareas")
            .document(id)
            .set(
                hashMapOf(
                    "descripcion" to tarea,
                    "fecha" to fecha,
                    "chuleada" to false,
                    "comentario" to coment
                )
            )
    }

    fun escribirObjetivo(objetivo:String, fecha:String,id:String){
        db.collection(usuario!!.email!!)
            .document(id)
            .set(
                hashMapOf(
                    "objetivo" to objetivo,
                    "fecha" to fecha,
                    "chuleadas" to 0,
                    "total" to 0
                )
            )
    }

    fun edicion(correo:String, valor:String, field:String, buscar:String){
        db.collection(correo)
            .whereEqualTo(field,buscar)
            .get().addOnCompleteListener {
                var idDocumento = "0"
                for(tarea in it.result.documents){
                    idDocumento = tarea.id
                }
                db.collection(correo)
                    .document(idDocumento)
                    .update(field,valor)
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