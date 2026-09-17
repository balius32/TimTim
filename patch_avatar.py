import re

file_path = "app/src/main/java/com/example/ui/components/AvatarComponents.kt"
with open(file_path, "r") as f:
    content = f.read()

if "import com.example.ui.localization.LocalAppStrings" not in content:
    content = content.replace("import androidx.compose.material3.ExperimentalMaterial3Api\n", "import androidx.compose.material3.ExperimentalMaterial3Api\nimport com.example.ui.localization.LocalAppStrings\n")

if "val strings = LocalAppStrings.current" not in content:
    content = content.replace("fun AvatarSelectionBottomSheet(\n    currentAvatarId: String,\n    onAvatarSelected: (String) -> Unit,\n    onDismiss: () -> Unit\n) {",
                              "fun AvatarSelectionBottomSheet(\n    currentAvatarId: String,\n    onAvatarSelected: (String) -> Unit,\n    onDismiss: () -> Unit\n) {\n    val strings = LocalAppStrings.current")

replacements = [
    ('text = "Choose Profile Picture"', 'text = strings.chooseProfilePicture'),
    ('text = "Pick a custom illustration or upload your photo"', 'text = strings.pickCustomIllustration'),
    ('text = "Upload from Your Phone"', 'text = strings.uploadFromPhone'),
    ('text = "Select any picture from your gallery or camera"', 'text = strings.selectPictureGalleryCamera'),
    ('text = "Gallery"', 'text = strings.galleryLabel'),
    ('text = "Default Avatar Collection"', 'text = strings.defaultAvatarCollection'),
]

for old, new in replacements:
    content = content.replace(old, new)

with open(file_path, "w") as f:
    f.write(content)
