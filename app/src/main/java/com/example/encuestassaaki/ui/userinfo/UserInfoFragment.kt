package com.example.encuestassaaki.ui.userinfo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.encuestassaaki.MainActivity
import com.example.encuestassaaki.R
import com.example.encuestassaaki.ui.selection.SurveySelectionFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragmento de Información del Usuario.
 *
 * Pantalla encargada de recoger o mostrar los datos demográficos del usuario
 * (Año de nacimiento y Sexo) asociados al código introducido en el Login.
 *
 * Funcionalidades clave:
 * 1. Verifica en el CSV local si el usuario ya existe.
 * 2. Si existe: Muestra los datos y bloquea la edición (modo lectura).
 * 3. Si no existe: Permite introducir datos y los guarda en `usuarios.csv`.
 * 4. Normaliza los datos (siempre guarda sexo en castellano) para consistencia en el CSV.
 */
class UserInfoFragment : Fragment() {

    private var code: String? = null

    /**
     * Patrón Factory para crear instancias del fragmento.
     * Permite pasar argumentos (el código de usuario) de forma segura para sobrevivir
     * a la recreación del fragmento por el sistema.
     */
    companion object {
        private const val ARG_CODE = "arg_code"

        fun newInstance(code: String): UserInfoFragment {
            val fragment = UserInfoFragment()
            val args = Bundle()
            args.putString(ARG_CODE, code)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * Interfaz para devolver los datos validados a la MainActivity.
     * La Activity guardará estos datos en variables globales para usarlos en las siguientes encuestas.
     */
    interface UserInfoListener {
        fun onUserInfoSaved(code: String, year: String, sex: String)
    }

    private var listener: UserInfoListener? = null

    // Ciclo de vida: Vincular listener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UserInfoListener) listener = context
    }

    // Ciclo de vida: Desvincular listener
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // Ciclo de vida: Recuperar argumentos (código de usuario)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        code = arguments?.getString(ARG_CODE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_userinfo, container, false)
    }

    /**
     * Al mostrarse la vista, activamos el TTS para leer las instrucciones
     * correspondientes a esta pantalla.
     */
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.speak(getString(R.string.ttsdata))
    }

    /**
     * Lógica principal de la interfaz y gestión de archivos.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val txtCode: TextView = view.findViewById(R.id.txt_code)
        val editYear: EditText = view.findViewById(R.id.edit_year)
        val spinnerSex: Spinner = view.findViewById(R.id.spinner_sex)
        val textDate: TextView = view.findViewById(R.id.text_date)
        val btnSave: Button = view.findViewById(R.id.btn_save)

        // Mostrar fecha actual en formato dd/MM/yyyy
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        textDate.text = getString(R.string.today_date, today)

        // Mostrar el código de usuario recibido
        txtCode.text = getString(R.string.user_code, code)

        // Configurar el desplegable (Spinner) de sexo
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sex_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSex.adapter = adapter

        // ========================================================================
        // LECTURA DE DATOS EXISTENTES
        // Buscamos si el usuario ya está registrado en "usuarios.csv"
        // Ruta: /Android/data/com.example.encuestassaaki/files/usuarios.csv
        // ========================================================================
        val file = File(requireContext().getExternalFilesDir(null), "usuarios.csv")

        var userExists = false

        if (file.exists()) {
            file.forEachLine { line ->
                val parts = line.split(",")
                // Formato esperado CSV: codigo,año,sexo
                if (parts.isNotEmpty() && parts[0] == code) {
                    // El usuario existe, recuperamos sus datos
                    if (parts.size >= 3) {
                        editYear.setText(parts[1])
                        val sex = parts[2]

                        // Intentamos seleccionar el valor correcto en el spinner
                        val sexIndex = resources.getStringArray(R.array.sex_options).indexOf(sex)
                        if (sexIndex >= 0) spinnerSex.setSelection(sexIndex)

                        // BLOQUEO DE EDICIÓN:
                        // Si el usuario ya existe, no permitimos modificar sus datos demográficos
                        // para asegurar la integridad de los datos históricos.
                        editYear.isEnabled = false
                        spinnerSex.isEnabled = false
                        userExists = true
                    }
                }
            }
        }

        // ========================================================================
        // BOTÓN GUARDAR / CONTINUAR
        // ========================================================================
        btnSave.setOnClickListener {
            val year = editYear.text.toString().trim()
            val selectedSex = spinnerSex.selectedItem.toString()

            // IMPORTANTE: Normalizamos el sexo a Español antes de guardar
            val sex = normalizeSexToSpanish(selectedSex)

            // Solo escribimos en el archivo si es un usuario nuevo (o no existía antes)
            if (!userExists) {
                val lines = mutableListOf<String>()
                var updated = false

                // Leemos todo el archivo actual para reconstruirlo
                if (file.exists()) {
                    file.forEachLine { line ->
                        val parts = line.split(",")
                        // Si por casualidad el código ya estaba (caso raro aquí), lo actualizamos
                        if (parts.isNotEmpty() && parts[0] == code) {
                            lines.add("${clean(code)},${clean(year)},${clean(sex)}")
                            updated = true
                        } else {
                            lines.add(line) // Mantenemos las líneas de otros usuarios
                        }
                    }
                } else {
                    // Si el archivo no existe, creamos la primera línea de cabecera
                    lines.add("codigo,año,sexo")
                }

                // Si no se actualizó una línea existente, agregamos el nuevo usuario al final
                if (!updated) {
                    lines.add("${clean(code)},${clean(year)},${clean(sex)}")
                }

                // Reescribimos el archivo completo con los nuevos datos
                file.writeText(lines.joinToString("\n"))

                // ================================================================
                // COPIA DE SEGURIDAD (.bak)
                // Se crea una copia inmediata por seguridad de datos.
                // ================================================================
                val bakFile = File(requireContext().getExternalFilesDir(null), "us.bak")
                bakFile.writeText(lines.joinToString("\n"))


                Toast.makeText(requireContext(), getString(R.string.data_saved), Toast.LENGTH_SHORT).show()
            }

            // Notificar a MainActivity para que actualice sus variables globales
            listener?.onUserInfoSaved(code ?: "", year, sex)

            // Navegar a la siguiente pantalla
            goToSurveySelection()
        }

    }

    /**
     * Helper para realizar la transacción del fragmento hacia la selección de encuesta.
     */
    private fun goToSurveySelection() {
        val fragment = SurveySelectionFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Normaliza el valor del sexo para el almacenamiento en CSV.
     *
     * Problema: Si la app está en Euskera, el spinner devuelve "Gizonezkoa".
     * Solución: Traducimos manualmente a "Masculino" antes de guardar.
     * Esto asegura que el CSV siempre esté en Castellano, facilitando el análisis de datos posterior.
     *
     * @param sexValue Valor obtenido del Spinner (puede estar en ES o EU).
     * @return String normalizado ("Masculino" o "Femenino" o el valor original si no coincide).
     */
    private fun normalizeSexToSpanish(sexValue: String): String {
        return when (sexValue.lowercase(Locale.ROOT)) {
            "gizonezkoa" -> "Masculino"
            "emakumezkoa" -> "Femenino"
            else -> sexValue // por si acaso ya viene en castellano
        }
    }

    /**
     * Función para limpiar imputs que irán al CSV.
     * Sirve para evitar que el archivo se rompa si alguien introduce caracteres que no debería
     * @param input Texto de un input
     * @return El string limpio para evitar que rompa CSVs
     */
    private fun clean(input: String?): String {
        return input?.replace(",", ".")   // Cambia comas por puntos
            ?.replace("\n", " ")   // Quita saltos de línea
            ?.trim()               // Quita espacios extra
            ?: ""
    }

}