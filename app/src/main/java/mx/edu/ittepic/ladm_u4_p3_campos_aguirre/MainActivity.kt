package mx.edu.ittepic.ladm_u4_p3_campos_aguirre

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    val siPermiso = 1
    val siPermisoReceiver = 2
    val siPermisoLectura = 3
    var mensajeTelefono = ""
    var numeroTelefono = ""
    var hilo : Hilo ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Pregunta si tiene otorgado un permiso PARA RECIBIR
        if(ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.RECEIVE_SMS), siPermisoReceiver)
        }

        //Pregunta si tiene otorgado un permiso PARA RECIBIR
        if(ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_SMS), siPermisoLectura)
        }

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS), siPermiso)
        }

        hilo = Hilo(this)
        hilo?.start()

    }

    fun EnviarMensaje() {
        baseRemota.collection("mensaje")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Toast.makeText(this, "Error, no existe conexion", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                for (document in querySnapshot!!) {
                    mensajeTelefono = document.getString("formato").toString()
                    numeroTelefono = document.getString("telefono").toString() //Telefono a quien le envias el formato
                    var cambio = mensajeTelefono
                    if (cambio != "") {
                        realizarConsulta()
                    } else {
                        limpiarCampos()
                    }
                }
            }
    }
     fun realizarConsulta() {
         var mensajeT = mensajeTelefono
         var array = mensajeT.split(" ")
            var mensajeR=""
         var numero = numeroTelefono
         if (array[0] == "Contraseña" && array.size == 2) {
             baseRemota.collection("usuario").whereEqualTo("email", array[1])
                 .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                     if (firebaseFirestoreException != null) {
                         return@addSnapshotListener
                     }
                     for (document in querySnapshot!!) {
                         mensajeR ="${document.getString("nombre")!!} \ntu contraseña del correo: ${document.getString("email")!!} \n es: ${document.getString("password")!!}"
                         if(mensajeR!=""){
                             envioSMS(mensajeR)
                         }
                     }
                 }

         }else{
             mensajeR = "FORMATO INCORRECTO. VERIFIQUE QUE SEA (Contraseña email)"
             envioSMS(mensajeR)
             limpiarCampos()
         }
     }
     fun envioSMS(m:String){
         var numero = numeroTelefono
        SmsManager.getDefault().sendTextMessage(numero, null, m, null, null)
        Toast.makeText(this,"SE ENVIO EL SMS", Toast.LENGTH_LONG)
            .show()
    }
    fun limpiarCampos(){
        baseRemota.collection("mensaje").document("u8qW2mza7MALhkboSgnG")
            .update(
                "telefono","", "formato", "")
    }
}

