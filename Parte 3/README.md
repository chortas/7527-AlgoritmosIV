# Tercera Parte

A partir del modelo obtenido en el tp anterior construir un servicio REST. 
Utilizar Http4s y que este reciba en un endpoint ```/score``` como entrada 
(en el body del POST) un JSON que represente a la clase ```InputRow```.
Este servicio debera fijarse si en la tabla ```fptp.scores``` ya existe el 
hash code de dicho registro y si es asi devolver el score que tiene guardado. En
caso contrario se debera evaluar el registro devolver el score y persistirlo 
en dicha tabla.


### DB
Se usara una nuevo container que tiene la nueva tabla, este es: ```fpfiuba/tpdb:3```

Para correrlo se realiza de la siguiente manera
```
$ docker run -p 5432:5432 -d --name db3 fpfiuba/tpdb:3
```

Nueva tabla para persistir los scores (ya creada en el container)
```roomsql
CREATE TABLE fptp.scores(
    hash_code int PRIMARY KEY,
    score double precision not null
);
```

### API Rest

Debe tener por lo menos dos endpoints

GET /health-check

cuya respuesta es (maintainer es el nombre de su grupo):
```json
{
  "version": "0.1",
  "maintainer": "changeme"
}
```

POST /score

recibe un JSON como este, aquí los campos Opcionales fueron excluidos,
pero podrían estar.

```json
{"id" : 158,
 "date" : "2020-12-02T14:49:15.841609",
 "last" : 0.0,
 "close" : 148.0,
 "diff" : 0.0,
 "curr": "D",
 "unit" : "TONS",
 "dollarBN": 2.919,
 "dollarItau": 2.91,
 "wDiff": -148.0
}
```

La respuesta tiene que ser
```json
{
  "score" : 93.166753131
}
```