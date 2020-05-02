# Evaluación y Adaptación del Software de una Aplicación de Streaming

## Tabla de Contenido
- [Autores](#Autores)
- [Introducción](#Introducción)
- [Mantemiento](#Mantenimiento)
  + [Diagrama Original](#Diagramas-Originales)
  + [Actualizar Código Desfasado](#Actualizar-Código-Desfasado)
  + [Eliminación de código inutil](#Eliminación-de-código-inutil)
  + [Análisis](#Análisis)
- [Evolución](#Evolución)
- [Ejecución](#Ejecución)
- [Programas Utilizados](#Programas-Utilizados)
  
      
## Introducción

Nos enfrentamos a una práctica de Mantenimiento y Evolución de un software de streaming de audio, este basado en el formato multimedia Ogg, concretamente en la parte de audio, Vorbis. El código original al que nos enfrentamos es un desarrollo de JCraft, liberado bajo licencia GPL.

JRoar se inspira en la existencia de Icecast, que no es más que una evolución 'libre' del protocolo Shoutcast. Para el entendimiento y desarrollo de la practica se puede revisar la documentación de [Icecast 2](http://icecast.org/).

En primer lugar deberemos adaptar el código para su funcionamiento correcto en Java 11, con Intel J Idea, una vez la aplacicación este operativa el objetivo del mantenimiento es eliminar todas aquellas partes que se consideren accesorias, como por ejemplo las que se encargan del protocolo PeerCast y JOrbisPlayer, ambos obsoletos y sin funcionalidad. 

En primer lugar realizaremos diagramas UML de la aplicación original con el fin de comprender su arquitecura y funcionamiento.

Además, nos apoyaremos en la herramienta de análisis SonarQube, para analizar la aplicación a través de varios metodos.

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

#### Actualizar Código Desfasado:
#### Eliminación de código inutil:
#### Análisis:

## Evolución

* Implementar una extensión/ampliación/modificación obteniendo una nueva versión del servidor JRoar con nuevas funcionalidades:
* Toda modificación debe ser aprobada:
* Esta fase de divide en dos subfases:

    - Propuesta de las modificaciones y extensiones, esperando su aprobación:
    
    - Desarrollo de dichas modificaciones:
    
## Ejecución

En primer lugar debe descargar a través de GitHub nuestra evolución del software en cuestión, en este momento el repositorio es privado, por lo que no se podría descargar.

Una vez descargado, recomendamos su apertura con Intel J Idea, el entorno de programación debe reconocer el projecto Java, en nuestro caso utilizamos Java 11. Para continuar localice la ruta src/com/Jcraft/JRoar/ y busque el archivo JRoar java, ejecutelo.

Una vez establecido que el archivo JRoar será la clase Java a ejecutar busqué en la esquina superior derecha un rectángulo en el que ahora ya debería aparecer JRoar, a su lado aparece el icono de abrir pestaña, este botón, le despliega una ventana de opciones, nos interesa "Edit Configurations", una vez dentro, debe escribir en la celda de Program Arguments lo siguiente:

**-port 9000 -playlist /test1.ogg ./././resources/OGG/TakeOnMe.ogg**

Seleccione el botón de "Apply" y haga click en "OK", ejecute de nuevo. Ahora le recomendamos el uso del programa VLC para reproducir el audio, una vez abierto los pasos a seguir son: medio, abrir ubicación de red, y donde se le solicita la URL, escriba: **http://localhost:9000/test1.ogg**.

## Programas Utilizados

* [Intel J Idea](https://www.jetbrains.com/es-es/idea/) -  Entorno de desarrollo integrado (IDE) para el desarrollo de programas informáticos.
* [Brackets](http://brackets.io/) - Editor de texto utilizado para la elaboración y modificación del Readme.
* [Trello](https://trello.com/b/7tXmEA17/daw) - Para dirigir las tareas a realizar en cada Sprint planteado.
* [VLC](https://www.videolan.org/vlc/index.es.html) - Reproductor multimedia libre usado para reproducir el streaming producido por JRoar.

## Autores

* **Javier Barrio Martín** - *Programmer* - [Git Account](https://github.com/JaviBarrio6) - Mail: j.barrio.2016@alumnos.urjc.es
* **Maria Gutierrez** - *Programmer* - [Git Account](https://github.com/Mariagt97) - Mail: m.gutierrezt.2016@alumnos.urjc.es
* **Alejandro Aguilar** - *Programmer* - [Git Account](https://github.com/Aaguilarf) - Mail: a.aguilarf.2016@alumnos.urjc.es
* **Adrián Gómez** - *Programmer* - [Git Account](https://github.com/adriang5) - Mail: a.gomezdej@alumnos.urjc.es

    - Si quieres ver la lista de contribuyentes mira en la sección de [contributors](https://github.com/Sw-Evolution/20E04/graphs/contributors).

## Licencia

Este proyecto esta bajo la licencia [LICENSE.md](https://github.com/Sw-Evolution/20E04/blob/master/LICENSE) para más detalles.
