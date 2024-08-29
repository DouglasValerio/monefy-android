package info.codementor.meusgastos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import info.codementor.meusgastos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setListFragment()
        binding.fab.setOnClickListener {
            handleFabClick()
        }

    }

    private fun handleFabClick() {
        val intent = Intent(this, HandleRecordActivity::class.java)
       val result =  startActivity(intent)
    }

    private fun setListFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.list_fragment_container, ListFragment())
        fragmentTransaction.commit()
    }
}