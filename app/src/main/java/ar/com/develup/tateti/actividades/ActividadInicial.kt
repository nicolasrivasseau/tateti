package ar.com.develup.tateti.actividades


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ar.com.develup.tateti.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.actividad_inicial.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class ActividadInicial : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_inicial)

        iniciarSesion.setOnClickListener { iniciarSesion() }
        registrate.setOnClickListener { registrate() }
        olvideMiContrasena.setOnClickListener { olvideMiContrasena() }
        firebaseAnalytics = Firebase.analytics


        // throw Exception("Hi There!") probando Crashlytics

        if (usuarioEstaLogueado()) {
            // Si el usuario esta logueado, se redirige a la pantalla
            // de partidas

            verPartidas()

            finish()
        }
        actualizarRemoteConfig()
    }

    private fun usuarioEstaLogueado(): Boolean {
        // TODO-05-AUTHENTICATION
        // Validar que currentUser sea != null
        return false
    }

    private fun verPartidas() {
        val intent = Intent(this, ActividadPartidas::class.java)
        startActivity(intent)
    }

    private fun registrate() {
        val intent = Intent(this, ActividadRegistracion::class.java)
        startActivity(intent)
    }

    private fun actualizarRemoteConfig() {
        configurarDefaultsRemoteConfig()
        configurarOlvideMiContrasena()
    }

    private fun configurarDefaultsRemoteConfig() {
        // TODO-04-REMOTECONFIG *LISTO*
        // Configurar los valores por default para remote config,
        // ya sea por codigo o por XML
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 5
            fetchTimeoutInSeconds = 15
        }
        Firebase.remoteConfig.setConfigSettingsAsync(settings)

        val defaults = mapOf(
            "botonOlvideHabilitado" to false
        )
        Firebase.remoteConfig.setDefaultsAsync(defaults)
    }

    private fun configurarOlvideMiContrasena() {
        // TODO-04-REMOTECONFIG *LISTO*
        // Obtener el valor de la configuracion para saber si mostrar
        // o no el boton de olvide mi contraseña

        Firebase.remoteConfig.fetchAndActivate()
            .addOnCompleteListener{

                val remoteConfig = Firebase.remoteConfig
                val botonOlvideHabilitado = remoteConfig.getBoolean("botonOlvideHabilitado")

                if (botonOlvideHabilitado) {
                    olvideMiContrasena.visibility = View.VISIBLE
                } else {
                    olvideMiContrasena.visibility = View.GONE
                }
            }
    }

    private fun olvideMiContrasena() {
        // Obtengo el mail
        val email = email.text.toString()

        // Si no completo el email, muestro mensaje de error
        if (email.isEmpty()) {
            Snackbar.make(rootView!!, "Completa el email", Snackbar.LENGTH_SHORT).show()
        } else {
            // TODO-05-AUTHENTICATION
            // Si completo el mail debo enviar un mail de reset
            // Para ello, utilizamos sendPasswordResetEmail con el email como parametro
            // Agregar el siguiente fragmento de codigo como CompleteListener, que notifica al usuario
            // el resultado de la operacion

            //  .addOnCompleteListener { task ->
            //      if (task.isSuccessful) {
            //          Snackbar.make(rootView, "Email enviado", Snackbar.LENGTH_SHORT).show()
            //      } else {
            //          Snackbar.make(rootView, "Error " + task.exception, Snackbar.LENGTH_SHORT).show()
            //      }
            //  }
        }
    }

    private fun iniciarSesion() {
        val email = email.text.toString()
        val password = password.text.toString()


        // TODO-05-AUTHENTICATION
        // IMPORTANTE: Eliminar  la siguiente linea cuando se implemente authentication
        verPartidas()


        // TODO-05-AUTHENTICATION
        // hacer signInWithEmailAndPassword con los valores ingresados de email y password
        // Agregar en addOnCompleteListener el campo authenticationListener definido mas abajo
    }

    //    private val authenticationListener: OnCompleteListener<AuthResult?> = OnCompleteListener<AuthResult?> { task ->
    //        if (task.isSuccessful) {
    //            if (usuarioVerificoEmail()) {
    //                verPartidas()
    //            } else {
    //                desloguearse()
    //                Snackbar.make(rootView!!, "Verifica tu email para continuar", Snackbar.LENGTH_SHORT).show()
    //            }
    //        } else {
    //            if (task.exception is FirebaseAuthInvalidUserException) {
    //                Snackbar.make(rootView!!, "El usuario no existe", Snackbar.LENGTH_SHORT).show()
    //            } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
    //                Snackbar.make(rootView!!, "Credenciales inválidas", Snackbar.LENGTH_SHORT).show()
    //            }
    //        }
    //    }

    private fun usuarioVerificoEmail(): Boolean {
        // TODO-05-AUTHENTICATION
        // Preguntar al currentUser si verifico email
        return false
    }

    private fun desloguearse() {
        // TODO-05-AUTHENTICATION
        // Hacer signOut de Firebase
    }
}


