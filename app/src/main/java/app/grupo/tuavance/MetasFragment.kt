package app.grupo.tuavance

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
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
        cargarDatos()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cerrarSesion.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                showIntro()
            }
        }
    }

    private fun showIntro(){
        val intent = Intent(requireContext(), IntroActivity::class.java)
        this.startActivity(intent)
    }

    private fun cargarDatos() {
        sharedViewModel.apply {
            db.collection(usuario!!.email!!)
                .addSnapshotListener { value, error ->
                    if(value!=null && error==null){
                        //Cargar lista objetivos
                        listaObjetivos = value.toObjects(Objetivo::class.java)

                        //Ordenar la lista por fechas de pronto a tarde


                        binding.rvTareas.adapter = ObjetivosAdapter(requireContext(), sharedViewModel.listaObjetivos,
                            usuario.email!!,this@MetasFragment)
                    }
                }
        }
    }

    override fun onItemClick(position: Int) {
        if(sharedViewModel.editar){
            //Aparece un diálogo para cambiar el objetivo
        } else {
            //Busca la lista de tareas del objetivo escogido y muestra la pantalla siguiente
        }
    }

}