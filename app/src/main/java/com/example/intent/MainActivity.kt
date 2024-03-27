package com.example.intent

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.intent.Constantes.PARAMETRO_EXTRA
import com.example.intent.databinding.ActivityMainBinding
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }


    private lateinit var parl: ActivityResultLauncher<Intent>

    private lateinit var pcarl: ActivityResultLauncher<String>

    private lateinit var piarl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        amb.mainTb.apply {
            title = getString(R.string.app_name)
            subtitle = this@MainActivity.javaClass.simpleName
            setSupportActionBar(this)
        }
        amb.entrarParametroBt.setOnClickListener {

            Intent(this, ParametroActivity::class.java).also {
                it.putExtra(PARAMETRO_EXTRA, amb.parametroTv.text.toString())
                parl.launch(it)
            }
        }

        parl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra(PARAMETRO_EXTRA)?.let {
                    amb.parametroTv.text = it
                }
            }
        }
        pcarl = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            permissionConcedida ->
            if (permissionConcedida){
                // REALIZAR A CHAMADA
                chamarNumero(chamar = true)
            }
            else {
                Toast.makeText(this, "Permissão necessária para continuar", Toast.LENGTH_SHORT).show()
            }
        }
        piarl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ resultado ->
                if (resultado.resultCode == RESULT_OK) {
                    resultado.data?.data?.let { imagemUri ->
                        amb.parametroTv.text = imagemUri.toString()
                        Intent(ACTION_VIEW, imagemUri).also {
                            startActivity(it)
                        }
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.viewMi -> {
//                val url: Uri = Uri.parse(amb.parametroTv.text.toString())
//                val navagadorIntent: Intent = Intent(ACTION_VIEW, url)
//                startActivity(navagadorIntent)

                Uri.parse(amb.parametroTv.text.toString()).let {
                    Intent(ACTION_VIEW, it).also {
                        startActivity(it)
                    }
                }
                true
            }
            R.id.callMi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        chamarNumero(chamar = true)
                    }else{
                        pcarl.launch(CALL_PHONE)
                    }
                }else{
                    chamarNumero(chamar = true)
                }
                true
            }

            R.id.dialMi -> {
                chamarNumero(chamar = false)
                true
            }
            R.id.pickMi -> {
                val pegarImagemIntent = Intent(ACTION_PICK)
                val diretorioImagens = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pegarImagemIntent.setDataAndType(Uri.parse(diretorioImagens), "image/")
                piarl.launch(pegarImagemIntent)

                true
            }
            R.id.chooseMi -> {
                Uri.parse(amb.parametroTv.text.toString()).let {uri ->
                    Intent(ACTION_VIEW, uri).also {navegadorIntent ->
                       val escolherAppIntent = Intent(ACTION_CHOOSER)
                        escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha seu navegador favorito")
                        escolherAppIntent.putExtra(EXTRA_INTENT, navegadorIntent)
                        startActivity(escolherAppIntent)
                    }
                }
                true
            }
            else -> { false }
        }
    }

    private fun chamarNumero(chamar: Boolean){
        val numeroUri: Uri = Uri.parse("tel: ${amb.parametroTv.text}")
        val chamarIntent: Intent = Intent(if(chamar) ACTION_CALL else ACTION_DIAL)
        chamarIntent.data = numeroUri
        startActivity(chamarIntent)
    }

}