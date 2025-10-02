fun main() {
    val maquina = MaquinaCafe()
    maquina.mostrarEstado()

    println("\n--- Encendiendo la máquina ---")
    maquina.encender()
    println("\n--- Insertando crédito ---")
    maquina.insertarCredito(2.5)
    println("\n--- Seleccionando producto ---")
    maquina.seleccionarProducto(2.0, "Espresso")
    println("\n--- Apagando la máquina ---")
    maquina.apagar()
    println("\n--- Estado final ---")
    maquina.mostrarEstado()
}

