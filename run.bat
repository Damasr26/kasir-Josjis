@echo off
echo ========================================================
echo   MENJALANKAN PROYEK KASIR JOSJISMART
echo ========================================================
if not exist "bin" mkdir bin

echo Mengompilasi source code Java...
javac -d bin src\main\java\com\kasir\model\*.java src\main\java\com\kasir\service\*.java src\main\java\com\kasir\Main.java

if %ERRORLEVEL% EQU 0 (
    echo [SUKSES] Kompilasi berhasil! Membuka Website Kasir Josjismart...
    echo.
    java -cp bin com.kasir.Main
) else (
    echo [ERROR] Terjadi kegagalan saat kompilasi. Pastikan JDK terpasang.
    pause
)
