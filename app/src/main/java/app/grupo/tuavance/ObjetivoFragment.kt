package app.grupo.tuavance

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import app.grupo.tuavance.databinding.FragmentObjetivoBinding
import app.grupo.tuavance.model.ViewModel

class ObjetivoFragment : Fragment() {

    private val sharedViewModel:ViewModel by activityViewModels()
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
        /*binding.actualizar.setOnClickListener {
            if(binding.objetivo.text.toString()!=""){
                sharedViewModel.apply {
                    objetivo = binding.objetivo.text.toString()
                    prefs.edit().putString("objetivo",objetivo).apply()
                    actualizar.value = true
                }
                //dialog?.dismiss()
            } else {
                binding.objetivo.error = "Escribe tu objetivo"
            }
        }
         agregar.setOnClickListener {
                sharedViewModel.editar = false
                sharedViewModel.descripcionTarea = ""
                sharedViewModel.fechaTarea = ""
            }
            var cumplidos = 0
                        for(elemento in listaTareas){
                            if(elemento.chuleada){
                                cumplidos++
                            }
                        }
                        chuleados.value = cumplidos
                        binding.rvTareas.adapter = CuentosAdapter(requireContext(), sharedViewModel.listaTareas, sharedViewModel.objetivo,this@MetasFragment)
                        binding.progressBar2.max = listaTareas.size
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
                        */
    }


}