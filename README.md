[README.md](https://github.com/user-attachments/files/26520084/README.md)
# Tournament Demo

Compile and run from VS Code terminal.

## Windows PowerShell
```powershell
mkdir bin -Force
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)
java -cp bin tournament.presentation.Main
```
