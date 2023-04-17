package app.grupo.tuavance.model

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import app.grupo.tuavance.MainActivity
import app.grupo.tuavance.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CuentosAdapter(val contexto:Context, val lista:MutableList<Tarea>, val objetivo:String, private val listener:OnItemClickListener)
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
        if(cuento.chuleada){
            holder.cardView.setCardBackgroundColor(contexto.resources.getColor(R.color.green))
        }
        holder.delete.setOnClickListener {
            sharedViewModel.db.collection(objetivo)
                .whereEqualTo("descripcion",cuento.descripcion)
                .get().addOnCompleteListener {
                    var idDocumento = "0"
                    for(tarea in it.result.documents){
                        idDocumento = tarea.id
                    }
                    sharedViewModel.db.collection(objetivo)
                        .document(idDocumento)
                        .delete()
                }
        }
        holder.check.setOnClickListener {
            sharedViewModel.db.collection(objetivo)
                .whereEqualTo("descripcion",cuento.descripcion)
                .get().addOnCompleteListener {
                    var idDocumento = "0"
                    for(tarea in it.result.documents){
                        idDocumento = tarea.id
                    }
                    sharedViewModel.db.collection(objetivo)
                        .document(idDocumento)
                        .update("chuleada",true)
                }
            holder.cardView.setCardBackgroundColor(contexto.resources.getColor(R.color.green))
        }
        holder.equis.setOnClickListener {
            sharedViewModel.db.collection(objetivo)
                .whereEqualTo("descripcion",cuento.descripcion)
                .get().addOnCompleteListener {
                    var idDocumento = "0"
                    for(tarea in it.result.documents){
                        idDocumento = tarea.id
                    }
                    sharedViewModel.db.collection(objetivo)
                        .document(idDocumento)
                        .update("chuleada",false)
                }
            holder.cardView.setCardBackgroundColor(contexto.resources.getColor(R.color.red))
        }
    }

    inner class CuentosHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val cardView = view.findViewById<CardView>(R.id.card_tarea)
        val tarea = view.findViewById<TextView>(R.id.tarea)
        val fecha =  view.findViewById<TextView>(R.id.fecha)
        val check = view.findViewById<ImageView>(R.id.chulo)
        val equis =  view.findViewById<ImageView>(R.id.equis)
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
}