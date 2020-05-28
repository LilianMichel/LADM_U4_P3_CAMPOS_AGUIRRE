package mx.edu.ittepic.ladm_u4_p3_campos_aguirre

class Hilo(p:MainActivity) : Thread() {
    var puntero = p

    override fun run() {
        super.run()
        while (true){
            sleep(18000)
            puntero.runOnUiThread {
                puntero.EnviarMensaje()
            }
        }
    }

}