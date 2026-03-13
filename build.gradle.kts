import com.diffplug.spotless.LineEnding
import org.gradle.kotlin.dsl.project

plugins {
    id("internal.common-convention")
    id("internal.idea-convention")
    id("jacoco-report-aggregation")
    id("test-report-aggregation")
    alias(libs.plugins.spotless)
}

dependencies {
    jacocoAggregation(project(":lemur-app"))
    jacocoAggregation(project(":lemur-flyway"))
    jacocoAggregation(project(":lemur-libs:lemur-log4j2"))
    jacocoAggregation(project(":lemur-libs:lemur-migration"))
    jacocoAggregation(project(":lemur-libs:lemur-testkit"))

    testReportAggregation(project(":lemur-app"))
    testReportAggregation(project(":lemur-flyway"))
    testReportAggregation(project(":lemur-libs:lemur-log4j2"))
    testReportAggregation(project(":lemur-libs:lemur-migration"))
    testReportAggregation(project(":lemur-libs:lemur-testkit"))
}

reporting {
    reports {
        register<JacocoCoverageReport>("testCodeCoverageReport") {
            testSuiteName = "test"
        }

        register<AggregateTestReport>("testAggregateTestReport") {
            testSuiteName = "test"
        }
    }
}

spotless {
    java {
        target("**/src/**/*.java")

        googleJavaFormat("1.35.0")
        forbidWildcardImports()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    sql {
        target("**/src/main/resources/**/*.sql")
        toggleOffOn()

        dbeaver()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    kotlin {
        target("**/src/**/*.kt")

        ktfmt("0.61").metaStyle()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    kotlinGradle {
        target("*.gradle.kts", "buildSrc/*.gradle.kts", "buildSrc/src/**/*.gradle.kts")
        targetExclude("**/build/**")

        ktlint("1.8.0").editorConfigOverride(mapOf("max_line_length" to "120"))
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    format("yaml") {
        target("**/*.yml", "**/*.yaml")
        targetExclude("**/build/**")

        trimTrailingWhitespace()
        leadingTabsToSpaces(2)
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }

    format("misc") {
        target("**/.gitattributes", "**/.gitignore")

        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }
}

tasks.named<Task>("check") {
    dependsOn(tasks.named<JacocoReport>("testCodeCoverageReport"))
    dependsOn(tasks.named<TestReport>("testAggregateTestReport"))
}

defaultTasks("spotlessApply", "build")
