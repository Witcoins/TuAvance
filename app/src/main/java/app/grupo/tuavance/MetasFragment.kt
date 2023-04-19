package app.grupo.tuavance

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import app.grupo.tuavance.databinding.FragmentMetasBinding
import app.grupo.tuavance.model.*
import com.google.firebase.auth.FirebaseAuth

class MetasFragment : Fragment(), ObjetivosAdapter.OnItemClickListener {

    private val sharedViewModel: ViewModel by activityViewModels()
    private var _binding: FragmentMetasBinding? = null
    private val binding get() = _binding!!

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
            val et_fecha = dialogo.findViewById<DatePicker>(R.id.fecha)
            val fecha = "${et_fecha.year}/${et_fecha.month+1}/${et_fecha.dayOfMonth}"
           if(et_objetivo.text.toString()!=""){
               //Siempre escribe
               val maxNumber = sharedViewModel.listaIds.map { it.toInt() }.maxByOrNull { it } ?: 0
                   sharedViewModel.escribirObjetivo(et_objetivo.text.toString(),fecha,(maxNumber+1).toString())
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
                        listaIds.clear()
                        //Cargar lista objetivos
                        listaObjetivos = value.toObjects(Objetivo::class.java)
                        for((index,elemento) in listaObjetivos.withIndex()){
                            listaIds.add(value.documents[index].id)
                            if(elemento.objetivo == ""){
                                listaObjetivos.removeAt(index)
                                listaIds.removeAt(index)
                            }
                        }
                        Log.i("prueba","Orden lista IDs antes:$listaIds")
                        //Ordenar la lista por fechas de pronto a tarde
                        val listaZip = listaIds.zip(listaObjetivos).sortedBy { it.second.fecha }.map { it.first to it.second }
                        listaObjetivos = listaZip.map{ it.second }.toMutableList()
                        listaIds = listaZip.map { it.first }.toMutableList()
                        Log.i("prueba","Orden lista IDs después:$listaIds")
                        binding.rvTareas.adapter = ObjetivosAdapter(requireContext(), listaObjetivos,
                            usuario.email!!,this@MetasFragment)
                        if(listaObjetivos.size>0){
                            binding.descripcion.text="Ahora ingresa a cada uno de los objetivos para completar las metas"
                        }
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