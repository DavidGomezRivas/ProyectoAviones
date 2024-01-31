package ProyectoAviones;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

//Suprime advertencias de serialización
@SuppressWarnings("serial")
class Inicial extends JFrame {
 public Inicial() {
     // Configura el comportamiento por defecto al cerrar la ventana, el tamaño, el título y la posición
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setSize(300, 300); // Tamaño de la ventana
     setTitle("Inicial"); // Título de la ventana
     setLocationRelativeTo(null); // Centra la ventana en la pantalla

     // Crea un panel con un GridLayout de 6 filas y 1 columna, con 5px de separación entre componentes
     JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));

     // Inicializa botones con etiquetas descriptivas
     JButton btnAgregarDatos = new JButton("Agregar datos");
     JButton btnVerDatos = new JButton("Ver datos MySQL");
     JButton btnJson = new JButton("Trabajar con JSON");
     JButton btnXml = new JButton("Trabajar con XML");
     JButton btnReporte = new JButton("Generar reporte");
     JButton btnApi = new JButton("Leer API");

     // Define acciones para cada botón al ser presionado
     btnAgregarDatos.addActionListener(e -> {
         // Crea una instancia de AgregarDatos, la hace visible y oculta la ventana actual
         AgregarDatos agregarDatos = new AgregarDatos(this);
         agregarDatos.setVisible(true);
         this.setVisible(false);
     });

     btnVerDatos.addActionListener(e -> {
         // Crea una instancia de VerDatos, la hace visible y oculta la ventana actual
         VerDatos verDatos = new VerDatos(this);
         verDatos.setVisible(true);
         this.setVisible(false);
     });

     btnJson.addActionListener(e -> {
         // Crea una instancia de MenuJSON, la hace visible y oculta la ventana actual
         MenuJSON menuJSON = new MenuJSON(this);
         menuJSON.setVisible(true);
         this.setVisible(false);
     });

     btnXml.addActionListener(e -> {
         // Crea una instancia de MenuXML, la hace visible y oculta la ventana actual
         MenuXML menuXML = new MenuXML(this);
         menuXML.setVisible(true);
         this.setVisible(false);
     });

     btnReporte.addActionListener(e -> {
         // Invoca el método estático generarReporte() de la clase Reporte
         Reporte.generarReporte();
     });

     btnApi.addActionListener(e -> {
         // Configura la base de datos y lee datos de una API para insertarlos en la base de datos
         API.setupDatabase();
         API.leerAPI();
     });

     // Añade los botones al panel
     panel.add(btnAgregarDatos);
     panel.add(btnVerDatos);
     panel.add(btnJson);
     panel.add(btnXml);
     panel.add(btnReporte);
     panel.add(btnApi);

     // Añade el panel al JFrame
     add(panel);
 }
}