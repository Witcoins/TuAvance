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

class ObjetivosAdapter(val contexto:Context, val lista:MutableList<Objetivo>, val correo:String, private val listener:OnItemClickListener)
    : RecyclerView.Adapter<ObjetivosAdapter.CuentosHolder>() {

    private val sharedViewModel = ViewModel()

    //Crea cada una de las vistas del elemento, según el diseño del layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuentosHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_objetivo, parent, false)
        return CuentosHolder(adapterLayout)
    }

    //Cuenta los elementos para saber cuántos poner
    override fun getItemCount(): Int = lista.size


    //Cuando ya está en la vista toma el holder y pone los duendes uno por uno al layout creado
    override fun onBindViewHolder(holder: CuentosHolder, position: Int) {
        val objetivo = lista[position]
        holder.objetivo.text = objetivo.objetivo
        holder.fecha.text = objetivo.fecha
        holder.progreso.max = objetivo.total
        holder.progreso.progress = objetivo.chuleadas

        if(objetivo.total==objetivo.chuleadas){
            holder.objetivo.setBackgroundColor(contexto.resources.getColor(R.color.green))
            holder.objetivo.setTextColor(contexto.resources.getColor(R.color.black))
        }

        sharedViewModel.apply {
            val dia = objetivo.fecha.split("/").component3()
            val mes = objetivo.fecha.split("/").component2()

            Log.i("prueba","Adapter Objetivo: diaHoy - $hoyDia mesHoy - $hoyMes diaObj - $dia mesObj - $mes")

            if (mes.toInt().toString() == hoyMes.toString()){
                if(dia.toInt().toString() == (hoyDia+1).toString()){
                    holder.objetivo.setBackgroundColor(contexto.resources.getColor(R.color.amarillo))
                    holder.objetivo.setTextColor(contexto.resources.getColor(R.color.black))
                } else if(dia.toInt().toString() == hoyDia.toString()){
                    holder.objetivo.setBackgroundColor(contexto.resources.getColor(R.color.red))
                }
            }
        }

        holder.delete.setOnClickListener {
            sharedViewModel.apply {
                db.collection(usuario!!.email!!)
                    .whereEqualTo("objetivo",objetivo.objetivo)
                    .get().addOnCompleteListener {
                        var idDocumento = ""
                        for(elemento in it.result.documents){
                            idDocumento = elemento.id
                        }
                        db.collection(usuario.email!!)
                            .document(idDocumento)
                            .delete()
                    }
            }
        }

        holder.edita.setOnClickListener {
            sharedViewModel.editar = true
           //Sale el diálogo de edición
            dialogoReferido(objetivo.objetivo,objetivo.fecha)
        }
    }

    inner class CuentosHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val objetivo = view.findViewById<TextView>(R.id.objetivo)
        val fecha =  view.findViewById<TextView>(R.id.fecha)
        val edita =  view.findViewById<ImageView>(R.id.edita)
        val delete = view.findViewById<ImageView>(R.id.delete)
        var progreso = view.findViewById<ProgressBar>(R.id.progressBar2)

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

    private fun dialogoReferido(obj:String,date:String) {
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
        dialogo.findViewById<EditText>(R.id.objetivo).setText(obj, TextView.BufferType.EDITABLE)
        dialogo.findViewById<EditText>(R.id.fecha).setText(date, TextView.BufferType.EDITABLE)
        dialogo.findViewById<Button>(R.id.boton_actualizar).setOnClickListener {
            val et_objetivo = dialogo.findViewById<EditText>(R.id.objetivo)
            val et_fecha = dialogo.findViewById<EditText>(R.id.fecha)
            Log.i("prueba","En el adapter: Objetivo - $obj, Fecha - $date")
            if(et_objetivo.text.toString()!="" && et_fecha.text.toString()!=""){
                //Siempre edita
                    sharedViewModel.edicion(correo,et_objetivo.text.toString(),"objetivo",obj)
                    sharedViewModel.edicion(correo,et_fecha.text.toString(),"fecha",date)
                dialogo.dismiss()
            } else {
                Toast.makeText(contexto,"Completa la info", Toast.LENGTH_LONG).show()
            }
        }
        dialogo.show()
    }
}