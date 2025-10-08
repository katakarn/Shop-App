# 🛍️ Shop App — Spring Boot + Postgres + Redis + Kafka + SonarQube

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="docs/assets/logo-dark.png">
    <source media="(prefers-color-scheme: light)" srcset="docs/assets/logo-light.png">
    <img alt="ShopApp Logo" src="docs/assets/logo-light.png" width="220">
  </picture>
</p>

[![Java](https://img.shields.io/badge/Java-21-007396)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/build-Maven-C71A36)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-D92B21)](https://redis.io/)
[![Kafka](https://img.shields.io/badge/Kafka-3.x-231F20)](https://kafka.apache.org/)
[![SonarQube](https://img.shields.io/badge/SonarQube-Community-4E9BCD)](https://www.sonarsource.com/products/sonarqube/)
[![License](https://img.shields.io/badge/License-MIT-blue)](#-license)

> แอปตัวอย่างระบบร้านค้า (สินค้า/สต็อก/ออเดอร์) ด้วย Spring Boot + PostgreSQL + Redis + Kafka พร้อมตั้งค่า SonarQube สำหรับคุณภาพโค้ด

---

## 👀 ภาพรวม

<p align="center">
  <img src="docs/assets/arch.png" width="800" alt="System Architecture">
</p>

- **Spring Boot** ให้ REST API
- **PostgreSQL** เก็บข้อมูลหลัก (รองรับ migration ด้วย Flyway)
- **Redis** ใช้เป็นแคช / lock / rate limit
- **Kafka** สื่อสารเหตุการณ์แบบ async
- **SonarQube** วิเคราะห์คุณภาพโค้ด

### Mermaid Diagram
```mermaid
flowchart LR
  Client --> API[Spring Boot API]
  API -->|JPA| PG[(PostgreSQL)]
  API -->|cache| Redis[(Redis)]
  API -->|publish/consume| Kafka[(Kafka)]
