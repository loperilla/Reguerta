package com.reguerta.presentation.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.reguerta.domain.enums.UiType
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.reguerta.presentation.resize

@Stable
object Dimens {
    object Spacing {
        val zero = 0.dp
        val xxs @Composable get() = 2.resize()
        val xs @Composable get() = 4.resize()
        val sm @Composable get() = 8.resize()
        val mdLow @Composable get() = 12.resize()
        val md @Composable get() = 16.resize()
        val lg @Composable get() = 24.resize()
        val xl @Composable get() = 48.resize()
        val xxl @Composable get() = 64.resize()
        val xxxl @Composable get() = 96.resize()
    }

    /** Border strokes. */
    object Border {
        val thin @Composable get() = 1.resize()
        val regular @Composable get() = 2.resize()
        val large @Composable get() = 4.resize()
    }

    /** Corner radius. */
    object Radius {
        val xs @Composable get() = 4.resize()
        val sm @Composable get() = 8.resize()
        val md @Composable get() = 16.resize()
        val lg @Composable get() = 24.resize()
        val xl @Composable get() = 32.resize()
    }

    /** Generic container sizes, used for icons/avatars/etc. */
    object Size {
        val dp16 @Composable get() = 16.resize()
        val dp24 @Composable get() = 24.resize()
        val dp32 @Composable get() = 32.resize()
        val dp36 @Composable get() = 36.resize()
        val dp40 @Composable get() = 40.resize()
        val dp44 @Composable get() = 44.resize()
        val dp48 @Composable get() = 48.resize()
        val dp64 @Composable get() = 64.resize()
        val dp72 @Composable get() = 72.resize()
        val dp88 @Composable get() = 88.resize()
        val dp96 @Composable get() = 96.resize()
        val dp128 @Composable get() = 128.resize()
        val dp144 @Composable get() = 144.resize()
        val dp166 @Composable get() = 166.resize()
        val dp188 @Composable get() = 188.resize()
        val dp296 @Composable get() = 296.resize()
        val dp330 @Composable get() = 330.resize()
    }
    /*
     * Component tokens: única fuente de verdad para tamaños de componentes.
     */
    object Components {

        /**
         * Diálogos (AlertDialog, popups de confirmación).
         * - Si quieres cambiar el tamaño del icono o los paddings de todos los diálogos,
         *   modifica estos valores y se propagará a toda la app.
         */
        object Dialog {
            /** Tamaño del icono/badge del diálogo. */
            val iconSize @Composable get() = Size.dp44
            /** Tamaño del contenedor/círculo del icono en diálogos. */
            val badgeSize @Composable get() = Size.dp88
            /** Padding horizontal interno del contenido. */
            val horizontalPadding @Composable get() = Spacing.md
            /** Padding vertical interno del contenido. */
            val verticalPadding @Composable get() = Spacing.md
            /** Radio de las esquinas del contenedor del icono (si aplica). */
            val iconCornerRadius @Composable get() = Radius.sm

            val widthRatio @Composable get() = 0.9f

            /** Estilo para el título del diálogo. */
            val titleStyle @Composable get() = MaterialTheme.typography.titleLarge
            /** Estilo para el cuerpo del diálogo. */
            val bodyStyle @Composable get() = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
            /** Estilo para el texto de los botones del diálogo. */
            val buttonTextStyle @Composable get() = MaterialTheme.typography.labelLarge

            @Stable
            data class DialogColorSet(
                val badgeContainer: Color, // container del badge (outer circle)
                val badgeInner: Color,     // círculo interior (solid)
                val icon: Color            // color del icono dentro del badge
            )

            @Composable
            fun colorsFor(type: UiType): DialogColorSet {
                val cs = MaterialTheme.colorScheme
                return when (type) {
                    UiType.INFO -> DialogColorSet(
                        badgeContainer = cs.primaryContainer,
                        badgeInner = cs.primary,
                        icon = cs.onPrimary
                    )
                    UiType.ERROR, UiType.WARNING -> DialogColorSet(
                        badgeContainer = cs.errorContainer,
                        badgeInner = cs.error,
                        icon = cs.onError
                    )
                }
            }
        }

        /**
         * Botones principales/secundarios de la app.
         */
        object Button {
            /** Altura mínima táctil del botón. */
            val minHeight @Composable get() = Size.dp36
            /** Padding horizontal interno del botón. */
            val horizontalPadding @Composable get() = Spacing.md
            /** Padding vertical interno del botón. */
            val verticalPadding @Composable get() = Spacing.xs
            /** Radio por defecto del botón. */
            val cornerRadius @Composable get() = Radius.sm

            /** Estilo para la etiqueta de botón primario (single action). */
            val labelStyle @Composable get() = MaterialTheme.typography.titleMedium
            /** Estilo para la etiqueta en botones secundarios / par de acciones. */
            val secondaryLabelStyle @Composable get() = MaterialTheme.typography.labelMedium
            /** Tamaño de iconos dentro de botones. */
            val iconSize @Composable get() = Size.dp24

            /** Altura por defecto (iOS-like). */
            val defaultHeight @Composable get() = Size.dp48

            /** Ancho fijo por defecto cuando hay un solo botón (iOS). */
            val fixedSingleWidth @Composable get() = Size.dp296
            /** Ancho fijo cuando hay dos botones fuera de un diálogo (iOS). */
            val fixedTwoButtonsWidth @Composable get() = Size.dp166
            /** Ancho fijo cuando hay dos botones dentro de un diálogo (iOS). */
            val fixedTwoButtonsDialogWidth @Composable get() = Size.dp144

            /** Fondo sutil para Flat. */
            val flatContainerColor @Composable get() = MaterialTheme.colorScheme.surfaceVariant
            /** Color de texto por defecto para Flat (no error). */
            val flatContentColor @Composable get() = MaterialTheme.colorScheme.primary

            /** Colores deshabilitados comunes. */
            val disabledContainerColor @Composable get() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            val disabledContentColor @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

            /**
             * Paleta de colores para botones rellenos según la semántica UiType.
             */
            @Composable
            fun colors(type: UiType): ButtonColors {
                val cs = MaterialTheme.colorScheme
                return when (type) {
                    UiType.INFO -> ButtonDefaults.buttonColors(
                        containerColor = cs.primary,
                        contentColor = cs.onPrimary,
                        disabledContainerColor = cs.surfaceVariant,
                        disabledContentColor = cs.onSurface.copy(alpha = 0.38f)
                    )
                    UiType.ERROR, UiType.WARNING -> ButtonDefaults.buttonColors(
                        containerColor = cs.error,
                        contentColor = cs.onError,
                        disabledContainerColor = cs.surfaceVariant,
                        disabledContentColor = cs.onSurface.copy(alpha = 0.38f)
                    )
                }
            }

            /**
             * Paleta de colores para botones "inversos" (outlined/ghost) según UiType.
             */
            @Composable
            fun inverseColors(type: UiType): ButtonColors {
                val cs = MaterialTheme.colorScheme
                return when (type) {
                    UiType.INFO -> ButtonDefaults.buttonColors(
                        containerColor = cs.background,
                        contentColor = cs.primary,
                        disabledContainerColor = cs.surfaceVariant,
                        disabledContentColor = cs.onSurface.copy(alpha = 0.38f)
                    )
                    UiType.ERROR, UiType.WARNING -> ButtonDefaults.buttonColors(
                        containerColor = cs.background,
                        contentColor = cs.error,
                        disabledContainerColor = cs.surfaceVariant,
                        disabledContentColor = cs.onSurface.copy(alpha = 0.38f)
                    )
                }
            }

            /**
             * Color del borde para variantes inversas.
             */
            @Composable
            fun borderColor(type: UiType) = when (type) {
                UiType.INFO -> MaterialTheme.colorScheme.primary
                UiType.ERROR, UiType.WARNING -> MaterialTheme.colorScheme.error
            }
        }

        /**
         * Barra superior (TopAppBar).
         */
        object TopBar {
            /** Altura estándar de la barra superior. */
            val height @Composable get() = Size.dp64
            /** Padding interno del título/acciones. */
            val contentPadding @Composable get() = Spacing.sm
            /** Tamaño estándar de los iconos de la top bar. */
            val iconSize @Composable get() = Size.dp24

            /** Tamaño específico para icono de navegación (por ahora igual a iconSize). */
            val leadingIconSize @Composable get() = iconSize
            /** Tamaño específico para iconos de acción (por ahora igual a iconSize). */
            val actionIconSize @Composable get() = iconSize

            /** Color del contenedor de la top bar. */
            val containerColor @Composable get() = MaterialTheme.colorScheme.surface
            /** Color del título. */
            val titleColor @Composable get() = MaterialTheme.colorScheme.onSurface
            /** Tint del icono de navegación. */
            val navIconTint @Composable get() = MaterialTheme.colorScheme.onSurface
            /** Tint de los iconos de acción. */
            val actionIconTint @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

            /** Colores de la TopAppBar centralizados. */
            @Composable
            fun colors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = containerColor,
                navigationIconContentColor = navIconTint,
                titleContentColor = titleColor,
                actionIconContentColor = actionIconTint,
            )

            /** Estilo del título de la top bar. */
            val titleStyle @Composable get() = MaterialTheme.typography.headlineSmall
        }

        /**
         * Campos de texto / inputs.
         */
        object Input {
            /** Altura mínima del campo (alineado con M3 TextField). */
            val minHeight @Composable get() = Size.dp48
            /** Radio del campo. */
            val cornerRadius @Composable get() = Radius.sm
            /** Padding vertical del contenido del input. */
            val contentPaddingVertical @Composable get() = Spacing.xs
            /** Padding horizontal del contenido del input. */
            val contentPaddingHorizontal @Composable get() = Spacing.sm
            /** Tamaño del icono final (trailing), p. ej. mostrar/ocultar contraseña. */
            val trailingIconSize @Composable get() = Size.dp24

            /** Estilo del texto del input. */
            val textStyle @Composable get() = MaterialTheme.typography.bodyLarge
            /** Estilo de la etiqueta (label/placeholder). */
            val labelStyle @Composable get() = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            /** Estilo del texto de apoyo/errores. */
            val supportingStyle @Composable get() = MaterialTheme.typography.bodySmall
        }

        /**
         * Imágenes y avatares.
         */
        object Image {
            /** Radio por defecto para miniaturas. */
            val cornerRadius @Composable get() = Radius.sm
            /** Grosor de borde por defecto para miniaturas. */
            val borderThickness @Composable get() = Border.thin

            /** Tamaño de avatar por defecto. */
            val avatar @Composable get() = Size.dp48
            /** Tamaño de thumbnail de producto por defecto. */
            val productThumb @Composable get() = Size.dp72
            /** Duración por defecto del crossfade para imágenes remotas. */
            val crossfadeMillis = 250
        }
        /**
         * Tokens para animaciones Lottie.
         */
        object Lottie {
            /** Configuración para la animación de loading. */
            object Loading {
                /** Tamaño por defecto del loader. */
                val size @Composable get() = Size.dp64
                /** Velocidad por defecto. */
                val speed = 5f
            }
        }
        /**
         * Tokens para Card. Centraliza variantes visuales y tamaños.
         */
        object Card {
            enum class Kind { Filled, Elevated, Outlined }

            /** Radio por defecto para las tarjetas. */
            val cornerRadius @Composable get() = Radius.md

            /** Elevación por variante. */
            fun elevation(kind: Kind = Kind.Filled): Dp = when (kind) {
                Kind.Filled -> 0.dp
                Kind.Elevated -> 2.dp
                Kind.Outlined -> 0.dp
            }

            /** Color del contenedor por variante. */
            @Composable
            fun containerColor(kind: Kind = Kind.Filled): Color = when (kind) {
                Kind.Filled, Kind.Elevated, Kind.Outlined -> MaterialTheme.colorScheme.surfaceVariant
            }

            /** Color del contenido por variante. */
            @Composable
            fun contentColor(kind: Kind = Kind.Filled): Color = MaterialTheme.colorScheme.onSurface

            /** Borde por variante (solo Outlined). */
            @Composable
            fun border(kind: Kind = Kind.Filled): BorderStroke? = when (kind) {
                Kind.Outlined -> BorderStroke(Border.thin, MaterialTheme.colorScheme.outlineVariant)
                else -> null
            }
        }

        /**
         * Tokens para Checkbox. Tamaños y colores centralizados.
         */
        object Checkbox {
            /** Tamaño estándar del checkbox. */
            val size @Composable get() = Size.dp32

            /** Paleta de colores por defecto del checkbox (M3-aware). */
            @Composable
            fun colors(): CheckboxColors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                disabledCheckedColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledUncheckedColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        /**
         * Tokens para Counter/Stepper. Centraliza tamaños y colores del contador +/−.
         */
        object Counter {
            /** Altura mínima táctil del contenedor. */
            val minHeight @Composable get() = Size.dp32
            /** Radio del contenedor. */
            val cornerRadius @Composable get() = Radius.sm
            /** Padding horizontal interno. */
            val horizontalPadding @Composable get() = Spacing.xs
            /** Padding vertical interno. */
            val verticalPadding @Composable get() = Spacing.xxs
            /** Tamaño del icono +/−. */
            val iconSize @Composable get() = Size.dp24

            /** Color del contenedor. */
            val containerColor @Composable get() = MaterialTheme.colorScheme.surfaceVariant
            /** Color del contenido (iconos) habilitado. */
            val contentColor @Composable get() = MaterialTheme.colorScheme.onSurface
            /** Color del contenido (iconos) deshabilitado. */
            val disabledContentColor @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        }

        /**
         * Tokens para Dropdown. Alturas y paddings del ancla y menú.
         */
        object Dropdown {
            /** Altura mínima del ancla. */
            val anchorHeight @Composable get() = Size.dp36
            /** Padding horizontal interno del ancla. */
            val contentPaddingHorizontal @Composable get() = Spacing.sm
            /** Padding vertical interno del ancla. */
            val contentPaddingVertical @Composable get() = Spacing.xs
            /** Altura estimada por ítem (para consistencia visual). */
            val itemHeight @Composable get() = Size.dp40
            /** Altura máxima del menú. */
            val menuMaxHeight @Composable get() = Size.dp330
        }

        /**
         * Tokens para Divider. Centraliza grosor y color por defecto.
         */
        object Divider {
            /** Grosor estándar del divider. */
            val thickness @Composable get() = Border.thin
            /** Color estándar del divider. */
            val color: Color @Composable get() = DividerDefaults.color

            /** Variantes de divider. */
            enum class Kind { Subtle, Strong }

            /** Grosor por variante (usa tokens de Border para strong). */
            @Composable
            fun thickness(kind: Kind): Dp = when (kind) {
                Kind.Subtle -> thickness
                Kind.Strong -> Border.regular
            }

            /** Color por variante. */
            @Composable
            fun color(kind: Kind): Color = when (kind) {
                Kind.Subtle -> color
                Kind.Strong -> MaterialTheme.colorScheme.outlineVariant
            }
        }
    }
}
