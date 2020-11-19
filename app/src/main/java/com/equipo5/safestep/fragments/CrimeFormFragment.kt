package com.equipo5.safestep.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.equipo5.safestep.R
import com.equipo5.safestep.models.Crime
import com.equipo5.safestep.network.AuthService
import com.equipo5.safestep.network.Callback
import com.equipo5.safestep.network.FirestoreService
import com.equipo5.safestep.network.StorageService
import com.equipo5.safestep.utils.FileUtil
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_crime_form.*
import java.io.File
import kotlin.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val GALLERY_REQUEST_CODE = 1

/**
 * A simple [Fragment] subclass.
 * Use the [CrimeFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrimeFormFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var title: String
    lateinit var description: String
    lateinit var crime: String
    private lateinit var imageFile: File
    val authService = AuthService()
    val storage = StorageService()
    val firestoreService = FirestoreService()
    private lateinit var latitude: String
    private lateinit var longitude: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        latitude = arguments?.getString("latitude").toString()
        longitude = arguments?.getString("longitude").toString()

        val view = inflater.inflate(R.layout.fragment_crime_form, container, false)

        val countries = arrayOf("Agresion Sexual", "Asalto", "Homicido", "Secuestro")

        val adapter = context?.let {
            ArrayAdapter(
                it,
                R.layout.dropdown_menu_item,
                countries
            )
        }

        val editTextFilledExposedDropdown = view.findViewById<AutoCompleteTextView>(R.id.filled_exposed_dropdown)

        editTextFilledExposedDropdown.setAdapter(adapter)

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("LONGITUDE", longitude)
        Log.d("LATITUDE", latitude)

        ivMainImage.setOnClickListener {
            openGallery()
        }

        btnCancelCrime.setOnClickListener {
            fragmentManager?.beginTransaction()?.remove(this)?.commit();
        }

        btnSubmitCrime.setOnClickListener {
            if (isFormValid()) {
                saveImages();
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                imageFile = FileUtil.from(context!!, data!!.data!!)
                ivMainImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.absolutePath))
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun saveImages() {
        rlLoadingCrimeRegister.visibility = View.VISIBLE
        context?.let {
            storage.saveImage(it, imageFile, object: Callback<Void> {

                override fun onSuccess(result: Void?) {
                    storage.getStorage().downloadUrl
                        .addOnSuccessListener {uri ->
                            val crimeRegister = Crime()
                            crimeRegister.image1 = uri.toString()
                            crimeRegister.title = title
                            crimeRegister.description = description
                            crimeRegister.category = crime
                            crimeRegister.idUser = authService.getUid().toString()
                            firestoreService.insertCrimeRegister(crimeRegister, object: Callback<Task<Void>>{
                                override fun onSuccess(result: Task<Void>?) {
                                    rlLoadingCrimeRegister.visibility = View.INVISIBLE
                                    Toast.makeText(context, "Registrado", Toast.LENGTH_LONG).show()
                                    fragmentManager?.beginTransaction()?.remove(this@CrimeFormFragment)?.commit();
                                }

                                override fun onFailure(exception: Exception) {
                                    rlLoadingCrimeRegister.visibility = View.INVISIBLE
                                }

                            })

                        }
                        .addOnFailureListener {
                            rlLoadingCrimeRegister.visibility = View.INVISIBLE
                            fragmentManager?.beginTransaction()?.remove(this@CrimeFormFragment)?.commit();
                        }
                }

                override fun onFailure(exception: Exception) {
                    rlLoadingCrimeRegister.visibility = View.INVISIBLE
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                    fragmentManager?.beginTransaction()?.remove(this@CrimeFormFragment)?.commit();
                }

            })
        }
    }

    private fun insertInDB() {
        saveImages()

    }


    fun isFormValid(): Boolean {
        var isCorrect = true

        title = etCrimeTitle.text.toString().trim()
        description = etCrimeDescription.text.toString().trim()
        crime = filled_exposed_dropdown.text.toString()

//        when {
//            title.isEmpty() -> {
//                tilCrimeTitle.error = getString(R.string.required_field)
//                tilCrimeTitle.isErrorEnabled = true
//                isCorrect = false
//            }
//            title.length < 6 -> {
//                tilCrimeTitle.error = "Titulo muy corto"
//                tilCrimeTitle.isErrorEnabled = true
//                isCorrect = false
//            }
//            else -> {
//                tilCrimeTitle.isErrorEnabled = false
//            }
//        }
//
//        when {
//            description.isEmpty() -> {
//                tilCrimeDescription.error = getString(R.string.required_field)
//                tilCrimeDescription.isErrorEnabled = true
//                isCorrect = false
//            }
//            description.length < 20 -> {
//                tilCrimeDescription.error = "Descripcion muy corta"
//                tilCrimeDescription.isErrorEnabled = true
//                isCorrect = false
//            }
//            else -> {
//                tilCrimeDescription.isErrorEnabled = false
//            }
//        }
//
//        when {
//            crime.isEmpty() -> {
//                tilCrime.error = getString(R.string.required_field)
//                tilCrime.isErrorEnabled = true
//                isCorrect = false
//            }
//            crime == getString(R.string.what_was_the_crime) -> {
//                tilCrime.error = "Escoja una opcion"
//                tilCrime.isErrorEnabled = true
//                isCorrect = false
//            }
//            else -> {
//                tilCrime.isErrorEnabled = false
//            }
//        }


        return isCorrect
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CrimeFormFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CrimeFormFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}