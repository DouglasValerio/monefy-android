package info.codementor.meusgastos

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import info.codementor.meusgastos.database.DatabaseHandler
import info.codementor.meusgastos.databinding.HandleRecordBinding
import info.codementor.meusgastos.entity.FinancialRecord
import java.time.Instant

class HandleRecordActivity : AppCompatActivity() {
    private lateinit var binding: HandleRecordBinding

    private  lateinit var banco: DatabaseHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HandleRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = "Novo Registro"
        banco = DatabaseHandler(this)

        binding.btnSave.setOnClickListener {
            onSaveAction()
        }
    }

    private fun onSaveAction() {
        val amount = binding.amount.text.toString().toDoubleOrNull()?:0.0
        val description = binding.description.text.toString()
        val record = FinancialRecord.createExpense(amount, description, Instant.now())
        banco.insert (record)
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