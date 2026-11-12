
package com.example.ecomarket.utils

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

fun traducirProducto(nombre: String): String {
    val traducciones = mapOf(
        // ABARROTES
        "apple" to "Manzana",
        "beef steak" to "Bistec de res",
        "cat food" to "Comida para gatos",
        "chicken meat" to "Carne de pollo",
        "cooking oil" to "Aceite de cocina",
        "banana" to "Banana",
        "eggs" to "Huevos",
        "fish" to "Pescado",
        "fish steak" to "Filete de pescado",
        "milk" to "Leche",
        "potatoes" to "Papas",
        "tomato" to "Tomate",
        "onion" to "Cebolla",
        "water" to "Agua",
        "rice" to "Arroz",
        "bread" to "Pan",
        "cucumber" to "Pepino",
        "dog food" to "Comida para perros",
        "green bell pepper" to "Chile pimiento verde",
        "green chili pepper" to "Chile verde",
        "honey jar" to "Frasco de miel",
        "ice cream" to "Helado",
        "juice" to "Jugo",
        "kiwi" to "Kiwi",

        // BELLEZA
        "essence mascara lash princess" to "Máscara de pestañas Lash Princess",
        "powder canister" to "Envase de polvos",
        "eyeshadow palette with mirror" to "Paleta de sombras con espejo",
        "eyesahdow palette with mirror" to "Paleta de sombras con espejo",
        "red lipstick" to "Labial rojo",
        "red nail polish" to "Esmalte de uñas rojo",

        // FRAGANCIAS
        "calvin klein ck one" to "Perfume CK One de Calvin Klein",
        "chanel coco noir eau de" to "Perfume Coco Noir de Chanel",
        "dior j'adore" to "Perfume J'adore de Dior",
        "dolce shine eau de" to "Perfume Dolce Shine",
        "gucci bloom eau de" to "Perfume Gucci Bloom",

        // MUEBLES
        "annibale colombo bed" to "Cama Annibale Colombo",
        "annibale colombo sofa" to "Sofá Annibale Colombo",
        "bedside table african cherry" to "Mesa de noche de cerezo africano",
        "knoll saarinen executive conference chair" to "Silla ejecutiva de conferencias Knoll Saarinen",
        "wooden bathroom sink with mirror" to "Lavabo de baño de madera con espejo"
    )

    return traducciones[nombre.trim().lowercase()] ?: nombre
}

fun traducirCategoria(categoria: String): String {
    return when (categoria.trim().lowercase()) {
        "beauty" -> "Belleza"
        "fragrances" -> "Fragancias"
        "furniture" -> "Muebles"
        "groceries" -> "Abarrotes"
        else -> categoria.replaceFirstChar { it.uppercase() }
    }
}

// TRADUCTOR AUTOMÁTICO CON GOOGLE ML KIT
object TraductorAutomatico {

    private val opciones = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.SPANISH)
        .build()

    private val traductor: Translator =
        Translation.getClient(opciones)

    suspend fun traducir(texto: String): String {
        if (texto.isBlank()) return texto

        return suspendCancellableCoroutine { continuation ->

            val condiciones = DownloadConditions.Builder().build()

            traductor.downloadModelIfNeeded(condiciones)
                .addOnSuccessListener {
                    traductor.translate(texto)
                        .addOnSuccessListener { resultado ->
                            if (continuation.isActive) {
                                continuation.resume(resultado)
                            }
                        }
                        .addOnFailureListener {
                            if (continuation.isActive) {
                                continuation.resume(texto)
                            }
                        }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(texto)
                    }
                }
        }
    }

    fun cerrar() {
        traductor.close()
    }
}
