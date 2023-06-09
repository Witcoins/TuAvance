package app.grupo.tuavance.model

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

        sharedViewModel.apply {
            if (mes.toInt() == hoyMes) {
                if (dia.toInt() == hoyDia+1) {
                    holder.tarea.setBackgroundColor(contexto.resources.getColor(R.color.amarillo))
                    holder.tarea.setTextColor(contexto.resources.getColor(R.color.black))
                } else if (dia.toInt() == hoyDia) {
                    holder.tarea.setBackgroundColor(contexto.resources.getColor(R.color.red))
                }
            } else if(mes.toInt() < hoyMes){
                holder.tarea.setBackgroundColor(contexto.resources.getColor(R.color.red))
            } else {
                holder.tarea.setBackgroundColor(contexto.resources.getColor(R.color.marron))
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
                        .update("total",objetivo.total-1,"chuleadas",objetivo.chuleadas-1)
                }
        }
        holder.check.setOnClickListener {
            sharedViewModel.db.collection(correo)
                .document(id)
                .collection("Tareas")
                .whereEqualTo("descripcion",cuento.descripcion)
                .get().addOnCompleteListener {
                    var idDocumento = "0"
                    var valor = true
                    var sumrest = 1
                    for(tarea in it.result.documents){
                        idDocumento = tarea.id
                        if(tarea.toObject(Tarea::class.java)!!.chuleada){
                            valor = false
                            sumrest = -1
                        }
                    }
                    sharedViewModel.db.collection(correo)
                        .document(id)
                        .collection("Tareas")
                        .document(idDocumento)
                        .update("chuleada",valor)
                    sharedViewModel.db.collection(correo)
                        .document(id)
                        .update("chuleadas",objetivo.chuleadas+sumrest)
                }
        }
        holder.edita.setOnClickListener {
            dialogoEditar(cuento)
        }

        if(cuento.chuleada){
            holder.tarea.setBackgroundColor(contexto.resources.getColor(R.color.green))
            holder.tarea.setTextColor(contexto.resources.getColor(R.color.black))
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
        val dia = actividad.fecha.split("/").component3().toInt()
        val mes = actividad.fecha.split("/").component2().toInt()
        val year = actividad.fecha.split("/").component1().toInt()
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
        dialogo.findViewById<DatePicker>(R.id.fecha).init(year,mes-1,dia,null)
        dialogo.findViewById<Button>(R.id.boton_actualizar).setOnClickListener {
            val et_descripcion = dialogo.findViewById<EditText>(R.id.objetivo)
            val et_fecha = dialogo.findViewById<DatePicker>(R.id.fecha)
            val fecha = "${et_fecha.year}/${et_fecha.month+1}/${et_fecha.dayOfMonth}"
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
                    .update("descripcion",et_descripcion.text.toString(),"fecha",fecha)
                dialogo.dismiss()
            }
        }
        dialogo.show()
    }
}