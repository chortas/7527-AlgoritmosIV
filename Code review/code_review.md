## Introducción

En el presente informe expondremos un _code review_ de los trabajos prácticos realizados a lo largo de la materia por el grupo de Julieta Caceres, Tomás Arjovsky y Lucía Kasman. 

## TP1

https://github.com/Arkenan/algo4-tp1 

A grandes rasgos observamos a lo largo del trabajo el correcto uso del for comprehension y el uso de la mónada IO sin materializar innecesariamente los resultados saliendose de la monada. Consideramos que esto es lo más importante ya que a nuestro criterio eran los temas importantes de este TP. 

Si bien en este TP nos dijeron que no era necesario, con los conocimientos de ahora pensamos que `Run` podría haber sido una `IOApp` ya que en ese caso no es necesario el `unsafeRunSync` que podría llevar a side effects.

Nos pareció bueno el objeto `Validator` ya que hace uso del `Either` en el caso en que no se pueda parsear la fila y de esta manera se guarda la excepción que causó el error. En línea con este pensamiento nos pareció también positivo que agregaron una excepción personalizada para el caso en que no se cuenta con la cantidad de atributos correctos. Un punto de mejora podría ser agregar más excepciones para los distintos casos de error de parseo de la fila y así tener una jerarquía de errores.


- En el validator está bueno el uso de either
- Esta bueno que hayan escrito las cosas en un log.txt para que quede para futuras corridas
- Buen uso del logger por lo anterior
- En la case class DB se mantuvieron en la mónada de IO y contemplaron el caso en el que falla la base en un either (bien) 
- Agregaron una excepción personalizada para el caso en el que no se cuenta con la cantidad de atributos correctos lo cual está bueno
- Los tests son muy completos y contemplan todos los casos

## TP2

https://github.com/Arkenan/TP-Algo-4-2



## TP3

https://github.com/Arkenan/algo4-tp3

En líneas generales el trabajo se encuentra muy bien organizado y con las responsabilidades bien divididas. Se mantiene correctamente la parametrización en F a lo largo del código y se especializa en el main con el uso de `IOApp`. Pensamos que esto trae dos ventajas, en primer lugar evita el uso de `unsafeRunSync()` para materializar los diversos resultados y a su vez permitiría cambiar `IO` por otra mónada como por ejemplo `Task` de la biblioteca `ZIO` sin modificar varias partes del código. 

Luego en cuanto al transactor utilizado consideramos que está implementado acorde a lo esperado ya que se hace buen uso de los recursos con el pool de conexiones Hikari. Además se abre la conexión en un único lugar y no con cada transacción. Por otro lado pensamos que está bien abstraido ya que usa `Resource` de la librería `cats` lo que delega en la misma la responsabilidad de manejar los recursos.

La implementación del trait `Cache` la encontramos muy similar a la de nuestro trait `Repository`. Creemos que se logran encapsular eficientemente las transacciones y a nuestro criterio también ayuda a la testeabilidad del código ya que permite mockear la funcionalidad de acceso a la base de datos y sus querys.

En cuanto al trait `Scorer` consideramos que está bueno que hayan usado la mónada `Sync` ya que notaron que no se requería una mónada más compleja para dicha implementación. Podemos notar en la línea 33 un code smell:

```scala
Score(Try(resultRecord.get("prediction").toString.toDouble).getOrElse(0.0))
```

Esto es ya que se eligió devolver un valor específico y materializar el resultado en caso de error cuando se podría mapear el resultado de la mónada `Try`.

Adicionalmente en la línea 17 consideramos que no se maneja el error si el archivo no existe:

```scala
  val evaluator: ModelEvaluator[_] = new LoadingModelEvaluatorBuilder()
                                    .load(new File(modelPathname))
                                    .build
```

Luego en `ScoreService` no vemos un manejo explícito del estado de la caché ya que los métodos `getScoreFromCache` y `saveScoreInCache` están acoplados. A nosotros nos pasó lo mismo y pensamos que podríamos haber usado la mónada `State` o `Writer`.

En `Fpfiuba43Server` el archivo pmml no es configurable ya que está atado a tener el nombre `model.pmml` y al current workdir.

Nos pareció muy bueno el uso de la librería `scalaMock` para testear el servicio sin conectarse a la base de datos ya que de esta manera se logra testear unitariamente el mismo.

Finalmente, el README del repositorio nos pareció muy completo ya que explica el sistema a utilizar en su totalidad e incluye hasta pruebas de performance.
