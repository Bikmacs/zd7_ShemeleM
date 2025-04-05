package com.bignerdranch.android.application_19

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var titleName: TextView
    private var selectedContactName: String = ""
    private var selectedContactNumber: String = ""

    companion object {
        private const val DATE_FORMAT = "EEE, MMM dd"
        private const val REQUEST_CONTACT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime(
            title = "Убийство",
            isSolved = true,
            date = LocalDate.now()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        reportButton = view.findViewById(R.id.send_crime_report)
        suspectButton = view.findViewById(R.id.choose_suspect)
        solvedCheckBox = view.findViewById(R.id.crime_solved)
        titleName = view.findViewById(R.id.textView)

        // Отключение кнопки dateButton по умолчанию
        dateButton.isEnabled = false

        dateButton.text = crime.date.toString()

        // Установка слушателя на CheckBox
        solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            dateButton.isEnabled = isChecked
        }

        // Установка TextWatcher для обновления TextView
        titleField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                titleName.text = s.toString()
            }
        })

        reportButton.setOnClickListener {
            sendContactInfo()
        }

        suspectButton.setOnClickListener {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(pickContactIntent, REQUEST_CONTACT)
        }

        dateButton.setOnClickListener {
            crime.title = titleField.text.toString()
            // Обновите интерфейс или выполните другие действия
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { contactUri ->
                try {
                    val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                    requireActivity().contentResolver.query(contactUri, queryFields, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            selectedContactName = cursor.getString(0)

                            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                            if (idIndex != -1) {
                                val id = cursor.getString(idIndex)
                                val phoneCursor = requireActivity().contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    arrayOf(id),
                                    null
                                )
                                phoneCursor?.use {
                                    if (it.moveToFirst()) {
                                        val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                        if (numberIndex != -1) {
                                            selectedContactNumber = it.getString(numberIndex)
                                        }
                                    }
                                }
                            }
                            // Обновите интерфейс
                            updateUI()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CrimeFragment", "Error querying contact", e)
                }
            } ?: run {
                Log.e("CrimeFragment", "Contact URI is null")
            }
        }
    }

    private fun sendContactInfo() {
        val message = "Контакт: $selectedContactName, $selectedContactNumber. Это ужас!"
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
        }.also { intent ->
            val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
            startActivity(chooserIntent)
        }
    }

    private fun updateUI() {
        // Обновите интерфейс
        suspectButton.text = if (selectedContactName.isBlank()) {
            getString(R.string.choose_suspect)
        } else {
            selectedContactName
        }
    }
}
