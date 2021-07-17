package ar.com.develup.tateti.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ar.com.develup.tateti.R
import ar.com.develup.tateti.adaptadores.AdaptadorPartidas
import ar.com.develup.tateti.modelo.Constantes
import ar.com.develup.tateti.modelo.Partida
import com.firebase.ui.auth.AuthUI
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.actividad_partidas.*

class ActividadPartidas : AppCompatActivity() {

    companion object {
        private const val TAG = "ActividadPartidas"
    }
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var adaptadorPartidas: AdaptadorPartidas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_partidas)
        adaptadorPartidas = AdaptadorPartidas(this)
        partidas.layoutManager = LinearLayoutManager(this)
        partidas.adapter = adaptadorPartidas
        firebaseAnalytics = Firebase.analytics

        nuevaPartida.setOnClickListener { nuevaPartida() }

        logout.setOnClickListener {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.ITEM_NAME, "click cerrar session")
            }
            desloguearse()
        }

    }

    override fun onResume() {
        super.onResume()
        // TODO-06-DATABASE *LISTO*
        // Obtener una referencia a la base de datos, suscribirse a los cambios en Constantes.TABLA_PARTIDAS
        // y agregar como ChildEventListener el listenerTablaPartidas definido mas abajo
        FirebaseDatabase.getInstance().getReference().child(Constantes.TABLA_PARTIDAS).addChildEventListener(listenerTablaPartidas)

    }
    private fun desloguearse() {
        // TODO-05-AUTHENTICATION *LISTO*
        AuthUI.getInstance()
            .signOut(this).addOnCompleteListener {
                val intent = Intent(this, ActividadInicial::class.java)
                startActivity(intent)
            }
    }
        fun nuevaPartida() {
            val intent = Intent(this, ActividadPartida::class.java)
            startActivity(intent)
        }

        private val listenerTablaPartidas: ChildEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                Log.i(TAG, "onChildAdded: $dataSnapshot")
                val partida =
                    dataSnapshot.getValue(Partida::class.java)!! // Obtener el valor del dataSnapshot
                partida.id = dataSnapshot.key // Asignar el valor del campo "key" del dataSnapshot
                adaptadorPartidas.agregarPartida(partida)
            }


            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                Log.i(TAG, "onChildChanged: $s")
                val partida =
                    dataSnapshot.getValue(Partida::class.java)!! // Obtener el valor del dataSnapshot
                partida.id = dataSnapshot.key // Asignar el valor del campo "key" del dataSnapshot
                adaptadorPartidas.partidaCambio(partida)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.i(TAG, "onChildRemoved: ")
                val partida =
                    dataSnapshot.getValue(Partida::class.java)!! // Obtener el valor del dataSnapshot
                partida.id = dataSnapshot.key// Asignar el valor del campo "key" del dataSnapshot
                adaptadorPartidas.remover(partida)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                Log.i(TAG, "onChildMoved: $s")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i(TAG, "onCancelled: ")
            }
        }


}