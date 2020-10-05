import javax.swing.*;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JFrame {

  private static final long serialVersionUID = 1L;

  public MenuPrincipal(String titulo) {
    super(titulo); // titulo de la ventana
    this.setSize(700, 300);// tamaño de la ventana
    iniciarComponentes();
  }

  private void iniciarComponentes() {
    JPanel panel = new JPanel();
    panel.setLayout(null); // desactivando el diseño
    this.getContentPane().add(panel);

    JLabel title = new JLabel("Presione alguna de las Opciones a ejecutar");
    title.setBounds(200, 10, 300, 30);
    panel.add(title);
    JButton boton1 = new JButton("Ingresar los datos por ciudad");
    boton1.setBounds(30, 50, 200, 40);
    panel.add(boton1);
    JButton boton2 = new JButton("Ingresar los datos de ventas en una ciudad");
    boton2.setBounds(300, 50, 300, 40);
    panel.add(boton2);
    JButton boton3 = new JButton("TercerPunto");
    boton3.setBounds(30, 100, 150, 40);
    panel.add(boton3);
    ActionListener oyenteDeAccion = new ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Punto1 datos = new Punto1("Ingreso de datos de los locales de una ciudad");
        datos.setVisible(true);
      }
    };
    boton1.addActionListener(oyenteDeAccion);
    ActionListener oyenteDeAccion1 = new ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Punto2 puntos = new Punto2("Ingreso de datos de las ventas de una ciudad");
        puntos.setVisible(true);
      }
    };
    boton2.addActionListener(oyenteDeAccion1);
    ActionListener oyenteDeAccion2 = new ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Punto3 graficador = new Punto3("Grafico de Locales y Puntos de venta de una ciudad");
        graficador.setVisible(true);
      }
    };
    boton3.addActionListener(oyenteDeAccion2);
  }

}
