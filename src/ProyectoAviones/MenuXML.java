package ProyectoAviones;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings("serial")
class MenuXML extends JFrame {
    // Datos de conexión a la base de datos
    static String url = "jdbc:mysql://monorail.proxy.rlwy.net:15847/";
    static String usuario = "root";
    static String contraseña = "-E3B6F3b-d5gAbchEbGFfBhdd6eCCH2e";
    static String dbName = "railway";

    public MenuXML(JFrame parent) {
        // Configuración básica de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setTitle("Menu XML");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel para botones "Leer" y "Guardar"
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        JButton btnLeer = new JButton("Leer");
        JButton btnGuardar = new JButton("Guardar");

        // Acción para guardar datos en XML
        btnGuardar.addActionListener(e -> guardarEnXML());
        buttonPanel.add(btnGuardar);

        // Panel superior para el botón "Volver"
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVolver = new JButton("Volver");
        btnVolver.addActionListener(e -> {
            parent.setVisible(true);
            this.dispose();
        });
        topPanel.add(btnVolver);

        // Agregar botones y paneles a la ventana
        buttonPanel.add(btnLeer);
        buttonPanel.add(btnGuardar);
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // Acción para leer datos XML (abre una nueva ventana LeerXML)
        btnLeer.addActionListener(e -> {
            LeerXML leerXML = new LeerXML(this);
            leerXML.setVisible(true);
            this.setVisible(false);
        });
    }
    private void guardarEnXML() {        
        try (Connection conn = DriverManager.getConnection(url + dbName, usuario, contraseña);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM aviones")) {

            // Creación del documento XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Elemento raíz del documento
            Element rootElement = doc.createElement("Aviones");
            doc.appendChild(rootElement);

            // Iteración sobre los resultados de la consulta y creación de elementos "Avion"
            while (rs.next()) {
                Element avion = doc.createElement("Avion");

                // Establecer atributos y elementos para cada "Avion"
                avion.setAttribute("id", String.valueOf(rs.getInt("id")));
                avion.appendChild(createElement(doc, "Plane", rs.getString("plane")));
                avion.appendChild(createElement(doc, "passenger_capacity", rs.getString("passenger_capacity")));
                avion.appendChild(createElement(doc, "fuel_capacity_litres", rs.getString("fuel_capacity_litres")));
                avion.appendChild(createElement(doc, "max_takeoff_weight_kg", rs.getString("max_takeoff_weight_kg")));
                avion.appendChild(createElement(doc, "max_landing_weight_kg", rs.getString("max_landing_weight_kg")));
                avion.appendChild(createElement(doc, "empty_weight_kg", rs.getString("empty_weight_kg")));
                avion.appendChild(createElement(doc, "range_km", rs.getString("range_km")));
                avion.appendChild(createElement(doc, "engine", rs.getString("engine")));
                avion.appendChild(createElement(doc, "cruise_speed_kmph", rs.getString("cruise_speed_kmph")));
                avion.appendChild(createElement(doc, "imgThumb", rs.getString("imgThumb")));
                
                
                rootElement.appendChild(avion);
            }

            // Guardar el documento XML en un archivo
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("aviones.xml"));

            transformer.transform(source, result);

            // Mensaje de confirmación
            JOptionPane.showMessageDialog(this, "Datos guardados en aviones.xml");

        } catch (Exception ex) {
            // Manejo de errores
            JOptionPane.showMessageDialog(this, "Error al guardar XML: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Método auxiliar para crear elementos XML
    private Element createElement(Document doc, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
}