package com.example.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.intent.Constantes.PARAMETRO_EXTRA
import com.example.intent.databinding.ActivityParametroBinding

class ParametroActivity : AppCompatActivity() {

    private val apb: ActivityParametroBinding by lazy {
        ActivityParametroBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(apb.root)
        apb.mainTb.apply {
            title = getString(R.string.app_name)
            subtitle = this@ParametroActivity.javaClass.simpleName
            setSupportActionBar(this)
        }
        apb.enviarParametroBt.setOnClickListener{
            val parametro = apb.parametroEt.text.toString()
            val resultadoIntent = Intent()
            resultadoIntent.putExtra(PARAMETRO_EXTRA, parametro)
            setResult(RESULT_OK, resultadoIntent)
            finish()
        }
        intent.getStringExtra(PARAMETRO_EXTRA).let{
            apb.parametroEt.setText(it)
        }
    }
}