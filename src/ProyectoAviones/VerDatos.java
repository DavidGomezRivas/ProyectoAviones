package ProyectoAviones;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
class VerDatos extends JFrame {
    // Datos de conexión a la base de datos
    static String url = "jdbc:mysql://monorail.proxy.rlwy.net:15847/";
    static String usuario = "root";
    static String contraseña = "-E3B6F3b-d5gAbchEbGFfBhdd6eCCH2e";
    static String dbName = "railway";
    private JTable table;

    public VerDatos(JFrame parent) {
        // Configuración básica de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setTitle("VerDatos");
        setLocationRelativeTo(null);

        // Panel superior con botón "Volver"
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVolver = new JButton("Volver");
        btnVolver.addActionListener(e -> {
            parent.setVisible(true);
            this.dispose();
        });
        panelSuperior.add(btnVolver);
        add(panelSuperior, BorderLayout.NORTH);

        // Inicializa y añade el JTable a un JScrollPane para permitir el desplazamiento
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Carga los datos de la base de datos en el JTable
        cargarDatos();
    }

    private void cargarDatos() {
        String query = "SELECT * FROM aviones"; // Consulta SQL para obtener todos los datos de la tabla 'aviones'
        try (Connection conn = DriverManager.getConnection(url + dbName, usuario, contraseña);
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            // Crea un modelo de tabla con nombres de columnas y lo asigna al JTable
            DefaultTableModel model = new DefaultTableModel(new String[] {
                "ID", "Avion", "Marca", "Cantidad Pasajeros", "Capacidad Combustible (litros)",
                "Peso Maximo Despegue (kg)", "Peso Maximo Aterrizaje (kg)", "Peso Vacio (kg)",
                "Alcance (km)", "Motor", "Velocidad de crucero (km/h)", "URL Imagen"}, 0);

            // Itera sobre los resultados de la consulta y añade filas al modelo de tabla
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("plane"),
                    rs.getString("brand"),
                    rs.getInt("passenger_capacity"),
                    rs.getInt("fuel_capacity_litres"),
                    rs.getInt("max_takeoff_weight_kg"),
                    rs.getInt("max_landing_weight_kg"),
                    rs.getInt("empty_weight_kg"),
                    rs.getInt("range_km"),
                    rs.getString("engine"),
                    rs.getInt("cruise_speed_kmph"),
                    rs.getString("imgThumb")
                });
            }
            table.setModel(model); // Asigna el modelo de tabla al JTable

            // Cierra el ResultSet después de su uso
            rs.close();
        } catch (SQLException e) {
            // Maneja y muestra los errores
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}