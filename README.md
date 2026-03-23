**EXHIBITION-LAND** (in progress) 

A web application I am building where people can show off their projects.
**Note:** This is a work in progress.

**So far:**
- User registration: users can sign up relatively secure (password is hashed, with some new conditions to be added on the front-end (around domain of email));
- Email verification: when the user registers, a unique token is generated that is incorporated in a link which is then sent to the user's email address to verify the account (it prohibits login without verification);
- Image upload: possibility to upload a thumbnail or profile picture. Security concerns around file uploaded are dealt with Apache Tika (to actually check the file uploaded are images (JPEG/PNG));
- Dynamic UI: for registration modal, and the login form, the loading and submitting are done dynamically through HTMX which saves a lot of loading time.

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
3. You will need an AWS account and a S3 compatible bucket (I recommend setting up `Garage`) (can be skipped for now);
4. A Gmail account and a password generated through their applications dashboard (for the SMTP server -> to send verification emails);
5. Set up the application.properties (basically either through the built-in environment variables if your IDE supports them, or some 3rd-party tool to load your variables from an env file into spring configuration file);
4. Run the application and then go (for now) to: http://localhost:8080/welcome to see the login form and chance to test the registration flow.

*Note: depending on the name you choose for your bucket you will have to modify the `S3Client` configuration. For `Garage` set the region to `garage` and can use `garage-bucket` for the bucket name. (can be skipped for now)* 

**What I am planning to add:**
- Guest login & default login;
- User profile, settings;
- Presentation (uploading, showcasing, etc.);
- About, contact, FAQ pages;
- Search functionality;
- Improved UI;
- GDPR compliance;
- More testing (unit, integration);
- Hosting on a cloud service.