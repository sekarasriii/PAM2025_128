package com.example.fespace.ui.theme

import androidx.compose.ui.graphics.Color

// ========================================
// ELEGANT HOMEY COLOR PALETTE
// Fusion: Luxury Dark Mode + Organic Earthy
// ========================================

// Dark Backgrounds - Sophisticated Foundation
val DarkCharcoal = Color(0xFF1A1A1A)        // Main background
val DarkGray = Color(0xFF2C2C2C)            // Card background
val DarkGrayLight = Color(0xFF3A3A3A)       // Elevated cards
val DarkGrayDark = Color(0xFF0F0F0F)        // Deep shadows

// Warm Primary - Terracotta
val Terracotta = Color(0xFFB35A42)          // Primary CTA, highlights
val TerracottaLight = Color(0xFFC17A5C)     // Hover states, lighter accents
val TerracottaDark = Color(0xFF9A4A32)      // Pressed states
val TerracottaAlpha = Color(0x33B35A42)     // Subtle backgrounds

// Organic Secondary - Sage Green
val SageGreen = Color(0xFF8F9777)           // Secondary accent, success
val SageGreenLight = Color(0xFFA8B88F)      // Subtle highlights
val SageGreenDark = Color(0xFF6F7757)       // Darker variant
val SageGreenAlpha = Color(0x338F9777)      // Subtle backgrounds

// Metallics - Premium Accents
val Gold = Color(0xFFD4AF37)                // Premium highlights
val GoldLight = Color(0xFFE4BF47)           // Lighter gold
val Copper = Color(0xFFB87333)              // Decorative elements
val CopperLight = Color(0xFFC88343)         // Lighter copper

// Neutrals - Text & Surfaces
val Cream = Color(0xFFF5F5DC)               // Primary text on dark
val Beige = Color(0xFFE6DCC3)               // Secondary text
val WarmWhite = Color(0xFFFFFDF7)           // Highlights, pure white alternative
val WarmGray = Color(0xFFB8B0A3)            // Tertiary text

// Legacy Colors (for compatibility)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// Grays - Supporting Colors
val Gray100 = Color(0xFFF8F6F3)
val Gray200 = Color(0xFFEAE6DF)
val Gray300 = Color(0xFFD4CFC5)
val Gray400 = Color(0xFFB8B0A3)
val Gray500 = Color(0xFF9B8D7F)
val Gray600 = Color(0xFF7A6E5F)
val Gray700 = Color(0xFF5A5047)
val Gray800 = Color(0xFF3A342D)
val Gray900 = Color(0xFF1F1C18)

// Text Colors - Dark Theme Optimized
val TextPrimary = Cream                     // Main text
val TextSecondary = Beige                   // Secondary text
val TextTertiary = WarmGray                 // Tertiary text
val TextDisabled = Gray600                  // Disabled text

// Accent Colors - Semantic
val AccentGold = Gold                       // Premium, featured
val AccentGreen = SageGreen                 // Success, active, positive
val AccentRed = Color(0xFFD45C50)          // Error, alert, danger
val AccentOrange = Color(0xFFD4956C)       // Warning, pending
val AccentBlue = Color(0xFF6B8CAE)         // Info, neutral action

// Status Colors - Order States
val StatusPending = AccentOrange            // Pending review
val StatusInProgress = Terracotta           // In progress
val StatusApproved = SageGreen              // Approved/Completed
val StatusInDesign = Gold                   // In design phase
val StatusCancelled = AccentRed             // Cancelled/Rejected
val StatusDelivered = SageGreenLight        // Delivered

// Category Colors - Service Types
val CategoryResidential = Terracotta        // Residential projects
val CategoryCommercial = Copper             // Commercial projects
val CategoryRenovation = AccentOrange       // Renovation
val CategoryInterior = SageGreen            // Interior design
val CategoryLandscape = SageGreenDark       // Landscape
val CategoryConsultation = Gold             // Consultation

// Functional Colors
val SuccessGreen = SageGreen
val ErrorRed = AccentRed
val WarningOrange = AccentOrange
val InfoBlue = AccentBlue

// Surface Variants
val SurfaceDim = DarkGrayDark
val Surface = DarkGray
val SurfaceBright = DarkGrayLight
val SurfaceContainer = Color(0xFF252525)
val SurfaceContainerHigh = Color(0xFF303030)

// Legacy color aliases for backward compatibility
val BrownWarm = Terracotta                  // Alias to Terracotta
val CreamPrimary = Cream                    // Alias to Cream
val BrownDark = TerracottaDark              // Alias to TerracottaDark
val BeigeLight = Beige                      // Alias to Beige

// Additional surface colors
val DarkSurface = DarkGray                  // Surface color for cards
val CardBeige = Color(0xFFF5F1E8)           // Light beige for cards
val Ivory = Color(0xFFFFFFF0)               // Ivory color for highlights

// Status color aliases (for components that still use old names)
val StatusCompleted = SageGreen             // Approved/Completed status
val StatusReview = AccentOrange             // Review status
val StatusSurvey = AccentBlue               // Survey status
val StatusFinal = Gold                      // Final status