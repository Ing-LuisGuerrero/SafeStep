package com.equipo5.safestep.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
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
import com.google.type.DateTime
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_report_crime.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "latitude"
private const val ARG_PARAM2 = "longitude"

private const val GALLERY_REQUEST_CODE = 1
private const val CAMERA_REQUEST_CODE = 4
private const val GALLERY_REQUEST_CODE_2 = 2
private const val CAMERA_REQUEST_CODE_2 = 5
private const val GALLERY_REQUEST_CODE_3 = 3
private const val CAMERA_REQUEST_CODE_3 = 6

/**
 * A simple [Fragment] subclass.
 * Use the [CrimeFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrimeFormFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    // TODO: Rename and change types of parameters
    private var latitude: String? = null
    private var longitude: String? = null

    lateinit var title: String
    lateinit var description: String
    lateinit var crime: String
    private var imageFile = mutableListOf<Pair<Int, File>>()
    private val authService = AuthService()
    val storage = StorageService()
    val firestoreService = FirestoreService()
    private var uri = mutableListOf<Pair<Int, String>>()
    private val uId = authService.getUid()
    private lateinit var alertDialog: AlertDialog.Builder
    private val options = arrayOf<CharSequence>("Imagen de galeria", "Tomar foto")
    private lateinit var mPhotoPath: String
    private lateinit var mAbsolutePhotoPath: String

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0


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
            selectOption(GALLERY_REQUEST_CODE, CAMERA_REQUEST_CODE)
        }

        ivSecondImage.setOnClickListener {
            selectOption(GALLERY_REQUEST_CODE_2, CAMERA_REQUEST_CODE_2)
        }

        ivThirdImage.setOnClickListener {
            selectOption(GALLERY_REQUEST_CODE_3, CAMERA_REQUEST_CODE_3)
        }

        btnCancelCrime.setOnClickListener {
            fragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        getDateTimeCalendar()

        tilDatePicker.setOnClickListener {
            context?.let { context -> DatePickerDialog(context, this, year, month, day).show() }
        }

        btnSubmitCrime.setOnClickListener {
            if (isFormValid()) {
                saveInDB((imageFile.size) - 1, 0)
            }
        }
    }

    private fun selectOption(requestCodeGallery: Int, requestCodePhoto: Int) {
        alertDialog.setItems(options) { _, which ->
            if (which == 0) {
                openGallery(requestCodeGallery)
            } else {
                takePhoto(requestCodePhoto)
            }
        }

        alertDialog.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhoto(requestCode: Int) {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if(context?.let { takePicture.resolveActivity(it.packageManager) } != null) {
            var photoFile: File? = null

            try {
                photoFile = createPhotoFile()
            } catch (e: Exception) {
                Toast.makeText(context, "Error en la operación", Toast.LENGTH_LONG).show()
            }

            if (photoFile != null) {
                var photoUri: Uri? = null
                try {
                    photoUri = FileProvider.getUriForFile(context!!, "com.equipo5.safestep", photoFile)
                } catch (ex: Exception){
                    Toast.makeText(context, "Error en la operación", Toast.LENGTH_LONG).show()
                    Log.e("Error context", ex.message.toString())
                }
                if(photoUri != null) {
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePicture, requestCode)
                }
            }
        }
    }

    private fun createPhotoFile(): File {
        var storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File.createTempFile(
            "${Date()}_photo",
            ".jpg",
            storageDir
        )
        mPhotoPath = "file:${photoFile.absolutePath}"
        mAbsolutePhotoPath = photoFile.absolutePath
        return photoFile
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
                imageFile.add(Pair(1, FileUtil.from(context!!, data!!.data!!)))
                ivMainImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.last().second.absolutePath))
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                imageFile.add(Pair(2, FileUtil.from(context!!, data!!.data!!)))
                ivSecondImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.last().second.absolutePath))
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == GALLERY_REQUEST_CODE_3 && resultCode == RESULT_OK) {
            try {
                imageFile.add(Pair(3, FileUtil.from(context!!, data!!.data!!)))
                ivThirdImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.last().second.absolutePath))
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_LONG).show()
            }
        } else if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            imageFile.add(Pair(1, File(mAbsolutePhotoPath)))
            Picasso.with(context).load(mPhotoPath).into(ivMainImage)
        } else if(requestCode == CAMERA_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            imageFile.add(Pair(2, File(mAbsolutePhotoPath)))
            Picasso.with(context).load(mPhotoPath).into(ivSecondImage)
        } else if(requestCode == CAMERA_REQUEST_CODE_3 && resultCode == RESULT_OK) {
            imageFile.add(Pair(3, File(mAbsolutePhotoPath)))
            Picasso.with(context).load(mPhotoPath).into(ivThirdImage)
        }
    }

    private fun saveInDB(nImages: Int, i: Int) {
        context?.let { context ->
            if (uId != null) {
                if(i in 0..nImages) {
                    rlLoadingCrimeRegister.visibility = View.VISIBLE
                    storage.saveImage(context, uId, imageFile[i].second, object : Callback<Void> {

                        override fun onSuccess(result: Void?) {
                            storage.getStorage().downloadUrl
                                .addOnSuccessListener { URI ->
                                    uri.add(Pair(imageFile[i].first, URI.toString()))

                                    if (i <= nImages) {
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
                } else {
                    insertCrimeRegister(buildData(uId, nImages))
                }
            }
        }
    }

    private fun buildData(uId: String, nImages: Int): Report {
        val crimeRegister = Report()

        for (j in 0..nImages) {
            when(uri[j].first) {
                1 -> crimeRegister.image1 = uri[j].second
                2 -> crimeRegister.image2 = uri[j].second
                3 -> crimeRegister.image3 = uri[j].second
            }
        }

        crimeRegister.title = title
        crimeRegister.description = description
        crimeRegister.category = crime
        crimeRegister.idUser = uId
        crimeRegister.datetime = Timestamp(Date())
        crimeRegister.longitude = longitude.toString()
        crimeRegister.latitude = latitude.toString()

        return crimeRegister
    }

    private fun insertCrimeRegister(crimeRegister: Report) {
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

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)

    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()
        TimePickerDialog(context, this, hour, minute, true).show()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        val date = Calendar.getInstance()
        date.set(savedYear + 1900, savedMonth, savedDay, savedHour, savedMinute)

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm a")

        val dateF = Date(date.timeInMillis)



    }
}