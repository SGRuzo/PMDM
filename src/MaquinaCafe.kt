
object MaquinaCafe {
    var estado: EstadoMaquinaCafe = Apagada
        private set

    var credito: Double = 0.0

    @Synchronized
    fun setEstado(nuevo: EstadoMaquinaCafe) {
        if (isValidTransition(estado, nuevo)) {
            estado = nuevo
            println("[MaquinaCafe] Estado cambiado a ${estado::class.simpleName}")
            try {
                estado.onEnter(this)
            } catch (t: Throwable) {
                println("[MaquinaCafe] Error en onEnter: ${t.message}")
                estado = Error("Excepción en onEnter")
            }
        } else {
            println("Transición inválida de ${estado::class.simpleName} a ${nuevo::class.simpleName}")
        }
    }

    private fun isValidTransition(from: EstadoMaquinaCafe, to: EstadoMaquinaCafe): Boolean {
        return when (from) {
            is Apagada -> to is SeleccionandoProducto
            is SeleccionandoProducto -> to is PreparandoCafe || to is Error
            is PreparandoCafe -> to is SirviendoCafe || to is Error
            is SirviendoCafe -> to is Apagada || to is SeleccionandoProducto
            is Error -> to is Apagada || to is SeleccionandoProducto
            else -> false
        }
    }

    // Delegación de operaciones al estado actual
    fun encender() = estado.encender(this)
    fun insertarCredito(cantidad: Double) = estado.insertarCredito(this, cantidad)
    fun seleccionarProducto(precio: Double, marca: String) = estado.seleccionarProducto(this, precio, marca)
    fun apagar() = estado.apagar(this)

    fun mostrarEstado() {
        println("ESTADO: ${estado::class.simpleName}, CRÉDITO: €${"%.2f".format(credito)}")
    }
}