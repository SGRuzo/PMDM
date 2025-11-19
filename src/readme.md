# Máquina de Café

Descripción breve  
Máquina de café implementada con una máquina de estados basada en una `sealed class`. Estados disponibles: `Apagada`, `SeleccionandoProducto`, `PreparandoCafe`, `SirviendoCafe` y `Error`. La lógica central está en `MaquinaCafe` y los comportamientos por estado en `EstadoMaquinaCafe.kt`. Punto de entrada: `Main.kt`.

## Archivos
- `src/EstadoMaquinaCafe.kt` — definición de la `sealed class` y los estados.
- `src/MaquinaCafe.kt` — singleton que gestiona el estado y delega operaciones.
- `src/Main.kt` — ejemplo de uso / flujo de ejecución.

## Flujo de ejecución (resumen)
1. `main`:
   - Llama a `MaquinaCafe.mostrarEstado()` para mostrar estado y crédito inicial.
   - Llama a `MaquinaCafe.encender()`.

2. Encendido:
   - `Apagada.encender` cambia a `SeleccionandoProducto` mediante `MaquinaCafe.setEstado(...)`.

3. Inserción de crédito:
   - `MaquinaCafe.insertarCredito(cantidad)` delega a `SeleccionandoProducto.insertarCredito`.
   - Se actualiza `MaquinaCafe.credito` si la cantidad es válida.

4. Selección de producto:
   - `SeleccionandoProducto.seleccionarProducto(precio, marca)` verifica crédito.
   - Si hay suficiente, descuenta el precio y cambia a `PreparandoCafe(marca)`.
   - Si no, cambia a `Error("Crédito insuficiente")`.

5. Preparación (asíncrona):
   - `PreparandoCafe.onEnter` lanza un `Thread` que simula la preparación y, tras un retardo, hace `MaquinaCafe.setEstado(SirviendoCafe(...))`.

6. Servicio y apagado:
   - `SirviendoCafe.onEnter` muestra mensaje. `SirviendoCafe.apagar` vuelve a `Apagada`.

7. Errores:
   - `Error.onEnter` muestra el mensaje. `Error.encender` permite volver a `SeleccionandoProducto`.

## Detalles técnicos
- `sealed class EstadoMaquinaCafe` obliga a manejar todos los subtipos en `when`.
- Estados sin datos se modelan con `object` (`Apagada`, `SeleccionandoProducto`).
- Estados con datos usan `class` o `data class` (`PreparandoCafe`, `SirviendoCafe`, `Error`).
- `MaquinaCafe.estado` tiene `private set` y todas las transiciones pasan por `setEstado`, que valida transiciones usando `is`.
- `PreparandoCafe` usa un `Thread` para simular trabajo en background (no se usan coroutines para simplicidad).

## Validación de transiciones
`MaquinaCafe.isValidTransition` permite solo transiciones válidas por tipo, por ejemplo:
- `Apagada -> SeleccionandoProducto`
- `SeleccionandoProducto -> PreparandoCafe | Error`
- `PreparandoCafe -> SirviendoCafe | Error`
- `SirviendoCafe -> Apagada | SeleccionandoProducto`
- `Error -> Apagada | SeleccionandoProducto`

## Diagrama de estados

```mermaid
stateDiagram-v2
direction TB
[*] --> Apagada

state Apagada {
  entry / println("Entrando en estado Apagada")
}

Apagada --> SeleccionandoProducto : encender

state SeleccionandoProducto as "SeleccionandoProducto" {
  entry / println("Esperando selección. Crédito actual: €")
}

SeleccionandoProducto --> SeleccionandoProducto : insertarCredito / credito += cantidad
SeleccionandoProducto --> PreparandoCafe_marca : seleccionarProducto (si credito >= precio)\nacción: credito -= precio
SeleccionandoProducto --> Error_mensaje : seleccionarProducto (si credito < precio)

state PreparandoCafe_marca as "PreparandoCafe(marca)" {
  entry / iniciar Thread (simula preparación)
}

PreparandoCafe_marca --> SirviendoCafe_marca_recipiente : preparación completada (hilo)
PreparandoCafe_marca --> Error_mensaje : excepción / preparación interrumpida

state SirviendoCafe_marca_recipiente as "SirviendoCafe(marca, recipiente)" {
  entry / println("Sirviendo café")
}

SirviendoCafe_marca_recipiente --> Apagada : apagar / println("Café servido. Máquina apagada.")
SirviendoCafe_marca_recipiente --> SeleccionandoProducto : listo para nueva selección

state Error_mensaje as "Error(mensaje)" {
  entry / println("Estado Error")
}

Error_mensaje --> SeleccionandoProducto : encender / reiniciar después del error
Error_mensaje --> Apagada : (válido según isValidTransition)

note right of PreparandoCafe_marca
Preparación asíncrona: entry lanza un Thread que tras sleep() llama a setEstado(SirviendoCafe(...)).
end note
```


