param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$ForgeiaArgs
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

Push-Location $repoRoot
try {
    mvn -q -pl forgeia-core -DskipTests compile
    java -cp "forgeia-core\target\classes" es.uco.ecoagent.core.cli.ForgeiaCli @ForgeiaArgs
}
finally {
    Pop-Location
}

