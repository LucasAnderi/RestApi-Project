<h1> RestApi-Project </h1>
This project was developed along 2022 , when i started to study the subject of software development at university.

The objective of the project is expose how to implement a basic web application with JAVA, SpringBoot+Thymeleaf and the SGBD used to store the data is PostgreSql.

<h2>Conceived concepts</h2>
<ul>
    <li>N-tier architecture concepts.</li>
    <li>Basic concepts of HTML and CSS with bootstrap.</li>
    <li>The basic structure of a RestAPI application and the manipulation of requests and responses using HTTP verbs (put, post, delete and get).</li>
    <li>CRUD operations.</li>
    <li>Login authentication using Security dependency of Spring boot.</li>
    <li>Jasper Report to generate .pdf reports</li>
    <li>GlobalExceptionHandler to throw any exception for all controllers.</li>
    <li>Unity testing with JUnit</li>
</ul>
<h2>How to run</h2>
<ul>
    <li>First download the project open the PGAdmin and create a database with the name 'db_fai_lds', copy and run the script fai-lds-db.sql</li>
    <li>Execute the lds-api and lds-client projects together.</li>
    <li>Open your browser and type localhost:8080 </li>
    <li>The login is admin and the password is admin.</li>
    <li>If you want to check out the Jasper Report function , you need to change the path on ReportServiceImpl at method generateAndGetPdfFilePath() to a valid path.      </li>
</ul>

