# Demarre le PostgreSQL portable (sans Docker)
$bin = "C:\Users\Hp\Tools\pgsql\bin"
$data = "C:\Users\Hp\Tools\pgdata"

if (-not (Test-Path "$data\PG_VERSION")) {
    & "$bin\initdb.exe" -D $data -U postgres --auth=trust --encoding=UTF8 --no-instructions
}
& "$bin\pg_ctl.exe" -D $data -l "$data\server.log" -o "-p 5432 -h 127.0.0.1" start
& "$bin\psql.exe" -h 127.0.0.1 -p 5432 -U postgres -d postgres -tc "SELECT 1 FROM pg_database WHERE datname='covoiturage'" |
    Where-Object { $_.Trim() -eq "0" } |
    ForEach-Object { & "$bin\createdb.exe" -h 127.0.0.1 -p 5432 -U postgres covoiturage }
Write-Host "PostgreSQL pret sur localhost:5432 (db=covoiturage, user=postgres, password=postgres)"
