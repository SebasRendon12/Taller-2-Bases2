# Taller 2 Bases de Datos 2
### 2020-2

## Antes de comenzar
Para cambiar la conexión, se debe ingresar a la clase [conexion.java] y cambiar las variables Nombre_PC con el nombre del computador, user con el nombre del usuario de Oracle y pass con la contraseña de la base de datos

## Punto 1
```
DROP TABLE city;
CREATE TABLE city(
NOMBRE_CIUDAD varchar2(100),
Locales XMLTYPE
);
```
## Punto 2
```
DROP TYPE ventas_ciudad FORCE;
CREATE OR REPLACE TYPE ventas_ciudad AS OBJECT(
    x NUMBER(38),
    y NUMBER(38),
    v NUMBER(38));
    /

CREATE OR REPLACE TYPE nest_ventas AS TABLE OF ventas_ciudad;
/



CREATE OR REPLACE TYPE VVCITY_TYPE AS OBJECT(
codigovendedor NUMBER(38),
ciudad VARCHAR2(100),
ventas nest_ventas
);
/

DROP TABLE VVCITY;
CREATE TABLE VVCITY OF VVCITY_TYPE
(PRIMARY KEY(codigovendedor,ciudad))
NESTED TABLE ventas STORE AS puntos_ventas;
```
## Punto 3


## Punto 4

