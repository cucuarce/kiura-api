API de Mi Proyecto

Descripción
Esta API forma parte de un proyecto destinado a una prueba técnica de Kiura, para contratar profesionales.

Documentación de la API
La documentación completa de la API está disponible a través de Swagger. Puedes acceder a ella en http://localhost:8080/swagger-ui/index.html (una vez hayas inicializado la aplicación). Aquí encontrarás información detallada sobre cada endpoint, sus parámetros y respuestas.

Repositorio en GitHub
El código fuente de este proyecto está disponible en GitHub. Puedes clonar el repositorio utilizando el siguiente comando:

bash
Copy code
git clone https://github.com/cucuarce/kiura-api.git
Configuración Local
Para configurar el proyecto localmente, sigue estos pasos:

Clona el repositorio utilizando el comando anterior.
Abre el proyecto en IntelliJ IDEA.
Actualiza las propiedades de conexión a la base de datos en el archivo application.properties para reflejar tu entorno local. Asegúrate de configurar el usuario y contraseña de MySQL correctamente.

Despliegue en un Entorno de Producción
Para desplegar la aplicación en un entorno de producción, sigue estos pasos:

Asegúrate de tener instalado un servidor compatible con Java y MySQL en tu entorno de producción.
Clona el repositorio en el servidor de producción utilizando el mismo comando de clonación de Git mencionado anteriormente.
Configura las propiedades de conexión a la base de datos en el archivo application.properties del entorno de producción.
Ejecuta la aplicación utilizando IntelliJ IDEA o utilizando los comandos de construcción y ejecución de Maven o Spring Boot.
Verifica que la aplicación esté funcionando correctamente accediendo a la URL correspondiente.
