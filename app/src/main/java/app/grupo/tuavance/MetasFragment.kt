package app.grupo.tuavance

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import app.grupo.tuavance.databinding.FragmentMetasBinding
import app.grupo.tuavance.model.CuentosAdapter
import app.grupo.tuavance.model.Tarea
import app.grupo.tuavance.model.ViewModel

class MetasFragment : Fragment(), CuentosAdapter.OnItemClickListener {

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
        cargarDatos()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            editar.setOnClickListener {   }
            agregar.setOnClickListener {
                sharedViewModel.editar = false
                sharedViewModel.descripcionTarea = ""
                sharedViewModel.fechaTarea = ""
            }
        }
    }

    private fun cargarDatos() {
        sharedViewModel.apply {
            objetivo = prefs.getString("objetivo","Escribe tu objetivo")?:"Escribe tu objetivo"
            binding.objetivo.text = sharedViewModel.objetivo
            db.collection(objetivo)
                .addSnapshotListener { value, error ->
                    if(value!=null && error==null){
                        //Cargar lista tareas
                        listaTareas = value.toObjects(Tarea::class.java)
                        var cumplidos = 0
                        for(elemento in listaTareas){
                            if(elemento.chuleada){
                                cumplidos++
                            }
                        }
                        chuleados.value = cumplidos
                        binding.rvTareas.adapter = CuentosAdapter(requireContext(), sharedViewModel.listaTareas, sharedViewModel.objetivo,this@MetasFragment)
                        binding.progressBar2.max = listaTareas.size
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.apply {
            actualizar.observe({ lifecycle }, {
                if (it) {
                    //Set Adapter
                    cargarDatos()
                }
            })
            actualizar.value = false
            chuleados.observe({lifecycle},{
                binding.progressBar2.progress = it
            })
        }
    }

    override fun onItemClick(position: Int) {
        sharedViewModel.apply {
            fechaTarea = listaTareas[position].fecha
            descripcionTarea = listaTareas[position].descripcion
            editar = true
        }
    }

}