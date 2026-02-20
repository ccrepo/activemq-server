# 📡 Project: Activemq-Server

Project ActiveMQ-Server is an example program that implememts a networked logger over http as a demo ActiveMQ based service.

## 📖 Usage

### 1️⃣ Pre-requisites:

#### Software:
      
```text
Linux Ubuntu 24.04.
Open JDK version 21.
Tomcat 10.
Active MQ 6.2.
```
        
### 2️⃣ Build:

Navigate to project home directory and execute the following commands

```bash
cd $projectDir
./gradlew clean
./gradlew build
```
    
### 3️⃣ Helper Script:

There are helper scripts in the $projectDir/bin directory
    
```text
projectDir/bin/c:  compile clean, build and generate javadoc.
```

### 4️⃣ Service:

The servlet exposes the following REST API URL base 
    
```text
http(s)://<domain>/activemq/log
```  

### 5️⃣ Security:

Security is implemented using public/private key pairs for brevity.

OAuth will be used in the main front end Sudoku Client.
