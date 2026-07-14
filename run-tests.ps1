<#
  run-tests.ps1
  ---------------------------------------------------------------------------
  Script de un solo comando para preparar el entorno y ejecutar la suite
  Appium + Cucumber del proyecto Art Gallery (Android).

  Hace, en orden:
    1. Verifica el JDK (fija JAVA_HOME al JDK 17 de Android Studio si hace falta).
    2. Descarga el APK a apps\app-home-test-mobile.apk (si no existe).
    3. Verifica que haya un emulador/dispositivo Android conectado (adb devices).
    4. Verifica/levanta el servidor Appium en el puerto 4723.
    5. Ejecuta:  mvn test

  Uso:
    powershell -ExecutionPolicy Bypass -File .\run-tests.ps1
    .\run-tests.ps1 -Tags "@scenario3"     # solo un escenario
    .\run-tests.ps1 -DryRun                # valida sin emulador ni APK
#>

param(
    [string]$Tags = "",
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
$ProjectDir = $PSScriptRoot
Set-Location $ProjectDir

function Write-Step($n, $msg) { Write-Host "`n[$n] $msg" -ForegroundColor Cyan }
function Write-Ok($msg)       { Write-Host "    OK  $msg" -ForegroundColor Green }
function Write-Warn($msg)     { Write-Host "    !!  $msg" -ForegroundColor Yellow }

# Datos del APK
$ApkUrl  = "https://github.com/automationapptest/home-test-mobile/releases/download/v1.0.0/app-home-test-app.apk"
$ApkDir  = Join-Path $ProjectDir "apps"
$ApkPath = Join-Path $ApkDir "app-home-test-mobile.apk"

Write-Host "==========================================================" -ForegroundColor White
Write-Host "  Art Gallery - Appium + Cucumber : preparacion y ejecucion" -ForegroundColor White
Write-Host "==========================================================" -ForegroundColor White

# ---------------------------------------------------------------------------
# 1. JDK
# ---------------------------------------------------------------------------
Write-Step 1 "Verificando JDK (se necesita javac / JDK 17+)"
$hasJavac = $null -ne (Get-Command javac -ErrorAction SilentlyContinue)
if (-not $hasJavac) {
    $studioJdk = "C:\Program Files\Android\Android Studio\jbr"
    if (Test-Path (Join-Path $studioJdk "bin\javac.exe")) {
        $env:JAVA_HOME = $studioJdk
        $env:Path = "$studioJdk\bin;$env:Path"
        Write-Ok "JAVA_HOME fijado al JDK de Android Studio: $studioJdk"
    } else {
        Write-Warn "No se encontro un JDK con javac. Instala un JDK 17 o fija JAVA_HOME manualmente."
        throw "JDK no disponible."
    }
} else {
    Write-Ok "javac disponible en el PATH."
}

# ---------------------------------------------------------------------------
# 1b. Android SDK (ANDROID_HOME)
# ---------------------------------------------------------------------------
Write-Step "1b" "Verificando Android SDK (ANDROID_HOME)"
if (-not $env:ANDROID_HOME) {
    # Toma el valor persistente de usuario si existe, o autodetecta la ruta tipica.
    $userAndroid = [Environment]::GetEnvironmentVariable("ANDROID_HOME", "User")
    $candidates = @($userAndroid, "$env:LOCALAPPDATA\Android\Sdk", "C:\Android\Sdk")
    $sdk = $candidates | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
    if ($sdk) {
        $env:ANDROID_HOME = $sdk
        $env:ANDROID_SDK_ROOT = $sdk
        $env:Path = "$sdk\platform-tools;$sdk\emulator;$env:Path"
        Write-Ok "ANDROID_HOME = $sdk"
    } else {
        Write-Warn "No se encontro el Android SDK. Instalalo con Android Studio y fija ANDROID_HOME."
        throw "Android SDK no disponible."
    }
} else {
    Write-Ok "ANDROID_HOME ya definido: $env:ANDROID_HOME"
}

# ---------------------------------------------------------------------------
# Modo DryRun: no necesita APK, emulador ni Appium
# ---------------------------------------------------------------------------
if ($DryRun) {
    Write-Step "*" "Modo DryRun: validando features y steps sin dispositivo"
    mvn test "-Dtest=DryRunTestRunner"
    exit $LASTEXITCODE
}

# ---------------------------------------------------------------------------
# 2. APK
# ---------------------------------------------------------------------------
Write-Step 2 "Verificando el APK de la aplicacion"
if (-not (Test-Path $ApkDir)) { New-Item -ItemType Directory -Path $ApkDir | Out-Null }
if (Test-Path $ApkPath) {
    $sizeMb = [math]::Round((Get-Item $ApkPath).Length / 1MB, 1)
    Write-Ok "APK ya presente ($sizeMb MB): $ApkPath"
} else {
    Write-Warn "APK no encontrado. Descargando (~163 MB) desde la release v1.0.0..."
    Write-Host "    $ApkUrl"
    try {
        # -UseBasicParsing y barra de progreso nativa de Invoke-WebRequest
        Invoke-WebRequest -Uri $ApkUrl -OutFile $ApkPath -UseBasicParsing
        $sizeMb = [math]::Round((Get-Item $ApkPath).Length / 1MB, 1)
        Write-Ok "Descargado ($sizeMb MB) en: $ApkPath"
    } catch {
        Write-Warn "Fallo la descarga automatica: $($_.Exception.Message)"
        Write-Warn "Descargalo manualmente y guardalo como: $ApkPath"
        Write-Warn "Pagina: https://github.com/automationapptest/home-test-mobile/releases/tag/v1.0.0"
        throw "APK no disponible."
    }
}

# ---------------------------------------------------------------------------
# 3. Dispositivo / emulador
# ---------------------------------------------------------------------------
Write-Step 3 "Verificando dispositivo Android (adb devices)"
$adb = Get-Command adb -ErrorAction SilentlyContinue
if (-not $adb) {
    $sdkAdb = Join-Path $env:LOCALAPPDATA "Android\Sdk\platform-tools\adb.exe"
    if (Test-Path $sdkAdb) { Set-Alias adb $sdkAdb -Scope Script; $adb = $true }
}
if ($adb) {
    $devices = (& adb devices) | Select-String -Pattern "\tdevice$"
    if ($devices) {
        Write-Ok "Dispositivo(s) conectado(s):"
        $devices | ForEach-Object { Write-Host "      $($_.Line)" }
    } else {
        Write-Warn "No hay dispositivos en estado 'device'."
        Write-Warn "Arranca un emulador desde Android Studio -> Device Manager, o conecta un telefono con depuracion USB."
        throw "Sin dispositivo Android."
    }
} else {
    Write-Warn "No se encontro 'adb'. Asegurate de tener Android SDK platform-tools en el PATH."
    throw "adb no disponible."
}

# ---------------------------------------------------------------------------
# 4. Servidor Appium
# ---------------------------------------------------------------------------
Write-Step 4 "Verificando servidor Appium (http://127.0.0.1:4723)"
$appiumUp = $false
try {
    $resp = Invoke-WebRequest -Uri "http://127.0.0.1:4723/status" -UseBasicParsing -TimeoutSec 3
    if ($resp.StatusCode -eq 200) { $appiumUp = $true }
} catch { $appiumUp = $false }

if ($appiumUp) {
    Write-Ok "Appium ya esta corriendo."
} else {
    $appium = Get-Command appium -ErrorAction SilentlyContinue
    if (-not $appium) {
        Write-Warn "Appium no esta corriendo y no se encontro el comando 'appium'."
        Write-Warn "Instalalo con:  npm install -g appium  &&  appium driver install uiautomator2"
        throw "Appium no disponible."
    }
    Write-Warn "Appium no responde. Levantandolo en una ventana nueva..."
    Start-Process powershell -ArgumentList "-NoExit","-Command","appium"
    Write-Host "    Esperando a que Appium responda..." -NoNewline
    $ready = $false
    for ($i = 0; $i -lt 30; $i++) {
        Start-Sleep -Seconds 2
        try {
            $resp = Invoke-WebRequest -Uri "http://127.0.0.1:4723/status" -UseBasicParsing -TimeoutSec 2
            if ($resp.StatusCode -eq 200) { $ready = $true; break }
        } catch { }
        Write-Host "." -NoNewline
    }
    Write-Host ""
    if ($ready) { Write-Ok "Appium listo." }
    else {
        Write-Warn "Appium no respondio a tiempo. Revisa la ventana que se abrio."
        throw "Appium no arranco."
    }
}

# ---------------------------------------------------------------------------
# 5. Ejecutar las pruebas
# ---------------------------------------------------------------------------
Write-Step 5 "Ejecutando la suite de pruebas (mvn test)"
if ($Tags -ne "") {
    Write-Host "    Filtrando por tags: $Tags" -ForegroundColor Cyan
    mvn test "-Dcucumber.filter.tags=$Tags"
} else {
    mvn test
}

$code = $LASTEXITCODE
Write-Host ""
if ($code -eq 0) {
    Write-Host "==========================================================" -ForegroundColor Green
    Write-Host "  BUILD SUCCESS - reporte: target\cucumber-reports\report.html" -ForegroundColor Green
    Write-Host "==========================================================" -ForegroundColor Green
} else {
    Write-Host "==========================================================" -ForegroundColor Yellow
    Write-Host "  Las pruebas terminaron con fallos (exit $code)." -ForegroundColor Yellow
    Write-Host "  Revisa el reporte: target\cucumber-reports\report.html" -ForegroundColor Yellow
    Write-Host "==========================================================" -ForegroundColor Yellow
}
exit $code
