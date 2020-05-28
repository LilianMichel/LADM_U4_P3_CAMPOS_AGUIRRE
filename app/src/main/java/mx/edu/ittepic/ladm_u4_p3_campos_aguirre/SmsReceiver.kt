package mx.edu.ittepic.ladm_u4_p3_campos_aguirre

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import com.google.firebase.firestore.FirebaseFirestore

class SmsReceiver:BroadcastReceiver() {
    var baseRemota = FirebaseFirestore.getInstance()

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras

        if (extras != null) {
            var sms = extras.get("pdus") as Array<Any>//Nombre del extra

            for (indice in sms.indices) {

                var formato = extras.getString("format")
                var smsMensaje = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                } else {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()
                baseRemota.collection("mensaje").document("u8qW2mza7MALhkboSgnG")
                    .update(
                        "telefono",celularOrigen, "formato", contenidoSMS)
            }
        }

    }
}