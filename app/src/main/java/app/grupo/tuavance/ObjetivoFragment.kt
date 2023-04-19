package app.grupo.tuavance

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import app.grupo.tuavance.databinding.FragmentObjetivoBinding
import app.grupo.tuavance.model.CuentosAdapter
import app.grupo.tuavance.model.ObjetivosAdapter
import app.grupo.tuavance.model.Tarea
import app.grupo.tuavance.model.ViewModel

class ObjetivoFragment : Fragment(), CuentosAdapter.OnItemClickListener {

    private val sharedViewModel: ViewModel by activityViewModels()
    private var _binding: FragmentObjetivoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentObjetivoBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            titulo.setOnClickListener { findNavController().navigate(R.id.action_objetivoFragment_to_metasFragment) }
            objetivo.text = sharedViewModel.dataObjetivo.objetivo
            fecha.text = sharedViewModel.dataObjetivo.fecha
            agregar.setOnClickListener { dialogoTarea() }
            editar.setOnClickListener { dialogoEditar() }
        }

    }

    private fun cargarDatos(){
        //Según el objetivo del que venga, entonces mostrar la lista
        sharedViewModel.apply {
            db.collection(usuario!!.email!!)
                .document(idObjetivo)
                .collection("Tareas")
                .addSnapshotListener { value, error ->
                    if (value != null && error == null) {
                        dataObjetivo.chuleadas = 0
                        dataObjetivo.total = 0
                        listaTareas = value.toObjects(Tarea::class.java)
                        listaTareas = listaTareas.sortedBy { it.fecha }.toMutableList()
                        for(tarea in listaTareas){
                            dataObjetivo.total++
                            if(tarea.chuleada){
                                dataObjetivo.chuleadas++
                            }
                        }
                        binding.progressBar2.max = dataObjetivo.total
                        binding.progressBar2.progress = dataObjetivo.chuleadas
                        binding.rvTareas.adapter = CuentosAdapter(requireContext(),listaTareas,dataObjetivo,usuario.email!!,idObjetivo,this@ObjetivoFragment)
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
        val callback = requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(R.id.action_objetivoFragment_to_metasFragment)
        }
        callback.isEnabled
    }

    private fun dialogoEditar(){
        val dialogo = Dialog(requireContext())
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
        dialogo.findViewById<EditText>(R.id.objetivo).setText(sharedViewModel.dataObjetivo.objetivo, TextView.BufferType.EDITABLE)
        dialogo.findViewById<EditText>(R.id.fecha).setText(sharedViewModel.dataObjetivo.fecha, TextView.BufferType.EDITABLE)
        dialogo.findViewById<Button>(R.id.boton_actualizar).setOnClickListener {
            val et_objetivo = dialogo.findViewById<EditText>(R.id.objetivo)
            val et_fecha = dialogo.findViewById<EditText>(R.id.fecha)
            if(et_objetivo.text.toString()!="" && et_fecha.text.toString()!=""){
                //Siempre edita
                    sharedViewModel.edicion(sharedViewModel.usuario!!.email!!,et_objetivo.text.toString(),"objetivo",sharedViewModel.dataObjetivo.objetivo)
                    sharedViewModel.edicion(sharedViewModel.usuario!!.email!!,et_fecha.text.toString(),"fecha",sharedViewModel.dataObjetivo.fecha)
                binding.objetivo.text = et_objetivo.text.toString()
                binding.fecha.text = et_fecha.text.toString()
                dialogo.dismiss()
            } else {
                Toast.makeText(requireContext(), "Completa la info", Toast.LENGTH_LONG).show()
            }
        }
        dialogo.show()
    }

    private fun dialogoTarea(){
        val dialogo = Dialog(requireContext())
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
        dialogo.findViewById<Button>(R.id.boton_actualizar).setOnClickListener {
            val et_descripcion = dialogo.findViewById<EditText>(R.id.objetivo)
            val et_fecha = dialogo.findViewById<EditText>(R.id.fecha)
            if (et_descripcion.text.toString() != "" && et_fecha.text.toString() != "") {
                sharedViewModel.apply {
                    escribirTarea(et_descripcion.text.toString(), et_fecha.text.toString(),"")
                    db.collection(usuario!!.email!!)
                        .document(idObjetivo)
                        .update("total",dataObjetivo.total+1)
                    dataObjetivo.total++
                    dialogo.dismiss()
                }
            } else {
                Toast.makeText(requireContext(),"Completa la info",Toast.LENGTH_LONG).show()
            }
        }
        dialogo.show()
    }

    override fun onItemClick(position: Int) {
        dialogoComentario(position)
    }

    private fun dialogoComentario(posicion:Int) {
        val dialogo = Dialog(requireContext())
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
        dialogo.findViewById<TextView>(R.id.titulo).text = "COMENTARIO"
        dialogo.findViewById<EditText>(R.id.objetivo).setText(sharedViewModel.listaTareas[posicion].comentario,TextView.BufferType.EDITABLE)
        dialogo.findViewById<EditText>(R.id.fecha).isGone = true
        dialogo.findViewById<Button>(R.id.boton_actualizar).setOnClickListener {
            val et_coment = dialogo.findViewById<EditText>(R.id.objetivo)
            sharedViewModel.apply {
                //editar el comentario de la tarea
                db.collection(usuario!!.email!!)
                    .document(idObjetivo)
                    .collection("Tareas")
                    .document((sharedViewModel.listaTareas.indexOf(sharedViewModel.listaTareas[posicion])+1).toString())
                    .update("comentario",et_coment.text.toString())
                dialogo.dismiss()
            }
        }
        dialogo.show()
    }


}