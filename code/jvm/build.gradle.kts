import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pt.isel.daw"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

val isArm = System.getProperty("os.arch") == "aarch64"
val isMac = System.getProperty("os.name").toLowerCase().contains("mac")

plugins {
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}
ext {
	set("projectMainClass", "pt.isel.daw.battleship.BattleshipApplicationKt")
}

springBoot {
	mainClass.set(project.ext.get("projectMainClass") as String)
}


repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jdbi:jdbi3-core:3.34.0")
	implementation("org.jdbi:jdbi3-kotlin:3.34.0")
	implementation("org.jdbi:jdbi3-postgres:3.34.0")
	implementation("org.postgresql:postgresql:42.5.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")

	if(isMac && isArm) {
		runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.82.Final:osx-aarch_64")
	}
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	environment(
		mapOf(
			"JDBC_TEST_DATABASE_URL" to "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=craquesdabola123"
		)
	)
}