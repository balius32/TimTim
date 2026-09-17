import re

file_path = "app/src/main/java/com/example/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("import com.example.ui.mvi.WorkUiEffect\n", "import com.example.ui.mvi.WorkUiEffect\nimport com.example.ui.localization.LocalAppStrings\n")

content = content.replace("is WorkUiEffect.ShowSnackbar -> {\n                    snackbarHostState.showSnackbar(effect.message)\n                }",
                          "is WorkUiEffect.ShowSnackbar -> {\n                    val msg = effect.message.asString(strings) + (effect.extra?.let { \" $it\" } ?: \"\")\n                    snackbarHostState.showSnackbar(msg)\n                }")

content = content.replace("is WorkUiEffect.TimeValidationError -> {\n                    snackbarHostState.showSnackbar(effect.message)\n                }",
                          "is WorkUiEffect.TimeValidationError -> {\n                    snackbarHostState.showSnackbar(effect.errorType.asString(strings))\n                }")

with open(file_path, "w") as f:
    f.write(content)
