package info.codementor.meusgastos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import info.codementor.meusgastos.database.DatabaseHandler
import info.codementor.meusgastos.databinding.ActivityListBinding
import info.codementor.meusgastos.entity.FinancialRecord
import info.codementor.meusgastos.entity.FinancialRecordType
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ListFragment : Fragment() {
    private lateinit var binding: ActivityListBinding
    private lateinit var banco: DatabaseHandler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshData()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        val context = requireContext()
        if (!::banco.isInitialized) {
            banco = DatabaseHandler(context)
        }
        var result = banco.findAll()
        val debts = result.filter { it.type == FinancialRecordType.EXPENSE }.sumOf { it.amount }
        val credits = result.filter { it.type == FinancialRecordType.INCOME }.sumOf { it.amount }
        val balance = credits - debts
        binding.totalBalanceAmount.text = "R$$balance"
        binding.incomeAmount.text = "R$$credits"
        binding.expensesAmount.text = "R$$debts"

        val adapter = CustomAdapter(
            context = context,
            items = result,
            callback = { id -> deleteItem(id) },
            editCallback = { id -> editItem(id) })
        binding.mainListView.adapter = adapter
    }

    private fun deleteItem(id: Int) {
        banco.delete(id)
        refreshData()
    }

    private fun editItem(id: Int) {
        val intent = Intent(context, HandleRecordActivity::class.java).apply {
            putExtra("item_id", id)
        }
        startActivity(intent)

    }

    class CustomAdapter(
        context: Context,
        items: List<FinancialRecord>,
        val callback: (Int) -> Unit,
        val editCallback: (Int) -> Unit
    ) :
        ArrayAdapter<FinancialRecord>(context, R.layout.activity_list_item, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.activity_list_item, parent, false
            )

            val currentItem = getItem(position)

            val text1 = view.findViewById<TextView>(R.id.listTitle)
            val text2 = view.findViewById<TextView>(R.id.listSubtitle)
            val tvDate = view.findViewById<TextView>(R.id.tvDate)
            val img = view.findViewById<ImageView>(R.id.imageView)
            val deleteBtn = view.findViewById<ImageButton>(R.id.deleteButton)
            val editBtn = view.findViewById<ImageButton>(R.id.editButton)
            deleteBtn.setOnClickListener {
                if (currentItem != null) {
                    callback(currentItem._id)
                }
            }
            editBtn.setOnClickListener {
                if (currentItem != null) {
                    editCallback(currentItem._id)
                }
            }

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val zonedDateTime = ZonedDateTime.ofInstant(currentItem?.date, ZoneId.systemDefault())


            text1.text = currentItem?.description
            tvDate.text = zonedDateTime.format(formatter)
            val sign = if (currentItem?.type == FinancialRecordType.INCOME) "+" else "-"
            img.setImageResource(
                if (currentItem?.type == FinancialRecordType.INCOME) R.drawable.currency_income else R.drawable.currency_expense
            )
            text2.text = "$sign R\$${currentItem?.amount.toString()}"


            return view
        }
    }
}