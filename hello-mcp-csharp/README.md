# Hello MCP C#

- <https://github.com/modelcontextprotocol/csharp-sdk/tags>
- <https://www.nuget.org/packages/ModelContextProtocol>


**Windows PowerShell:**
```powershell
.\run.ps1 server
.\run.ps1 client
.\run.ps1 ollama

.\run.ps1 server -Port 8080
.\run.ps1 client -Port 8080
```

**Windows CMD:**
```cmd
run.cmd server
run.cmd client
run.cmd ollama
```

**Linux/macOS:**
```bash
./run.sh server
./run.sh client
./run.sh ollama
```

```sh
cd mcp-server
dotnet run
dotnet run -- --port 9900
dotnet run -- --log-level Debug
```

```sh
cd mcp-client
dotnet run --project TestClient.csproj
dotnet run --project TestClient.csproj -- --port 9900
dotnet run --project TestOllama.csproj
dotnet run --project TestOllama.csproj -- --port 9900
```
