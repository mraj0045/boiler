package com.boiler_compiler

import com.boiler.Entry
import com.boiler.Ignore
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
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
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
            //IGNORE
        }
        return true
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return mutableSetOf(
            MapBuilder::class.java.canonicalName,
            Entry::class.java.canonicalName,
            Ignore::class.java.canonicalName
        )
    }

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()
        var suffix = element.getAnnotation(MapBuilder::class.java).suffix.capitalize()
        if (suffix.isEmpty()) suffix = SUFFIX

        val fileName = "$className$suffix"
        val fileBuilder = FileSpec.builder(pack, fileName)
        val classBuilder = createClassBuilder(fileName)
        val companionBuilder = createCompanionBuilder(pack, fileName)
        val mapBuilder = mapBuilder()
        val objectBuilder = objectBuilder(element)
        val fieldsList = mutableListOf<String>()

        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.FIELD && enclosed.getAnnotation(Ignore::class.java) == null) {
                val fieldName = enclosed.simpleName.toString()
                fieldsList.add("$fieldName = $fieldName")

                val entry: Entry? = enclosed.getAnnotation(Entry::class.java)
                var key = entry?.key
                if (key.isNullOrEmpty()) key = enclosed.simpleName.toString()

                val fieldType = getTypeName(enclosed)

                companionBuilder.addProperty(createCompanionProperty(fieldName, key))

                classBuilder.addProperty(createFieldProperty(fieldName, fieldType))

                classBuilder.addFunction(createSetterFunction(pack, fileName, fieldName, fieldType))

                addMapStatement(mapBuilder, fieldName, entry)

                addObjectStatement(objectBuilder, enclosed, fieldName, entry)

            }
        }
        mapBuilder.addStatement("return map")
        objectBuilder.addStatement(
            "return %T(${fieldsList.toString().removePrefix("[").removeSuffix("]")})",
            element.asType().asTypeName()
        )
        classBuilder.addFunction(mapBuilder.build())
        classBuilder.addFunction(objectBuilder.build())
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

    private fun mapBuilder(): FunSpec.Builder {
        return FunSpec.builder("toMap")
            .returns(mapClass().parameterizedBy(stringClass(), stringClass()))
            .addStatement("val map = hashMapOf<%T,%T>()", String::class, String::class)
    }

    private fun addMapStatement(mapBuilder: FunSpec.Builder, fieldName: String, entry: Entry?) {
        val className = getTypeConverter(entry)
        if (className.simpleName == "IgnoreConverter") {
            mapBuilder.addStatement("$fieldName?.run{ map.put(${fieldName.toUpperCase()}, this.toString()) } ")
        } else {
            mapBuilder.addStatement(
                "$fieldName?.run{ map.put(${fieldName.toUpperCase()}, %T().format(this)) } ",
                className
            )
        }
    }

    private fun objectBuilder(element: Element): FunSpec.Builder {
        val className = element.asType().asTypeName()
        return FunSpec.builder("toObject")
            .returns(className)
            .addParameter(
                ParameterSpec.builder(
                    "map",
                    mapClass().parameterizedBy(stringClass(), stringClass())
                ).build()
            )
    }


    private fun addObjectStatement(
        objectBuilder: FunSpec.Builder,
        element: Element,
        fieldName: String,
        entry: Entry?
    ) {
        val className = getTypeConverter(entry)
        if (className.simpleName == "IgnoreConverter") {
            objectBuilder.addStatement(
                "if(map.contains(${fieldName.toUpperCase()})) $fieldName = ${objectStatement(
                    fieldName,
                    element
                )}"
            )
        } else {
            objectBuilder.addStatement(
                "if(map.contains(${fieldName.toUpperCase()})) $fieldName = ${objectStatement(
                    fieldName,
                    element
                )}", className
            )
        }
    }

    private fun objectStatement(key: String, element: Element): String {
        val entry: Entry? = element.getAnnotation(Entry::class.java)
        val className = getTypeConverter(entry)
        return when {
            element.asType().asTypeName().isInt() -> "map[${key.toUpperCase()}]?.toIntOrNull()"
            element.asType().asTypeName().isFloat() -> "map[${key.toUpperCase()}]?.toFloatOrNull()"
            element.asType().asTypeName().isDouble() -> "map[${key.toUpperCase()}]?.toDoubleOrNull()"
            element.asType().asTypeName().isBoolean() -> "map[${key.toUpperCase()}]?.toBoolean()"
            else -> {
                if (className.simpleName == "IgnoreConverter") {
                    "map[${key.toUpperCase()}]"
                } else {
                    "%T().parse(map[${key.toUpperCase()}])"
                }
            }
        }
    }

    private fun getTypeConverter(entry: Entry?): ClassName {
        try {
            entry?.typeConverter
        } catch (e: MirroredTypeException) {
            val type = e.typeMirror as DeclaredType
            val pack = processingEnv.elementUtils.getPackageOf(type.asElement()).toString()
            val className = type.asElement().simpleName.toString()
            return ClassName(pack, className)
        }

        return ClassName("com.boiler", "IgnoreConverter")
    }
}

