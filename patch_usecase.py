import re

file_path = "app/src/main/java/com/example/domain/usecase/LogWorkTimeUseCase.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("import com.example.domain.repository.WorkRepository\n", "import com.example.domain.repository.WorkRepository\nimport com.example.ui.mvi.AppMessage\n")

content = content.replace("data class Error(val message: String) : TimeValidationResult", "data class Error(val errorType: AppMessage) : TimeValidationResult")

content = content.replace('TimeValidationResult.Error("Exit time cannot be earlier than enter time")', 'TimeValidationResult.Error(AppMessage.EXIT_TIME_CANNOT_BE_EARLIER)')
content = content.replace('TimeValidationResult.Error("Enter time cannot be later than exit time")', 'TimeValidationResult.Error(AppMessage.ENTER_TIME_CANNOT_BE_LATER)')

with open(file_path, "w") as f:
    f.write(content)
