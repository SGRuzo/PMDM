/**
 * Clase principal de la máquina de café.
 * Mantiene el estado actual y el crédito.
 */
class MaquinaCafe {
    var estado: EstadoMaquinaCafe = Apagada
    var credito: Double = 0.0

    // Delegación de operaciones al estado actual
    fun encender() = estado.encender(this)
    fun insertarCredito(cantidad: Double) = estado.insertarCredito(this, cantidad)
    fun seleccionarProducto(precio: Double, marca: String) = estado.seleccionarProducto(this, precio, marca)
    fun apagar() = estado.apagar(this)

    fun mostrarEstado() {
        println("ESTADO: ${estado::class.simpleName}, CRÉDITO: €${"%.2f".format(credito)}")
    }
}