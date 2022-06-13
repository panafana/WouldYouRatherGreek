package panafana.example.panaf.wouldyourather

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import panafana.example.panaf.wouldyourather.databinding.ActivityRegister2Binding
import panafana.example.panaf.wouldyourather.utils.Manager
import panafana.example.panaf.wouldyourather.utils.Utils

class RegisterActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister2Binding
    private val utils = Utils()
    private val fireabase = FirebaseAnalytics.getInstance(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initialize()

    }

    private fun initialize(){

        binding.password.setOnEditorActionListener{v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                signupListener()
                true
            } else {
                false
            }
        }

        binding.spinner1.adapter = ArrayAdapter.createFromResource(this,R.array.genders,R.layout.support_simple_spinner_dropdown_item)

        binding.emailSignInButton.setOnClickListener { signupListener() }


    }

    fun signupListener(){
        if (binding.username.text.isNullOrEmpty()) binding.username.error = getString(R.string.username_error)
        if (binding.password.text.isNullOrEmpty()) binding.password.error = getString(R.string.password_error)
        if (binding.email.text.isNullOrEmpty()) binding.email.error = getString(R.string.email_error)

        if(!binding.username.text.isNullOrEmpty() && !binding.password.text.isNullOrEmpty() && !binding.email.text.isNullOrEmpty() && binding.spinner1.selectedItem.toString().isNotEmpty() && utils.isNetworkAvailable(this)){
            utils.show_progressBar(this)
            Manager().signup2(binding.email.text.toString(),binding.username.text.toString(), binding.password.text.toString(), binding.spinner1.selectedItem.toString(),this,this)
        }else if(!utils.isNetworkAvailable(this)){
            Toast.makeText(this,getString(R.string.internet), Toast.LENGTH_SHORT).show()
        }

    }

    fun register(l: Boolean, username: String?, gender: String) {
        utils.hide_progressBar()
        if (l) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, username)
            fireabase.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).setContentText(getString(R.string.register_success)).setTitleText(getString(R.string.action_register)).setConfirmClickListener {
                it.dismiss()
                finish()
            }.show()
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            binding.username.error = getString(R.string.error_incorrect_username_or_password)
        }
    }
}