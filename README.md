# Primera Parte

La primera instancia consta de un desarrollo en el lenguaje de programación Scala haciendo uso exclusivamente de herramientas del paradigma funcional y comentando el mismo utilizando el estilo Scaladoc.
Es decir que el código producido deberá ser Funcional Puro.

Construir un pipeline de datos de manera puramente funcional para llenar una base de datos a partir del csv train.csv provisto.

Se debe mapear las filas del archivo a objetos ```DataSetRow``` validando su contenido segun los constrains de la base de datos.
```sql
CREATE TABLE fptp.dataset
(
    id int PRIMARY KEY,
    date timestamp without time zone NOT NULL,
    open double precision,
    high double precision,
    low double precision,
    last double precision not null,
    close double precision not null,
    dif double precision not null,
    curr character varying(1) NOT NULL,
    o_vol int,
    o_dif int,
    op_vol int,
    unit character varying(4) NOT NULL,
    dollar_BN double precision NOT NULL,
    dollar_itau double precision not null,
    w_diff double precision not null,
    hash_code int not null
);
```

Si un campo nulleable tiene error se guarda como null, sino se descarta la linea.
