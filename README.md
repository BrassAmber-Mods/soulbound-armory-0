The update for Minecraft 1.16.5 is in progress.

### testing
1. Clone this repository. If you don't know how to do that, then click the green button labeled "Code" and in the popup "Download ZIP".
2. If you downloaded the repository as a ZIP, then extract it. Make it your current directory.
3. Run `gradlew.bat --no-daemon runClient` in Windows or `./gradlew --no-daemon runClient` in Linux in a command prompt.
<sub>The flag `--no-daemon` is included because an available Gradle daemon that has been used in a ForgeGradle project will cause errors if used in a clean project.</sub>
