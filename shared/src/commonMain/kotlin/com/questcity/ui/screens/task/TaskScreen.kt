package com.questcity.ui.screens.task

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.questcity.domain.model.TaskType
import com.questcity.ui.i18n.Language
import com.questcity.ui.i18n.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    state: TaskState,
    onAction: (TaskAction) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val strings = LocalStrings.current
    val language = strings.language

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.task) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Text("\u2190")
                        }
                    }
                },
                actions = {
                    Text(
                        text = formatTime(state.timeRemaining),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (state.timeRemaining < 30) Color.Red else Color.Unspecified,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null && state.task == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (onBack != null) {
                            Button(onClick = onBack) {
                                Text(strings.back)
                            }
                        }
                    }
                }
            }
            state.task != null -> {
                TaskContent(
                    task = state.task,
                    userAnswer = state.userAnswer,
                    showHints = state.showHints,
                    hintsShown = state.hintsShown,
                    result = state.result,
                    isAnswerSubmitted = state.isAnswerSubmitted,
                    isCompleted = state.isCompleted,
                    attempts = state.attempts,
                    totalScore = state.totalScore,
                    language = language,
                    onAction = onAction,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun TaskContent(
    task: com.questcity.domain.model.Task,
    userAnswer: String,
    showHints: Boolean,
    hintsShown: Int,
    result: com.questcity.domain.usecase.CheckResult?,
    isAnswerSubmitted: Boolean,
    isCompleted: Boolean,
    attempts: Int,
    totalScore: Int,
    language: Language,
    onAction: (TaskAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = task.type.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.prompt.get(language),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${strings.hints}: $attempts/$MAX_ATTEMPTS",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (attempts >= MAX_ATTEMPTS) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${strings.totalScore}: $totalScore",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (task.type) {
            TaskType.QUIZ -> QuizInput(
                answerKey = task.answerKey,
                userAnswer = userAnswer,
                onAnswerChanged = { onAction(TaskAction.AnswerChanged(it)) },
                isSubmitted = isAnswerSubmitted
            )
            TaskType.COUNT -> CountInput(
                userAnswer = userAnswer,
                onAnswerChanged = { onAction(TaskAction.AnswerChanged(it)) },
                isSubmitted = isAnswerSubmitted
            )
            TaskType.FACT_OR_FICT -> FactOrFictInput(
                userAnswer = userAnswer,
                onAnswerChanged = { onAction(TaskAction.AnswerChanged(it)) },
                isSubmitted = isAnswerSubmitted
            )
        }

        if (result != null) {
            Spacer(modifier = Modifier.height(16.dp))
            ResultCard(
                result = result,
                imdbFact = task.imdbFact.get(language),
                attempts = attempts,
                maxAttempts = MAX_ATTEMPTS,
                isCompleted = isCompleted,
                onRetry = { onAction(TaskAction.RetryAnswer) },
                onNext = { onAction(TaskAction.NextTask) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!isAnswerSubmitted && hintsShown < MAX_HINTS) {
                OutlinedButton(
                    onClick = { onAction(TaskAction.ShowHint) }
                ) {
                    Text("${strings.hint} ($hintsShown/$MAX_HINTS)")
                }
            }

            if (!isAnswerSubmitted && !isCompleted) {
                Button(
                    onClick = { onAction(TaskAction.SubmitAnswer) },
                    enabled = userAnswer.isNotBlank()
                ) {
                    Text(strings.submit)
                }
            }
        }

        if (showHints && task.hintsJson.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            HintsCard(hintsJson = task.hintsJson, hintsShown = hintsShown)
        }
    }
}

@Composable
fun QuizInput(
    answerKey: String,
    userAnswer: String,
    onAnswerChanged: (String) -> Unit,
    isSubmitted: Boolean
) {
    val strings = LocalStrings.current
    val options = remember(answerKey) {
        listOf(answerKey, strings.option1, strings.option2, strings.option3).shuffled()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = userAnswer == option
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.7f,
                animationSpec = tween(300)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(animatedAlpha),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { if (!isSubmitted) onAnswerChanged(option) },
                        enabled = !isSubmitted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option)
                }
            }
        }
    }
}

@Composable
fun CountInput(
    userAnswer: String,
    onAnswerChanged: (String) -> Unit,
    isSubmitted: Boolean
) {
    val strings = LocalStrings.current

    OutlinedTextField(
        value = userAnswer,
        onValueChange = { if (!isSubmitted) onAnswerChanged(it.filter { c -> c.isDigit() }) },
        label = { Text(strings.enterNumber) },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isSubmitted,
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactOrFictInput(
    userAnswer: String,
    onAnswerChanged: (String) -> Unit,
    isSubmitted: Boolean
) {
    val strings = LocalStrings.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (userAnswer == "fact")
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { if (!isSubmitted) onAnswerChanged("fact") },
                    enabled = !isSubmitted
                ) {
                    Text(strings.truth, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (userAnswer == "fiction")
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { if (!isSubmitted) onAnswerChanged("fiction") },
                    enabled = !isSubmitted
                ) {
                    Text(strings.lie, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun ResultCard(
    result: com.questcity.domain.usecase.CheckResult,
    imdbFact: String,
    attempts: Int,
    maxAttempts: Int,
    isCompleted: Boolean,
    onRetry: () -> Unit,
    onNext: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isCorrect)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (result.isCorrect) strings.correct else strings.incorrect,
                style = MaterialTheme.typography.titleMedium,
                color = if (result.isCorrect)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )

            if (result.notAtLocation) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.tooFarFromLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (imdbFact.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.imdbFact.format(imdbFact),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!result.isCorrect && attempts < maxAttempts) {
                    OutlinedButton(
                        onClick = onRetry,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(strings.retry)
                    }
                }
                Button(onClick = onNext) {
                    Text(strings.next)
                }
            }
        }
    }
}

@Composable
fun HintsCard(hintsJson: String, hintsShown: Int) {
    val strings = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = strings.hints,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = strings.hintOf.format(hintsShown, MAX_HINTS),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(minutes, secs)
}
