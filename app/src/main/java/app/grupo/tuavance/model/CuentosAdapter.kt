package app.grupo.tuavance.model

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import app.grupo.tuavance.MainActivity
import app.grupo.tuavance.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CuentosAdapter(val contexto:Context, val lista:MutableList<Tarea>, val objetivo:Objetivo, val correo:String, val id:String, private val listener:OnItemClickListener)
    : RecyclerView.Adapter<CuentosAdapter.CuentosHolder>() {

    private val sharedViewModel = ViewModel()

    //Crea cada una de las vistas del elemento, según el diseño del layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuentosHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarea, parent, false)
        return CuentosHolder(adapterLayout)
    }

    //Cuenta los elementos para saber cuántos poner
    override fun getItemCount(): Int = lista.size


    //Cuando ya está en la vista toma el holder y pone los duendes uno por uno al layout creado
    override fun onBindViewHolder(holder: CuentosHolder, position: Int) {
        val cuento = lista[position]
        holder.tarea.text = cuento.descripcion
        holder.fecha.text = cuento.fecha

        val dia = cuento.fecha.split("/").component3()
        val mes = cuento.fecha.split("/").component2()

        if(cuento.chuleada){
            holder.tarea.setBackgroundColor(contexto.resources.getColor(R.color.green))
            holder.tarea.setTextColor(contexto.resources.getColor(R.color.black))
        }

        sharedViewModel.apply {
            Log.i("prueba","Adapter Tarea: diaHoy - $hoyDia mesHoy - $hoyMes diaObj - ${dia.toInt()} mesObj - ${mes.toInt()}")
            if (mes.toInt().toString() == hoyMes.toString()) {
                if (dia.toInt().toString() == (hoyDia+1).toString()) {
                    holder.tarea.setBackgroundColor(contexto.resources.getColor(R.color.amarillo))
                    holder.tarea.setTextColor(contexto.resources.getColor(R.color.black))
                } else if (dia.toInt().toString() == hoyDia.toString()) {
                    holder.tarea.setBackgroundColor(contexto.resources.getColor(R.color.red))
                }
            }
        }

        holder.delete.setOnClickListener {
            sharedViewModel.db.collection(correo)
                .document(id)
                .collection("Tareas")
                .whereEqualTo("descripcion",cuento.descripcion)
                .get().addOnCompleteListener {
                    var idDocumento = "0"
                    for(tarea in it.result.documents){
                        idDocumento = tarea.id
                    }
                    sharedViewModel.db.collection(correo)
                        .document(id)
                        .collection("Tareas")
                        .document(idDocumento)
                        .delete()
                    sharedViewModel.db.collection(correo)
                        .document(id)
                        .update("total",objetivo.total-1,"chuleadas",0)
                }
        }
        holder.check.setOnClickListener {
            sharedViewModel.db.collection(correo)
                .document(id)
                .collection("Tareas")
                .whereEqualTo("descripcion",cuento.descripcion)
                .get().addOnCompleteListener {
                    var idDocumento = "0"
                    for(tarea in it.result.documents){
                        idDocumento = tarea.id
                    }
                    sharedViewModel.db.collection(correo)
                        .document(id)
                        .collection("Tareas")
                        .document(idDocumento)
                        .update("chuleada",true)
                    sharedViewModel.db.collection(correo)
                        .document(id)
                        .update("chuleadas",objetivo.chuleadas+1)
                }
        }
        holder.edita.setOnClickListener {
            dialogoEditar(cuento)
        }
    }

    inner class CuentosHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val tarea = view.findViewById<TextView>(R.id.tarea)
        val fecha =  view.findViewById<TextView>(R.id.fecha)
        val check = view.findViewById<ImageView>(R.id.chulo)
        val edita =  view.findViewById<ImageView>(R.id.editar)
        val delete = view.findViewById<ImageView>(R.id.delete)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    //Interfaz que se inicializa en el constructor del fragmento que fija este adapter
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private fun dialogoEditar(actividad:Tarea){
        val dialogo = Dialog(contexto)
        dialogo.setContentView(R.layout.dialog_objetivo)
        dialogo.window?.setBackgroundDrawableResource(R.color.colorTransparent2)
        dialogo.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialogo.setCancelable(false)
        dialogo.findViewById<Button>(R.id.boton_cancelar).setOnClickListener {
            dialogo.dismiss()
        }
        dialogo.findViewById<TextView>(R.id.titulo).text = "TAREA"
        dialogo.findViewById<EditText>(R.id.objetivo).setText(actividad.descripcion, TextView.BufferType.EDITABLE)
        dialogo.findViewById<EditText>(R.id.fecha).setText(actividad.fecha, TextView.BufferType.EDITABLE)
        dialogo.findViewById<Button>(R.id.boton_actualizar).setOnClickListener {
            val et_descripcion = dialogo.findViewById<EditText>(R.id.objetivo)
            val et_fecha = dialogo.findViewById<EditText>(R.id.fecha)
        sharedViewModel.db.collection(correo)
            .document(id)
            .collection("Tareas")
            .whereEqualTo("descripcion",actividad.descripcion)
            .get().addOnCompleteListener {
                var idDocumento = "0"
                for(tarea in it.result.documents){
                    idDocumento = tarea.id
                }
                sharedViewModel.db.collection(correo)
                    .document(id)
                    .collection("Tareas")
                    .document(idDocumento)
                    .update("descripcion",et_descripcion.text.toString(),"fecha",et_fecha.text.toString())
                dialogo.dismiss()
            }
        }
        dialogo.show()
    }
}