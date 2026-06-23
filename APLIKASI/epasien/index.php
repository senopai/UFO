<?php
    header("X-Robots-Tag: noindex", true);
    ob_start();
    session_start();
    date_default_timezone_set('Asia/Jakarta');
    require_once('conf/command.php');
    require_once('conf/conf.php');
    header("Expires: Tue, 01 Jan 2000 00:00:00 GMT");
    header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
    header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
    header("Cache-Control: post-check=0, pre-check=0", false);
    header("Pragma: no-cache");

    $preloaded_user = null;
    if (isset($_SESSION["ses_pasien"])) {
        $norm = cleankar(encrypt_decrypt($_SESSION['ses_pasien'], 'd'));
        // Fetch patient profile
        $queryuser = bukaquery("select pasien.nm_pasien, pasien.no_ktp, pasien.email, pasien.jk, personal_pasien.gambar, pasien.no_tlp, pasien.tmp_lahir, date_format(pasien.tgl_lahir,'%d/%m/%Y') as tgl_lahir, pasien.alamat from pasien inner join personal_pasien on personal_pasien.no_rkm_medis=pasien.no_rkm_medis where pasien.no_rkm_medis='$norm'");
        if ($queryuser && mysqli_num_rows($queryuser) > 0) {
            $user = mysqli_fetch_array($queryuser);
            $protocol = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off' || $_SERVER['SERVER_PORT'] == 443) ? "https://" : "http://";
            $photo = "";
            if ($user['gambar'] == "" || $user['gambar'] == "-") {
                $photo = ($user['jk'] == "L") ? "images/userlaki.png" : "images/userperempuan.png";
            } else {
                $photo = $protocol . $_SERVER['HTTP_HOST'] . "/webapps/photopasien/" . $user['gambar'];
            }
            $preloaded_user = [
                'name' => $user['nm_pasien'],
                'norm' => $norm,
                'nik' => $user['no_ktp'] ? $user['no_ktp'] : '',
                'phone' => $user['no_tlp'] ? $user['no_tlp'] : '',
                'email' => $user['email'] ? $user['email'] : '',
                'gender' => $user['jk'],
                'pob' => $user['tmp_lahir'] ? $user['tmp_lahir'] : '',
                'dob' => $user['tgl_lahir'] ? $user['tgl_lahir'] : '',
                'address' => $user['alamat'] ? $user['alamat'] : '',
                'photo' => $photo
            ];
        }
    }

    $indexPath = 'epasien-modern/dist/index.html';
    if (!file_exists($indexPath)) {
        die("<h3>EPasien Modern tidak dapat dimuat. Harap jalankan 'npm run build' di dalam folder epasien-modern terlebih dahulu.</h3>");
    }

    $html = file_get_contents($indexPath);

    // Inject Preloaded Session
    $preload_script = '<script>';
    if ($preloaded_user) {
        $preload_script .= 'window.__PRELOADED_USER__ = ' . json_encode($preloaded_user) . ';';
    } else {
        $preload_script .= 'window.__PRELOADED_USER__ = null;';
    }
    $preload_script .= '</script>';

    $html = str_replace('<head>', '<head>' . $preload_script, $html);

    // Replace absolute assets path to relative so it loads correctly in subfolder
    $html = str_replace('href="/assets/', 'href="./epasien-modern/dist/assets/', $html);
    $html = str_replace('src="/assets/', 'src="./epasien-modern/dist/assets/', $html);
    $html = str_replace('href="./assets/', 'href="./epasien-modern/dist/assets/', $html);
    $html = str_replace('src="./assets/', 'src="./epasien-modern/dist/assets/', $html);
    
    // Replace favicon and icons
    $html = str_replace('href="/favicon.svg"', 'href="./epasien-modern/dist/favicon.svg"', $html);
    $html = str_replace('href="./favicon.svg"', 'href="./epasien-modern/dist/favicon.svg"', $html);
    $html = str_replace('href="/icons.svg"', 'href="./epasien-modern/dist/icons.svg"', $html);
    $html = str_replace('href="./icons.svg"', 'href="./epasien-modern/dist/icons.svg"', $html);

    echo $html;
?>
