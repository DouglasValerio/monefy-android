package info.codementor.meusgastos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import info.codementor.meusgastos.database.DatabaseHandler
import info.codementor.meusgastos.databinding.ActivityListBinding
import info.codementor.meusgastos.entity.FinancialRecord
import info.codementor.meusgastos.entity.FinancialRecordType
import java.time.Instant

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
       if(!::banco.isInitialized){
           banco = DatabaseHandler(context)
       }
        var result = banco.findAll()
        if (result.isEmpty()) {
            result = mutableListOf(
                FinancialRecord.createIncome(2000.0, "Salário", Instant.now()),
                FinancialRecord.createIncome(1500.0, "Freelance", Instant.now())
            )
        }

        val adapter = CustomAdapter(context, result)
        binding.mainListView.adapter = adapter
    }


}

class CustomAdapter(context: Context, items: List<FinancialRecord>) :
    ArrayAdapter<FinancialRecord>(context, R.layout.activity_list_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.activity_list_item, parent, false
        )

        val currentItem = getItem(position)

        val text1 = view.findViewById<TextView>(R.id.listTitle)
        val text2 = view.findViewById<TextView>(R.id.listSubtitle)

        text1.text = currentItem?.description
        val sign = if (currentItem?.type == FinancialRecordType.INCOME) "+" else "-"
        text2.text = "$sign R\$${currentItem?.amount.toString()}"

        return view
    }
}