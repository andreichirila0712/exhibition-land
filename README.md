**EXHIBITION-LAND** (in progress) 

A web application I am building where people can show off their projects.
**Note:** This is a work in progress.

**So far:**
- Registration/Login: users can sign up using their email, then will receive a confirmation link, and after clicking on it, the account will be activated;
- Email confirmation: for registration, privacy: account deletion, and security settings (user-related) such as email change, password;
- User related settings: general (like theme, language, and date format), privacy (visibility, account deletion), and security (changing email, password);
- Image upload: possibility to upload a thumbnail or profile picture. Security concerns around file upload are dealt with Apache Tika (to actually check the file uploaded are images (JPEG/PNG)). Currently unused feature, but will be available in the next update;
- Dynamic UI: for modals (spanning from registration to user settings) the loading and submitting are done through HTMX. Quite fast and saves memory.

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
4. A Gmail account and a password generated through their application dashboard (for the SMTP server -> to send verification emails);
5. Set up the application.properties (basically either through the built-in environment variables if your IDE supports them, or some third-party tool to load your variables from an env file into the Spring configuration file);
6. Run the application and then go (for now) to: http://localhost:8080/exhibition-land-api/v1/welcome, from there you can test registration, login, and user-related settings.

*Note: depending on the name you choose for your bucket you will have to modify the `S3Client` configuration. For `Garage` set the region to `garage` and can use `garage-bucket` for the bucket name. (can be skipped for now)* 

**What I am planning to add:**
- Guest login;
- User profile; (working on this currently)
- Presentation (uploading, showcasing, etc.);
- About, contact, FAQ pages;
- Search functionality;
- Improved UI;
- GDPR compliance; (have some, but probably not necessary)
- More testing (unit, integration);
- Hosting on a cloud service.