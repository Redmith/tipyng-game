package com.tipeo.velocidad

/** Frases del juego (las mismas de la versión en Python). */
val FRASES = listOf(
    "El veloz murciélago hindú comía feliz cardillo y kiwi.",
    "La cigüeña tocaba el saxofón detrás del palenque de paja.",
    "El pingüino Wenceslao hizo kilómetros bajo exhaustiva lluvia y frío, ¡ni un refresco ha probado!",
    "La rápida liebre marrón salta sobre el perro perezoso.",
    "Pack my box with five dozen liquor jugs."
)

private val ESPACIOS = Regex("\\s+")

/** Equivalente a str.split() de Python: separa por cualquier espacio en blanco y descarta vacíos. */
private fun palabrasDe(texto: String): List<String> =
    texto.trim().split(ESPACIOS).filter { it.isNotEmpty() }

/** Palabras por minuto, contando las palabras que escribió el usuario. */
fun calcularVelocidad(tiempoSegundos: Double, textoEscrito: String): Int {
    if (tiempoSegundos <= 0.0) return 0
    val numPalabras = palabrasDe(textoEscrito).size
    return ((numPalabras / tiempoSegundos) * 60).toInt()
}

/** Porcentaje de palabras correctas, comparando posición por posición con el texto original. */
fun calcularPrecision(textoOriginal: String, textoEscrito: String): Double {
    val originales = palabrasDe(textoOriginal)
    val escritas = palabrasDe(textoEscrito)
    if (originales.isEmpty()) return 0.0

    var correctas = 0
    for (i in 0 until minOf(originales.size, escritas.size)) {
        if (originales[i] == escritas[i]) correctas++
    }
    return correctas.toDouble() / originales.size * 100
}
