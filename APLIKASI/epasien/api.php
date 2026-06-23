<?php
    ob_start();
    session_start();
    date_default_timezone_set('Asia/Jakarta');
    require_once('conf/command.php');
    require_once('conf/conf.php');

    header('Content-Type: application/json');
    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type, Authorization');

    $action = isset($_GET['action']) ? $_GET['action'] : '';

    // Helper to get patient norm from session
    function getSessionNorm() {
        if (isset($_SESSION['ses_pasien'])) {
            return cleankar(encrypt_decrypt($_SESSION['ses_pasien'], 'd'));
        }
        return null;
    }

    // Helper: Format date in Indonesian style
    function formatIndoDate($dateStr) {
        $timestamp = strtotime($dateStr);
        if (!$timestamp) return $dateStr;
        
        $daysIndo = ['Minggu', 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
        $monthsIndo = [
            1 => 'Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni',
            'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember'
        ];
        
        $dayName = $daysIndo[date('w', $timestamp)];
        $day = date('j', $timestamp);
        $monthNum = (int)date('n', $timestamp);
        $year = date('Y', $timestamp);
        
        return "$dayName, $day " . $monthsIndo[$monthNum] . " $year";
    }

    // Helper: Send WhatsApp Message using Fonnte API
    function sendFonnteMessage($token, $target, $message) {
        if (empty($token) || empty($target)) {
            return;
        }
        
        $cleanTarget = preg_replace('/[^0-9]/', '', $target);
        if (substr($cleanTarget, 0, 1) === '0') {
            $cleanTarget = '62' . substr($cleanTarget, 1);
        } else if (substr($cleanTarget, 0, 1) === '8') {
            $cleanTarget = '62' . $cleanTarget;
        } else if (substr($cleanTarget, 0, 2) !== '62') {
            $cleanTarget = '62' . $cleanTarget;
        }
        
        $curl = curl_init();
        curl_setopt_array($curl, array(
          CURLOPT_URL => 'https://api.fonnte.com/send',
          CURLOPT_RETURNTRANSFER => true,
          CURLOPT_ENCODING => '',
          CURLOPT_MAXREDIRS => 10,
          CURLOPT_TIMEOUT => 15,
          CURLOPT_FOLLOWLOCATION => true,
          CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
          CURLOPT_CUSTOMREQUEST => 'POST',
          CURLOPT_POSTFIELDS => array(
            'target' => $cleanTarget,
            'message' => $message,
            'countryCode' => '62'
          ),
          CURLOPT_HTTPHEADER => array(
            'Authorization: ' . $token
          ),
        ));
        
        $response = curl_exec($curl);
        $err = curl_error($curl);
        curl_close($curl);
        
        if ($err) {
            error_log("Fonnte cURL Error: " . $err);
        } else {
            error_log("Fonnte Response: " . $response);
        }
    }

    if ($action == 'login') {
        $norm = isset($_POST['norm']) ? cleankar($_POST['norm']) : '';
        $password = isset($_POST['password']) ? $_POST['password'] : '';

        if (empty($norm) || empty($password)) {
            echo json_encode(['success' => false, 'message' => 'Nomer Rekam Medis dan Password wajib diisi.']);
            exit;
        }

        // Query database using AES encryption matching SIMKES Khanza
        $check = getOne2("select count(*) from personal_pasien where no_rkm_medis='$norm' and password=aes_encrypt('$password','windi')");
        if ($check > 0) {
            $_SESSION["ses_pasien"] = encrypt_decrypt($norm, "e");
            
            // Fetch patient profile
            $queryuser = bukaquery("select pasien.nm_pasien, pasien.no_ktp, pasien.email, pasien.jk, personal_pasien.gambar, pasien.no_tlp, pasien.tmp_lahir, date_format(pasien.tgl_lahir,'%d/%m/%Y') as tgl_lahir, pasien.alamat from pasien inner join personal_pasien on personal_pasien.no_rkm_medis=pasien.no_rkm_medis where pasien.no_rkm_medis='$norm'");
            $user = mysqli_fetch_array($queryuser);

            $protocol = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off' || $_SERVER['SERVER_PORT'] == 443) ? "https://" : "http://";
            $photo = "";
            if ($user['gambar'] == "" || $user['gambar'] == "-") {
                $photo = ($user['jk'] == "L") ? "images/userlaki.png" : "images/userperempuan.png";
            } else {
                $photo = $protocol . $_SERVER['HTTP_HOST'] . "/webapps/photopasien/" . $user['gambar'];
            }

            // Set session variables
            $_SESSION["nm_pasien"]  = $user["nm_pasien"];
            $_SESSION["email"]      = $user["email"];
            $_SESSION["jk"]         = $user["jk"];
            $_SESSION["no_tlp"]     = $user["no_tlp"];
            $_SESSION["tmp_lahir"]  = $user["tmp_lahir"];
            $_SESSION["tgl_lahir"]  = $user["tgl_lahir"];
            $_SESSION["photo"]      = $photo;

            echo json_encode([
                'success' => true,
                'user' => [
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
                ]
            ]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Nomor rekam medis atau password Anda salah.']);
        }
        exit;
    }

    if ($action == 'logout') {
        session_destroy();
        echo json_encode(['success' => true]);
        exit;
    }

    if ($action == 'get_dashboard_data') {
        $norm = getSessionNorm();
        if (!$norm) {
            echo json_encode(['success' => false, 'message' => 'Session expired. Silakan login kembali.']);
            exit;
        }

        // Active queue / latest registration from reg_periksa
        $today = date('Y-m-d');
        $q = "select reg_periksa.tgl_registrasi as tanggal_periksa, reg_periksa.kd_dokter, dokter.nm_dokter, reg_periksa.kd_poli, poliklinik.nm_poli, reg_periksa.no_reg, reg_periksa.stts as status from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rkm_medis='$norm' and reg_periksa.tgl_registrasi>='$today' and reg_periksa.stts<>'Batal' and reg_periksa.stts<>'Sudah' order by reg_periksa.tgl_registrasi asc limit 1";
        $res = bukaquery($q);
        $activeQueue = null;
        if ($res && mysqli_num_rows($res) > 0) {
            $row = mysqli_fetch_array($res);
            $activeQueue = [
                'status' => ($row['status'] == 'Belum') ? 'Terdaftar' : $row['status'],
                'queueNum' => $row['no_reg'],
                'doctor' => $row['nm_dokter'],
                'clinic' => $row['nm_poli'],
                'kd_dokter' => $row['kd_dokter'],
                'kd_poli' => $row['kd_poli'],
                'date' => $row['tanggal_periksa']
            ];
        }

        // Counts
        $labCount = getOne("select count(distinct periksa_lab.no_rawat) from periksa_lab inner join reg_periksa on periksa_lab.no_rawat=reg_periksa.no_rawat where reg_periksa.no_rkm_medis='$norm'");
        $radCount = getOne("select count(distinct periksa_rad.no_rawat) from periksa_rad inner join reg_periksa on periksa_rad.no_rawat=reg_periksa.no_rawat where reg_periksa.no_rkm_medis='$norm'");
        $visitCount = getOne("select count(*) from reg_periksa where no_rkm_medis='$norm'");
        $rajalCount = getOne("select count(*) from reg_periksa where no_rkm_medis='$norm' and status_lanjut='Ralan'");
        $ranapCount = getOne("select count(*) from reg_periksa where no_rkm_medis='$norm' and status_lanjut='Ranap'");

        // Get hospital name
        $hospitalName = 'Rumah Sakit';
        $querysetting = bukaquery("select nama_instansi from setting limit 1");
        if ($querysetting && $setting = mysqli_fetch_array($querysetting)) {
            $hospitalName = $setting['nama_instansi'];
        }

        // Get today's doctor schedules
        $daysIndo = ['Minggu', 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
        $todayIndo = $daysIndo[date('w')];

        $schedules = [];
        $queryschedule = bukaquery("select jadwal.kd_dokter, dokter.nm_dokter, jadwal.kd_poli, poliklinik.nm_poli, jadwal.jam_mulai, jadwal.jam_selesai, jadwal.kuota, (select count(*) from reg_periksa where reg_periksa.kd_dokter=jadwal.kd_dokter and reg_periksa.kd_poli=jadwal.kd_poli and reg_periksa.tgl_registrasi=CURDATE() and reg_periksa.stts<>'Batal') as checkin from jadwal inner join dokter on jadwal.kd_dokter=dokter.kd_dokter inner join poliklinik on jadwal.kd_poli=poliklinik.kd_poli where jadwal.hari_kerja='$todayIndo'");
        while ($row = mysqli_fetch_array($queryschedule)) {
            $schedules[] = [
                'name' => $row['nm_dokter'],
                'clinic' => $row['nm_poli'],
                'start' => $row['jam_mulai'],
                'end' => $row['jam_selesai'],
                'quota' => intval($row['kuota']),
                'checkin' => intval($row['checkin'])
            ];
        }

        echo json_encode([
            'success' => true,
            'activeQueue' => $activeQueue,
            'hospitalName' => $hospitalName,
            'schedules' => $schedules,
            'counts' => [
                'labCount' => intval($labCount),
                'radCount' => intval($radCount),
                'visitCount' => intval($visitCount),
                'rajalCount' => intval($rajalCount),
                'ranapCount' => intval($ranapCount)
            ]
        ]);
        exit;
    }

    if ($action == 'get_booking_data') {
        // Fetch active clinics
        $clinics = [];
        $querypoli = bukaquery("select kd_poli, nm_poli from poliklinik where status='1' order by nm_poli");
        while ($r = mysqli_fetch_array($querypoli)) {
            $clinics[] = ['id' => $r['kd_poli'], 'name' => $r['nm_poli']];
        }

        // Fetch doctors mapped by clinic from jadwal
        $doctors = [];
        $queryjadwal = bukaquery("select jadwal.kd_dokter, dokter.nm_dokter, jadwal.kd_poli from jadwal inner join dokter on jadwal.kd_dokter=dokter.kd_dokter inner join poliklinik on jadwal.kd_poli=poliklinik.kd_poli");
        while ($r = mysqli_fetch_array($queryjadwal)) {
            $kd_poli = $r['kd_poli'];
            if (!isset($doctors[$kd_poli])) {
                $doctors[$kd_poli] = [];
            }
            $exists = false;
            foreach ($doctors[$kd_poli] as $doc) {
                if ($doc['id'] == $r['kd_dokter']) {
                    $exists = true;
                    break;
                }
            }
            if (!$exists) {
                $fee = getOne("select registrasi from poliklinik where kd_poli='$kd_poli'");
                $doctors[$kd_poli][] = [
                    'id' => $r['kd_dokter'],
                    'name' => $r['nm_dokter'],
                    'fee' => 'Rp. ' . number_format($fee, 0, ',', '.')
                ];
            }
        }

        echo json_encode([
            'success' => true,
            'clinics' => $clinics,
            'doctors' => $doctors
        ]);
        exit;
    }

    if ($action == 'save_booking') {
        $norm = getSessionNorm();
        if (!$norm) {
            echo json_encode(['success' => false, 'message' => 'Session expired. Silakan login kembali.']);
            exit;
        }

        $kd_poli = isset($_POST['clinic']) ? cleankar($_POST['clinic']) : '';
        $kd_dokter = isset($_POST['doctor']) ? cleankar($_POST['doctor']) : '';
        $tanggalRaw = isset($_POST['date']) ? cleankar($_POST['date']) : '';
        $kd_pj = 'UM'; // Code UM/UMUM from database penjab

        if (empty($kd_poli) || empty($kd_dokter) || empty($tanggalRaw)) {
            echo json_encode(['success' => false, 'message' => 'Poliklinik, Dokter, dan Tanggal Periksa wajib diisi.']);
            exit;
        }

        // Clean date format (handle YYYYMMDD and format back to YYYY-MM-DD)
        $tanggal = $tanggalRaw;
        if (strlen($tanggalRaw) === 8 && ctype_digit($tanggalRaw)) {
            $tanggal = substr($tanggalRaw, 0, 4) . '-' . substr($tanggalRaw, 4, 2) . '-' . substr($tanggalRaw, 6, 2);
        }

        $checkCount = getOne("select count(*) from reg_periksa where no_rkm_medis='$norm' and tgl_registrasi='$tanggal' and stts<>'Batal'");
        if ($checkCount > 0) {
            echo json_encode(['success' => false, 'message' => 'Anda sudah terdaftar untuk pemeriksaan pada tanggal tersebut.']);
            exit;
        }

        // Cek closing kasir
        $validasiregistrasi = getOne2("select wajib_closing_kasir from set_validasi_registrasi");
        if ($validasiregistrasi == "Yes") {
            if (getOne("select count(no_rkm_medis) from reg_periksa where no_rkm_medis='$norm' and status_bayar='Belum Bayar' and stts<>'Batal'") > 0) {
                echo json_encode(['success' => false, 'message' => 'Gagal registrasi. Anda memiliki tagihan kunjungan sebelumnya yang belum lunas.']);
                exit;
            }
        }

        // Calculate age
        Ubah2("pasien","umur=CONCAT(CONCAT(CONCAT(TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()), ' Th '),CONCAT(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12), ' Bl ')),CONCAT(TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()), ' Hr')) where no_rkm_medis='$norm'");
        
        $statuspoli = getOne2("select if((select count(no_rkm_medis) from reg_periksa where no_rkm_medis='$norm' and kd_poli='$kd_poli')>0,'Lama','Baru' )");
        $max = getOne2("select ifnull(MAX(CONVERT(RIGHT(no_rawat,6),signed)),0)+1 from reg_periksa where tgl_registrasi='$tanggal'");
        $no_rawat = str_replace("-","/",$tanggal."/").sprintf("%06s", $max);
        
        $sttsumur = "Th";
        $umur = 0;
        $querypasien = bukaquery2("select no_rkm_medis,nm_pasien,no_tlp,namakeluarga,alamatpj,kelurahanpj,kecamatanpj,kabupatenpj,propinsipj,keluarga,TIMESTAMPDIFF(YEAR, pasien.tgl_lahir, CURDATE()) as tahun,(TIMESTAMPDIFF(MONTH, pasien.tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(pasien.tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, pasien.tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, pasien.tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari,tgl_daftar from pasien where no_rkm_medis='$norm' ");
        
        if ($rsquerypasien = mysqli_fetch_array($querypasien)) {
            if ($rsquerypasien["tahun"] > 0) {
                $umur = $rsquerypasien["tahun"];
                $sttsumur = "Th";
            } else if ($rsquerypasien["tahun"] == 0) {
                if ($rsquerypasien["bulan"] > 0) {
                    $umur = $rsquerypasien["bulan"];
                    $sttsumur = "Bl";
                } else if ($rsquerypasien["bulan"] == 0) {
                    $umur = $rsquerypasien["hari"];
                    $sttsumur = "Hr";
                }
            }

            $biayareg = 0;
            if ($rsquerypasien["tgl_daftar"] == $tanggal) {
                $biayareg = getOne2("select registrasi from poliklinik where kd_poli='$kd_poli'");
            } else {
                $biayareg = getOne2("select registrasilama from poliklinik where kd_poli='$kd_poli'");
            }

            // Queue number logic matching SIMKES Khanza settings from reg_periksa
            $nourut = "";
            switch (URUTNOREG) {
                case "poli" : 
                    $maxVal = getOne("select ifnull(MAX(CONVERT(no_reg,signed)),0)+1 from reg_periksa where kd_poli='$kd_poli' and tgl_registrasi='$tanggal'");
                    $nourut = sprintf("%03s", $maxVal);
                    break;
                case "dokter" : 
                    $maxVal = getOne("select ifnull(MAX(CONVERT(no_reg,signed)),0)+1 from reg_periksa where kd_dokter='$kd_dokter' and tgl_registrasi='$tanggal'");
                    $nourut = sprintf("%03s", $maxVal);
                    break;
                case "dokter + poli" : 
                    $maxVal = getOne("select ifnull(MAX(CONVERT(no_reg,signed)),0)+1 from reg_periksa where kd_poli='$kd_poli' and kd_dokter='$kd_dokter' and tgl_registrasi='$tanggal'");
                    $nourut = sprintf("%03s", $maxVal);
                    break;
                default : 
                    $maxVal = getOne("select ifnull(MAX(CONVERT(no_reg,signed)),0)+1 from reg_periksa where kd_dokter='$kd_dokter' and tgl_registrasi='$tanggal'");
                    $nourut = sprintf("%03s", $maxVal);
                    break;
            }

            $insert = Tambah4("reg_periksa","'$nourut','$no_rawat','$tanggal',current_time(),'$kd_dokter','$norm','$kd_poli','".$rsquerypasien["namakeluarga"]."','".$rsquerypasien["alamatpj"]."','".$rsquerypasien["keluarga"]."','".$biayareg."','Belum','Lama','Ralan','$kd_pj','$umur','$sttsumur','Belum Bayar','$statuspoli'");
            if ($insert) {
                Ubah3("skdp_bpjs","status='Sudah Periksa' where no_rkm_medis='$norm' and tanggal_datang='$tanggal'");
                
                $doctorName = getOne("select nm_dokter from dokter where kd_dokter='$kd_dokter'");
                $clinicName = getOne("select nm_poli from poliklinik where kd_poli='$kd_poli'");
                $fee = getOne("select registrasi from poliklinik where kd_poli='$kd_poli'");
                $hospitalName = getOne("select nama_instansi from setting limit 1");
                if (!$hospitalName) $hospitalName = 'Rumah Sakit';

                // --- WhatsApp Notification Fonnte ---
                if (defined('FONNTE_TOKEN') && !empty(FONNTE_TOKEN) && !empty($rsquerypasien['no_tlp'])) {
                    $formattedDate = formatIndoDate($tanggal);
                    $waMessage = 
"*BUKTI PENDAFTARAN ONLINE*\n\n" .
"Halo *".$rsquerypasien['nm_pasien'] . "*, pendaftaran online Anda telah berhasil disimpan. Berikut rincian kunjungan Anda:\n\n" .
"• *No. RM:* " . $norm . "\n" .
"• *No. Rawat:* " . $no_rawat . "\n" .
"• *Poliklinik:* " . $clinicName . "\n" .
"• *Dokter:* " . $doctorName . "\n" .
"• *Tanggal Periksa:* " . $formattedDate . "\n" .
"• *Nomor Antrean:* " . $nourut . "\n\n" .
"Silakan datang 15-30 menit sebelum jadwal untuk verifikasi berkas dan pembayaran. Tunjukkan pesan pendaftaran ini kepada petugas saat check-in.\n\n" .
"Terima kasih atas kepercayaan Anda.\n" .
"*".$hospitalName."*";

                    sendFonnteMessage(FONNTE_TOKEN, $rsquerypasien['no_tlp'], $waMessage);
                }
                // -------------------------------------

                echo json_encode([
                    'success' => true,
                    'booking' => [
                        'patientName' => $_SESSION['nm_pasien'],
                        'clinicName' => $clinicName,
                        'doctorName' => $doctorName,
                        'date' => date('d M Y', strtotime($tanggal)),
                        'fee' => 'Rp. ' . number_format($fee, 0, ',', '.'),
                        'noReg' => $nourut
                    ]
                ]);
            } else {
                echo json_encode(['success' => false, 'message' => 'Gagal melakukan pendaftaran langsung di database SIMRS.']);
            }
        } else {
            echo json_encode(['success' => false, 'message' => 'Data pasien tidak ditemukan.']);
        }
        exit;
    }

    if ($action == 'check_in') {
        $norm = getSessionNorm();
        if (!$norm) {
            echo json_encode(['success' => false, 'message' => 'Session expired. Silakan login kembali.']);
            exit;
        }

        $kd_dokter = isset($_POST['kd_dokter']) ? cleankar($_POST['kd_dokter']) : '';
        $kd_poli = isset($_POST['kd_poli']) ? cleankar($_POST['kd_poli']) : '';
        $tanggalRaw = isset($_POST['date']) ? cleankar($_POST['date']) : '';
        $no_reg = isset($_POST['queueNum']) ? cleankar($_POST['queueNum']) : '';
        $kd_pj = 'UM';

        if (empty($kd_dokter) || empty($kd_poli) || empty($tanggalRaw)) {
            echo json_encode(['success' => false, 'message' => 'Parameter check-in tidak lengkap.']);
            exit;
        }

        // Clean date format (handle YYYYMMDD and format back to YYYY-MM-DD)
        $tanggal = $tanggalRaw;
        if (strlen($tanggalRaw) === 8 && ctype_digit($tanggalRaw)) {
            $tanggal = substr($tanggalRaw, 0, 4) . '-' . substr($tanggalRaw, 4, 2) . '-' . substr($tanggalRaw, 6, 2);
        }

        // Cek closing kasir
        $validasiregistrasi = getOne2("select wajib_closing_kasir from set_validasi_registrasi");
        if ($validasiregistrasi == "Yes") {
            if (getOne("select count(no_rkm_medis) from reg_periksa where no_rkm_medis='$norm' and status_bayar='Belum Bayar' and stts<>'Batal'") > 0) {
                echo json_encode(['success' => false, 'message' => 'Gagal check-in. Anda memiliki tagihan kunjungan sebelumnya yang belum lunas.']);
                exit;
            }
        }

        // Calculate age
        Ubah2("pasien","umur=CONCAT(CONCAT(CONCAT(TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()), ' Th '),CONCAT(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12), ' Bl ')),CONCAT(TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()), ' Hr')) where no_rkm_medis='$norm'");
        $statuspoli = getOne2("select if((select count(no_rkm_medis) from reg_periksa where no_rkm_medis='$norm' and kd_poli='$kd_poli')>0,'Lama','Baru' )");
        $max = getOne2("select ifnull(MAX(CONVERT(RIGHT(no_rawat,6),signed)),0)+1 from reg_periksa where tgl_registrasi='$tanggal'");
        $no_rawat = str_replace("-","/",$tanggal."/").sprintf("%06s", $max);
        
        $sttsumur = "Th";
        $umur = 0;
        $querypasien = bukaquery2("select no_rkm_medis,nm_pasien,no_tlp,namakeluarga,alamatpj,kelurahanpj,kecamatanpj,kabupatenpj,propinsipj,keluarga,TIMESTAMPDIFF(YEAR, pasien.tgl_lahir, CURDATE()) as tahun,(TIMESTAMPDIFF(MONTH, pasien.tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(pasien.tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, pasien.tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, pasien.tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari,tgl_daftar from pasien where no_rkm_medis='$norm' ");
        
        if ($rsquerypasien = mysqli_fetch_array($querypasien)) {
            if ($rsquerypasien["tahun"] > 0) {
                $umur = $rsquerypasien["tahun"];
                $sttsumur = "Th";
            } else if ($rsquerypasien["tahun"] == 0) {
                if ($rsquerypasien["bulan"] > 0) {
                    $umur = $rsquerypasien["bulan"];
                    $sttsumur = "Bl";
                } else if ($rsquerypasien["bulan"] == 0) {
                    $umur = $rsquerypasien["hari"];
                    $sttsumur = "Hr";
                }
            }

            $biayareg = 0;
            if ($rsquerypasien["tgl_daftar"] == $tanggal) {
                $biayareg = getOne2("select registrasi from poliklinik where kd_poli='$kd_poli'");
            } else {
                $biayareg = getOne2("select registrasilama from poliklinik where kd_poli='$kd_poli'");
            }

            $insert = Tambah4("reg_periksa","'$no_reg','$no_rawat','$tanggal',current_time(),'$kd_dokter','$norm','$kd_poli','".$rsquerypasien["namakeluarga"]."','".$rsquerypasien["alamatpj"]."','".$rsquerypasien["keluarga"]."','".$biayareg."','Belum','Lama','Ralan','$kd_pj','$umur','$sttsumur','Belum Bayar','$statuspoli'");
            if ($insert) {
                Ubah3("skdp_bpjs","status='Sudah Periksa' where no_rkm_medis='$norm' and tanggal_datang='$tanggal'");
                Ubah3("booking_registrasi","status='Terdaftar' where no_rkm_medis='$norm' and tanggal_periksa='$tanggal' and kd_dokter='$kd_dokter' and kd_poli='$kd_poli' and kd_pj='$kd_pj'");

                $doctorName = getOne("select nm_dokter from dokter where kd_dokter='$kd_dokter'");
                $clinicName = getOne("select nm_poli from poliklinik where kd_poli='$kd_poli'");
                $hospitalName = getOne("select nama_instansi from setting limit 1");
                if (!$hospitalName) $hospitalName = 'Rumah Sakit';

                // --- WhatsApp Notification Fonnte ---
                if (defined('FONNTE_TOKEN') && !empty(FONNTE_TOKEN) && !empty($rsquerypasien['no_tlp'])) {
                    $formattedDate = formatIndoDate($tanggal);
                    $waMessage = 
"*BUKTI PENDAFTARAN ONLINE (CHECK-IN)*\n\n" .
"Halo *".$rsquerypasien['nm_pasien'] . "*, check-in pendaftaran online Anda telah berhasil. Berikut rincian kunjungan Anda:\n\n" .
"• *No. RM:* " . $norm . "\n" .
"• *No. Rawat:* " . $no_rawat . "\n" .
"• *Poliklinik:* " . $clinicName . "\n" .
"• *Dokter:* " . $doctorName . "\n" .
"• *Tanggal Periksa:* " . $formattedDate . "\n" .
"• *Nomor Antrean:* " . $no_reg . "\n\n" .
"Silakan menuju ke poliklinik tujuan Anda. Tunjukkan bukti check-in ini bila diperlukan.\n\n" .
"Terima kasih atas kepercayaan Anda.\n" .
"*".$hospitalName."*";

                    sendFonnteMessage(FONNTE_TOKEN, $rsquerypasien['no_tlp'], $waMessage);
                }
                // -------------------------------------

                echo json_encode(['success' => true]);
            } else {
                echo json_encode(['success' => false, 'message' => 'Gagal membuat registrasi kunjungan.']);
            }
        } else {
            echo json_encode(['success' => false, 'message' => 'Data pasien tidak ditemukan.']);
        }
        exit;
    }

    if ($action == 'get_medical_records') {
        $norm = getSessionNorm();
        if (!$norm) {
            echo json_encode(['success' => false, 'message' => 'Session expired. Silakan login kembali.']);
            exit;
        }

        // Fetch visits
        $visits = [];
        $queryvis = bukaquery("select reg_periksa.no_rawat, reg_periksa.no_reg, reg_periksa.tgl_registrasi, dokter.nm_dokter, poliklinik.nm_poli, reg_periksa.status_lanjut from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rkm_medis='$norm' order by reg_periksa.tgl_registrasi desc");
        while ($row = mysqli_fetch_array($queryvis)) {
            $diagnosa = '';
            $querydiag = bukaquery("select penyakit.nm_penyakit from diagnosa_pasien inner join penyakit on diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit where diagnosa_pasien.no_rawat='".$row['no_rawat']."' limit 1");
            if ($diag = mysqli_fetch_array($querydiag)) {
                $diagnosa = $diag['nm_penyakit'];
            }

            $tensi = '-';
            $suhu = '-';
            $berat = '-';
            if ($row['status_lanjut'] == 'Ralan') {
                $querypem = bukaquery("select suhu_tubuh, tensi, berat from pemeriksaan_ralan where no_rawat='".$row['no_rawat']."' limit 1");
            } else {
                $querypem = bukaquery("select suhu_tubuh, tensi, berat from pemeriksaan_ranap where no_rawat='".$row['no_rawat']."' limit 1");
                if (!$querypem || mysqli_num_rows($querypem) == 0) {
                    $querypem = bukaquery("select suhu_tubuh, tensi, berat from pemeriksaan_ralan where no_rawat='".$row['no_rawat']."' limit 1");
                }
            }
            if ($querypem && $pem = mysqli_fetch_array($querypem)) {
                $suhu = $pem['suhu_tubuh'] ? $pem['suhu_tubuh'] . ' °C' : '-';
                $tensi = $pem['tensi'] ? $pem['tensi'] . ' mmHg' : '-';
                $berat = $pem['berat'] ? $pem['berat'] . ' kg' : '-';
            }

            $resep = '';
            $queryres = bukaquery("select databarang.nama_brng, detail_pemberian_obat.jml from detail_pemberian_obat inner join databarang on detail_pemberian_obat.kode_brng=databarang.kode_brng where detail_pemberian_obat.no_rawat='".$row['no_rawat']."'");
            $resList = [];
            while ($resRow = mysqli_fetch_array($queryres)) {
                $resList[] = $resRow['nama_brng'] . ' (' . $resRow['jml'] . ')';
            }
            if (count($resList) > 0) {
                $resep = implode(', ', $resList);
            } else {
                $resep = 'Tidak ada resep obat.';
            }

            $visits[] = [
                'id' => $row['no_rawat'],
                'noRawat' => $row['no_rawat'],
                'noReg' => $row['no_reg'],
                'date' => date('d M Y', strtotime($row['tgl_registrasi'])),
                'type' => ($row['status_lanjut'] == 'Ralan') ? 'Rawat Jalan' : 'Rawat Inap',
                'clinic' => $row['nm_poli'],
                'doctor' => $row['nm_dokter'],
                'status' => 'Selesai',
                'diagnosa' => $diagnosa ? $diagnosa : 'Belum diinput',
                'tensi' => $tensi,
                'suhu' => $suhu,
                'berat' => $berat,
                'resep' => $resep,
                'tindakan' => 'Konsultasi & Pemeriksaan Fisik'
            ];
        }

        // Fetch labs
        $labs = [];
        $querylab = bukaquery("select distinct reg_periksa.no_rawat, reg_periksa.tgl_registrasi, dokter.nm_dokter from periksa_lab inner join reg_periksa on periksa_lab.no_rawat=reg_periksa.no_rawat inner join dokter on periksa_lab.kd_dokter=dokter.kd_dokter where reg_periksa.no_rkm_medis='$norm' order by reg_periksa.tgl_registrasi desc");
        while ($row = mysqli_fetch_array($querylab)) {
            $results = [];
            $querydet = bukaquery("select detail_periksa_lab.nilai, detail_periksa_lab.nilai_rujukan, detail_periksa_lab.keterangan, template_laboratorium.Pemeriksaan, template_laboratorium.satuan from detail_periksa_lab inner join template_laboratorium on detail_periksa_lab.id_template=template_laboratorium.id_template where detail_periksa_lab.no_rawat='".$row['no_rawat']."'");
            while ($d = mysqli_fetch_array($querydet)) {
                $results[] = [
                    'name' => $d['Pemeriksaan'],
                    'value' => $d['nilai'],
                    'unit' => $d['satuan'],
                    'ref' => $d['nilai_rujukan'],
                    'status' => $d['keterangan'] ? $d['keterangan'] : 'Normal'
                ];
            }
            $labs[] = [
                'id' => $row['no_rawat'],
                'date' => date('d M Y', strtotime($row['tgl_registrasi'])),
                'title' => 'Pemeriksaan Laboratorium',
                'doctor' => $row['nm_dokter'],
                'status' => 'Selesai',
                'results' => $results,
                'kesimpulan' => 'Hasil periksa laboratorium PK selesai.'
            ];
        }

        // Fetch rads
        $rads = [];
        $queryrad = bukaquery("select periksa_radiologi.no_rawat, periksa_radiologi.tgl_periksa, dokter.nm_dokter, hasil_radiologi.hasil from periksa_radiologi inner join reg_periksa on periksa_radiologi.no_rawat=reg_periksa.no_rawat inner join dokter on periksa_radiologi.kd_dokter=dokter.kd_dokter left join hasil_radiologi on periksa_radiologi.no_rawat=hasil_radiologi.no_rawat where reg_periksa.no_rkm_medis='$norm' order by periksa_radiologi.tgl_periksa desc");
        while ($row = mysqli_fetch_array($queryrad)) {
            $images = [];
            $queryimg = bukaquery("select lokasi_gambar from gambar_radiologi where no_rawat='".$row['no_rawat']."'");
            $protocol = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off' || $_SERVER['SERVER_PORT'] == 443) ? "https://" : "http://";
            while ($img = mysqli_fetch_array($queryimg)) {
                $images[] = $protocol . $_SERVER['HTTP_HOST'] . "/webapps/radiologi/" . $img['lokasi_gambar'];
            }

            $rads[] = [
                'id' => $row['no_rawat'],
                'date' => date('d M Y', strtotime($row['tgl_periksa'])),
                'title' => 'Pemeriksaan Radiologi',
                'doctor' => $row['nm_dokter'],
                'status' => 'Selesai',
                'examType' => 'Rontgen / USG',
                'findings' => $row['hasil'] ? $row['hasil'] : 'Tidak ada catatan temuan.',
                'kesimpulan' => 'Hasil pemeriksaan radiologi selesai.',
                'images' => $images
            ];
        }

        echo json_encode([
            'success' => true,
            'visits' => $visits,
            'labs' => $labs,
            'rads' => $rads
        ]);
        exit;
    }

    if ($action == 'change_password') {
        $norm = getSessionNorm();
        if (!$norm) {
            echo json_encode(['success' => false, 'message' => 'Session expired. Silakan login kembali.']);
            exit;
        }

        $oldPassword = isset($_POST['oldPassword']) ? $_POST['oldPassword'] : '';
        $newPassword = isset($_POST['newPassword']) ? $_POST['newPassword'] : '';

        if (empty($oldPassword) || empty($newPassword)) {
            echo json_encode(['success' => false, 'message' => 'Password lama dan baru wajib diisi.']);
            exit;
        }

        $check = getOne2("select count(*) from personal_pasien where no_rkm_medis='$norm' and password=aes_encrypt('$oldPassword','windi')");
        if ($check > 0) {
            $update = Ubah2("personal_pasien","password=aes_encrypt('$newPassword','windi') where no_rkm_medis='$norm'");
            if ($update) {
                echo json_encode(['success' => true]);
            } else {
                echo json_encode(['success' => false, 'message' => 'Gagal memperbarui password di database.']);
            }
        } else {
            echo json_encode(['success' => false, 'message' => 'Password lama tidak sesuai.']);
        }
        exit;
    }

    if ($action == 'download_image') {
        $norm = getSessionNorm();
        if (!$norm) {
            echo json_encode(['success' => false, 'message' => 'Session expired. Silakan login kembali.']);
            exit;
        }

        $imgUrl = isset($_GET['url']) ? $_GET['url'] : '';
        if (empty($imgUrl)) {
            echo json_encode(['success' => false, 'message' => 'Parameter URL wajib diisi.']);
            exit;
        }

        // Security check
        $parsedUrl = parse_url($imgUrl);
        if (!$parsedUrl || !isset($parsedUrl['host']) || !isset($parsedUrl['path'])) {
            echo json_encode(['success' => false, 'message' => 'URL tidak valid.']);
            exit;
        }

        $allowedHosts = ['localhost', '127.0.0.1', 'rsmardhatillah.com'];
        $host = $parsedUrl['host'];
        $path = $parsedUrl['path'];

        // Check if host matches allowed list or ends with rsmardhatillah.com
        $isAllowedHost = in_array($host, $allowedHosts) || (substr($host, -18) === 'rsmardhatillah.com');
        $isWebapps = (substr($path, 0, 9) === '/webapps/');

        if (!$isAllowedHost || !$isWebapps) {
            echo json_encode(['success' => false, 'message' => 'Domain atau path tidak diizinkan.']);
            exit;
        }

        $filename = basename($path);
        if (empty($filename)) {
            $filename = 'ronsen.jpg';
        }

        // Determine local path
        $cleanPath = str_replace('/webapps', '', $path);
        // Normalize slashes
        $cleanPath = str_replace('\\', '/', $cleanPath);
        $localPath = __DIR__ . '/../webapps' . $cleanPath;
        
        // Remove duplicate slashes if any
        $localPath = str_replace('//', '/', $localPath);

        if (file_exists($localPath) && is_file($localPath)) {
            // Serve local file
            if (ob_get_level()) {
                ob_end_clean();
            }
            
            $finfo = finfo_open(FILEINFO_MIME_TYPE);
            $mimeType = finfo_file($finfo, $localPath);
            finfo_close($finfo);
            
            if (!$mimeType) {
                $mimeType = 'image/jpeg';
            }

            header('Content-Description: File Transfer');
            header('Content-Type: ' . $mimeType);
            header('Content-Disposition: attachment; filename="' . $filename . '"');
            header('Expires: 0');
            header('Cache-Control: must-revalidate');
            header('Pragma: public');
            header('Content-Length: ' . filesize($localPath));
            readfile($localPath);
            exit;
        } else {
            // Fetch remote file via cURL
            $remoteUrl = $imgUrl;
            if ($host === 'localhost' || $host === '127.0.0.1') {
                $remoteUrl = 'https://rsmardhatillah.com' . $path;
            }

            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $remoteUrl);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_TIMEOUT, 30);
            
            $fileData = curl_exec($ch);
            $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
            $contentType = curl_getinfo($ch, CURLINFO_CONTENT_TYPE);
            curl_close($ch);

            if ($httpCode === 200 && $fileData !== false) {
                if (ob_get_level()) {
                    ob_end_clean();
                }
                if (empty($contentType)) {
                    $contentType = 'image/jpeg';
                }
                header('Content-Description: File Transfer');
                header('Content-Type: ' . $contentType);
                header('Content-Disposition: attachment; filename="' . $filename . '"');
                header('Expires: 0');
                header('Cache-Control: must-revalidate');
                header('Pragma: public');
                header('Content-Length: ' . strlen($fileData));
                echo $fileData;
                exit;
            } else {
                header("HTTP/1.0 404 Not Found");
                echo json_encode(['success' => false, 'message' => 'File tidak ditemukan di server produksi.']);
                exit;
            }
        }
    }

    echo json_encode(['success' => false, 'message' => 'Aksi API tidak valid.']);
?>
