package app.grupo.tuavance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import app.grupo.tuavance.databinding.FragmentTareaBinding
import app.grupo.tuavance.model.ViewModel


class TareaFragment : DialogFragment() {

    private val sharedViewModel: ViewModel by activityViewModels()
    private var _binding: FragmentTareaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentTareaBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            eliminar.setOnClickListener {
                dialog?.dismiss()
            }
            chulo.setOnClickListener {
                if(fecha.text.toString() !=""&& tarea.text.toString()!="" && sharedViewModel.objetivo!="Escribe tu objetivo"){
                    if(sharedViewModel.editar) {
                        sharedViewModel.edicion("fecha",fecha.text.toString())
                        sharedViewModel.edicion("descripcion",tarea.text.toString())
                        dialog?.dismiss()
                    } else {
                        sharedViewModel.escribir(tarea.text.toString(), fecha.text.toString())
                        dialog?.dismiss()
                    }
                } else {
                    Toast.makeText(requireContext(),"Completa la info",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            Log.i("prueba","Tarea:${sharedViewModel.descripcionTarea} y fecha: ${sharedViewModel.fechaTarea}")
            fecha.setText(sharedViewModel.fechaTarea, TextView.BufferType.EDITABLE)
            tarea.setText(sharedViewModel.descripcionTarea, TextView.BufferType.EDITABLE)
        }
    }


}