package ar.com.develup.tateti.actividades


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ar.com.develup.tateti.R
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.actividad_inicial.*
import kotlinx.android.synthetic.main.actividad_partidas.*
import java.text.DateFormat
import java.util.*
import java.util.regex.Pattern


class ActividadInicial : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_inicial)

        iniciarSesion.setOnClickListener { iniciarSesion()

            firebaseAnalytics.logEvent("session hora"){
                param("hora",DateFormat.getDateTimeInstance().format(Date()))
            }
        }
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

        Firebase.messaging.token.addOnCompleteListener {
            if (it.isSuccessful) {
// En este momento conocemos el valor del token
                Log.d("Notificaciones", it.result!!)
            }
        }
    }



    private fun usuarioEstaLogueado(): Boolean {
        // TODO-05-AUTHENTICATION *listo*
        val user = FirebaseAuth.getInstance().currentUser
        var userIsLog = false
        if(user != null){
            userIsLog = true
        }
        // Validar que currentUser sea != null
        return userIsLog
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
            // TODO-05-AUTHENTICATION *listo*
            // Si completo el mail debo enviar un mail de reset
            // Para ello, utilizamos sendPasswordResetEmail con el email como parametro
            // Agregar el siguiente fragmento de codigo como CompleteListener, que notifica al usuario
            // el resultado de la operacion

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      Snackbar.make(rootView, "Email enviado", Snackbar.LENGTH_SHORT).show()
                  } else {
                      Snackbar.make(rootView, "Error " + task.exception, Snackbar.LENGTH_SHORT).show()
                  }
              }
        }
    }

    private fun validarEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun validarPassword(password: String): Boolean {
        var passwordValida = false
        if(password != "" && password != null){
            passwordValida = true
        }
        return passwordValida
    }



    private fun iniciarSesion() {
        val email = email.text.toString()
        val password = password.text.toString()

        // TODO-05-AUTHENTICATION *LISTO*
        // IMPORTANTE: Eliminar  la siguiente linea cuando se implemente authentication
        // verPartidas()





        if (validarEmail(email)) {
            if (validarPassword(password)) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(authenticationListener)
            } else {
                Snackbar.make(
                    rootView!!,
                    "El campo contrasenia no puede estar vacio",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            Snackbar.make(
                rootView!!,
                "Verifique que ha ingresado un formato valido de mail o que el campo no este vacio",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        if (validarEmail(email)) {
            if (validarPassword(password)) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(authenticationListener)
            } else {
                Snackbar.make(
                    rootView!!,
                    "El campo contrasenia no puede estar vacio",
                    Snackbar.LENGTH_SHORT
                ).show()
                firebaseAnalytics.logEvent("intento de iniciar session con mail vacio") {
                    param("email", email)
                }
            }
        } else {
            Snackbar.make(
                rootView!!,
                "Verifique que ha ingresado un formato valido de mail o que el campo no este vacio",
                Snackbar.LENGTH_SHORT
            ).show()
            firebaseAnalytics.logEvent("intento de iniciar session con mail invalido") {
                param("email", email)
            }


            // TODO-05-AUTHENTICATION *LISTO*
            // hacer signInWithEmailAndPassword con los valores ingresados de email y password
            // Agregar en addOnCompleteListener el campo authenticationListener definido mas abajo
        }
    }

        private val authenticationListener: OnCompleteListener<AuthResult?> =
            OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    if (usuarioVerificoEmail()) {
                        verPartidas()
                    } else {
                        desloguearse()
                        Snackbar.make(
                            rootView!!,
                            "Verifica tu email para continuar",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        Snackbar.make(rootView!!, "El usuario no existe", Snackbar.LENGTH_SHORT)
                            .show()
                    } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(rootView!!, "Credenciales inválidas", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        private fun usuarioVerificoEmail(): Boolean {
            // TODO-05-AUTHENTICATION *LISTO*
            val verificoEmail = FirebaseAuth.getInstance().currentUser!!.isEmailVerified
            // Preguntar al currentUser si verifico email
            return verificoEmail
        }

     private fun desloguearse() {
         // TODO-05-AUTHENTICATION *LISTO*
         AuthUI.getInstance()
             .signOut(this).addOnCompleteListener {
         val intent = Intent(this, this::class.java)
         startActivity(intent)
     }
            // Hacer signOut de Firebase
        }
    }




