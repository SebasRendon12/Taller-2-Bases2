public class conexion {
  private final String Nombre_PC = "RENDONARTEAGA";
  private final String usuario = "sebas";
  private final String contraseña = "sebas123";

  public String getConn() {
    return "jdbc:oracle:thin:@" + Nombre_PC + ":1521:xe";
  }

  public String getUser() {
    return usuario;
  }

  public String getPass() {
    return contraseña;
  }
}
