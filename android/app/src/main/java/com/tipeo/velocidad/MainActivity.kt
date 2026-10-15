package com.tipeo.velocidad

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity() {

    private enum class Estado { LISTO, ESCRIBIENDO, RESULTADO }

    private var estado = Estado.LISTO
    private var frase = ""
    private var inicioMs: Long? = null

    private lateinit var tvFrase: TextView
    private lateinit var etEntrada: EditText
    private lateinit var btnAccion: Button
    private lateinit var tvResultados: TextView
    private lateinit var tvComparacion: TextView

    private fun dp(valor: Int) = (valor * resources.displayMetrics.density).toInt()

    private fun params(arriba: Int) = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply { topMargin = dp(arriba) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val titulo = TextView(this).apply {
            text = "Juego de Velocidad de Tipeo"
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
        }

        val instruccion = TextView(this).apply {
            text = "Escribí la siguiente frase lo más rápido y preciso posible. " +
                "El tiempo empieza a correr con la primera letra."
            textSize = 15f
        }

        tvFrase = TextView(this).apply {
            textSize = 20f
            setTextColor(0xFF111111.toInt())
            setBackgroundColor(0xFFEFEFEF.toInt())
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        etEntrada = EditText(this).apply {
            hint = "Tocá Comenzar y escribí acá"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            imeOptions = EditorInfo.IME_ACTION_DONE
            // Con estas dos líneas el texto salta de renglón y el teclado mantiene la tecla "Listo".
            setHorizontallyScrolling(false)
            maxLines = 4
            isEnabled = false
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (estado == Estado.ESCRIBIENDO && inicioMs == null && !s.isNullOrEmpty()) {
                        inicioMs = SystemClock.elapsedRealtime()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    terminar()
                    true
                } else {
                    false
                }
            }
        }

        btnAccion = Button(this).apply {
            setOnClickListener {
                when (estado) {
                    Estado.LISTO -> comenzar()
                    Estado.ESCRIBIENDO -> terminar()
                    Estado.RESULTADO -> nuevaRonda()
                }
            }
        }

        tvResultados = TextView(this).apply {
            textSize = 18f
            visibility = View.GONE
        }

        tvComparacion = TextView(this).apply {
            textSize = 15f
            visibility = View.GONE
        }

        val raiz = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(24))
            addView(titulo, params(0))
            addView(instruccion, params(8))
            addView(tvFrase, params(16))
            addView(etEntrada, params(16))
            addView(btnAccion, params(8))
            addView(tvResultados, params(16))
            addView(tvComparacion, params(12))
        }

        setContentView(ScrollView(this).apply {
            isFillViewport = true
            addView(raiz)
        })

        nuevaRonda()
    }

    private fun nuevaRonda() {
        val anterior = frase
        frase = FRASES.filter { it != anterior }.random()
        estado = Estado.LISTO
        inicioMs = null

        tvFrase.text = frase
        etEntrada.setText("")
        etEntrada.isEnabled = false
        tvResultados.visibility = View.GONE
        tvComparacion.visibility = View.GONE
        btnAccion.text = "Comenzar"
    }

    private fun comenzar() {
        estado = Estado.ESCRIBIENDO
        inicioMs = null
        etEntrada.isEnabled = true
        etEntrada.requestFocus()
        imm().showSoftInput(etEntrada, InputMethodManager.SHOW_IMPLICIT)
        btnAccion.text = "Terminar"
    }

    private fun terminar() {
        val inicio = inicioMs
        if (estado != Estado.ESCRIBIENDO) return
        if (inicio == null) {
            Toast.makeText(this, "Escribí algo primero", Toast.LENGTH_SHORT).show()
            return
        }

        val segundos = (SystemClock.elapsedRealtime() - inicio) / 1000.0
        val texto = etEntrada.text.toString()
        val ppm = calcularVelocidad(segundos, texto)
        val precision = calcularPrecision(frase, texto)

        estado = Estado.RESULTADO
        etEntrada.isEnabled = false
        imm().hideSoftInputFromWindow(etEntrada.windowToken, 0)

        tvResultados.text = String.format(
            Locale.getDefault(),
            "Tiempo total: %.2f segundos\nPalabras por minuto (PPM): %d\nPrecisión: %.2f%%",
            segundos, ppm, precision
        )
        tvResultados.visibility = View.VISIBLE

        if (texto.trim() != frase) {
            tvComparacion.text = "Texto original:\n$frase\n\nTu texto:\n$texto"
            tvComparacion.visibility = View.VISIBLE
        }

        btnAccion.text = "Jugar de nuevo"
    }

    private fun imm() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}
