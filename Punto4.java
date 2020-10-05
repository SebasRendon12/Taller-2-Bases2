import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Punto4 extends JFrame {

  private static final long serialVersionUID = 1L;

  public Punto4(String titulo) {
    super(titulo); // titulo de la ventana
    this.setSize(500, 200);// tamaño de la ventana
    iniciarComponentes();
  }

  private void iniciarComponentes() {
    JPanel panel = new JPanel();
    panel.setLayout(null); // desactivando el diseño
    this.getContentPane().add(panel);

    JLabel title = new JLabel("Grafico de Locales y Puntos de venta de una ciudad");
    title.setBounds(50, 10, 400, 30);
    panel.add(title);

    JLabel title2 = new JLabel("Seleccione Una Ciudad");
    title2.setBounds(100, 50, 150, 30);
    panel.add(title2);
    Connection conn;
    Statement sentencia;
    ResultSet resultado;
    JComboBox<String> ciudades = new JComboBox<>();
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
    } catch (Exception err) {
      JOptionPane.showMessageDialog(null, "No se pudo cargar el driver JDBC");
      return;
    }

    try {
      conexion connection = new conexion();
      conn = DriverManager.getConnection(connection.getConn(), connection.getUser(), connection.getPass());
      sentencia = conn.createStatement();
    } catch (SQLException err) {
      JOptionPane.showMessageDialog(null, "No hay conexión con la base de datos.");
      return;
    }

    try {
      resultado = sentencia.executeQuery("select nombre_ciudad from city");
      while (resultado.next()) {
        ciudades.addItem(resultado.getString("nombre_ciudad"));
      }
    } catch (SQLException err) {
      JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
    }
    ciudades.setBounds(100, 100, 100, 30);
    panel.add(ciudades);
    JButton boton1 = new JButton("Graficar");
    boton1.setBounds(250, 100, 100, 40);
    panel.add(boton1);
    ActionListener oyenteDeAccion = new ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        GraficoPunto4 DrawWindow = new GraficoPunto4(ciudades.getSelectedItem().toString());
        DrawWindow.setSize(600, 600);
        DrawWindow.setResizable(false);
        DrawWindow.setLocation(200, 50);
        DrawWindow.setTitle("Pintando figuras almacenadas en la BD");
        DrawWindow.setVisible(true);
      }
    };
    boton1.addActionListener(oyenteDeAccion);

  }

}
