# 📦Plane Issue Export CLI

> Plane issue export cli app is intended as a reporting helper. In its current configuration it pulls issues via plane api,
filters only the issues that are "in progress" or "done". There is a further optional filter for issues that fall between the dates specified in the run options --startDate and --endDate. these filtered issues are then sorted by assigned user and exported as a csv.


---

## 🚀 Features

- ✅ REST API support
- ✅ Maven-based build system
- ✅ JUnit 5 testing setup
- ✅ Configurable via `application.properties`

---

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Maven
- JUnit 5

---

## 📦 Requirements

- Java 17+
- Maven 3.6+

---

## 🏗️ Build and Run

-	Create a plane api key, refer to the following documentation (you may need elevated privileges): https://developers.plane.so/api-reference/introduction
-	Create the following environment variables: key=PLANE_API_KEY value=plane_api_<your-token>, key=PLANE_HIPPO_BENCH_URL value=<hippo-issues-url>.
-	Open terminal and nav to directory
-	run mvn clean package
-	java -jar target/plane-issue-export-cli-1.0.0.jar
-	options for issues between specified dates: --startDate=<yyyy-MM-dd> --endDate=<yyyy-MM-dd>


