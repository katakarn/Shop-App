# Stage 1: Build Stage (ใช้ image ที่มี JDK และ Maven สำหรับ Build)
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# คัดลอกไฟล์ pom.xml และ source code เพื่อให้ Docker สามารถ Build ได้
COPY pom.xml .
COPY src /app/src

# สั่ง Build โปรเจกต์ (นี่จะรัน tests ทั้งหมดด้วย)
RUN mvn clean package -DskipTests

# ----------------------------------------------------------------------------------

# Stage 2: Runtime Stage (ใช้ image ที่เบาที่สุดสำหรับรัน Java Application)
# เราใช้ JRE เท่านั้น เพื่อลดขนาดของ Container
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# 1. คัดลอกไฟล์ JAR ที่ได้จากการ Build ใน Stage 1
COPY --from=build /app/target/*.jar shopapp-backend.jar

# 2. กำหนด Environment Variables สำหรับ Production
# (นี่คือการตั้งค่าที่จะ override application.properties ใน production)
ENV DB_HOST=postgres
ENV DB_NAME=shopapp_db
ENV DB_USER=shopuser
ENV DB_PASS=supersecretpassword

# 3. ระบุ Port ที่ Spring Boot รันอยู่
EXPOSE 8080

# 4. สั่งรันแอปพลิเคชันเมื่อ Container ถูก Start
ENTRYPOINT ["java", "-jar", "shopapp-backend.jar",
            "--spring.datasource.url=jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}",
            "--spring.datasource.username=${DB_USER}",
            "--spring.datasource.password=${DB_PASS}",
            "--spring.jpa.hibernate.ddl-auto=none"
           ]