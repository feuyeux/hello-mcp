# PowerShell script to start server with UTF-8 encoding
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
Set-Location mcp-server
mvn spring-boot:run
