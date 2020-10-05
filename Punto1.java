import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Punto1 extends JFrame {

  private static final long serialVersionUID = 1L;

  public Punto1(String titulo) {
    super(titulo); // titulo de la ventana
    this.setSize(700, 400);// tamaño de la ventana
    iniciarComponentes();
  }

  private void iniciarComponentes() {
    JPanel panel = new JPanel();
    panel.setLayout(null); // desactivando el diseño
    this.getContentPane().add(panel);

    JLabel title = new JLabel("Ingreso de datos de los locales de una ciudad");
    title.setBounds(200, 10, 300, 30);
    panel.add(title);
    JLabel title1 = new JLabel("Ingrese el nombre de la ciudad");
    title1.setBounds(400, 100, 300, 30);
    panel.add(title1);
    JButton boton1 = new JButton("Insertar");
    boton1.setBounds(350, 200, 80, 40);
    panel.add(boton1);
    JTextField ciudad = new JTextField();
    ciudad.setBounds(435, 130, 100, 30);
    panel.add(ciudad);
    JTextArea datos = new JTextArea();
    datos.setBounds(50, 50, 250, 200);
    panel.add(datos);
    ActionListener oyenteDeAccion = new ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Connection conn;
        Statement sentencia;
        ResultSet resultado;
        try { // Se carga el driver JDBC-ODBC
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
          ciudad Ciudad = new ciudad();

          Ciudad.NombreCiudad = ciudad.getText();

          List<Local> lstLocales1 = new ArrayList<Local>();

          for (String local : datos.getText().split("\n")) {
            Local newLocal = new Local();
            String[] medidas = local.split(",");
            newLocal.a = Integer.parseInt(medidas[0].trim());
            newLocal.b = Integer.parseInt(medidas[1].trim());
            newLocal.c = Integer.parseInt(medidas[2].trim());
            newLocal.d = Integer.parseInt(medidas[3].trim());

            lstLocales1.add(newLocal);
          }
          Ciudad.Locales = lstLocales1;

          resultado = sentencia
              .executeQuery("SELECT nombre_ciudad FROM CITY WHERE nombre_ciudad = '" + ciudad.getText() + "'");
          if (resultado.next()) {
            resultado = sentencia
                .executeQuery("SELECT DBMS_XMLGEN.GETXML('SELECT nombre_ciudad, Locales FROM CITY') docxml FROM DUAL");
            if (resultado.next()) {
              String consulta = resultado.getString("docxml");
              String rectangulo[] = consulta.split("<ROW>");
              int number = 0;
              for (int i = 1; i < rectangulo.length; i++) {
                String primeraLinea = rectangulo[i].split("\n")[1];
                if (primeraLinea.contains(ciudad.getText())) {
                  number = i;
                  break;
                }

              }
              String[] local = rectangulo[number].split("<rectangulo>");
              ciudad CiudadOld = new ciudad();
              List<Local> lstLocales = new ArrayList<>();
              CiudadOld.NombreCiudad = ciudad.getText();
              for (String string : local) {
                Local newLocal = new Local();
                Pattern patterna = Pattern.compile("\s*(?:(<a>\\w*<..>))", Pattern.CASE_INSENSITIVE);
                Pattern patternb = Pattern.compile("\s*(?:(<b>\\w*<..>))", Pattern.CASE_INSENSITIVE);
                Pattern patternc = Pattern.compile("\s*(?:(<c>\\w*<..>))", Pattern.CASE_INSENSITIVE);
                Pattern patternd = Pattern.compile("\s*(?:(<d>\\w*<..>))", Pattern.CASE_INSENSITIVE);
                Matcher matchera = patterna.matcher(string);
                Matcher matcherb = patternb.matcher(string);
                Matcher matcherc = patternc.matcher(string);
                Matcher matcherd = patternd.matcher(string);
                if (matchera.find()) {
                  newLocal.a = Integer.parseInt(matchera.group().substring(6 + 1, (matchera.group().length() - 4)));
                }
                if (matcherb.find()) {
                  newLocal.b = Integer.parseInt(matcherb.group().substring(6 + 1, (matcherb.group().length() - 4)));
                }
                if (matcherc.find()) {
                  newLocal.c = Integer.parseInt(matcherc.group().substring(6 + 1, (matcherc.group().length() - 4)));
                }
                if (matcherd.find()) {
                  newLocal.d = Integer.parseInt(matcherd.group().substring(6 + 1, (matcherd.group().length() - 4)));
                }
                if (newLocal.a != null && newLocal.b != null && newLocal.c != null && newLocal.d != null) {
                  lstLocales.add(newLocal);
                }
              }
              CiudadOld.Locales = lstLocales;

              boolean sePuede = true;
              for (Local local1 : Ciudad.Locales) {
                if (sePuede) {
                  for (Local local2 : Ciudad.Locales) {
                    if (sePuede) {
                      if (local1 != local2) {
                        if (((local2.c.intValue() + local2.a.intValue()) <= local1.a.intValue()
                            || (local2.d.intValue() + local2.b.intValue()) <= local1.b.intValue())
                            && (local2.a.intValue() < local1.a.intValue() || local2.b.intValue() < local1.b.intValue())
                            || (local2.a.intValue() >= (local1.c.intValue() + local1.a.intValue())
                                || local2.b.intValue() >= (local1.d.intValue() + local1.b.intValue()))) {

                        } else {
                          JOptionPane.showMessageDialog(null,
                              "No se pudo ingresar los locales (se intersectan los locales ingresados)");
                          sePuede = false;
                        }
                      }
                    } else {
                      break;
                    }
                  }
                } else {
                  break;
                }
              }

              if (sePuede) {
                for (Local local1 : Ciudad.Locales) {
                  if (sePuede) {
                    for (Local local2 : CiudadOld.Locales) {
                      if (sePuede) {
                        if (((local2.c.intValue() + local2.a.intValue()) <= local1.a.intValue()
                            || (local2.d.intValue() + local2.b.intValue()) <= local1.b.intValue())
                            && (local2.a.intValue() < local1.a.intValue() || local2.b.intValue() < local1.b.intValue())
                            || (local2.a.intValue() >= (local1.c.intValue() + local1.a.intValue())
                                || local2.b.intValue() >= (local1.d.intValue() + local1.b.intValue()))) {
                        } else {
                          JOptionPane.showMessageDialog(null,
                              "No se pudo ingresar los locales (se intersectan con los locales existentes)");
                          sePuede = false;
                        }
                      } else {
                        break;
                      }
                    }
                  } else {
                    break;
                  }
                }
              }

              if (sePuede) {
                // Se guarda como XML
                try {
                  String update = "UPDATE city SET locales = XMLTYPE('<locales>";
                  for (Local localesOld : CiudadOld.Locales) {
                    update += "<rectangulo><a>" + localesOld.a + "</a><b>" + localesOld.b + "</b><c>" + localesOld.c
                        + "</c><d>" + localesOld.d + "</d></rectangulo>";
                  }
                  for (Local localesNew : Ciudad.Locales) {
                    update += "<rectangulo><a>" + localesNew.a + "</a><b>" + localesNew.b + "</b><c>" + localesNew.c
                        + "</c><d>" + localesNew.d + "</d></rectangulo>";
                  }
                  update += "</locales>') WHERE nombre_ciudad = '" + ciudad.getText() + "'";
                  resultado = sentencia.executeQuery(update);
                  resultado = sentencia.executeQuery("commit");
                  JOptionPane.showMessageDialog(null, "Ciudad editada exitosamente");
                } catch (Exception er) {
                  JOptionPane.showMessageDialog(null, "Error: " + er.getMessage());
                }
              }

            }
          } else {

            boolean sePuede = true;
            for (Local local1 : Ciudad.Locales) {
              if (sePuede) {
                for (Local local2 : Ciudad.Locales) {
                  if (sePuede) {
                    if (local1 != local2) {
                      if (((local2.c.intValue() + local2.a.intValue()) <= local1.a.intValue()
                          || (local2.d.intValue() + local2.b.intValue()) <= local1.b.intValue())
                          && (local2.a.intValue() < local1.a.intValue() || local2.b.intValue() < local1.b.intValue())
                          || (local2.a.intValue() >= (local1.c.intValue() + local1.a.intValue())
                              || local2.b.intValue() >= (local1.d.intValue() + local1.b.intValue()))) {

                      } else {
                        JOptionPane.showMessageDialog(null, "No se pudo ingresar los locales (se intersectan)");
                        sePuede = false;
                      }
                    }
                  } else {
                    break;
                  }
                }
              } else {
                break;
              }
            }
            if (sePuede) {
              // Se guarda como XML
              try {
                String insert = "INSERT INTO city VALUES('" + Ciudad.NombreCiudad + "',XMLTYPE('<locales>";
                for (Local local : Ciudad.Locales) {
                  insert += "<rectangulo><a>" + local.a + "</a><b>" + local.b + "</b><c>" + local.c + "</c><d>"
                      + local.d + "</d></rectangulo>";
                }
                insert += "</locales>'))";
                resultado = sentencia.executeQuery(insert);
                resultado = sentencia.executeQuery("commit");
                JOptionPane.showMessageDialog(null, "Ciudad agregada exitosamente");
              } catch (Exception er) {
                JOptionPane.showMessageDialog(null, "Error: " + er.getMessage());
              }
            }
          }
          conn.close();
        } catch (SQLException err) {
          JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
      }
    };
    boton1.addActionListener(oyenteDeAccion);
  }

  public class ciudad {
    String NombreCiudad;
    List<Local> Locales;

    public ciudad(String name, List<Local> locals) {
      NombreCiudad = name;
      Locales = locals;
    }

    public ciudad() {
    }
  }

  public class Local {
    Number a;
    Number b;
    Number c;
    Number d;

    public Local(int a, int b, int c, int d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
    }

    public Local() {

    }
  }

}
