package ProyectoAviones;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

//Suprime advertencias de serialización
@SuppressWarnings("serial")
class AgregarDatos extends JFrame {
 // Datos de conexión a la base de datos
 static String url = "jdbc:mysql://monorail.proxy.rlwy.net:15847/";
 static String usuario = "root";
 static String contraseña = "-E3B6F3b-d5gAbchEbGFfBhdd6eCCH2e";
 static String dbName = "railway";

 // Lista para almacenar los campos de texto dinámicamente creados
 private ArrayList<JTextField> textFields = new ArrayList<>();

 public AgregarDatos(JFrame parent) {
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configura la operación de cierre por defecto
     setSize(400, 600); // Establece el tamaño de la ventana
     setTitle("AgregarDatos"); // Establece el título de la ventana
     setLocationRelativeTo(null); // Centra la ventana en la pantalla

     JPanel panelPrincipal = new JPanel(new BorderLayout()); // Panel principal con diseño BorderLayout

     // Panel para los campos de entrada con diseño GridLayout para 13 filas y 2 columnas
     JPanel panelCampos = new JPanel(new GridLayout(13, 2, 5, 5)); 

     // Agregar campos de texto y sus etiquetas correspondientes al panel de campos
     agregarCampo("ID:", panelCampos);
        agregarCampo("Avion:", panelCampos);
        agregarCampo("Marca:", panelCampos);
        agregarCampo("Capacidad de Pasajeros:", panelCampos);
        agregarCampo("Capacidad Combustible (Litros):", panelCampos);
        agregarCampo("Peso Maximo Despegue(kg):", panelCampos);
        agregarCampo("Peso Maximo Aterrizaje(kg):", panelCampos);
        agregarCampo("Peso Bacio(kg):", panelCampos);
        agregarCampo("Rango (km):", panelCampos);
        agregarCampo("Motor:", panelCampos);
        agregarCampo("Velocidad de Crucero (km/h):", panelCampos);
        agregarCampo("URL Imagen:", panelCampos);

        JButton btnAgregarDatos = new JButton("Agregar Datos");
        btnAgregarDatos.addActionListener(e -> insertarDatos()); // Acción para insertar datos en la base

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel inferior para el botón
        panelInferior.add(btnAgregarDatos);

        JButton btnVolver = new JButton("Volver"); // Botón para volver a la ventana principal
        btnVolver.addActionListener(e -> {
            parent.setVisible(true); // Hace visible la ventana principal
            this.dispose(); // Cierra la ventana actual
        });

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Panel superior para el botón de volver
        panelSuperior.add(btnVolver);

        // Agrega los paneles al panel principal en las posiciones correspondientes
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelCampos, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal); // Agrega el panel principal a la ventana
    }

    // Método para agregar un campo de texto y su etiqueta al panel
    private void agregarCampo(String nombre, JPanel panel) {
        JLabel label = new JLabel(nombre); // Crea una etiqueta con el nombre especificado
        JTextField textField = new JTextField(20); // Crea un campo de texto
        panel.add(label); // Agrega la etiqueta al panel
        panel.add(textField); // Agrega el campo de texto al panel
        textFields.add(textField); // Añade el campo de texto a la lista para su posterior uso
    }

    // Método para insertar los datos de los campos de texto en la base de datos
    private void insertarDatos() {
        // Consulta SQL para insertar los datos en la tabla 'aviones'
        String sql = "INSERT INTO aviones (id, plane, brand, passenger_capacity, fuel_capacity_litres, max_takeoff_weight_kg, max_landing_weight_kg, empty_weight_kg, range_km, engine, cruise_speed_kmph, imgThumb) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(url + dbName, usuario, contraseña);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < textFields.size(); i++) {
                pstmt.setString(i + 1, textFields.get(i).getText()); // Establece los valores de los campos de texto en la consulta
            }
            pstmt.executeUpdate(); // Ejecuta la consulta
            JOptionPane.showMessageDialog(this, "Datos agregados con éxito"); // Muestra un mensaje de éxito
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar datos: " + e.getMessage()); // Muestra un mensaje de error
        }
    }
}