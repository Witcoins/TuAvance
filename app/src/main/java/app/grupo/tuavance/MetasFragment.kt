package app.grupo.tuavance

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import app.grupo.tuavance.databinding.FragmentMetasBinding
import app.grupo.tuavance.model.*
import com.google.firebase.auth.FirebaseAuth

class MetasFragment : Fragment(), ObjetivosAdapter.OnItemClickListener {

    private val sharedViewModel: ViewModel by activityViewModels()
    private var _binding: FragmentMetasBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogo: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentMetasBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cerrarSesion.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                showIntro()
            }
            agregar.setOnClickListener {
                dialogoReferido()
            }
        }
    }

    private fun dialogoReferido() {
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
        dialogo.findViewById<Button>(R.id.boton_actualizar).setOnClickListener {
            val et_objetivo = dialogo.findViewById<EditText>(R.id.objetivo)
            val et_fecha = dialogo.findViewById<EditText>(R.id.fecha)
           if(et_objetivo.text.toString()!="" && et_fecha.text.toString()!=""){
               //Siempre escribe
                   sharedViewModel.escribirObjetivo(et_objetivo.text.toString(),et_fecha.text.toString())
               dialogo.dismiss()
           } else {
               Toast.makeText(requireContext(),"Completa la info",Toast.LENGTH_LONG).show()
           }
        }
        dialogo.show()
    }

    private fun showIntro(){
        val intent = Intent(requireContext(), IntroActivity::class.java)
        this.startActivity(intent)
    }

    private fun cargarDatos() {
        sharedViewModel.apply {
            Log.i("prueba","Pasa por cargarDatos")
            db.collection(usuario!!.email!!)
                .addSnapshotListener { value, error ->
                    if(value!=null && error==null){
                        //Cargar lista objetivos
                        listaObjetivos = value.toObjects(Objetivo::class.java)
                        for((index,elemento) in listaObjetivos.withIndex()){
                            if(elemento.objetivo == ""){
                                listaObjetivos.removeAt(index)
                            }
                        }
                        //Ordenar la lista por fechas de pronto a tarde
                        listaObjetivos = listaObjetivos.sortedBy { it.fecha }.toMutableList()
                        binding.rvTareas.adapter = ObjetivosAdapter(requireContext(), listaObjetivos,
                            usuario.email!!,this@MetasFragment)
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    override fun onItemClick(position: Int) {
        //Busca la lista de tareas del objetivo escogido y muestra la pantalla siguiente
        sharedViewModel.apply{
            dataObjetivo = listaObjetivos[position]
            db.collection(usuario!!.email!!)
                .whereEqualTo("objetivo",dataObjetivo.objetivo)
                .get()
                .addOnCompleteListener {
                    for(tarea in it.result.documents){
                        idObjetivo = tarea.id
                    }
                    findNavController().navigate(R.id.action_metasFragment_to_objetivoFragment)
                }
        }
    }

}