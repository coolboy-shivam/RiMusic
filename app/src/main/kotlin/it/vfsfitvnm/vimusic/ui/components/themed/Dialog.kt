package it.vfsfitvnm.vimusic.ui.components.themed

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.ColorFilter
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.Info
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.ui.styling.shimmer
import it.vfsfitvnm.vimusic.utils.bold
import it.vfsfitvnm.vimusic.utils.center
import it.vfsfitvnm.vimusic.utils.drawCircle
import it.vfsfitvnm.vimusic.utils.medium
import it.vfsfitvnm.vimusic.utils.secondary
import it.vfsfitvnm.vimusic.utils.semiBold
import kotlinx.coroutines.delay

@Composable
fun TextFieldDialog(
    hintText: String,
    onDismiss: () -> Unit,
    onDone: (String) -> Unit,
    modifier: Modifier = Modifier,
    cancelText: String = stringResource(R.string.cancel),
    doneText: String = stringResource(R.string.done),
    initialTextInput: String = "",
    singleLine: Boolean = true,
    maxLines: Int = 1,
    onCancel: () -> Unit = onDismiss,
    isTextInputValid: (String) -> Boolean = { it.isNotEmpty() }
) {
    val focusRequester = remember {
        FocusRequester()
    }
    val (colorPalette, typography) = LocalAppearance.current

    var textFieldValue by rememberSaveable(initialTextInput, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = initialTextInput,
                selection = TextRange(initialTextInput.length)
            )
        )
    }

    DefaultDialog(
        onDismiss = onDismiss,
        modifier = modifier

    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            textStyle = typography.xs.semiBold.center,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(imeAction = if (singleLine) ImeAction.Done else ImeAction.None),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isTextInputValid(textFieldValue.text)) {
                        onDismiss()
                        onDone(textFieldValue.text)
                    }
                }
            ),
            cursorBrush = SolidColor(colorPalette.text),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = textFieldValue.text.isEmpty(),
                        enter = fadeIn(tween(100)),
                        exit = fadeOut(tween(100)),
                    ) {
                        BasicText(
                            text = hintText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = typography.xs.semiBold.secondary,
                        )
                    }

                    innerTextField()
                }
            },
            modifier = Modifier
                .padding(all = 16.dp)
                .weight(weight = 1f, fill = false)
                .focusRequester(focusRequester)

        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            DialogTextButton(
                text = cancelText,
                onClick = onCancel
            )

            DialogTextButton(
                primary = true,
                text = doneText,
                onClick = {
                    if (isTextInputValid(textFieldValue.text)) {
                        onDismiss()
                        onDone(textFieldValue.text)
                    }
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }
}

@Composable
fun ConfirmationDialog(
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: String = stringResource(R.string.cancel),
    confirmText: String = stringResource(R.string.confirm),
    onCancel: () -> Unit = onDismiss,
    cancelBackgroundPrimary: Boolean = false,
    confirmBackgroundPrimary: Boolean = true
) {
    val (_, typography) = LocalAppearance.current

    DefaultDialog(
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        BasicText(
            text = text,
            style = typography.xs.medium.center,
            modifier = Modifier
                .padding(all = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            DialogTextButton(
                text = cancelText,
                primary = cancelBackgroundPrimary,
                onClick = onCancel
            )

            DialogTextButton(
                text = confirmText,
                primary = confirmBackgroundPrimary,
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
inline fun DefaultDialog(
    noinline onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    crossinline content: @Composable ColumnScope.() -> Unit
) {
    val (colorPalette) = LocalAppearance.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            horizontalAlignment = horizontalAlignment,
            modifier = modifier
                .padding(all = 10.dp)
                .background(
                    color = colorPalette.background1,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 24.dp, vertical = 16.dp),
            content = content
        )
    }
}

@Composable
inline fun <T> ValueSelectorDialog(
    noinline onDismiss: () -> Unit,
    title: String,
    titleSecondary: String? = null,
    selectedValue: T,
    values: List<T>,
    crossinline onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    crossinline valueText: @Composable (T) -> String = { it.toString() }
) {
    val (colorPalette, typography) = LocalAppearance.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .padding(all = 10.dp)
                .background(color = colorPalette.background1, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp)
        ) {
            BasicText(
                text = title,
                style = typography.s.semiBold,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )
            if (titleSecondary != null) {
                BasicText(
                    text = titleSecondary,
                    style = typography.xxs.semiBold,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 24.dp)
                )
            }
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                values.forEach { value ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onDismiss()
                                    onValueSelected(value)
                                }
                            )
                            .padding(vertical = 12.dp, horizontal = 24.dp)
                            .fillMaxWidth()
                    ) {
                        if (selectedValue == value) {
                            Canvas(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(
                                        color = colorPalette.accent,
                                        shape = CircleShape
                                    )
                            ) {
                                drawCircle(
                                    color = colorPalette.onAccent,
                                    radius = 4.dp.toPx(),
                                    center = size.center,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        blurRadius = 4.dp.toPx(),
                                        offset = Offset(x = 0f, y = 1.dp.toPx())
                                    )
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .size(18.dp)
                                    .border(
                                        width = 1.dp,
                                        color = colorPalette.textDisabled,
                                        shape = CircleShape
                                    )
                            )
                        }

                        BasicText(
                            text = valueText(value),
                            style = typography.xs.medium
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 24.dp)
            ) {
                DialogTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismiss,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
inline fun SelectorDialog(
    noinline onDismiss: () -> Unit,
    title: String,
    values: List<Info>?,
    crossinline onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    showItemsIcon: Boolean = false
) {
    val (colorPalette, typography) = LocalAppearance.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .padding(all = 10.dp)
                .background(color = colorPalette.background1, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp)
        ) {
            BasicText(
                text = title,
                style = typography.s.semiBold,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {

                values?.distinct()?.forEach { value ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onDismiss()
                                    onValueSelected(value.id)
                                }
                            )
                            .padding(vertical = 12.dp, horizontal = 24.dp)
                            .fillMaxWidth()
                    ) {
                        if (showItemsIcon)
                            IconButton(
                                onClick = {},
                                icon = R.drawable.playlist,
                                color = colorPalette.text,
                                modifier = Modifier
                                    .size(18.dp)
                            )

                        BasicText(
                            text = value.name ?: "Not selectable",
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = typography.xs.medium
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 24.dp)
            ) {
                DialogTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismiss,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
inline fun InputNumericDialog(
    noinline onDismiss: () -> Unit,
    title: String,
    value: String,
    valueMin: String,
    valueMax: String,
    placeholder: String,
    crossinline setValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography, thumbnailShape) = LocalAppearance.current
    val txtFieldError = remember { mutableStateOf("") }
    val txtField = remember { mutableStateOf(value) }
    val value_cannot_empty = stringResource(R.string.value_cannot_be_empty)
    val value_must_be_greater = stringResource(R.string.value_must_be_greater_than)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .padding(all = 10.dp)
                .background(color = colorPalette.background1, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp)
                .requiredHeight(190.dp)
        ) {
            BasicText(
                text = title,
                style = typography.s.semiBold,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                TextField(
                    modifier = Modifier
                        //.padding(horizontal = 30.dp)
                        .fillMaxWidth(0.7f),
                    /*
                    .border(
                        BorderStroke(
                            width = 1.dp,
                            color = if (txtFieldError.value.isEmpty()) colorPalette.textDisabled else colorPalette.red
                        ),

                        shape = thumbnailShape
                    ),
                     */
                    colors = TextFieldDefaults.textFieldColors(
                        placeholderColor = colorPalette.textDisabled,
                        cursorColor = colorPalette.text,
                        textColor = colorPalette.text,
                        backgroundColor = if (txtFieldError.value.isEmpty()) colorPalette.background1 else colorPalette.red,
                        focusedIndicatorColor = colorPalette.accent,
                        unfocusedIndicatorColor = colorPalette.textDisabled
                    ),
                    leadingIcon = {
/*
                        Image(
                            painter = painterResource(R.drawable.app_icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(colorPalette.background0),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable(
                                    indication = rememberRipple(bounded = false),
                                    interactionSource = remember { MutableInteractionSource() },
                                    enabled = true,
                                    onClick = { onDismiss() }
                                )
                        )

 */


                    },
                    placeholder = { Text(text = placeholder) },
                    value = txtField.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        txtField.value = it.take(10)
                    })
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                BasicText(
                    text = if (txtFieldError.value.isNotEmpty()) txtFieldError.value else "---",
                    style = typography.xs.medium,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 24.dp)
                )
            }


            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DialogTextButton(
                    text = stringResource(R.string.confirm),
                    onClick = {
                        if (txtField.value.isEmpty()) {
                            txtFieldError.value = value_cannot_empty
                            return@DialogTextButton
                        }
                        if (txtField.value.isNotEmpty() && txtField.value.toInt() < valueMin.toInt()) {
                            txtFieldError.value = value_must_be_greater + valueMin
                            return@DialogTextButton
                        }
                        setValue(txtField.value)
                    }
                )

                DialogTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismiss,
                    modifier = Modifier
                )
            }

        }
    }

}

@Composable
inline fun InputTextDialog(
    noinline onDismiss: () -> Unit,
    title: String,
    value: String,
    setValueRequireNotNull: Boolean = true,
    placeholder: String,
    crossinline setValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography, thumbnailShape) = LocalAppearance.current
    val txtFieldError = remember { mutableStateOf("") }
    val txtField = remember { mutableStateOf(value) }
    val value_cannot_empty = stringResource(R.string.value_cannot_be_empty)
    val value_must_be_greater = stringResource(R.string.value_must_be_greater_than)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .padding(all = 10.dp)
                .background(color = colorPalette.background1, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp)
                .defaultMinSize(Dp.Unspecified, 190.dp)
        ) {
            BasicText(
                text = title,
                style = typography.s.semiBold,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                TextField(
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    maxLines = 30,
                    colors = TextFieldDefaults.textFieldColors(
                        placeholderColor = colorPalette.textDisabled,
                        cursorColor = colorPalette.text,
                        textColor = colorPalette.text,
                        backgroundColor = if (txtFieldError.value.isEmpty()) colorPalette.background1 else colorPalette.red,
                        focusedIndicatorColor = colorPalette.accent,
                        unfocusedIndicatorColor = colorPalette.textDisabled
                    ),
                    leadingIcon = {
/*
                        Image(
                            painter = painterResource(R.drawable.app_icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(colorPalette.background0),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable(
                                    indication = rememberRipple(bounded = false),
                                    interactionSource = remember { MutableInteractionSource() },
                                    enabled = true,
                                    onClick = { onDismiss() }
                                )
                        )

 */
                    },
                    placeholder = { Text(text = placeholder) },
                    value = txtField.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {
                        txtField.value = it
                    })
            }

            Spacer(modifier = Modifier.height(10.dp))
            /*
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                BasicText(
                    text = if (txtFieldError.value.isNotEmpty()) txtFieldError.value else "---",
                    style = typography.xs.medium,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 24.dp)
                )
            }
             */


            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DialogTextButton(
                    text = stringResource(R.string.confirm),
                    onClick = {
                        if (txtField.value.isEmpty() && setValueRequireNotNull) {
                            txtFieldError.value = value_cannot_empty
                            return@DialogTextButton
                        }
                        //if (txtField.value.isNotEmpty() && !setValueRequireNotNull) {
                            setValue(txtField.value)
                            onDismiss()
                        //}
                    }
                )

                DialogTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismiss,
                    modifier = Modifier
                )
            }

        }
    }

}

@Composable
inline fun StringListDialog(
    title: String,
    addTitle: String,
    addPlaceholder: String,
    removeTitle: String,
    conflictTitle: String,
    list: List<String>,
    crossinline add: (String) -> Unit,
    crossinline remove: (String) -> Unit,
    noinline onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current
    var showStringAddDialog by remember {
        mutableStateOf(false)
    }
    var showStringRemoveDialog by remember {
        mutableStateOf(false)
    }
    var removingItem by remember { mutableStateOf("") }
    var errorDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .padding(all = 10.dp)
                .background(color = colorPalette.background1, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp)
                .defaultMinSize(Dp.Unspecified, 190.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicText(
                    text = title,
                    style = typography.s.semiBold,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 24.dp)
                )
                DialogTextButton(
                    text = addTitle,
                    primary = true,
                    onClick = {
                        showStringAddDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                list.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp),
                    ) {
                        BasicText(
                            text = item,
                            style = typography.s.semiBold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.trash),
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.clickable {
                                removingItem = item
                                showStringRemoveDialog = true
                            }
                        )
                    }
                }
            }

        }

    }

    if (showStringAddDialog) {
        InputTextDialog(
            onDismiss = { showStringAddDialog = false },
            placeholder = addPlaceholder,
            setValue = {
                if (it !in list) {
                    add(it)
                } else {
                    errorDialog = true
                }
            },
            title = addTitle,
            value = ""
        )
    }

    if (showStringRemoveDialog) {
        ConfirmationDialog(
            text = removeTitle,
            onDismiss = { showStringRemoveDialog = false },
            onConfirm = {
                remove(removingItem)
            }
        )
    }

    if (errorDialog) {
        DefaultDialog(
            onDismiss = {errorDialog = false},
            modifier = modifier
        ) {
            BasicText(
                text = conflictTitle,
                style = typography.xs.medium.center,
                modifier = Modifier
                    .padding(all = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                DialogTextButton(
                    text = stringResource(R.string.confirm),
                    primary = true,
                    onClick = {
                        errorDialog = false
                    }
                )
            }
        }
    }

}



@Composable
inline fun GenericDialog(
    noinline onDismiss: () -> Unit,
    title: String,
    textButton: String = stringResource(R.string.cancel),
    crossinline content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .padding(all = 48.dp)
                .background(color = colorPalette.background1, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp)
        ) {
            BasicText(
                text = title,
                style = typography.s.bold,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )

            content()

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 24.dp)
            ) {
                DialogTextButton(
                    text = textButton,
                    onClick = onDismiss,
                    modifier = Modifier
                )
            }
        }
    }
}
@Composable
fun NewVersionDialog (
    updatedProductName: String,
    updatedVersionName: String,
    updatedVersionCode: Int,
    onDismiss: () -> Unit
) {
    val (colorPalette, typography) = LocalAppearance.current
    val uriHandler = LocalUriHandler.current
    DefaultDialog(
        onDismiss = { onDismiss() },
        content = {
            BasicText(
                text = stringResource(R.string.update_available),
                style = typography.s.bold.copy(color = colorPalette.text),
            )
            Spacer(modifier = Modifier.height(10.dp))
            BasicText(
                text = String.format(stringResource(R.string.app_update_dialog_new),updatedVersionName),
                style = typography.xs.semiBold.copy(color = colorPalette.text),
            )
            Spacer(modifier = Modifier.height(10.dp))
            BasicText(
                text = stringResource(R.string.actions_you_can_do),
                style = typography.xs.semiBold.copy(color = colorPalette.textSecondary),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            ) {
                BasicText(
                    text = stringResource(R.string.open_the_github_releases_web_page_and_download_latest_version),
                    style = typography.xxs.semiBold.copy(color = colorPalette.textSecondary),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Image(
                    painter = painterResource(R.drawable.globe),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colorPalette.shimmer),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onDismiss()
                            uriHandler.openUri("https://github.com/fast4x/RiMusic/releases/latest")
                        }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            ) {
                BasicText(
                    text = stringResource(R.string.download_latest_version_from_github_you_will_find_the_file_in_the_notification_area_and_you_can_install_by_clicking_on_it),
                    style = typography.xxs.semiBold.copy(color = colorPalette.textSecondary),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Image(
                    painter = painterResource(R.drawable.downloaded),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colorPalette.shimmer),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onDismiss()
                            uriHandler.openUri("https://github.com/fast4x/RiMusic/releases/download/$updatedVersionName/app-release.apk")
                        }
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            ) {
                BasicText(
                    text = stringResource(R.string.f_droid_users_can_wait_for_the_update_info),
                    style = typography.xxs.semiBold.copy(color = colorPalette.textSecondary),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    )
}