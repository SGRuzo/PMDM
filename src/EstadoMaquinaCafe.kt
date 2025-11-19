sealed class EstadoMaquinaCafe {
    open fun encender(maquina: MaquinaCafe) = println("Operación no válida en ${this::class.simpleName}")
    open fun insertarCredito(maquina: MaquinaCafe, cantidad: Double) = println("Operación no válida en ${this::class.simpleName}")
    open fun seleccionarProducto(maquina: MaquinaCafe, precio: Double, marca: String) = println("Operación no válida en ${this::class.simpleName}")
    open fun apagar(maquina: MaquinaCafe) = println("Operación no válida en ${this::class.simpleName}")

    /** Llamado automáticamente al entrar en el estado */
    open fun onEnter(maquina: MaquinaCafe) { /* comportamiento por defecto */ }
}

object Apagada : EstadoMaquinaCafe() {
    override fun encender(maquina: MaquinaCafe) {
        println("MÁQUINA ENCENDIDA")
        maquina.setEstado(SeleccionandoProducto)
    }

    override fun onEnter(maquina: MaquinaCafe) {
        println("Entrando en estado Apagada")
    }
}

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
            println("Producto seleccionado: $marca (€${"%.2f".format(precio)})")
            maquina.credito -= precio
            // arrancar la preparación sin bloquear
            maquina.setEstado(PreparandoCafe(marca))
        } else {
            maquina.setEstado(Error("Crédito insuficiente"))
        }
    }

    override fun onEnter(maquina: MaquinaCafe) {
        println("Esperando selección. Crédito actual: €${"%.2f".format(maquina.credito)}")
    }
}

class PreparandoCafe(private val marca: String) : EstadoMaquinaCafe() {
    override fun encender(maquina: MaquinaCafe) = println("La máquina ya está encendida preparando café")
    override fun apagar(maquina: MaquinaCafe) = println("No se puede apagar mientras se prepara el café")

    override fun onEnter(maquina: MaquinaCafe) {
        println("Preparando café $marca...")
        Thread {
            try {
                Thread.sleep(2000) // simulación de preparación en background
                maquina.setEstado(SirviendoCafe(this.marca, "Taza estándar"))
            } catch (e: InterruptedException) {
                maquina.setEstado(Error("Preparación interrumpida"))
            } catch (t: Throwable) {
                maquina.setEstado(Error("Error durante preparación: ${t.message}"))
            }
        }.start()
    }
}

data class SirviendoCafe(val marca: String, val recipiente: String) : EstadoMaquinaCafe() {
    override fun apagar(maquina: MaquinaCafe) {
        println("Café servido en $recipiente. Máquina apagada.")
        maquina.setEstado(Apagada)
    }

    override fun onEnter(maquina: MaquinaCafe) {
        println("Sirviendo café: $marca en $recipiente. Toma tu bebida.")
    }
}

data class Error(val mensaje: String) : EstadoMaquinaCafe() {
    override fun encender(maquina: MaquinaCafe) {
        println("Reiniciando máquina después del error: $mensaje")
        maquina.setEstado(SeleccionandoProducto)
    }

    override fun onEnter(maquina: MaquinaCafe) {
        println("Estado Error: $mensaje")
    }
}