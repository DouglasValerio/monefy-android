package info.codementor.meusgastos

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import info.codementor.meusgastos.database.DatabaseHandler
import info.codementor.meusgastos.databinding.HandleRecordBinding
import info.codementor.meusgastos.entity.FinancialRecord
import info.codementor.meusgastos.entity.FinancialRecordType
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class HandleRecordActivity : AppCompatActivity() {
    private lateinit var binding: HandleRecordBinding

    private lateinit var banco: DatabaseHandler
    private var title: String = "Novo Registro"
    private var isEditMode: Boolean = false
    private var recordForEdition: FinancialRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HandleRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleToolbar()

        banco = DatabaseHandler(this)
        handleEditMode()

        handleClickListeners()

        handleSpinner()
    }

    private fun handleEditMode() {
        val id = intent.getIntExtra("item_id", -1)
        recordForEdition = banco.find(id)
        if (recordForEdition != null) {
            isEditMode = true
            title = "Editar Registro"
            binding.dateTextField.setText(recordForEdition!!.date.toString())
            binding.amount.setText(recordForEdition!!.amount.toString())
            binding.description.setText(recordForEdition!!.description)
            binding.typeSpinner.setSelection(if (recordForEdition!!.type == FinancialRecordType.EXPENSE) 0 else 1)
            binding.btnSave.text = "Atualizar"

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val zonedDateTime =
                ZonedDateTime.ofInstant(recordForEdition!!.date, ZoneId.systemDefault())
            zonedDateTime.format(formatter)
            binding.dateTextField.setText(zonedDateTime.format(formatter))
        }
    }

    private fun handleToolbar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = title
    }

    private fun handleClickListeners() {
        binding.btnSave.setOnClickListener {
            onSaveAction()
        }

        binding.calendarField.setOnClickListener() {
            onCalendarFieldClick()
        }
    }

    private fun handleSpinner() {
        val spinner: Spinner = binding.typeSpinner

        val options = arrayOf("Débito", "Crédito")

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
    }

    private fun onCalendarFieldClick() {

        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Show DatePickerDialog
        val dateButton = binding.dateTextField
        DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = "${selectedDay.toString().padStart(2, '0')}/${
                    (selectedMonth + 1).toString().padStart(2, '0')
                }/$selectedYear"
                dateButton.setText("$selectedDate")
            },
            year,
            month,
            day
        ).show()

    }

    private fun onSaveAction() {
        val amount = binding.amount.text.toString().toDoubleOrNull() ?: 0.0
        val description = binding.description.text.toString()
        val dateStr = binding.dateTextField.text.toString()
        val type = binding.typeSpinner.selectedItem.toString()
        val enumType =
            if (type == "Débito") FinancialRecordType.EXPENSE else FinancialRecordType.INCOME


        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val localDate = LocalDate.parse(dateStr, formatter)

        val date = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()

        if (recordForEdition != null) {
            val record = recordForEdition!!.update(amount, description, date, enumType)
            banco.update(record)
        } else {
            val record = if (enumType == FinancialRecordType.EXPENSE) FinancialRecord.createExpense(
                amount,
                description,
                date
            ) else FinancialRecord.createIncome(amount, description, date)
            banco.insert(record)
        }
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button press
                onBackPressed()  // or NavUtils.navigateUpFromSameTask(this)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}