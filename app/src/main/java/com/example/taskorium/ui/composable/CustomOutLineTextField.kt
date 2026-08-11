package com.example.taskorium.ui.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomOutLineTextField(
    modifier: Modifier = Modifier,
    value: String,
    shape: Dp = 0.dp,
    onValueChange: (String)-> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(shape),
        label = { Text(text = label ) },
        placeholder = { Text(text = placeholder ) },
        leadingIcon = leadingIcon?.let {
            { Icon(
                imageVector = it,
                contentDescription = "$label icon",
                ) }
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
    )
}


//
//fun Modifier.scaleOnPress() = composed {
//
//    val interaction = remember {
//        MutableInteractionSource()
//    }
//
//    val pressed by interaction.collectIsPressedAsState()
//
//    val scale by animateFloatAsState(
//        if (pressed) .97f else 1f,
//        label = ""
//    )
//
//    graphicsLayer {
//        scaleX = scale
//        scaleY = scale
//    }.clickable(
//        interactionSource = interaction,
//        indication = rememberRipple(),
//        onClick = {}
//    )
//}