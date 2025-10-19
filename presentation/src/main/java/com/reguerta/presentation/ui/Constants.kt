package com.reguerta.presentation.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.reguerta.domain.enums.UiType
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults

@Stable
object Dimens {
    /** Typography scale tokens. Prefer `Typography` for styles; use these for ad‑hoc fontSizes. */
    object Text {
        val xxs = 10.sp
        val xs = 12.sp
        val sm = 14.sp
        /** Small-plus step used in some screens (15sp). */
        val smPlus = 15.sp
        val md = 16.sp
        val lg = 18.sp
        val xl = 20.sp
        val xxl = 24.sp
        val xxxl = 26.sp

        object Button {
            /** Pair actions, historically 18sp. */
            val pair = lg
            /** Single prominent buttons, historically 22sp. */
            val single = 22.sp
            /** Special emphasis actions, historically 26sp. */
            val special = xxxl
        }

        object Dialog {
            val title = xl
            val body = smPlus
        }
    }

    /** 4dp grid with a few pragmatic outliers kept for compatibility. */
    object Spacing {
        val zero = 0.dp
        val xxs = 2.dp
        val xs = 4.dp
        val sm = 8.dp
        val mdLow = 12.dp
        val md = 16.dp
        val lg = 24.dp
        val xl = 48.dp
        val xxl = 64.dp
    }

    /** Border strokes. */
    object Border {
        val thin = 1.dp
        val regular = 2.dp
        val default = regular
    }

    /** Corner radii. */
    object Radius {
        val xs = 4.dp
        val sm = 8.dp
        val md = 16.dp
        val lg = 32.dp
    }

    /** Generic container sizes, used for icons/avatars/etc. */
    object Size {
        val dp16 = 16.dp
        val dp36 = 36.dp
        val dp48 = 48.dp
        val dp64 = 64.dp
        val dp72 = 72.dp
        val dp88 = 88.dp
        val dp96 = 96.dp
    }
    /**
     * Component tokens: única fuente de verdad para tamaños de componentes.
     * Úsalos en los *composables base* (dialogs, buttons, top bars, inputs, imágenes).
     * Así evitamos repetir números mágicos en cada pantalla.
     */
    object Components {

        /**
         * Diálogos (AlertDialog, popups de confirmación).
         * - Si quieres cambiar el tamaño del icono o los paddings de todos los diálogos,
         *   modifica estos valores y se propagará a toda la app.
         */
        object Dialog {
            /** Tamaño del icono/badge del diálogo. */
            val iconSize = Size.dp36
            /** Tamaño del contenedor/círculo del icono en diálogos. */
            val badgeSize = Size.dp88
            /** Padding horizontal interno del contenido. */
            val horizontalPadding = Spacing.md
            /** Padding vertical interno del contenido. */
            val verticalPadding = Spacing.md
            /** Radio de las esquinas del contenedor del icono (si aplica). */
            val iconCornerRadius = Radius.sm

            /** Estilo para el título del diálogo. */
            val titleStyle @Composable get() = MaterialTheme.typography.titleLarge
            /** Estilo para el cuerpo del diálogo. */
            val bodyStyle @Composable get() = MaterialTheme.typography.bodyMedium
            /** Estilo para el texto de los botones del diálogo. */
            val buttonTextStyle @Composable get() = MaterialTheme.typography.labelLarge

            /** Proporción del círculo interior respecto al badge externo (0f..1f). */
            val innerBadgeFraction = 0.6f

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
            val minHeight = 40.dp
            /** Padding horizontal interno del botón. */
            val horizontalPadding = Spacing.md
            /** Padding vertical interno del botón. */
            val verticalPadding = Spacing.xs
            /** Radio por defecto del botón. */
            val cornerRadius = Radius.sm

            /** Estilo para la etiqueta de botón primario (single action). */
            val labelStyle @Composable get() = MaterialTheme.typography.labelLarge
            /** Estilo para la etiqueta en botones secundarios / par de acciones. */
            val secondaryLabelStyle @Composable get() = MaterialTheme.typography.labelMedium

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
            val height = 64.dp
            /** Padding interno del título/acciones. */
            val contentPadding = Spacing.sm
            /** Tamaño estándar de los iconos de la top bar. */
            val iconSize = 24.dp

            /** Tamaño específico para icono de navegación (por ahora igual a iconSize). */
            val leadingIconSize = iconSize
            /** Tamaño específico para iconos de acción (por ahora igual a iconSize). */
            val actionIconSize = iconSize

            /** Color del contenedor de la top bar. */
            val containerColor @Composable get() = MaterialTheme.colorScheme.surface
            /** Color del título. */
            val titleColor @Composable get() = MaterialTheme.colorScheme.onSurface
            /** Tint del icono de navegación. */
            val navIconTint @Composable get() = MaterialTheme.colorScheme.onSurface
            /** Tint de los iconos de acción. */
            val actionIconTint @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

            /** Colores de la TopAppBar (Medium por defecto) centralizados. */
            @Composable
            fun colors(): TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = containerColor,
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
            val minHeight = 56.dp
            /** Radio del campo. */
            val cornerRadius = Radius.sm
            /** Padding vertical del contenido del input. */
            val contentPaddingVertical = Spacing.xs
            /** Padding horizontal del contenido del input. */
            val contentPaddingHorizontal = Spacing.sm

            /** Estilo del texto del input. */
            val textStyle @Composable get() = MaterialTheme.typography.bodyLarge
            /** Estilo de la etiqueta (label/placeholder). */
            val labelStyle @Composable get() = MaterialTheme.typography.labelMedium
            /** Estilo del texto de apoyo/errores. */
            val supportingStyle @Composable get() = MaterialTheme.typography.bodySmall
        }

        /**
         * Imágenes y avatares.
         */
        object Image {
            /** Radio por defecto para miniaturas. */
            val cornerRadius = Radius.sm
            /** Grosor de borde por defecto para miniaturas. */
            val borderThickness = Border.thin

            /** Tamaño de avatar por defecto. */
            val avatar = Size.dp48
            /** Tamaño de thumbnail de producto por defecto. */
            val productThumb = Size.dp72
        }
    }
}
