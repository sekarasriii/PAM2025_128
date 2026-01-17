package com.example.fespace.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Visual transformation for WhatsApp number input
 * Formats: +62 8XXX-XXXX-XXXX
 * Example: +6281366359496 -> +62 8136-6359-496
 */
class WhatsAppVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        
        // If empty or doesn't start with +62, return as is
        if (original.isEmpty() || !original.startsWith("+62")) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        
        // Remove +62 prefix for formatting
        val digits = original.substring(3)
        
        // Format: +62 8XXX-XXXX-XXXX
        val formatted = buildString {
            append("+62 ")
            digits.forEachIndexed { index, char ->
                if (index == 4 || index == 8) append("-")
                append(char)
            }
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                val digitsOffset = offset - 3
                return when {
                    digitsOffset <= 4 -> offset + 1  // +62 + space
                    digitsOffset <= 8 -> offset + 2  // +62 + space + first dash
                    else -> offset + 3  // +62 + space + both dashes
                }
            }
            
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return minOf(offset, 3)
                return when {
                    offset <= 9 -> offset - 1
                    offset <= 14 -> offset - 2
                    else -> offset - 3
                }
            }
        }
        
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
