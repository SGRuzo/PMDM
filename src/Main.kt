fun main() {
    MaquinaCafe.mostrarEstado()

    println("--- Encendiendo la máquina ---")
    MaquinaCafe.encender()
    println("--- Insertando crédito ---")
    MaquinaCafe.insertarCredito(2.5)
    println("--- Seleccionando producto ---")
    MaquinaCafe.seleccionarProducto(2.0, "Espresso")
    println("--- Intentando apagar (mientras sirve o después) ---")
    MaquinaCafe.apagar()
    println("--- Estado final ---")
    MaquinaCafe.mostrarEstado()
}
