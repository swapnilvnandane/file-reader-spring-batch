# File Reader Application Using Spring Batch Processing

## Overview
This project is a Spring Boot application that processes files using Spring Batch. It includes validation, database persistence, and file archiving/error handling.
Along with file system, it also supports reading files from AWS S3 bucket with the help of AWS SQS queue service.

## Technologies Used
- Java
- Spring Boot
- Spring Batch
- Spring Data JPA
- Liquibase
- Maven
- Lombok
- Jasypt
- SQL
- AWS S3 and SQS

## Setup and Installation
1. **Clone the repository:**
    ```sh
    git clone https://github.com/swapnilvnandane/file-reader-spring-batch.git
    cd file-reader-spring-batch
    ```

2. **Build the project:**
    ```sh
    mvn clean install
    ```

3. **Generate SALT for Jasypt**
- This SALT key required to encrypt and decrypt the username and password using Jasypt. This key also use by Spring boot to decode the encrypted properties.
- Pass this SALT to application while runtime.
    ```sh
    -Djasypt.encryptor.password=<jasypt_salt>
    ```

4. **Run the application:**
    ```sh
    mvn spring-boot:run -Dspring.profiles.active=<requires_profile> -Djasypt.encryptor.password=<jasypt_salt>
    ```

## Configuration
- **Spring profiles:**
    - `aws` - To read / watch files on AWS S3 bucket.
    - `filesystem` - To read / watch files from file system.
 

- **Application YML properties:** Configure the application in `src/main/resources/application.yml`.
    ```properties
    com:
      file:
        location: <directory-path>
        separator: "," # file content separator
        archive: ${com.file.location}archive/
        error: ${com.file.location}error/
    ```
- **Database properties:** Put either same way mentioned above in `application.yml` or in application runtime environment (Environment Varable).
    ```properties
    com.filereader.mysql.driverClassName=com.mysql.cj.jdbc.Driver
    com.filereader.mysql.username=<jasypt_encrypted_username>
    com.filereader.mysql.password=<jasypt_encrypted_password>
    com.filereader.mysql.connectionUrl=jdbc:mysql://localhost:3306/file-reader?useSSL=false&autoReconnect=true
    com.filereader.mysql.maxWait=10000
    com.filereader.mysql.maxActive=30
    com.filereader.mysql.validationQuery=SELECT 1
    com.filereader.mysql.minIdle=0
    com.aws.accessKey.id=<jasypt_encrypted_aws_access_key_id>
    com.aws.accessKey.secret=<jasypt_encrypted_aws_access_key_secret>
    com.aws.s3.bucket-name=<jasypt_encrypted_aws_bucket_name>
    com.aws.region=<jasypt_encrypted_aws_region>
    com.aws.sqs.queuename=<jasypt_encrypted_aws_sqs_queue_name>
    ```

## [Liquibase](https://www.liquibase.com/)
- Database schema changes are managed using Liquibase.
- Configuration is defined in `src/main/resources/liquibase/liquibase-config.xml`.
- ChangeSets are defined in `src/main/resources/liquibase/changelog.xml`.
- SQL files for ChangeSets are located in `src/main/resources/sql/`.
- Run the following command to know status of the database sql scripts:
    ```sh
    mvn liquibase:status -f liquibase-config.xml
    ```
- Run the following command to update the database with the sql scripts:
    ```sh
    mvn liquibase:update -f liquibase-config.xml
    ```

## [Jasypt Encryption and Decryption](http://www.jasypt.org/)
To encrypt and decrypt the SQL username and password using Jasypt, use the following commands:

1. **Encrypt:**
- This will generate the encrypted text like `ENC(<encrypted-text>)`. Use this encrypted text in the application properties.
    ```sh
    mvn jasypt:encrypt-value -Djasypt.encryptor.password=<jasypt_salt> -Djasypt.plugin.value=<your-text>
    ```

2. **Decrypt:**
    ```sh
    jasypt:decrypt-value -Djasypt.encryptor.password=<jasypt_salt> -Djasypt.plugin.value=<encrypted-text>
    ```
## License
This project is licensed under the MIT License.