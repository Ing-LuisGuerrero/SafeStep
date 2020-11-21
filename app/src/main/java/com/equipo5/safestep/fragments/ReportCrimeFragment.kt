package com.equipo5.safestep.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.equipo5.safestep.R
import com.equipo5.safestep.models.Report
import com.equipo5.safestep.network.AuthService
import com.equipo5.safestep.network.Callback
import com.equipo5.safestep.network.FirestoreService
import com.equipo5.safestep.network.StorageService
import com.equipo5.safestep.utils.FileUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_report_crime.*
import java.io.File
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "latitude"
private const val ARG_PARAM2 = "longitude"

private const val GALLERY_REQUEST_CODE = 1
private const val GALLERY_REQUEST_CODE_2 = 2
private const val GALLERY_REQUEST_CODE_3 = 3

/**
 * A simple [Fragment] subclass.
 * Use the [CrimeFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrimeFormFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var latitude: String? = null
    private var longitude: String? = null

    lateinit var title: String
    lateinit var description: String
    lateinit var crime: String
    private var imageFile = mutableListOf<File>()
    private val authService = AuthService()
    val storage = StorageService()
    val firestoreService = FirestoreService()
    private var uri = mutableListOf<String>()
    private val uId = authService.getUid()
    private lateinit var alertDialog: AlertDialog.Builder
    private lateinit var charSequence: Array<Char>
    private val OPTIONS = arrayOf<CharSequence>("Imagen de galeria", "Tomar foto")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getString(ARG_PARAM1)
            longitude = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_report_crime, container, false)

        val countries = arrayOf(
            "Agresion Sexual",
            "Asalto",
            "Homicido",
            "Robo",
            "Secuestro",
            "Otro"
        )

        val adapter = context?.let {
            ArrayAdapter(
                it,
                R.layout.dropdown_menu_item,
                countries
            )
        }

        val editTextFilledExposedDropdown =
            view.findViewById<AutoCompleteTextView>(R.id.filled_exposed_dropdown)

        editTextFilledExposedDropdown.setAdapter(adapter)
        alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Selecciona una opción: ")

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        longitude?.let { Log.d("LONGITUDE", it) }
        latitude?.let { Log.d("LATITUDE", it) }

        ivMainImage.setOnClickListener {
            selectOption(GALLERY_REQUEST_CODE)
        }

        ivSecondImage.setOnClickListener {
            selectOption(GALLERY_REQUEST_CODE_2)
        }

        ivThirdImage.setOnClickListener {
            selectOption(GALLERY_REQUEST_CODE_3)
        }

        btnCancelCrime.setOnClickListener {
            fragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        btnSubmitCrime.setOnClickListener {
            if (isFormValid()) {
                saveInDB((imageFile.size) - 1, 0)
            }
        }
    }

    private fun selectOption(requestCode: Int) {
        alertDialog.setItems(OPTIONS) { _, which ->
            if (which == 0) {
                openGallery(requestCode)
            } else {
                takePhoto()
            }
        }

        alertDialog.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhoto() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if(this.context?.packageManager?.let { takePicture.resolveActivity(it) } != null) {
            var photo = null

            try {

            } catch (e: Exception) {

            }

        }
    }


    private fun openGallery(requestCode: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                imageFile.add(FileUtil.from(context!!, data!!.data!!))
                ivMainImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.last().absolutePath))
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                imageFile.add(FileUtil.from(context!!, data!!.data!!))
                ivSecondImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.last().absolutePath))
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == GALLERY_REQUEST_CODE_3 && resultCode == RESULT_OK) {
            try {
                imageFile.add(FileUtil.from(context!!, data!!.data!!))
                ivThirdImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.last().absolutePath))
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveInDB(nImages: Int, i: Int) {
        if (nImages >= 0) {
            context?.let {
                if (uId != null) {
                    rlLoadingCrimeRegister.visibility = View.VISIBLE
                    storage.saveImage(it, uId, imageFile[i], object : Callback<Void> {

                        override fun onSuccess(result: Void?) {
                            storage.getStorage().downloadUrl
                                .addOnSuccessListener { URI ->
                                    uri.add(URI.toString())

                                    if (i == nImages) {
                                        val crimeRegister = Report()

                                        if (uri.size >= 1) {
                                            crimeRegister.image1 = uri[0]
                                        }

                                        if (uri.size >= 2) {
                                            crimeRegister.image2 = uri[1]
                                        }

                                        if (uri.size == 3) {
                                            crimeRegister.image3 = uri[2]
                                        }

                                        crimeRegister.title = title
                                        crimeRegister.description = description
                                        crimeRegister.category = crime
                                        crimeRegister.idUser = uId
                                        crimeRegister.datetime = Timestamp(Date())
                                        crimeRegister.longitude = longitude.toString()
                                        crimeRegister.latitude = latitude.toString()

                                        firestoreService.insertCrimeRegister(
                                            crimeRegister,
                                            object : Callback<Task<Void>> {
                                                override fun onSuccess(result: Task<Void>?) {
                                                    rlLoadingCrimeRegister.visibility =
                                                        View.INVISIBLE
                                                    Toast.makeText(
                                                        context,
                                                        "Registrado",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    fragmentManager?.beginTransaction()
                                                        ?.remove(this@CrimeFormFragment)
                                                        ?.commit()
                                                }

                                                override fun onFailure(exception: Exception) {
                                                    rlLoadingCrimeRegister.visibility =
                                                        View.INVISIBLE
                                                }

                                            })
                                    } else {
                                        saveInDB(nImages, i + 1)
                                    }
                                }
                                .addOnFailureListener {
                                    rlLoadingCrimeRegister.visibility = View.INVISIBLE
                                    fragmentManager?.beginTransaction()
                                        ?.remove(this@CrimeFormFragment)
                                        ?.commit()
                                }
                        }

                        override fun onFailure(exception: Exception) {
                            rlLoadingCrimeRegister.visibility = View.INVISIBLE
                            Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                            fragmentManager?.beginTransaction()?.remove(this@CrimeFormFragment)
                                ?.commit()
                        }
                    })
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        var isCorrect = true

        title = etCrimeTitle.text.toString().trim()
        description = etCrimeDescription.text.toString().trim()
        crime = filled_exposed_dropdown.text.toString()

        when {
            title.isEmpty() -> {
                tilCrimeTitle.error = getString(R.string.required_field)
                tilCrimeTitle.isErrorEnabled = true
                isCorrect = false
            }
            title.length < 6 -> {
                tilCrimeTitle.error = "Titulo muy corto"
                tilCrimeTitle.isErrorEnabled = true
                isCorrect = false
            }
            else -> {
                tilCrimeTitle.isErrorEnabled = false
            }
        }

        when {
            description.isEmpty() -> {
                tilCrimeDescription.error = getString(R.string.required_field)
                tilCrimeDescription.isErrorEnabled = true
                isCorrect = false
            }
            description.length < 20 -> {
                tilCrimeDescription.error = "Descripcion muy corta"
                tilCrimeDescription.isErrorEnabled = true
                isCorrect = false
            }
            else -> {
                tilCrimeDescription.isErrorEnabled = false
            }
        }

        when {
            crime.isEmpty() -> {
                tilCrime.error = getString(R.string.required_field)
                tilCrime.isErrorEnabled = true
                isCorrect = false
            }
            crime == getString(R.string.what_was_the_crime) -> {
                tilCrime.error = "Escoja una opcion"
                tilCrime.isErrorEnabled = true
                isCorrect = false
            }
            else -> {
                tilCrime.isErrorEnabled = false
            }
        }

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