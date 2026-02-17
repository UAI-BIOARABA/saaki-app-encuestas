package org.bioaraba.saakiappencuestas.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.bioaraba.saakiappencuestas.R
import org.bioaraba.saakiappencuestas.utils.LocaleHelper
import org.bioaraba.saakiappencuestas.MainActivity

/**
 * Fragmento de Inicio de Sesión (Login).
 *
 * Es la primera pantalla que ve el usuario. Sus responsabilidades son:
 * 1. Autenticar al usuario mediante un código simple (validación de campo no vacío).
 * 2. Permitir el cambio de idioma de toda la aplicación (Castellano/Euskera).
 * 3. Iniciar la interacción por voz (TTS) dando la bienvenida.
 */
class LoginFragment : Fragment() {

    /**
     * Interfaz de comunicación con la Activity contenedora (MainActivity).
     * Obliga a la Activity a implementar la lógica de qué hacer cuando el login es exitoso.
     */
    interface LoginListener {
        /**
         * Se invoca cuando el usuario introduce un código válido y pulsa "Entrar".
         * @param code El código introducido por el usuario.
         */
        fun onLogin(code: String)
    }

    private var listener: LoginListener? = null

    /**
     * Ciclo de vida: Se vincula el fragmento a la actividad.
     * Verificamos que la Activity implemente la interfaz LoginListener para evitar
     * ClassCastException más adelante.
     */
    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        if (context is LoginListener) listener = context
    }

    /**
     * Ciclo de vida: El fragmento se desvincula.
     * Limpiamos la referencia al listener para evitar fugas de memoria (memory leaks).
     */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Ciclo de vida: Inflado de la vista (XML layout).
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    /**
     * Ciclo de vida: El fragmento es visible e interactivo.
     * Usamos este momento para activar el TTS (Texto a Voz) y dar la bienvenida,
     * asegurándonos de que la app ya está lista para "hablar".
     */
    override fun onResume() {
        super.onResume()
        // Casteamos la actividad a MainActivity para acceder a su método público 'speak'
        (activity as? MainActivity)?.speak(getString(R.string.ttswelcome))
    }

    /**
     * Configuración de la lógica de la interfaz (Botones, Listeners, etc).
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val editCode: EditText = view.findViewById(R.id.edit_code)
        val btnLogin: Button = view.findViewById(R.id.btn_login)

        // Configuración del botón de Login
        btnLogin.setOnClickListener {
            val code = editCode.text.toString().trim()

            // Validación simple: el código no puede estar vacío
            if (code.isEmpty()) {
                Toast.makeText(requireContext(), R.string.error_empty_code, Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Notificar a la MainActivity que el login fue correcto
                listener?.onLogin(code)
            }
        }

        // ========================================================================
        // GESTIÓN DE IDIOMAS
        // Al cambiar el idioma, es necesario recrear la Actividad para que
        // Android recargue los recursos (strings.xml) en el nuevo idioma.
        // ========================================================================

        // Botón idioma: Español
        val btnSpanish: Button = view.findViewById(R.id.btn_spanish)
        btnSpanish.setOnClickListener {
            LocaleHelper.setLocale(requireContext(), "es")
            requireActivity().recreate() // Recarga toda la UI en español
        }

        // Botón idioma: Euskera
        val btnEuskera: Button = view.findViewById(R.id.btn_euskera)
        btnEuskera.setOnClickListener {
            LocaleHelper.setLocale(requireContext(), "eu")
            requireActivity().recreate() // Recarga toda la UI en euskera
        }

        // ====================================================================================
        // HERRAMIENTAS DE DEPURACIÓN (DEBUG/ADMIN) - CÓDIGO COMENTADO
        //
        // Este bloque contiene funcionalidades para compartir o borrar los archivos CSV
        // generados internamente. Útil durante el desarrollo o soporte técnico, pero
        // deshabilitado para el usuario final.
        //
        // No borrar: puede ser necesario reactivarlo para extraer datos manualmente.
        // ====================================================================================
        /*
            // Botón compartir usuarios
            val botonUsuarios: Button? = view.findViewById(R.id.botonUsuarios)
            botonUsuarios?.setOnClickListener {
                // Localiza el archivo en el almacenamiento privado
                val file = File(requireContext().getExternalFilesDir(null), "usuarios.csv")
                if (file.exists()) {
                    // Genera una URI segura para compartir el archivo con otras apps (Gmail, Drive, etc.)
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.provider",
                        file
                    )

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    startActivity(Intent.createChooser(intent, "Compartir usuarios"))
                } else {
                    Toast.makeText(requireContext(), "No hay usuarios guardados todavía", Toast.LENGTH_SHORT).show()
                }
            }

            // Botón compartir respuestas
            val botonCompartir: Button? = view.findViewById(R.id.botonCompartir)
            botonCompartir?.setOnClickListener {
                val file = File(requireContext().getExternalFilesDir(null), "encuesta_b.csv")
                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.provider",
                        file
                    )

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    startActivity(Intent.createChooser(intent, "Compartir respuestas"))
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No hay respuestas guardadas todavía",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Botón para eliminar datos (Reset)
            val botonLimpiar = view.findViewById<Button>(R.id.botonLimpiar)
            botonLimpiar?.setOnClickListener {
                val file = File(requireContext().getExternalFilesDir(null), "encuesta_b.csv")
                if (file.exists()) {
                    if (file.delete()) {
                        Toast.makeText(requireContext(), "Archivo eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "No se pudo eliminar el archivo", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No hay archivo para eliminar", Toast.LENGTH_SHORT).show()
                }
            }*/
    }

}