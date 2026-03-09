**EXHIBITION-LAND** (in progress) 

A web application I am building where people can show off their projects.
**Note:** This is a work in progress.

**So far:**
- Login with socials: basic login with socials (Google, GitHub);
- CRUD operations for presentations (you can add, edit, delete a presentation);
- Image upload: possibility to upload a thumbnail or profile picture;
- Dashboard: a simple page where you can see the presentations you have added.

**Tech:**
- Java 25: using the latest version of Java (will use ahead-of-time compilation feature, and some others);
- Spring Boot 4: main framework;
- Thymeleaf: for building the HTML pages;
- HTMX & Hyperscript: speeding mainly, not having to rely on reloading the page at every change;
- PostgreSQL: for storing the data;
- Spectre.css: a lightweight CSS library, used for a simple styling;
- AWS SDK: for uploading images to `Garage`(hosted on an `EC2` instance) bucket (`S3` compatible).
- JUnit 5, Mockito: testing purposes;
- Docker: on the `AWS EC2` instance for `Garage`.

**Try it out:**
1. Clone the repository;
2. Set up a PostgreSQL database (or any other, as long as you modify the `pom.xml` file and `application.properties`);
3. You will need an AWS account and a S3 compatible bucket (I recommend setting up `Garage`);
4. For both Google, and GitHub you will have to create/generate credentials (for `OAuth2` authentication);
5. You will have to configure the `application.properties` file with your credentials and database details;
6. Run the application with `mvnw spring-boot:run`.

*Note: depending on the name you choose for your bucket you will have to modify the `S3Client` configuration. For `Garage` set the region to `garage` and can use `garage-bucket` for the bucket name.*

**What I am planning to add:**
- Guest login;
- User profile, settings;
- About, contact, FAQ pages;
- Search functionality;
- Improved UI;
- GDPR compliance;
- More testing (unit, integration);
- Hosting on a cloud service.