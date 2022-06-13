package panafana.example.panaf.wouldyourather

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import panafana.example.panaf.wouldyourather.databinding.ActivityLogin2Binding
import panafana.example.panaf.wouldyourather.utils.Manager
import panafana.example.panaf.wouldyourather.utils.Utils

class LoginActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityLogin2Binding
    private val fireabase = FirebaseAnalytics.getInstance(this)
    private val utils = Utils()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(this.layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initialize()
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        if(!sp.getString("username",null).isNullOrEmpty()){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    private fun initialize(){

        binding.password.setOnEditorActionListener{v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                loginListener()
                true
            } else {
                false
            }
        }

        binding.emailSignInButton.setOnClickListener { loginListener() }

        binding.registerButton.setOnClickListener {
            val i = Intent(this, RegisterActivity2::class.java)
            startActivity(i) }

        binding.offlineButton.setOnClickListener {
            val i = Intent(this, SelectGender::class.java)
            startActivity(i)
            finish()}
    }

    fun loginListener(){
        if (binding.username.text.isNullOrEmpty()) binding.username.error = getString(R.string.username_error)
        if (binding.password.text.isNullOrEmpty()) binding.password.error = getString(R.string.password_error)

        if(!binding.username.text.isNullOrEmpty() && !binding.password.text.isNullOrEmpty() && utils.isNetworkAvailable(this)){
            utils.show_progressBar(this)
            Manager().login2(binding.username.text.toString(), binding.password.text.toString(),this,this)
        }else if(!utils.isNetworkAvailable(this)){
            Toast.makeText(this,getString(R.string.internet),Toast.LENGTH_SHORT).show()
        }

    }

    fun login(l: Boolean, username: String?, gender: String) {
        utils.hide_progressBar()
        val sP = getSharedPreferences("gender", MODE_PRIVATE)
        val sP2 = getSharedPreferences("user", MODE_PRIVATE)
        val sPE = sP.edit()
        val sPE2 = sP2.edit()
        if (l) {
            sPE2.putString("username", username).apply()
            if (gender == "Male") {
                sPE.putString("gender", "male").apply()
                //Log.d("gender", "male")
            } else if (gender == "Female") {
                sPE.putString("gender", "female").apply()
                //Log.d("gender", "female")
            } else {
                sPE.putString("gender", "other").apply()
                //Log.d("gender", "other")
            }
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, username)
            fireabase.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val i = Intent(this, MainActivity::class.java)
                finish()
                startActivity(i)
            } else {
                val i = Intent(this, MainActivityCompatibility::class.java)
                finish()
                startActivity(i)
            }
        } else {
            binding.password.error = getString(R.string.error_incorrect_username_or_password)
            binding.username.error = getString(R.string.error_incorrect_username_or_password)
            binding.username.requestFocus()
        }
    }
}