<?php
    // Konfigurasi Database SIMRS (Arahkan ke Server RS Anda)
    $db_hostname            = "192.168.5.100";
    $db_username            = "root";
    $db_password            = "";
    $db_name                = "051023";
    
    // IP/Host Apache Webapps (kosongkan jika sama dengan db_hostname)
    $webapps_hostname       = "192.168.2.69";

    // Urutan nomor registrasi: 'dokter', 'poli', 'dokter + poli'
    if (!defined('URUTNOREG')) {
        define('URUTNOREG', 'dokter + poli');
    }

    // Token WhatsApp Gateway Fonnte (https://fonnte.com)
    if (!defined('FONNTE_TOKEN')) {
        define('FONNTE_TOKEN', '7L9VJDQs86AZFHxcEKy3');
    }
?>
