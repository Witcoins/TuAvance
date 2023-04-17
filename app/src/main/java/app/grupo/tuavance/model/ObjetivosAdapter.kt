package app.grupo.tuavance.model

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
           //¿Cómo hacer que salga un diálogo?
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
            edita.setOnClickListener(this)
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
}