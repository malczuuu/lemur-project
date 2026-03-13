package arch

import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.core.domain.JavaParameterizedType
import com.tngtech.archunit.core.domain.JavaType
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent.violated
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import io.github.problem4j.spring.web.resolver.ProblemResolver
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@AnalyzeClasses(packages = ["io.github.malczuuu.lemur.app"])
class ControllerArchTests {

    @ArchTest
    fun controllerClassesMustHaveControllerSuffix(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(Controller::class.java)
            .or().areAnnotatedWith(RestController::class.java)
            .should().haveSimpleNameEndingWith("Controller")
            .check(classes)
    }

    @ArchTest
    fun controllerClassesMustBeWithinAdapterRestPackage(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(Controller::class.java)
            .or().areAnnotatedWith(RestController::class.java)
            .should().resideInAPackage("io.github.malczuuu.lemur.app.adapter.rest..")
            .check(classes)
    }

    @ArchTest
    fun controllerAdviceClassesMustBeWithinAdapterRestPackage(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(ControllerAdvice::class.java)
            .or().areAnnotatedWith(RestControllerAdvice::class.java)
            .should().resideInAPackage("io.github.malczuuu.lemur.app.adapter.rest..")
            .check(classes)
    }

    @ArchTest
    fun problemResolverClassesMustBeWithinAdapterRestPackage(classes: JavaClasses) {
        classes()
            .that().implement(ProblemResolver::class.java)
            .should().resideInAPackage("io.github.malczuuu.lemur.app.adapter.rest..")
            .check(classes)
    }

    @ArchTest
    fun controllerClassesMustNotDependOnDomain(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(Controller::class.java)
            .or().areAnnotatedWith(RestController::class.java)
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("io.github.malczuuu.lemur.app.domain..")
            .check(classes)
    }

    @ArchTest
    fun controllerMethodsParametersMustOnlyUsePrimitivesJavaOrContractRestTypes(classes: JavaClasses) {
        methods()
            .that(areRequestMappings())
            .should(haveAllowedParameterType())
            .check(classes)
    }

    @ArchTest
    fun controllerMethodsReturnTypeMustOnlyBePrimitivesJavaOrContractRestTypes(classes: JavaClasses) {
        methods()
            .that(areRequestMappings())
            .should(haveAllowedReturnType())
            .check(classes)
    }

    private fun areRequestMappings() = describe<JavaMethod>("are Spring @RequestMapping methods") {
        it.isAnnotatedWith(GetMapping::class.java) ||
            it.isAnnotatedWith(PostMapping::class.java) ||
            it.isAnnotatedWith(PutMapping::class.java) ||
            it.isAnnotatedWith(DeleteMapping::class.java) ||
            it.isAnnotatedWith(PatchMapping::class.java) ||
            it.isAnnotatedWith(RequestMapping::class.java)
    }

    private fun haveAllowedParameterType(): ArchCondition<JavaMethod> {
        val description = "only use primitives, 'java.*' or '..contract.rest.*' types as parameters"
        return object : ArchCondition<JavaMethod>(description) {
            override fun check(method: JavaMethod, events: ConditionEvents) {
                method.parameters.forEach { param ->
                    val allowed = param.rawType.isValidControllerType()
                    if (!allowed) {
                        val error = "method ${method.fullName} has parameter of disallowed type '${param.type.name}'"
                        events.add(violated(method, error))
                    }
                }
            }
        }
    }

    private fun haveAllowedReturnType(): ArchCondition<JavaMethod> {
        val description =
            "return only primitives, 'java.*' or '..contract.rest.*' types (ResponseEntity<T> allowed if T qualifies)"
        return object : ArchCondition<JavaMethod>(description) {
            override fun check(method: JavaMethod, events: ConditionEvents) {
                val allowed = when {
                    method.rawReturnType.isValidControllerType() -> true

                    method.rawReturnType.isEquivalentTo(ResponseEntity::class.java) -> {
                        val parameterized = method.returnType as? JavaParameterizedType
                        val typeArgs = parameterized?.actualTypeArguments ?: emptyList()
                        typeArgs.all { it.isValidControllerType() }
                    }

                    else -> false
                }
                if (!allowed) {
                    val error = "method ${method.fullName} has disallowed return type '${method.returnType.name}'"
                    events.add(violated(method, error))
                }
            }
        }
    }

    private fun JavaType.isValidControllerType(): Boolean {
        val rawType = toErasure()
        return rawType.isPrimitive ||
            rawType.name.startsWith("java.") ||
            rawType.name.startsWith("io.github.malczuuu.lemur.contract.rest.")
    }
}
