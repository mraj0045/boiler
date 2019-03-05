package com.boiler_compiler

import com.boiler.Entry
import com.boiler.MapBuilder
import com.boiler_compiler.MapProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class MapProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        private const val SUFFIX = "Builder"
    }

    private var messager: Messager? = null
    private var elements: Elements? = null

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
    }

    override fun process(set: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            for (element in roundEnv.getElementsAnnotatedWith(MapBuilder::class.java)) {
                // Only class which is annotated with Map is supported.
                if (element.kind != ElementKind.CLASS) {
                    messager?.printMessage(Diagnostic.Kind.ERROR, "@Map annotation cannot be applied to $element")
                    return true
                }
                processAnnotation(element)
            }
        } catch (e: Exception) {
        }
        return true
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return mutableSetOf(MapBuilder::class.java.canonicalName, Entry::class.java.canonicalName)
    }

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "$className$SUFFIX"
        val fileBuilder = FileSpec.builder(pack, fileName)
        val classBuilder = createClassBuilder(fileName)
        val companionBuilder = createCompanionBuilder(pack, fileName)
        val mapBuilder = mapBuilder()

        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.FIELD) {
                val fieldName = enclosed.simpleName.toString()

                var key = enclosed.getAnnotation(Entry::class.java)?.key
                if (key.isNullOrEmpty()) key = enclosed.simpleName.toString()

                val fieldType = getTypeName(enclosed)

                companionBuilder.addProperty(createCompanionProperty(fieldName, key))

                classBuilder.addProperty(createFieldProperty(fieldName, fieldType))

                classBuilder.addFunction(createSetterFunction(pack, fileName, fieldName, fieldType))

                mapBuilder.addStatement("$fieldName?.run{ map.put(${fieldName.toUpperCase()}, this.toString()) }")
            }
        }
        mapBuilder.addStatement("return map")
        classBuilder.addFunction(mapBuilder.build())
        classBuilder.addType(companionBuilder.build())
        val file = fileBuilder.addType(classBuilder.build()).build()
        val genDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(genDir))
    }

    private fun createClassBuilder(fileName: String): TypeSpec.Builder {
        return TypeSpec.classBuilder(fileName).primaryConstructor(
            FunSpec.constructorBuilder().addModifiers(KModifier.PRIVATE).build()
        )
    }

    private fun createCompanionBuilder(pack: String, fileName: String): TypeSpec.Builder {
        val className = ClassName(pack, fileName)
        return TypeSpec.companionObjectBuilder().addFunction(
            FunSpec.builder("init").returns(className).addStatement("return %T()", className).build()
        )
    }

    private fun mapBuilder(): FunSpec.Builder {
        return FunSpec.builder("toMap")
            .returns(mapClass().parameterizedBy(stringClass(), stringClass()))
            .addStatement("val map = hashMapOf<%T,%T>()", String::class, String::class)
    }

    private fun createCompanionProperty(fieldName: String, key: String): PropertySpec {
        return PropertySpec.builder(fieldName.toUpperCase(), String::class, KModifier.PRIVATE, KModifier.CONST)
            .initializer("%S", key)
            .build()
    }

    private fun createFieldProperty(fieldName: String, fieldType: TypeName): PropertySpec {
        return PropertySpec.builder(fieldName, fieldType, KModifier.PRIVATE)
            .mutable()
            .initializer("null")
            .build()
    }

    private fun createSetterFunction(pack: String, className: String, fieldName: String, fieldType: TypeName): FunSpec {
        return FunSpec.builder("set${fieldName.capitalize()}")
            .addParameter(ParameterSpec.builder(fieldName, fieldType).build())
            .returns(ClassName(pack, className))
            .addStatement("this.%N = %N", fieldName, fieldName)
            .addStatement("return this")
            .build()
    }
}
