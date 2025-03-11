package com.example.consecutivepractice.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Gamepad: ImageVector
    get() {
        if (_Gamepad != null) {
            return _Gamepad!!
        }
        _Gamepad = ImageVector.Builder(
            name = "Gamepad",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(480f, 420f)
                lineTo(360f, 300f)
                verticalLineToRelative(-220f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(220f)
                close()
                moveToRelative(180f, 180f)
                lineTo(540f, 480f)
                lineToRelative(120f, -120f)
                horizontalLineToRelative(220f)
                verticalLineToRelative(240f)
                close()
                moveToRelative(-580f, 0f)
                verticalLineToRelative(-240f)
                horizontalLineToRelative(220f)
                lineToRelative(120f, 120f)
                lineToRelative(-120f, 120f)
                close()
                moveTo(360f, 880f)
                verticalLineToRelative(-220f)
                lineToRelative(120f, -120f)
                lineToRelative(120f, 120f)
                verticalLineToRelative(220f)
                close()
                moveToRelative(120f, -574f)
                lineToRelative(40f, -40f)
                verticalLineToRelative(-106f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(106f)
                close()
                moveTo(160f, 520f)
                horizontalLineToRelative(106f)
                lineToRelative(40f, -40f)
                lineToRelative(-40f, -40f)
                horizontalLineTo(160f)
                close()
                moveToRelative(280f, 280f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(-106f)
                lineToRelative(-40f, -40f)
                lineToRelative(-40f, 40f)
                close()
                moveToRelative(254f, -280f)
                horizontalLineToRelative(106f)
                verticalLineToRelative(-80f)
                horizontalLineTo(694f)
                lineToRelative(-40f, 40f)
                close()
            }
        }.build()
        return _Gamepad!!
    }

private var _Gamepad: ImageVector? = null
