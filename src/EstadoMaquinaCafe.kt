/**
 * Clase sellada que define todas las operaciones posibles de la máquina.
 * Cada estado sobrescribe solo las operaciones válidas para él.
 */
sealed class EstadoMaquinaCafe {
    open fun encender(maquina: MaquinaCafe) = println("Operación no válida en ${this::class.simpleName}")
    open fun insertarCredito(maquina: MaquinaCafe, cantidad: Double) = println("Operación no válida en ${this::class.simpleName}")
    open fun seleccionarProducto(maquina: MaquinaCafe, precio: Double, marca: String) = println("Operación no válida en ${this::class.simpleName}")
    open fun apagar(maquina: MaquinaCafe) = println("Operación no válida en ${this::class.simpleName}")
}


/**
 * Estado inicial de la máquina: apagada.
 * Solo permite encender.
 */
object Apagada : EstadoMaquinaCafe() {
    override fun encender(maquina: MaquinaCafe) {
        println("MÁQUINA ENCENDIDA")
        maquina.estado = SeleccionandoProducto
    }
}


/**
 * Estado donde se puede insertar crédito y seleccionar producto.
 */
object SeleccionandoProducto : EstadoMaquinaCafe() {
    override fun insertarCredito(maquina: MaquinaCafe, cantidad: Double) {
        if (cantidad > 0) {
            maquina.credito += cantidad
            println("Crédito insertado: €${"%.2f".format(cantidad)}")
        } else {
            println("Cantidad inválida")
        }
    }

    override fun seleccionarProducto(maquina: MaquinaCafe, precio: Double, marca: String) {
        if (maquina.credito >= precio) {
            println("Producto seleccionado: $marca (€$precio)")
            maquina.credito -= precio
            maquina.estado = PreparandoCafe(marca)
        } else {
            maquina.estado = Error("Crédito insuficiente")
        }
    }
}

/**
 * Estado que representa la preparación del café.
 * Mantiene información del producto.
 */
class PreparandoCafe(private val marca: String) : EstadoMaquinaCafe() {
    override fun encender(maquina: MaquinaCafe) = println("La máquina ya está encendida preparando café")
    override fun apagar(maquina: MaquinaCafe) = println("No se puede apagar mientras se prepara el café")

    override fun seleccionarProducto(maquina: MaquinaCafe, precio: Double, marca: String) {
        println("Preparando café $marca...")
        Thread.sleep(2000) // Simula tiempo de preparación
        maquina.estado = SirviendoCafe(this.marca, "Taza estándar")
    }
}

/**
 * Estado que representa el café servido.
 */
data class SirviendoCafe(val marca: String, val recipiente: String) : EstadoMaquinaCafe() {
    override fun apagar(maquina: MaquinaCafe) {
        println("Café servido en $recipiente. Máquina apagada.")
        maquina.estado = Apagada
    }
}

/**
 * Estado que captura errores y permite reinicio.
 */
data class Error(val mensaje: String) : EstadoMaquinaCafe() {
    override fun encender(maquina: MaquinaCafe) {
        println("Reiniciando máquina después del error: $mensaje")
        maquina.estado = SeleccionandoProducto
    }
}
