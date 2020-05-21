# Evaluación y Adaptación del Software de una Aplicación de Streaming

## Tabla de Contenido
- [Autores](#Autores)
- [Introducción](#Introducción)
- [Mantemiento](#Mantenimiento)
  + [Diagrama Original](#Diagramas-Originales)
  + [Eliminación de código inutil / Análisis](#Eliminación-de-código-inutil-y-Análisis)
  + [Actualizar Código Desfasado](#Actualizar-Código-Desfasado)
- [Evolución](#Evolución)
  + [Propuestas](#Propuesta-de-modificaciones-y-extensiones)
  + [Aprobacion](#Aprobacion-de-las-modificaciones-y-extensiones)
  + [Desarrollo](#Desarrollo-de-dichas-modificaciones) 
- [Ejecución](#Ejecución)
- [Programas Utilizados](#Programas-Utilizados)


## Introducción

Nos enfrentamos a una práctica de Mantenimiento y Evolución de un software de streaming de audio, este basado en el formato multimedia Ogg, concretamente en la parte de audio, Vorbis. El código original al que nos enfrentamos es un desarrollo de JCraft, liberado bajo licencia GPL.

JRoar se inspira en la existencia de Icecast, que no es más que una evolución 'libre' del protocolo Shoutcast. Para el entendimiento y desarrollo de la practica se puede revisar la documentación de [Icecast 2](http://icecast.org/).

En primer lugar deberemos adaptar el código para su funcionamiento correcto en Java 11, con Intel J Idea, una vez la aplacicación este operativa el objetivo del mantenimiento es eliminar todas aquellas partes que se consideren accesorias, como por ejemplo las que se encargan del protocolo PeerCast y JOrbisPlayer, ambos obsoletos y sin funcionalidad.

En primer lugar realizaremos diagramas UML de la aplicación original con el fin de comprender su arquitecura y funcionamiento.

Además, nos apoyaremos en la herramienta de análisis SonarQube, para analizar la aplicación a través de varios metodos, por otro lado, utilizamos la aplicación Trello para distribuir las tareas y realizar dailys diarios sobre el trabajo realizado.

## Mantenimiento

#### Diagramas Originales

En un primer momento hemos realizado varios diagramas UML para entender el funcionamiento y relaciones existentes entre las clases de Java.
Los diagramas se dividen en 6, el primero es un diagrama muy general de la aplicación, este no nos va a aclarar la arquitectura, pero tampoco está de más echarle un ojo.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Diagramas/Origen/01%20-%20GeneralOrigenUML.png">
</p>

Ahora nos adentramos en el paquete Com, que contiene el paquete Jcraft, el UML en este caso ya nos permite ver con mayor claridad como pueden relacionarse de una forma muy superficial nuestras clases más internas.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Diagramas/Origen/02%20-%20ComJCraftOrigen.png">
</p>

Jcraft se divide en 3 subpaquetes: Jogg, Jorbis y JRoar, cada uno con su UML y relaciones internas, esto no quiere decir que no exista relación entre ellos, como hemos podido ver en el UML anterior los tres subpaquetes tienen relación y deberemos descubrir cuál es.

En un primer momento nos centraremos en el paquete Jogg, cuya extensión en clases y métodos ya es notoria.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Diagramas/Origen/03%20-%20ComJCraftJoggOrigen.png">
</p>

En segundo lugar encontramos el paquete JOrbis al que habra que analizar en detalle más adelante debido a que se encuentra obsoleto, su diagrama de clases es el siguiente:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Diagramas/Origen/04%20-%20ComJCraftJorbisOrigen.png">
</p>

En penultimo lugar nuestro paquete principal JRoar en el que se basa la práctica cuyo diagrama de clases es el siguiente:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Diagramas/Origen/05%20-%20ComJCraftJroarOrigen.png">
</p>

Para acabar hemos hecho un diagrama del paquete Misc que incluyen unas cuantas clases auxiliares sin ningún tipo de relación entre ellas como podemos ver:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Diagramas/Origen/06%20-%20Misc.png">
</p>


#### Eliminación de código inutil y Análisis

En primer lugar analizamos la carpeta JOrbis, ya que sus funcionalidades están obsoletas y ya no son soportadas, el objetivo es eliminar todo código inútil o obsoleto, es decir, todo aquel código que no sea necesario ni necesario para otras clases funcionales.

Para realizar dicha eliminación de código inútil nos hemos apoyado en la herramienta [SonarCloud](sonarcloud.io), en concreto este es el [nuestro](https://sonarcloud.io/dashboard?id=JaviBarrio6_EASCloud), comenzamos con la carpeta JOrbis como mencionamos en el parrafo anterior, dentro de esta carpeta se eliminaron en su totalidad las siguientes clases: AllocChain, Block, ChainingExample, CodeBook, DecodeExample, Drft, DspState, EncodeAuxNearestMatch, EncodeAuxThreshtMatch, Floor0, Floor1, Mdct, PsyLook, Resideu1, Resideu2 y VorbisFile.

En cuanto al resto, Comment,FuncFllor, FuncMapping, FuncResidue, FuncTime, Info, InfoMode, Mapping0, PsyInfo, Residue0, StaticCodeBook y Time0, han sido analizadas, manteniendo así, unicamente las funciones relevantes y eliminando todo aquel código que ya no era utilizado.

Una vez limpiado dicho código, volvimos a realizar un análisis mediante SonarCloud, dandonos estos resultados:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/SonarCloud/01%20-%20LimpiezaJOrbis.png">
</p>
<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/SonarCloud/02%20-%20LimpiezaJOrbis.png">
</p>

Como podemos ver en estas capturas, contamos con ocho bugs en el código, ocho vulnerabilidades, cuatro puntos calientes, dieceis días de deuda técnica, ochocientos cincuenta y ocho malos olores y un cuatro por ciento de código duplicado.

Una vez finalizado este primer análisis, lo hicimos de forma similar con la carpeta JOgg, en esta carpeta se mantuvieron las cinco clases presentes, pero se eliminaron ciertas funciones que ya no eran necesarias, la eliminación de dichas funciones nos obligo a analizar la carpeta JOrbis de nuevo, ya que existían dependencias, esto hizo que alguna de las mencionadas clases o funciones borradas en JOrbis anteriomente, fueran borradas en este segundo análisis.

Tras esto repetimos el analisis SonarCloud, aportandonos los siguientes resultados:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/SonarCloud/01%20-%20LimpiezaJOgg.png">
</p>
<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/SonarCloud/02%20-%20LimpiezaJOgg.png">
</p>

Como podemos ver, tras analizar esta cinco clases hemos reducido la deuda técnica a quince días y eliminado cuarenta y siete malos olores, además, al eliminar código, el porcentaje de código repetido en el código ha aumentado hasta el 4,8 %, aunque dicho valor sigue sin ser demasiado alto.

Analizamos ahora la carpeta JRoar, en esta carpeta sucedió algo similar a JOgg, no se eliminaron clases, pero si cierto código inútil o sin uso. Además, se vió que las clases referidas al UDP han quedado obsoletas, pero serán tratas en la siguiente fase, el análisis de código desfasado.

Tras esto repetimos el analisis SonarCloud, aportandonos los siguientes resultados:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/SonarCloud/01%20-%20LimpiezaJRoar.png">
</p>
<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/SonarCloud/02%20-%20LimpiezaJRoar.png">
</p>

Observamos que se han reducido los Bugs a 5, la deuda técnica a catorce días, los malos olores son ahora setecientos dieciseis y se ha reducido el código duplicado hasta el 4,3 %. Cabe mencionar que también se han eliminado los muchos comentarios, o código comentado innecesario, que contenia el código y se ha tabulado correctamente para mejorar su legibilidad.


Para finalizar esta fase repetimos el proceso con la carpeta Misc, analizandola nos dimos cuenta que unicamente se necesitaba el archivo debug.log y el JRoar.html para el desarrollo de la aplicación web.

#### Actualizar Código Desfasado:

Una vez finalizada la fase anterior, el equipo se reune para intentar corregir cierto código que produce esos Bugs, vulnerabilidades, puntos calientes, deuda técnica, y, reducir si es posible, el porcentaje de código duplicado.

De forma similar a la fase anterior, hemos analizado en primer lugar la carpeta JOgg en busca de resolver Warnings remitidos por Intel J Idea o Bad Smells, en esta carpeta nos hemos encontrado dos Warnings en la clase Buffer y uno en la clase Page. 

El primer Warning de la clase Buffer se prodoce por inicializar una variable a null, cuando Java ya hace esta inicialización por defecto, en cuanto al segundo, se nos recomienda cambiar un bucle for por un for each, de manera que realizamos el cambio:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/Inicializaci%C3%B3nNull.png">
</p>
<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/forEach.png">
</p>

En el primer caso hemos declarado la variable byte[] buffer; en el segundo caso el nuevo for each quedaría como sigue: for (byte b : s){;

En la clase Page en un return concreto, Intel nos recomendaba cambiar un AND logico por la variable en si del AND (&), ya que dependía de su valor, por lo que hemos sustituido r & 0xffffffff por r.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/returnR.png">
</p>

En la carpeta JOrbis, en concreto en la clase StreamState hemos sustituido tres bucles anidados cuyas iteraciones venían marcadas por .lengths en llamadas conflictivas, por ello se han modificado por lo siguiente:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/initStreamState.png">
</p>

La variable maxvals inicializada como = (lacing_fill>255?255:lacing_fill); se ha modificado por:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/maxVals.png">
</p>

El método public void reset finalizaba con un return 0, pero al ser una función void no debe devolver nada, por lo que se retiro el return. Dentro de la clase Comment se modifico la inicialización de la variable String foo = "Vendor: " + new String(vendor, 0, vendor.length-1); de la función toString(), y se aprovecho para su inicialización la función StringBuilder. A su vez en la parte de abajo, se modificó foo = foo + "\n" por la función append, quedando foo.append("\n"). Para finalizar la clase Comment, el return(foo), se modificó por return(foo.toString());

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/toString.png">
</p>

Dentro de la clase SyncState en los métododos clear, wrote y reset se devolvía un cero en caso de realizar su código correctamente, se han cambiado por funciones void, a su vez, las variables Page pageseek y byte[] chksum no se modificaban a lo largo del código, por lo que las convertimos a constantes.

De forma similar, hemos analizado el resto de clases en busca de resolver Warnings remitidos por Intel J Idea, se han repetido varios Warnings ya eliminados previamente, vamos a indicar algunos más relevantes.

Uno de los warning que aparece con mayor frecuencia es indicado como "raw use of parameterized class X", este warning lo que nos indica es que no especificamos que tipo de datos debe recibir una estructura de datos concreta, para resolverlo indicamos el tipo de datos que se usará:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/VectorWarning.png">
</p>

En otras casos, no podemos establecer el tipo de datos que se usará, por ello, utilizamos la simbología <?>, esto indica a Intel que no conocemos el dato que se guardará en la estructa de datos concreta, eliminando así, el warning.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/kickFunction.png">
</p>

Otro warning modificado es el uso de StringBuffer, el propio Intel nos recomendaba sustituirlo por StringBuilder, es cierto, que StringBuffer es más seguro en su uso con Threads, pero es más lento, como en este caso su uso no está ligado a Threads hemos realizado la sustitución del mismo.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/StringBuilder.png">
</p>

En la clase HomePage contemplabamos varios bucles for anidados con numeros ifs, por ello, hemos resuelto este warning utilizando un forEach que simplifique el código.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/forEachHomePage.png">
</p>

El penúltimo warning significativo se recogía en el tratamiento de las excepciones, en numerosos casos, aparecía la captura de una excepción, pero no se trataba de ninguna forma, como no conocemos como se planteaba el tratamiento de las mismas, hemos decidido capturarlas, pero no tratarlas:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/catchVacio.png">
</p>

Para finalizar, habia ciertos if que comparaban parámetros a null, para su correcto tratamiento hemos añadido el uso de los asserts, así si ejecutamos la aplicación con la opción de habilitar los asserts, y esta falla, saltará una excepción en caso de que el parametro sea null.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/C%C3%B3digo%20Desfasado/assertNull.png">
</p>

Trás esto, nuestro código queda como sigue:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/SonarCloud/LimpiezaWarnings.png">
</p>

Unicamente dos Bugs, veintitrés vulnerabilidades, cuatro puntos calientes once días de deuda técnica y quinientos seis mal olores, para finalizar la fase de mantenimiento, nos centraremos ahora, en internar reducir estos valores al mínimo.

La situación final de la parte de mantenimiento es la siguiente:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/SonarCloud/FinMantenimiento.png">
</p>

Los dos bugs tenían relación con la funcionalidad Applet, a día de hoy obsoleta, y con como la aplicación utilizaba el control de excepciones de tipo FileInputStream, ambos solventados con exito, en cuanto a las veintitrés vulnerabilidades, realmente había dos repetidas numerosas veces. La primera era la utilización de printSacktrace para notificar excepciones, se solucionó utilizando log.error(); la segunda vulnerabilidad se solucionaba definiendo los métodos estáticos como protected final.

Además, se redujeron los puntos calientes a tres, la deuda técnica a diez dias y los bad smells a cuatrocientos setenta y cinco dando por finalizado el mantenimiento.


## Evolución

#### Propuesta de modificaciones y extensiones

La primera propuesta es la creación de un icono personalizado para nuestra aplicación, el icono debe ser visual, atractivo y servir para reconocer a la aplicación, esta propuesta está ligada a la sustitución del HTML existente por una aplicación web MVC multifuncional. 

Aparte de la implementación de está aplicación web se considera necesario la existencia de un reproductor web en esta, el objetivo es no necesitar un programa de terceros como VLC para poder reproducir el contenido. 

En el caso de no querer utilizar la aplicación en el servidor, se plantea implementar una re dirección local, es decir, en el momento que se ejecute la aplicación desde el entorno de desarrollo se realice una llamada a VLC y el programa se abra simultáneamente, ejecutando, una canción o playlist en función de los argumentos. Finalmente, se plantea incluir un botón de aleatoriedad al reproducir una playlist, si dicho botón esta activado la siguiente canción a reproducir no será la siguiente en la lista, si no una aleatoria de la misma.

#### Aprobacion de las modificaciones y extensiones

Tras la propuesta los profesores aceptaron las modificaciones y extensiones tomadas, siempre que la aplicación web tuviese entidad y solidez.

#### Desarrollo de dichas modificaciones

En primer lugar desarrollamos los templates que posteriormente formarán nuestra aplicación web, esta, constará de cuatro templates. El primero es el diseño de la página pública y principal:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Página/Index.png">
</p>

Como podemos ver en el centro de la página, partimos de un logo representativo a JRoar, debajo de este, tenemos un icono muy visual para redirigirnos a la página de Login / Registro. Centrandonos en las nuevas funcionalidades de este template podemos observar un reproductor de música, este está formado por una lista de canciones disponibles, un pequeño controlador, donde pausar la música, gestionar el volumen, o descargar la canción en reproducción (disponible para Google Chrome), debajo del controlador podemos ver tres botones, avanzar a la siguiente canción de la lista, retroceder o reproducir una canción aleatoria, pese a estos botones, se puede seleccionar cualquier canción de la lista sin tener que ir avanzando una a una.

La nueva funcionalidad nos proporciona un reproductor personalizable, además, nos permite disfrutar de la música sin disponer de un programa externo.

El siguiente template corresponde a la página de inicio de sesión y registro:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Página/Login.png">
</p>

En caso de no estar registrados, se nos facilita un proceso de listado simple, y en el caso de estarlo y confirmar el correo recibido, podremos iniciar sesión para disfrutar de las ventajas de la sección del perfil.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Página/Profile1.png">
</p>

Dentro de nuestro perfil podemos encontrar todas las funcionalidades existentes anteriormente por JRoar, pero con un diseño más visual y agradable.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Página/Profile2.png">
</p>

Además, se nos permite cambiar el nombre de usuario y la contraseña.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Página/Profile3.png">
</p>

El último template se utiliza para el control de errores que puedan suceder en la pagina, como el intento de establecer una URL inexistente.

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Página/Error.png">
</p>

Para finalizar, hemos creado una funcionalidad que al ejecutar la aplicación se abrá la aplicación VLC con nuestra radio, esto se ha realizado para facilitar su apertura a usuarios inexpertos, además, evita tener que utilizar un motor de búsqueda para disfrutar del contenido, eso sí, es una funcionalidad en paralelo, a través de VLC disfrutamos de una playlist similar a una radio, mientras que a través de la aplicación web estamos ante una funcionalidad más similar a Spotify, en la que podemos elegir que canción escuchar de las disponibles, e incluso, añadir canciones a la lista a través de la sección de perfil.


## Ejecución

En primer lugar debe descargar a través de GitHub nuestra evolución del software en cuestión, en este momento el repositorio es privado, por lo que no se podría descargar.

Una vez descargado, recomendamos su apertura con Intel J Idea, el entorno de programación debe reconocer el projecto Java, en nuestro caso utilizamos Java 11. Indicaremos la configuración recomenadada para la ejecución de nuestro ahora proyecto Maven, Spring Boot, en el apartado de File/Settings:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Intel/Settings.png">
</p>

Utilizando un lenguaje de nivel 7, en nuestro File/ProjectStructure:

<p align="center">
  <img src="https://github.com/Sw-Evolution/20E04/blob/Mantenimiento/App/resources/Intel/Project.png">
</p>

Para continuar localice la ruta src/com/Jcraft/webapp/ y busque el archivo Aplication java, ejecutelo, es posible que no disponga de las librerías de Maven o Spring Boot, por lo general, al abrir el proyecto con Intel, este, le recomienda las librerías necesarias si es que no dispone de ellas mediante una notificación abajo a la derecha, solo debería dar a aceptar o importar.

Una vez ejecutada la clase Aplication Spring debería ejecutar la aplicación en el servidor, y, a su vez, JRoar comienza a transmitir su funcionalidad radio, si dispone de VLC, se abrirá en pantalla con la radio ejecutandose, en caso contrario, deberá dirigirse a la URL http://localhost:8443/, recomendamos el uso de Google Chrome.

En su defecto, mediante el puerto 9000 se ejecuta la radio, disponible en VLC o aplicación similar, y en el puerto 8443, la aplicación web. En el caso de querer añadir manualmente canciones a través de una carpeta, se deberan añadir a la carpeta: **./././resources/OGG/** con el formato **./././resources/OGG/nombreCancion.ogg**.

A su vez, habría que modificar el archivo foo de la misma carpeta con la ruta propia de las canciones, por ejemplo, **C:/Users/user/Desktop/EAS/20E04/App/src/main/resources/static/OGG/INeedAHero.ogg**

## Programas Utilizados

* [Intel J Idea](https://www.jetbrains.com/es-es/idea/) -  Entorno de desarrollo integrado (IDE) para el desarrollo de programas informáticos.
* [Brackets](http://brackets.io/) - Editor de texto utilizado para la elaboración y modificación del Readme.
* [Trello](https://trello.com/b/7tXmEA17/daw) - Para dirigir las tareas a realizar en cada Sprint planteado.
* [VLC](https://www.videolan.org/vlc/index.es.html) - Reproductor multimedia libre usado para reproducir el streaming producido por JRoar.

## Autores

* **Javier Barrio Martín** - *Programmer* - [Git Account](https://github.com/JaviBarrio6) - Mail: j.barrio.2016@alumnos.urjc.es
* **Maria Gutierrez** - *Programmer* - [Git Account](https://github.com/Mariagt97) - Mail: m.gutierrezt.2016@alumnos.urjc.es
* **Alejandro Aguilar** - *Programmer* - [Git Account](https://github.com/Aaguilarf) - Mail: a.aguilarf@alumnos.urjc.es
* **Adrián Gómez** - *Programmer* - [Git Account](https://github.com/adriang5) - Mail: a.gomezdej@alumnos.urjc.es

    - Si quieres ver la lista de contribuyentes mira en la sección de [contributors](https://github.com/Sw-Evolution/20E04/graphs/contributors).

## Licencia

Este proyecto esta bajo la licencia [LICENSE.md](https://github.com/Sw-Evolution/20E04/blob/master/LICENSE) para más detalles.
