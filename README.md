# Local development starter

This repository contains all information and assets to start local development with optional docker.

## Prerequisites

The following tools must be installed in your system.

* git (https://gitforwindows.org for Windows)
* jdk 17 (https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)

* Optional:
    * docker
    * docker-compose

### Setup the development environment

```bash
git clone git@github.com:nplinh0402/capstone.git
```

- Build the application
```bash
cd ems
```

- If you have installed docker and docker-compose, run commands as below to set up Mysql
```bash
cd ems/dev
docker-compose up -d
```
- Access Swagger: http://localhost:8080/swagger-ui.html

### Technical Notes

- When we do not deploy code to real system, avoid to use many migration scripts.
- Avoid to throw Exception for Errors relates to business logic
- Create a new Entity, must to extends EntityBase
- DTO Response should extend BaseResponse
- POST API request must have validation by declare DTO Request which have annotation validation(Ex: LoginRequest)
- Setting permissions for an endpoint, should configure in WebSecurityConfig
- Resource Bundle: when add a new key, the key must exist in 2 file, messages.properties and messages_th.properties
- Review code before creating new MR or push code to dev branch
### DO NOT MODIFY CODE IN BRANCH DEV
- Fetch origin
```bash
git checkout dev
git pull
```
- Merge and solve conflicts
```bash
git checkout [branch_name]
git merge dev
```
- Commit changes
```bash
git add .
git commit -m 'mo ta thay doi'
git push
```
- Create merge requset to branch dev