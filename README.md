![Descripción alternativa](./assets/imagen.png)

<p align="center">
    <a href="https://youtube.com/@unprogramadornace" target="_blank"><img align="center" src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=youtube&logoColor=white" alt="@unprogramadornace" /></a>
    <a href="https://www.linkedin.com/in/UnProgramadorNace"  target="_blank"><img align="center" src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" alt="@unprogramadornace"/></a>
    <a href="https://www.facebook.com/people/Un-Programador-Nace/61552057605223/"  target="_blank"><img align="center" src="https://img.shields.io/badge/Facebook-1877F2?style=for-the-badge&logo=facebook&logoColor=white" alt="@unprogramadornace" /></a>
    <a href = "mailto:unprogramadornace@gmail.com" target="_blank"><img align="center" src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white" alt="@unprogramadornace" /></a>
</p>


# AWS S3 Spring Boot Project

---
**Muchas gracias por estar aquí, si te gusta mi contenido y son de gran utilidad para ti por favor ayudame dando tu valioso like al video y suscribiéndote al canal, ya que esto es de gran ayuda para mí y me motiva a seguir creando contenido de gran utilidad para ti.**

**También me ayuda mucho si puedes marcar el repositorio con una estrella en la parte superior de la derecha, sería un gran detalle de tu parte.**

---

## 🔹 ¿Que es S3? 🤔
**AWS S3 (Amazon Simple Storage Service)** es un servicio de almacenamiento en la nube que permite guardar y recuperar datos como archivos, imágenes, videos y otros tipos de información a través de Internet. Funciona como un "contenedor" donde puedes organizar tus archivos en "buckets" (barriles), y proporciona un acceso fácil y seguro desde cualquier lugar del mundo. Es ideal para almacenar grandes cantidades de datos de forma escalable y fiable.

Por supuesto Spring Boot, al ser un framework tan poderoso por supuesto que puede integrarse perfectamente con AWS S3 y en esta guía te mostraré como hacerlo.

## 🔹 ¿Como conecto Spring Boot a S3? 🤔

### 🔸 **Paso 1: AGREGAR DEPENDENCIAS Y CREAR CLAVES DE ACCESO** 

Primero que todo vamos a agregar a nuestro proyecto las dependencias necesarias para trabajar con **AWS S3**, es importante que tengas en cuenta que a la fecha de creación de este articulo existen 2 versiones de AWS SDK para JAVA **(V1 y V2)**, yo estoy usando la **V2**, es decir, la mas actualizada. Debes tener en cuenta esto debido a que V1 y V2 tienen diferencias de sintaxis y se trabajan de formas un poco diferentes. Las dependencias para usar el `AWS SDK V2` son las siguientes:

```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>x.x.x</version>
</dependency>

<dependency> <!-- OPCIONAL -->
    <groupId>software.amazon.awssdk.crt</groupId>
    <artifactId>aws-crt</artifactId>
    <version>x.x.x</version>
</dependency>
```
Por supuesto, el numero de la version puede cambiar por el tiempo, te recomiendo buscar las versiones en el [Sitio oficial de maven](https://mvnrepository.com/) para que encuentres las versiones mas actualizadas.

La dependencia `software.amazon.awssdk` es obligatoria debido a que dicha dependencia contiene las herramientas basicas para conectarnos a S3.

La dependencia `software.amazon.awssdk.crt` es completamente opcional debido a que esta dependencia solo es necesaria cuando trabajas con programación asincrona, mas adelante en la documentación te mostraré que existen 2 tipos de clientes para S3, clientes sincronos y clientes asincronos.

Debes ir a tu cuenta de AWS y crear un usuario IAM (Si ya tienes uno creado, puedes usarlo), una vez tengas tu usuario IAM creado, debes generar un par de claves de acceso, tal como lo vimos en el video. Este par de claves nos serviran para autenticarnos correctamente en nuestra cuenta de AWS y poder hacer diferentes operaciones sobre AWS. Una vez tengas tu par de claves debes ir a tu `application.properties` y crear las siguientes propiedades:

```properties
spring.application.name = <Nombre de tu aplicacion>
aws.access.key = <Clave publica generada en AWS>
aws.secret.key = <Clave privada generada en AWS>
aws.region = <Region con la que estas trabajando>
```

### 🔸 **Paso 2: CREAR CLIENTE PARA S3** 

Muy bien, ahora que ya tenemos configurado nuestro `pom.xml` y nuestro `application.properties`, vamos a configurar nuestro cliente S3. Debes tener en cuenta que existen diferentes clientes S3 los cuales puedes consultar en la [Documentacion oficial de los clientes S3](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-s3.html), pero en lineas generales se dividen en 2 grupos, los clientes _SINCRONOS_ y los clientes _ASINCRONOS_, de ti depende decidir cuando usar uno u otro. Hay clientes mucho mas avanzados que otros, pero si en lineas generales no te decides por cual usar, te recomiendo usar 2 de ellos: 

- **🛠️ S3Client**, para funcionalidad sincrona, es ideal para operaciones simples y bloqueantes.
- **⚡ S3AsyncClient**, para funcionalidad asincrona, permite operaciones no bloqueantes y es perfecto para aplicaciones que necesitan manejar tareas concurrentes.

```java
@Value("${aws.access.key}")
private String awsAccessKey;

@Value("${aws.secret.key}")
private String awsSecretKey;

@Value("${aws.region}")
private String region;

/**
 * Cliente S3 Síncrono
 */
@Bean
public S3Client getS3Client() {
    AwsBasicCredentials basicCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);

    return S3Client.builder()
            .region(Region.of(region))
            .endpointOverride(URI.create("https://s3.us-east-1.amazonaws.com"))
            .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
            .build();
}

/**
 * Cliente S3 Asíncrono
 */
@Bean
public S3AsyncClient getS3AsyncClient() {
    AwsBasicCredentials basicCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);

    return S3AsyncClient.builder()
            .region(Region.of(region))
            .endpointOverride(URI.create("https://s3.us-east-1.amazonaws.com"))
            .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
            .forcePathStyle(true)
            .build();
}


```

### 🔸 **Paso 3: UTILIZAR NUESTRO CLIENTE DONDE QUERRAMOS** 

Genial, ahora que ya tenemos configurado nuestro cliente S3 podemos inyectarlo en cualquier lugar de nuestra aplicación y utilizar las diferentes funcionalidades que nos provee AWS S3. Las forma de inyectarlo es la siguiente:

```java
@Autowired
private S3Client s3Client;

@Autowired
private S3AsyncClient s3AsyncClient;
```

Te dejo una lista de 10 metodos importantes para cada uno de los clientes que configuramos:

### Métodos importantes para S3Client (Síncrono) 🚀
1) `createBucket(CreateBucketRequest request):`
📂 Crea un nuevo bucket en S3.

2) `listBuckets():`
📋 Lista todos los buckets disponibles en tu cuenta.

3) `putObject(PutObjectRequest request, RequestBody body):`
☁️ Sube un objeto al bucket especificado.

4) `getObject(GetObjectRequest request, ResponseTransformer<?, ?> transformer):`
📥 Descarga un objeto desde un bucket.

5) `deleteObject(DeleteObjectRequest request):`
🗑️ Elimina un objeto específico de un bucket.

6) `copyObject(CopyObjectRequest request):`
🔄 Copia un objeto dentro de S3 (entre buckets o carpetas).

7) `listObjectsV2(ListObjectsV2Request request):`
📂 Lista los objetos dentro de un bucket o prefijo específico.

8) `headObject(HeadObjectRequest request):`
🧐 Obtiene los metadatos de un objeto en S3.

9) `deleteBucket(DeleteBucketRequest request):`
🚫 Elimina un bucket (debe estar vacío).

10) `presignGetObject(PresignedGetObjectRequest request):`
🔗 Genera una URL prefirmada para acceder a un objeto privado.

### Métodos importantes para S3AsyncClient (Asíncrono) ⚡
1) `createBucket(CreateBucketRequest request)`
📂 Crea un nuevo bucket de manera asíncrona (Devuelve un CompletableFuture).

2) `listBuckets()`
📋 Lista todos los buckets en la cuenta de manera asíncrona.

3) `putObject(PutObjectRequest request, AsyncRequestBody body)`
☁️ Sube un objeto a S3 de forma asíncrona.

4) `getObject(GetObjectRequest request, AsyncResponseTransformer<?, ?> transformer)`
📥 Descarga un objeto desde S3 de forma asíncrona.

5) `deleteObject(DeleteObjectRequest request)`
🗑️ Elimina un objeto específico de un bucket.

6) `listObjectsV2(ListObjectsV2Request request)`
📂 Lista los objetos de un bucket o prefijo, devolviendo un CompletableFuture.

7) `headObject(HeadObjectRequest request)`
🧐 Obtiene los metadatos de un objeto, también asíncronamente.

8) `copyObject(CopyObjectRequest request)`
🔄 Copia un objeto dentro de S3.

9) `deleteBucket(DeleteBucketRequest request)`
🚫 Elimina un bucket de manera asíncrona (asegúrate de que esté vacío).

10) `getBucketPolicy(GetBucketPolicyRequest request)`
📜 Recupera la política asociada con un bucket.️

---

Video demostración: 

### 🔹 Espero que te haya gustado el articulo, no olvides suscribirte a mi canal de Youtube y dar like al video 🔹