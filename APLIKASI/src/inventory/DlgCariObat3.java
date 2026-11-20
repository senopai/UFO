/*//GEN-FIRST:event_TanggalKeyPressed
  Dilarang keras menggandakan/mengcopy/menyebarkan/membajak/mendecompile//GEN-LAST:event_TanggalKeyPressed
  Software ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami 
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */

package inventory;

import fungsi.WarnaTable;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import simrskhanza.DlgCariBangsal;

/**
 *
 * @author dosen
 */
public final class DlgCariObat3 extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private final sekuel Sequel=new sekuel();
    private final validasi Valid=new validasi();
    private final Connection koneksi=koneksiDB.condb();
    private riwayatobat Trackobat=new riwayatobat();
    private int i=0,jml=0;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    private ResultSet rstampilbarang,rsstokmasuk,rspemberian,rskeluar,rsretur,rscariharga,rspasien;
    private PreparedStatement pstampilbarang,psstokmasuk,pspemberian,pskeluar,psretur,psimpanretur,pscariharga,
                              pshapusobat,pshapusretur,psobatsimpan,psupdategudang,psupdategudang2,pspasien;
    private double stokmasuk=0,pagi=0,siang=0,sore=0,malam=0,keluar=0,retur=0,harga=0,kapasitas=0,
                    kenaikan=0,returshs=0,hilang=0,beli=0,embalase=Sequel.cariIsiAngka("select embalase_per_obat from set_embalase"),
                    tuslah=Sequel.cariIsiAngka("select tuslah_per_obat from set_embalase");
    private String aktifkanbatch="no",hppfarmasi="";
    private DlgCariBangsal bangsal=new DlgCariBangsal(null,false);
    private static final int KOL_JAM_AWAL=12,KOL_JAM_AKHIR=35,KOL_KET=36,KET_MAKS=255;
    private final java.util.List<boolean[]> jadwalJam=new java.util.concurrent.CopyOnWriteArrayList<>();
    private boolean bacaSaja=false;
    private final java.util.List<double[]> keluarPerBaris=new java.util.concurrent.CopyOnWriteArrayList<>();
    
    /** Creates new form DlgPenyakit
     * @param parent
     * @param modal */
    public DlgCariObat3(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(856,350);
        // [FIX-JAM] Pg/Sg/Sr/Ml diganti 24 kolom jam (00-23): isi jumlah obat yang diberikan pada jam tersebut
        Object[] row={"K","Kode Barang","Nama Barang","Msk","Ttl.Msk","Ttl.Klr","Retur","Rtr.Sh","Ttl.Hlg","No.Batch","No.Faktur","Aturan Pakai",
                      "00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","Keterangan"};
        tabMode=new DefaultTableModel(null,row){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                if(bacaSaja){   // [FIX-BACASAJA]
                    return false;
                }
                boolean a = true;
                if ((colIndex==1)||(colIndex==2)||(colIndex==3)||(colIndex==4)||(colIndex==5)||(colIndex==7)||(colIndex==8)||(colIndex==9)||(colIndex==10)||(colIndex==11)) {
                    a=false;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class,
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class,
                // [FIX-JAM] 24 kolom jam (00-23)
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class,
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class,
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class,
                // [FIX-KETERANGAN] kolom Keterangan (teks)
                java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbObat.setModel(tabMode);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i <= KOL_KET; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(90);
            }else if(i==2){
                column.setPreferredWidth(200);
            }else if(i==3){
                column.setPreferredWidth(35);
            }else if(i<=8){
                column.setPreferredWidth(40);
            }else if(i==9){
                column.setPreferredWidth(70);
            }else if(i==10){
                column.setPreferredWidth(100);
            }else if(i==11){
                column.setPreferredWidth(100);
            }else if(i==KOL_KET){
                column.setPreferredWidth(240);   // [FIX-KETERANGAN]
            }else{
                column.setPreferredWidth(30);
            }
        }
        
        tbObat.setDefaultRenderer(Object.class, new WarnaTable()); 
        // [FIX-JAM] kolom jam 00-23 = input jumlah obat yang diberikan per jam; latar kuning = jam terjadwal
        RendererJam rendererJam=new RendererJam();
        for(i=KOL_JAM_AWAL;i<=KOL_JAM_AKHIR;i++){
            tbObat.getColumnModel().getColumn(i).setCellRenderer(rendererJam);
        }
        tbObat.getTableHeader().setToolTipText("Kolom 00-23: isi jumlah obat yang diberikan pada jam tersebut. Latar kuning = jam terjadwal. Kolom Keterangan: catatan petugas untuk obat ini pada tanggal terpilih (disimpan saat klik Simpan, bersama jam pemberiannya; obat yang belum ada jumlah jamnya belum bisa menyimpan keterangan).");
        // [FIX-KETERANGAN] kolom Keterangan: teks bebas yang diisi petugas, maksimal KET_MAKS karakter (sesuai varchar(255) di database)
        javax.swing.JTextField editorKeterangan=new javax.swing.JTextField();
        ((javax.swing.text.AbstractDocument)editorKeterangan.getDocument()).setDocumentFilter(new BatasKeterangan());
        tbObat.getColumnModel().getColumn(KOL_KET).setCellEditor(new javax.swing.DefaultCellEditor(editorKeterangan));
        
        bangsal.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(bangsal.getTable().getSelectedRow()!= -1){                   
                    kdgudang.setText(bangsal.getTable().getValueAt(bangsal.getTable().getSelectedRow(),0).toString());
                    nmgudang.setText(bangsal.getTable().getValueAt(bangsal.getTable().getSelectedRow(),1).toString());
                } 
                kdgudang.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        try {
            aktifkanbatch = koneksiDB.AKTIFKANBATCHOBAT();
        } catch (Exception e) {
            System.out.println("E : "+e);
            aktifkanbatch = "no";
        }
        
        try {
            hppfarmasi=koneksiDB.HPPFARMASI();
        } catch (Exception e) {
            hppfarmasi="dasar";
        }
    }    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                                                    
    private void initComponents() {

        TNoRw = new widget.TextBox();
        Kd2 = new widget.TextBox();
        KdPj = new widget.TextBox();
        kelas = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel1 = new javax.swing.JPanel();
        panelisi2 = new widget.panelisi();
        ChkTgl = new widget.CekBox();
        Tgl1 = new widget.Tanggal();
        label14 = new widget.Label();
        Tgl2 = new widget.Tanggal();
        panelisi3 = new widget.panelisi();
        label12 = new widget.Label();
        Jeniskelas = new widget.ComboBox();
        BtnCari = new widget.Button();
        label13 = new widget.Label();
        BtnSimpan = new widget.Button();
        btnCetak = new widget.Button();
        BtnKeluar = new widget.Button();
        FormInput = new widget.PanelBiasa();
        jLabel5 = new widget.Label();
        Tanggal = new widget.Tanggal();
        label21 = new widget.Label();
        kdgudang = new widget.TextBox();
        nmgudang = new widget.TextBox();
        BtnGudang = new widget.Button();

        TNoRw.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N

        Kd2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        Kd2.setHighlighter(null);
        Kd2.setName("Kd2"); // NOI18N
        Kd2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kd2KeyPressed(evt);
            }
        });

        KdPj.setHighlighter(null);
        KdPj.setName("KdPj"); // NOI18N
        KdPj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPjKeyPressed(evt);
            }
        });

        kelas.setHighlighter(null);
        kelas.setName("kelas"); // NOI18N
        kelas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kelasKeyPressed(evt);
            }
        });

        TNoRM.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N

        TPasien.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Obat, Alkes & BHP Medis ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbObatPropertyChange(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 98));
        jPanel1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi2.setName("panelisi2"); // NOI18N
        panelisi2.setPreferredSize(new java.awt.Dimension(100, 41));
        panelisi2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        ChkTgl.setText("Semua resep (stok habis juga) tgl. :");
        ChkTgl.setToolTipText("Centang lalu klik tombol Cari: tampilkan SEMUA resep (termasuk obat yang stoknya sudah habis) yang dibuat pada rentang tanggal ini. Tanpa centang, obat yang stoknya sudah habis disembunyikan.");
        ChkTgl.setName("ChkTgl"); // NOI18N
        ChkTgl.setOpaque(false);
        ChkTgl.setPreferredSize(new java.awt.Dimension(250, 23));
        panelisi2.add(ChkTgl);

        Tgl1.setForeground(new java.awt.Color(50, 70, 50));
        Tgl1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-11-2026" }));
        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1"); // NOI18N
        Tgl1.setOpaque(false);
        Tgl1.setPreferredSize(new java.awt.Dimension(100, 23));
        panelisi2.add(Tgl1);

        label14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label14.setText("s.d.");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(30, 23));
        panelisi2.add(label14);

        Tgl2.setForeground(new java.awt.Color(50, 70, 50));
        Tgl2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-11-2026" }));
        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setName("Tgl2"); // NOI18N
        Tgl2.setOpaque(false);
        Tgl2.setPreferredSize(new java.awt.Dimension(100, 23));
        panelisi2.add(Tgl2);

        jPanel1.add(panelisi2, java.awt.BorderLayout.PAGE_START);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 56));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label12.setText("Tarif :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(40, 23));
        panelisi3.add(label12);

        Jeniskelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kelas 1", "Kelas 2", "Kelas 3", "Utama/BPJS", "VIP", "VVIP", "Beli Luar", "Karyawan" }));
        Jeniskelas.setName("Jeniskelas"); // NOI18N
        Jeniskelas.setPreferredSize(new java.awt.Dimension(120, 23));
        panelisi3.add(Jeniskelas);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('1');
        BtnCari.setToolTipText("Alt+1");
        BtnCari.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelisi3.add(BtnCari);

        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(40, 23));
        panelisi3.add(label13);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelisi3.add(BtnSimpan);

        btnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        btnCetak.setMnemonic('T');
        btnCetak.setText("Cetak");
        btnCetak.setToolTipText("Alt+T");
        btnCetak.setName("btnCetak"); // NOI18N
        btnCetak.setPreferredSize(new java.awt.Dimension(100, 30));
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });
        panelisi3.add(btnCetak);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelisi3.add(BtnKeluar);

        jPanel1.add(panelisi3, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        FormInput.setBackground(new java.awt.Color(215, 225, 215));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 43));
        FormInput.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 9));

        jLabel5.setText("Tanggal :");
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel5);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-02-2022" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.setPreferredSize(new java.awt.Dimension(100, 23));
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);

        label21.setText("Retur Obat Ke :");
        label21.setName("label21"); // NOI18N
        label21.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(label21);

        kdgudang.setEditable(false);
        kdgudang.setName("kdgudang"); // NOI18N
        kdgudang.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(kdgudang);

        nmgudang.setEditable(false);
        nmgudang.setName("nmgudang"); // NOI18N
        nmgudang.setPreferredSize(new java.awt.Dimension(200, 23));
        FormInput.add(nmgudang);

        BtnGudang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnGudang.setMnemonic('2');
        BtnGudang.setToolTipText("Alt+2");
        BtnGudang.setName("BtnGudang"); // NOI18N
        BtnGudang.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnGudang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnGudangActionPerformed(evt);
            }
        });
        FormInput.add(BtnGudang);

        internalFrame1.add(FormInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>                                                


    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {                                          
       dispose();
    }                                         

private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {                                          
        if(bacaSaja){   // [FIX-BACASAJA] pengaman: tombol sudah dimatikan, ini kalau Simpan terpanggil dari jalur lain
            JOptionPane.showMessageDialog(rootPane,"Billing sudah terverifikasi, data pemberian obat hanya bisa dilihat dan dicetak.");
            return;
        }
        // [FIX-KETERANGAN] sel yang masih diketik (belum Enter / belum pindah sel) diselesaikan dulu, supaya isinya ikut tersimpan
        if(tbObat.isEditing()){
            tbObat.getCellEditor().stopCellEditing();
        }
        if(TNoRw.getText().trim().equals("")){
            Valid.textKosong(Jeniskelas,"Data");
        }else if(kdgudang.getText().equals("")){
            Valid.textKosong(kdgudang,"Lokasi");
        }else{
            int reply = JOptionPane.showConfirmDialog(rootPane,"Eeiiiiiits, udah bener belum data yang mau disimpan..??","Konfirmasi",JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                try {  
                    Sequel.AutoComitFalse(); 
                    jml=tbObat.getRowCount();
                    boolean gagalKeterangan=false;   // [FIX-KETERANGAN] true bila keterangan petugas tidak bisa disimpan (mis. tabel ket_pemberian_obat_ranap belum dibuat)
                    for(i=0;i<jml;i++){   
                        harga=0;
                        kapasitas=1;
                        beli=0;
                        pscariharga=koneksi.prepareStatement("select databarang.kelas1,databarang.kelas2,databarang.kelas3,"+
                            "databarang.utama,databarang.vip,databarang.vvip,databarang.beliluar,databarang.karyawan,databarang.h_beli,"+
                            "IFNULL(kapasitas,0) as kapasitas,databarang."+hppfarmasi+" as dasar from databarang "+
                            "where databarang.kode_brng=?");
                        try {
                            pscariharga.setString(1,tbObat.getValueAt(i,1).toString());
                            rscariharga=pscariharga.executeQuery();
                            while(rscariharga.next()){
                                beli=rscariharga.getDouble("dasar");
                                if(kenaikan>0){
                                    harga=Valid.roundUp(rscariharga.getDouble("h_beli")+(rscariharga.getDouble("h_beli")*kenaikan),100);
                                }else{
                                    if(Jeniskelas.getSelectedItem().equals("Kelas 1")){
                                        harga=Valid.roundUp(rscariharga.getDouble("kelas1"),100);
                                    }else if(Jeniskelas.getSelectedItem().equals("Kelas 2")){
                                        harga=Valid.roundUp(rscariharga.getDouble("kelas2"),100);
                                    }else if(Jeniskelas.getSelectedItem().equals("Kelas 3")){
                                        harga=Valid.roundUp(rscariharga.getDouble("kelas3"),100);
                                    }else if(Jeniskelas.getSelectedItem().equals("Utama/BPJS")){
                                        harga=Valid.roundUp(rscariharga.getDouble("utama"),100);
                                    }else if(Jeniskelas.getSelectedItem().equals("VIP")){
                                        harga=Valid.roundUp(rscariharga.getDouble("vip"),100);
                                    }else if(Jeniskelas.getSelectedItem().equals("VVIP")){
                                        harga=Valid.roundUp(rscariharga.getDouble("vvip"),100);
                                    }else if(Jeniskelas.getSelectedItem().equals("Beli Luar")){  
                                        harga=Valid.roundUp(rscariharga.getDouble("beliluar"),100);
                                    }else if(Jeniskelas.getSelectedItem().equals("Karyawan")){  
                                        harga=Valid.roundUp(rscariharga.getDouble("karyawan"),100);
                                    }
                                }

                                if(rscariharga.getDouble("kapasitas")>0){
                                   kapasitas=rscariharga.getDouble("kapasitas");                            
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notofikasi : "+e);
                        } finally{
                            if(rscariharga != null){
                                rscariharga.close();
                            }
                            if(pscariharga != null){
                                pscariharga.close();
                            }
                        }                    

                        // [FIX-JAM] jumlah per jam dibaca dari kolom 00-23 saat insert di bawah
                        retur=0;
                        try {
                            retur=Double.parseDouble(tbObat.getValueAt(i,6).toString()); 
                        } catch (Exception e) {
                            retur=0;
                        } 

                        pshapusobat=koneksi.prepareStatement(
                            "delete from detail_pemberian_obat where detail_pemberian_obat.status='Ranap' and detail_pemberian_obat.no_rawat=? and "+
                            "detail_pemberian_obat.tgl_perawatan=? and detail_pemberian_obat.kode_brng=? and detail_pemberian_obat.no_batch=? and "+
                            "detail_pemberian_obat.no_faktur=?");
                        try {
                            pshapusobat.setString(1,TNoRw.getText());
                            pshapusobat.setString(2,Valid.SetTgl(Tanggal.getSelectedItem()+""));
                            pshapusobat.setString(3,tbObat.getValueAt(i,1).toString());
                            pshapusobat.setString(4,tbObat.getValueAt(i,9).toString());
                            pshapusobat.setString(5,tbObat.getValueAt(i,10).toString());
                            pshapusobat.executeUpdate();
                        } catch (Exception e) {
                            System.out.println("Notofikasi : "+e);
                        } finally{
                            if(pshapusobat != null){
                                pshapusobat.close();
                            }
                        }

                        if(retur>0){
                            psretur=koneksi.prepareStatement(
                                "select sum(returpasien.jml) as jml from returpasien where returpasien.no_rawat=? and "+
                                "returpasien.kode_brng=? and returpasien.no_batch=? and returpasien.no_faktur=?");
                            try {
                                psretur.setString(1,TNoRw.getText());
                                psretur.setString(2,tbObat.getValueAt(i,1).toString());
                                psretur.setString(3,tbObat.getValueAt(i,9).toString());
                                psretur.setString(4,tbObat.getValueAt(i,10).toString());
                                rsretur=psretur.executeQuery();
                                if(rsretur.next()){
                                    if(aktifkanbatch.equals("yes")){
                                        Trackobat.catatRiwayat(tbObat.getValueAt(i,1).toString(),0,rsretur.getDouble("jml"),"Retur Pasien",akses.getkode(),kdgudang.getText(),"Hapus",tbObat.getValueAt(i,9).toString(),tbObat.getValueAt(i,10).toString(),TNoRw.getText()+" "+TNoRM.getText()+" "+TPasien.getText());
                                        Sequel.mengedit("data_batch","no_batch=? and no_faktur=? and kode_brng=?","sisa=sisa-?",4,new String[]{
                                            ""+rsretur.getDouble("jml"),tbObat.getValueAt(i,9).toString(),tbObat.getValueAt(i,10).toString(),tbObat.getValueAt(i,1).toString()
                                        });
                                        psupdategudang = koneksi.prepareStatement("update gudangbarang set stok=stok-? where kode_brng=? and kd_bangsal=? and no_batch=? and no_faktur=?");           
                                        try {
                                            psupdategudang.setDouble(1,rsretur.getDouble("jml"));
                                            psupdategudang.setString(2,tbObat.getValueAt(i,1).toString());
                                            psupdategudang.setString(3,kdgudang.getText());
                                            psupdategudang.setString(4,tbObat.getValueAt(i,9).toString());
                                            psupdategudang.setString(5,tbObat.getValueAt(i,10).toString());
                                            psupdategudang.executeUpdate(); 
                                        } catch (Exception e) {
                                            System.out.println("Notofikasi : "+e);
                                        } finally{
                                            if(psupdategudang != null){
                                                psupdategudang.close();
                                            }
                                        }
                                    }else{
                                        Trackobat.catatRiwayat(tbObat.getValueAt(i,1).toString(),0,rsretur.getDouble("jml"),"Retur Pasien",akses.getkode(),kdgudang.getText(),"Hapus","","",TNoRw.getText()+" "+TNoRM.getText()+" "+TPasien.getText());
                                        psupdategudang= koneksi.prepareStatement("update gudangbarang set stok=stok-? where kode_brng=? and kd_bangsal=? and no_batch='' and no_faktur=''");           
                                        try {
                                            psupdategudang.setDouble(1,rsretur.getDouble("jml"));
                                            psupdategudang.setString(2,tbObat.getValueAt(i,1).toString());
                                            psupdategudang.setString(3,kdgudang.getText());
                                            psupdategudang.executeUpdate(); 
                                        } catch (Exception e) {
                                            System.out.println("Notofikasi : "+e);
                                        } finally{
                                            if(psupdategudang != null){
                                                psupdategudang.close();
                                            }
                                        }
                                    }
                                }  
                            } catch (Exception e) {
                                System.out.println("Notifikasi : "+e);
                            } finally{
                                if(rsretur != null){
                                    rsretur.close();
                                }
                                if(psretur != null){
                                    psretur.close();
                                }
                            }

                            pshapusretur=koneksi.prepareStatement(
                                "delete from returpasien where returpasien.no_rawat=? and returpasien.kode_brng=? and returpasien.no_batch=? and returpasien.no_faktur=?");
                            try {
                                pshapusretur.setString(1,TNoRw.getText());
                                pshapusretur.setString(2,tbObat.getValueAt(i,1).toString());
                                pshapusretur.setString(3,tbObat.getValueAt(i,9).toString());
                                pshapusretur.setString(4,tbObat.getValueAt(i,10).toString());
                                pshapusretur.executeUpdate();  
                            } catch (Exception e) {
                                System.out.println("Notofikasi : "+e);
                            } finally{
                                if(pshapusretur != null){
                                    pshapusretur.close();
                                }
                            }

                            psimpanretur= koneksi.prepareStatement("insert into returpasien values(?,?,?,?,?,?)");
                            try {
                                psimpanretur.setString(1,Valid.SetTgl(Tanggal.getSelectedItem()+""));
                                psimpanretur.setString(2,TNoRw.getText());
                                psimpanretur.setString(3,tbObat.getValueAt(i,1).toString());
                                psimpanretur.setDouble(4,retur);
                                psimpanretur.setString(5,tbObat.getValueAt(i,9).toString());
                                psimpanretur.setString(6,tbObat.getValueAt(i,10).toString());
                                psimpanretur.executeUpdate();

                                if(aktifkanbatch.equals("yes")){
                                    Trackobat.catatRiwayat(tbObat.getValueAt(i,1).toString(),retur,0,"Retur Pasien",akses.getkode(),kdgudang.getText(),"Simpan",tbObat.getValueAt(i,9).toString(),tbObat.getValueAt(i,10).toString(),TNoRw.getText()+" "+TNoRM.getText()+" "+TPasien.getText());
                                    Sequel.mengedit("data_batch","no_batch=? and no_faktur=? and kode_brng=?","sisa=sisa+?",4,new String[]{
                                        ""+retur,tbObat.getValueAt(i,9).toString(),tbObat.getValueAt(i,10).toString(),tbObat.getValueAt(i,1).toString()
                                    });
                                    psupdategudang2= koneksi.prepareStatement("update gudangbarang set stok=stok+? where kode_brng=? and kd_bangsal=? and no_batch=? and no_faktur=?");
                                    try {
                                        psupdategudang2.setDouble(1,retur);
                                        psupdategudang2.setString(2,tbObat.getValueAt(i,1).toString());
                                        psupdategudang2.setString(3,kdgudang.getText());
                                        psupdategudang2.setString(4,tbObat.getValueAt(i,9).toString());
                                        psupdategudang2.setString(5,tbObat.getValueAt(i,10).toString());
                                        psupdategudang2.executeUpdate();
                                    } catch (Exception e) {
                                        System.out.println("Notofikasi : "+e);
                                    } finally{
                                        if(psupdategudang2 != null){
                                            psupdategudang2.close();
                                        }
                                    }
                                }else{
                                    Trackobat.catatRiwayat(tbObat.getValueAt(i,1).toString(),retur,0,"Retur Pasien",akses.getkode(),kdgudang.getText(),"Simpan","","",TNoRw.getText()+" "+TNoRM.getText()+" "+TPasien.getText());
                                    psupdategudang2= koneksi.prepareStatement("update gudangbarang set stok=stok+? where kode_brng=? and kd_bangsal=? and no_batch='' and no_faktur=''");
                                    try {
                                        psupdategudang2.setDouble(1,retur);
                                        psupdategudang2.setString(2,tbObat.getValueAt(i,1).toString());
                                        psupdategudang2.setString(3,kdgudang.getText());
                                        psupdategudang2.executeUpdate();
                                    } catch (Exception e) {
                                        System.out.println("Notofikasi : "+e);
                                    } finally{
                                        if(psupdategudang2 != null){
                                            psupdategudang2.close();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Notofikasi : "+e);
                            } finally{
                                if(psimpanretur != null){
                                    psimpanretur.close();
                                }
                            }   
                        }                  

                        // [FIX-KETERANGAN] detail_pemberian_obat disimpan dengan 14 kolom bernama seperti semula; keterangan petugas TIDAK lagi ditaruh di tabel ini
                        // (disimpan di ket_pemberian_obat_ranap, lihat simpanKeterangan), jadi insert/validasi di form lain tidak terpengaruh.
                        psobatsimpan= koneksi.prepareStatement(
                            "insert into detail_pemberian_obat(tgl_perawatan,jam,no_rawat,kode_brng,h_beli,biaya_obat,jml,embalase,tuslah,total,status,kd_bangsal,no_batch,no_faktur) "+
                            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                        try {
                            // [FIX-JAM] satu baris detail_pemberian_obat per jam yang diisi (jam HH:00:00)
                            for(int jam=0;jam<24;jam++){
                                double jmlJam=angkaSel(i,KOL_JAM_AWAL+jam);
                                if(jmlJam>0){
                                    psobatsimpan.setString(1,Valid.SetTgl(Tanggal.getSelectedItem()+""));
                                    psobatsimpan.setString(2,(jam<10?"0":"")+jam+":00:00");
                                    psobatsimpan.setString(3,TNoRw.getText());
                                    psobatsimpan.setString(4,tbObat.getValueAt(i,1).toString());
                                    psobatsimpan.setDouble(5,beli);
                                    psobatsimpan.setDouble(6,harga);
                                    if(tbObat.getValueAt(i,0).toString().equals("true")){                                
                                        psobatsimpan.setDouble(7,(jmlJam/kapasitas));
                                        psobatsimpan.setDouble(10,(harga*(jmlJam/kapasitas)));  
                                    }else{                                
                                        psobatsimpan.setDouble(7,jmlJam);
                                        psobatsimpan.setDouble(10,(harga*jmlJam));
                                    }
                                    psobatsimpan.setString(8,"0");
                                    psobatsimpan.setString(9,"0");
                                    psobatsimpan.setString(11,"Ranap");
                                    psobatsimpan.setString(12,kdgudang.getText());
                                    psobatsimpan.setString(13,tbObat.getValueAt(i,9).toString());
                                    psobatsimpan.setString(14,tbObat.getValueAt(i,10).toString());
                                    psobatsimpan.executeUpdate();  
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notofikasi : "+e);
                        } finally{
                            if(psobatsimpan != null){
                                psobatsimpan.close();
                            }
                        }                     
                        // [FIX-KETERANGAN] keterangan petugas disimpan di tabel sendiri; dikosongkan = barisnya dihapus. Tidak bergantung pada jam yang terisi.
                        if(!simpanKeterangan(Valid.SetTgl(Tanggal.getSelectedItem()+""),tbObat.getValueAt(i,1).toString(),tbObat.getValueAt(i,9).toString(),tbObat.getValueAt(i,10).toString(),teksKeterangan(i))){
                            gagalKeterangan=true;
                        }
                    }    
                    Sequel.AutoComitTrue();
                    if(gagalKeterangan){
                        JOptionPane.showMessageDialog(rootPane,"Keterangan belum tersimpan. Pastikan tabel ket_pemberian_obat_ranap sudah dibuat di database (SQL: ket_pemberian_obat_ranap.sql).\nPemberian obat tetap tersimpan.");
                    }
                    runBackground(() ->tampil()); 
                }catch(Exception e){
                    System.out.println(e);
                }
            }                
        }
}                                         

private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {                                   
    if(evt.getKeyCode()==KeyEvent.VK_DOWN){
        tbObat.requestFocus();
    }else{
        Valid.pindah(evt,BtnKeluar,btnCetak);
    }
}                                  

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {                                        
        runBackground(() ->tampil());
    }                                       

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // [FIX-CETAK] pilih cakupan cetak: tanggal terpilih (seperti sebelumnya) / rentang Tgl1 s.d. Tgl2 / semua tanggal pemberian
        // [FIX-KETERANGAN] sel yang sedang diketik (mis. Keterangan) dimasukkan dulu ke tabel supaya ikut tercetak
        if(tbObat.isEditing()){
            tbObat.getCellEditor().stopCellEditing();
        }
        if(TNoRw.getText().equals("")){
            Valid.textKosong(TNoRw,"Pasien");
            return;
        }
        String tglAwal=Valid.SetTgl(Tgl1.getSelectedItem()+"");
        String tglAkhir=Valid.SetTgl(Tgl2.getSelectedItem()+"");
        if(tglAwal.compareTo(tglAkhir)>0){
            String tukar=tglAwal;
            tglAwal=tglAkhir;
            tglAkhir=tukar;
        }
        Object[] pilihan={
            "Tanggal terpilih ("+tanggalIndo(Valid.SetTgl(Tanggal.getSelectedItem()+""))+")",
            "Rentang "+tanggalIndo(tglAwal)+" s.d. "+tanggalIndo(tglAkhir),
            "Semua pemberian obat",
            "Batal"
        };
        int pilih=JOptionPane.showOptionDialog(this,
                "Cetak pemberian obat pasien ini untuk:\n"+
                "- Tanggal terpilih : seperti tampilan tabel sekarang\n"+
                "- Rentang : semua pemberian antara tanggal filter bawah (Tgl1 s.d. Tgl2), per tanggal\n"+
                "- Semua pemberian obat : seluruh tanggal pemberian, per tanggal",
                "Cetak Pemberian Obat",JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,pilihan,pilihan[0]);
        if(pilih==0){
            cetakTanggalTerpilih();
        }else if(pilih==1){
            cetakBanyakTanggal(tglAwal,tglAkhir);
        }else if(pilih==2){
            cetakBanyakTanggal(null,null);
        }
//        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        if(TNoRw.getText().equals("")){
//            Valid.textKosong(TNoRw,"Pasien");
//        }else if(tbObat.getRowCount()==0){
//            Valid.textKosong(Tanggal,"Data Obat");
//        }else{
//            try {
//                pspasien=koneksi.prepareStatement(
//                        "select pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.umur from pasien inner join reg_periksa on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
//                                "where reg_periksa.no_rawat=?");
//                pspasien.setString(1,TNoRw.getText());
//                rspasien=pspasien.executeQuery();
//                while(rspasien.next()){
//                    Sequel.queryu("delete from temporary where temp37='"+akses.getalamatip()+"'");
//                    // [FIX-JAM] Cetak per jam: kolom Pagi/Siang/Sore/Malam diganti 24 kolom jam (00-23), diambil dari tabel.
//                    // temp1=Nama, temp2=S.Msk, temp3..temp26=jumlah diberikan jam 00..23,
//                    // temp27..temp31=Ttl.Msk,Ttl.Klr,Retur,Rtr.Sh,Ttl.Hlg, temp32..temp36 kosong, temp37=IP user
//                    for(i=0;i<tbObat.getRowCount();i++){
//                        StringBuilder kolomJam=new StringBuilder();
//                        for(int jam=0;jam<24;jam++){
//                            kolomJam.append(formatJmlPerJam(angkaSel(i,KOL_JAM_AWAL+jam))).append("','");
//                        }
//                        Sequel.menyimpan("temporary","'"+i+"','"+
//                                tbObat.getValueAt(i,2).toString().replace("'","''")+"','"+
//                                tbObat.getValueAt(i,3).toString()+"','"+
//                                kolomJam.toString()+
//                                tbObat.getValueAt(i,4).toString()+"','"+
//                                tbObat.getValueAt(i,5).toString()+"','"+
//                                tbObat.getValueAt(i,6).toString()+"','"+
//                                tbObat.getValueAt(i,7).toString()+"','"+
//                                tbObat.getValueAt(i,8).toString()+"','','','','','','"+akses.getalamatip()+"'","Data User");
//                    }
//                    Map<String, Object> param = new HashMap<>();
//                    param.put("norm",rspasien.getString("no_rkm_medis"));
//                    param.put("namapasien",rspasien.getString("nm_pasien"));
//                    param.put("jkel",rspasien.getString("jk"));
//                    param.put("umur",rspasien.getString("umur"));
//                    param.put("tanggal",Valid.SetTgl(Tanggal.getSelectedItem()+""));param.put("namars",akses.getnamars());
//                    param.put("alamatrs",akses.getalamatrs());
//                    param.put("kotars",akses.getkabupatenrs());
//                    param.put("propinsirs",akses.getpropinsirs());
//                    param.put("kontakrs",akses.getkontakrs());
//                    param.put("emailrs",akses.getemailrs());   
//                    param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
//                    Valid.MyReportqry("rptObatPasienPerJam.jasper","report","::[ Obat Keluar Masuk Per Jam ]::","select * from temporary where temporary.temp37='"+akses.getalamatip()+"' order by temporary.no",param);
//                }                
//            } catch (Exception ex) {
//                System.out.println(ex);
//            }
//        }
//        this.setCursor(Cursor.getDefaultCursor());
    }
    
    // [FIX-CETAK] cetak tanggal terpilih = perilaku cetak yang lama (isi tabel apa adanya)
    private void cetakTanggalTerpilih() {                                         
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(TNoRw.getText().equals("")){
            Valid.textKosong(TNoRw,"Pasien");
        }else if(tbObat.getRowCount()==0){
            Valid.textKosong(Tanggal,"Data Obat");
        }else{
            try {
                pspasien=koneksi.prepareStatement(
                        "select pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.umur from pasien inner join reg_periksa on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                                "where reg_periksa.no_rawat=?");
                pspasien.setString(1,TNoRw.getText());
                rspasien=pspasien.executeQuery();
                while(rspasien.next()){
                    Sequel.queryu("delete from temporary where temp37='"+akses.getalamatip()+"'");
                    // [FIX-JAM] Cetak per jam: kolom Pagi/Siang/Sore/Malam diganti 24 kolom jam (00-23), diambil dari tabel.
                    // temp1=Nama, temp2=S.Msk, temp3..temp26=jumlah diberikan jam 00..23,
                    // temp27..temp31=Ttl.Msk,Ttl.Klr,Retur,Rtr.Sh,Ttl.Hlg, temp32=Keterangan, temp33..temp36 kosong, temp37=IP user
                    for(i=0;i<tbObat.getRowCount();i++){
                        StringBuilder kolomJam=new StringBuilder();
                        for(int jam=0;jam<24;jam++){
                            kolomJam.append(formatJmlPerJam(angkaSel(i,KOL_JAM_AWAL+jam))).append("','");
                        }
                        Sequel.menyimpan("temporary","'"+i+"','"+
                                tbObat.getValueAt(i,2).toString().replace("'","''")+"','"+
                                tbObat.getValueAt(i,3).toString()+"','"+
                                kolomJam.toString()+
                                tbObat.getValueAt(i,4).toString()+"','"+
                                tbObat.getValueAt(i,5).toString()+"','"+
                                tbObat.getValueAt(i,6).toString()+"','"+
                                tbObat.getValueAt(i,7).toString()+"','"+
                                tbObat.getValueAt(i,8).toString()+"','"+
                                sqlTeks(teksKeterangan(i))+"','','','','','"+akses.getalamatip()+"'","Data User");   // [FIX-KETERANGAN] temp32 = Keterangan
                    }
                    Map<String, Object> param = new HashMap<>();
                    param.put("norm",rspasien.getString("no_rkm_medis"));
                    param.put("namapasien",rspasien.getString("nm_pasien"));
                    param.put("jkel",rspasien.getString("jk"));
                    param.put("umur",rspasien.getString("umur"));
                    param.put("tanggal",Valid.SetTgl(Tanggal.getSelectedItem()+""));param.put("namars",akses.getnamars());
                    param.put("alamatrs",akses.getalamatrs());
                    param.put("kotars",akses.getkabupatenrs());
                    param.put("propinsirs",akses.getpropinsirs());
                    param.put("kontakrs",akses.getkontakrs());
                    param.put("emailrs",akses.getemailrs());   
                    param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                    Valid.MyReportqry("rptObatPasienPerJam.jasper","report","::[ Obat Keluar Masuk Per Jam ]::","select * from temporary where temporary.temp37='"+akses.getalamatip()+"' order by temporary.no",param);
                }                
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }
    
    // [FIX-CETAK] begin
    // [FIX-ANGKA] bulatkan ke 6 desimal (membuang sisa error floating point: 5.999999999999998 -> 6.0, 1.7763568394002505E-15 -> 0.0)
    private double bulatkanAngka(double nilai) {
        return Math.round(nilai*1000000.0)/1000000.0;
    }

    // [FIX-ANGKA] teks angka untuk cetak: 6 desimal dibulatkan, tanpa notasi ilmiah (E), minimal 1 desimal agar sama dengan tampilan tabel (3.0, 0.5)
    private String formatAngkaCetak(double nilai) {
        java.math.BigDecimal angka=java.math.BigDecimal.valueOf(bulatkanAngka(nilai)).stripTrailingZeros();
        if(angka.scale()<1){
            angka=angka.setScale(1);
        }
        return angka.toPlainString();
    }

    // yyyy-MM-dd -> dd-MM-yyyy
    private String tanggalIndo(String tgl) {
        try {
            String[] bagian=tgl.substring(0,10).split("-");
            return bagian[2]+"-"+bagian[1]+"-"+bagian[0];
        } catch (Exception e) {
            return tgl;
        }
    }

    // yyyy-MM-dd -> dd-MM-yy
    private String tanggalSingkat(String tgl) {
        try {
            String[] bagian=tgl.substring(0,10).split("-");
            return bagian[2]+"-"+bagian[1]+"-"+bagian[0].substring(2);
        } catch (Exception e) {
            return tgl;
        }
    }

    // Jalankan query "kolom kunci..., sum(jumlah)" (parameter 1 = no_rawat) -> map kunci (kolom 1..jumlahKolomKunci digabung "|") -> jumlah (kolom berikutnya)
    private Map<String,Double> jumlahPerKunci(String sql,String norawat,int jumlahKolomKunci) throws Exception {
        Map<String,Double> hasil=new HashMap<>();
        PreparedStatement psjml=null;
        ResultSet rsjml=null;
        try {
            psjml=koneksi.prepareStatement(sql);
            psjml.setString(1,norawat);
            rsjml=psjml.executeQuery();
            while(rsjml.next()){
                StringBuilder kunci=new StringBuilder();
                for(int k=1;k<=jumlahKolomKunci;k++){
                    if(k>1){
                        kunci.append('|');
                    }
                    kunci.append(rsjml.getString(k));
                }
                hasil.put(kunci.toString(),rsjml.getDouble(jumlahKolomKunci+1));
            }
        } finally {
            if(rsjml!=null){
                rsjml.close();
            }
            if(psjml!=null){
                psjml.close();
            }
        }
        return hasil;
    }

    // Cetak SEMUA pemberian obat (tglAwal=tglAkhir=null) atau pemberian pada rentang tglAwal s.d. tglAkhir (yyyy-MM-dd),
    // satu laporan yang dikelompokkan per tanggal pemberian. Kolom Ttl.Msk/Ttl.Klr/Retur/Rtr.SH/Ttl.Hlg = total seluruh periode (sama dengan tabel).
    private void cetakBanyakTanggal(String tglAwal,String tglAkhir) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PreparedStatement pspem=null;
        ResultSet rspem=null;
        try {
            final String norawat=TNoRw.getText();
            final boolean dibatasi=(tglAwal!=null)&&(tglAkhir!=null);
            final Map<String,Map<String,double[]>> pemberian=new TreeMap<>();   // tanggal -> kunci obat -> jumlah per jam
            final Map<String,String> namaObat=new HashMap<>();                  // kunci obat (kode|batch|faktur) -> nama
            pspem=koneksi.prepareStatement(
                    "select detail_pemberian_obat.tgl_perawatan,detail_pemberian_obat.kode_brng,databarang.nama_brng,"+
                    "detail_pemberian_obat.no_batch,detail_pemberian_obat.no_faktur,hour(detail_pemberian_obat.jam) as jam,"+
                    "sum(detail_pemberian_obat.jml) as jml "+
                    "from detail_pemberian_obat inner join databarang on databarang.kode_brng=detail_pemberian_obat.kode_brng "+
                    "where detail_pemberian_obat.status='Ranap' and detail_pemberian_obat.no_rawat=? "+
                    (dibatasi?"and detail_pemberian_obat.tgl_perawatan between ? and ? ":"")+
                    "group by detail_pemberian_obat.tgl_perawatan,detail_pemberian_obat.kode_brng,databarang.nama_brng,"+
                    "detail_pemberian_obat.no_batch,detail_pemberian_obat.no_faktur,hour(detail_pemberian_obat.jam)");
            pspem.setString(1,norawat);
            if(dibatasi){
                pspem.setString(2,tglAwal);
                pspem.setString(3,tglAkhir);
            }
            rspem=pspem.executeQuery();
            while(rspem.next()){
                int jam=rspem.getInt("jam");
                if((jam<0)||(jam>23)){
                    continue;
                }
                String kunci=rspem.getString("kode_brng")+"|"+rspem.getString("no_batch")+"|"+rspem.getString("no_faktur");
                String tgl=rspem.getString("tgl_perawatan");
                namaObat.put(kunci,String.valueOf(rspem.getString("nama_brng")));
                Map<String,double[]> perObat=pemberian.get(tgl);
                if(perObat==null){
                    perObat=new HashMap<>();
                    pemberian.put(tgl,perObat);
                }
                double[] perJam=perObat.get(kunci);
                if(perJam==null){
                    perJam=new double[24];
                    perObat.put(kunci,perJam);
                }
                perJam[jam]+=rspem.getDouble("jml");
            }
            rspem.close();
            pspem.close();
            rspem=null;
            pspem=null;

            // total seluruh periode per obat (rumus sama dengan tampil())
            Map<String,Double> totalMasuk=jumlahPerKunci(
                    "select stok_obat_pasien.kode_brng,stok_obat_pasien.no_batch,stok_obat_pasien.no_faktur,sum(stok_obat_pasien.jumlah) "+
                    "from stok_obat_pasien where stok_obat_pasien.no_rawat=? "+
                    "group by stok_obat_pasien.kode_brng,stok_obat_pasien.no_batch,stok_obat_pasien.no_faktur",norawat,3);
            Map<String,Double> totalKeluar=jumlahPerKunci(
                    "select detail_pemberian_obat.kode_brng,detail_pemberian_obat.no_batch,detail_pemberian_obat.no_faktur,sum(detail_pemberian_obat.jml) "+
                    "from detail_pemberian_obat where detail_pemberian_obat.status='Ranap' and detail_pemberian_obat.no_rawat=? "+
                    "group by detail_pemberian_obat.kode_brng,detail_pemberian_obat.no_batch,detail_pemberian_obat.no_faktur",norawat,3);
            Map<String,Double> totalRetur=jumlahPerKunci(
                    "select returpasien.kode_brng,returpasien.no_batch,returpasien.no_faktur,sum(returpasien.jml) "+
                    "from returpasien where returpasien.no_rawat=? "+
                    "group by returpasien.kode_brng,returpasien.no_batch,returpasien.no_faktur",norawat,3);
            // kolom S.Msk = stok masuk pada tanggal pemberian itu (kunci: tanggal|kode|batch|faktur)
            Map<String,Double> masukPerTanggal=jumlahPerKunci(
                    "select stok_obat_pasien.tanggal,stok_obat_pasien.kode_brng,stok_obat_pasien.no_batch,stok_obat_pasien.no_faktur,sum(stok_obat_pasien.jumlah) "+
                    "from stok_obat_pasien where stok_obat_pasien.no_rawat=? "+
                    "group by stok_obat_pasien.tanggal,stok_obat_pasien.kode_brng,stok_obat_pasien.no_batch,stok_obat_pasien.no_faktur",norawat,4);

            // [FIX-KETERANGAN] keterangan petugas per tanggal dan obat (kunci tanggal|kode|batch|faktur); kosong bila kolom keterangan belum dibuat
            final Map<String,String> keteranganPerTanggal=ambilKeteranganPeriode(norawat,tglAwal,tglAkhir);

            // baris cetak: [0]=tanggal dd-MM-yyyy, [1]=nama, [2]=S.Msk, [3..26]=jam 00..23, [27..31]=Ttl.Msk,Ttl.Klr,Retur,Rtr.SH,Ttl.Hlg, [32]=Keterangan
            List<String[]> baris=new ArrayList<>();
            String tglPertama=null,tglTerakhir=null;
            for(Map.Entry<String,Map<String,double[]>> perTanggal:pemberian.entrySet()){
                List<String> urutan=new ArrayList<>(perTanggal.getValue().keySet());
                urutan.sort((a,b) -> {
                    int banding=namaObat.get(a).compareToIgnoreCase(namaObat.get(b));
                    return banding!=0?banding:a.compareTo(b);
                });
                for(String kunci:urutan){
                    double[] perJam=perTanggal.getValue().get(kunci);
                    double jumlahHariItu=0;
                    for(int jam=0;jam<24;jam++){
                        jumlahHariItu+=perJam[jam];
                    }
                    if(jumlahHariItu==0){
                        continue;
                    }
                    // [FIX-ANGKA] total dibulatkan dulu (SUM double di MySQL bisa 5.999999999999998) baru dihitung sisanya
                    Double nilai=totalMasuk.get(kunci);
                    double ttlMasuk=(nilai==null)?0:bulatkanAngka(nilai.doubleValue());
                    nilai=totalKeluar.get(kunci);
                    double ttlKeluar=(nilai==null)?0:bulatkanAngka(nilai.doubleValue());
                    nilai=totalRetur.get(kunci);
                    double ttlRetur=(nilai==null)?0:bulatkanAngka(nilai.doubleValue());
                    nilai=masukPerTanggal.get(perTanggal.getKey()+"|"+kunci);
                    double masukHariItu=(nilai==null)?0:bulatkanAngka(nilai.doubleValue());
                    double returSh=bulatkanAngka(ttlMasuk-ttlKeluar);
                    if(returSh<0.000001){
                        returSh=0;
                    }
                    double hilangSisa=bulatkanAngka((ttlMasuk-ttlKeluar)-ttlRetur);
                    if(hilangSisa<0.000001){
                        hilangSisa=0;
                    }
                    String[] b=new String[33];
                    b[0]=tanggalIndo(perTanggal.getKey());
                    b[1]=namaObat.get(kunci);
                    b[2]=formatAngkaCetak(masukHariItu);
                    for(int jam=0;jam<24;jam++){
                        b[3+jam]=formatJmlPerJam(perJam[jam]);
                    }
                    b[27]=formatAngkaCetak(ttlMasuk);
                    b[28]=formatAngkaCetak(ttlKeluar);
                    b[29]=formatAngkaCetak(ttlRetur);
                    b[30]=formatAngkaCetak(returSh);
                    b[31]=formatAngkaCetak(hilangSisa);
                    String ketHariItu=keteranganPerTanggal.get(perTanggal.getKey()+"|"+kunci);
                    b[32]=(ketHariItu==null)?"":ketHariItu;   // [FIX-KETERANGAN]
                    baris.add(b);
                    if(tglPertama==null){
                        tglPertama=perTanggal.getKey();
                    }
                    tglTerakhir=perTanggal.getKey();
                }
            }
            if(baris.isEmpty()){
                JOptionPane.showMessageDialog(this,dibatasi?
                        "Tidak ada pemberian obat pada rentang "+tanggalIndo(tglAwal)+" s.d. "+tanggalIndo(tglAkhir)+" .":
                        "Belum ada pemberian obat untuk pasien ini.");
                return;
            }

            Map<String,Object> param=new HashMap<>();
            PreparedStatement pspas=null;
            ResultSet rspas=null;
            try {
                pspas=koneksi.prepareStatement(
                        "select pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.umur from pasien inner join reg_periksa on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                        "where reg_periksa.no_rawat=?");
                pspas.setString(1,norawat);
                rspas=pspas.executeQuery();
                if(!rspas.next()){
                    return;
                }
                param.put("norm",rspas.getString("no_rkm_medis"));
                param.put("namapasien",rspas.getString("nm_pasien"));
                param.put("jkel",rspas.getString("jk"));
                param.put("umur",rspas.getString("umur"));
            } finally {
                if(rspas!=null){
                    rspas.close();
                }
                if(pspas!=null){
                    pspas.close();
                }
            }

            Sequel.queryu("delete from temporary where temp37='"+akses.getalamatip()+"'");
            int noUrut=0;
            String tanggalSebelumnya="";
            for(int n=0;n<baris.size();n++){
                String[] b=baris.get(n);
                if(!b[0].equals(tanggalSebelumnya)){
                    // baris pemisah per tanggal (hanya kolom nama yang terisi), supaya laporan lama bisa dipakai tanpa diubah
                    tanggalSebelumnya=b[0];
                    StringBuilder pemisah=new StringBuilder("'"+noUrut+"','Tanggal : "+b[0]+"'");
                    for(int kolom=2;kolom<=36;kolom++){
                        pemisah.append(",''");
                    }
                    pemisah.append(",'"+akses.getalamatip()+"'");
                    Sequel.menyimpan("temporary",pemisah.toString(),"Data User");
                    noUrut++;
                }
                StringBuilder kolomJam=new StringBuilder();
                for(int jam=0;jam<24;jam++){
                    kolomJam.append(b[3+jam]).append("','");
                }
                Sequel.menyimpan("temporary","'"+noUrut+"','"+
                        b[1].replace("'","''")+"','"+
                        b[2]+"','"+
                        kolomJam.toString()+
                        b[27]+"','"+b[28]+"','"+b[29]+"','"+b[30]+"','"+b[31]+"','"+
                        sqlTeks(b[32])+"','','','','','"+akses.getalamatip()+"'","Data User");   // [FIX-KETERANGAN] temp32 = Keterangan
                noUrut++;
            }
            param.put("tanggal",tglPertama.equals(tglTerakhir)?tglPertama:(tanggalSingkat(tglPertama)+" s.d. "+tanggalSingkat(tglTerakhir)));
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            param.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptObatPasienPerJam.jasper","report","::[ Obat Keluar Masuk Per Jam ]::","select * from temporary where temporary.temp37='"+akses.getalamatip()+"' order by temporary.no",param);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            try {
                if(rspem!=null){
                    rspem.close();
                }
                if(pspem!=null){
                    pspem.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }
    // [FIX-CETAK] end

    // [FIX-JAM] Jumlah obat yang diberikan pada tanggal terpilih, dikelompokkan per jam (0..23).
    // Kunci map: kode_brng|no_batch|no_faktur (sama dengan pengelompokan baris di tabel).
    private Map<String,double[]> ambilPemberianPerJam() {
        Map<String,double[]> hasil=new HashMap<>();
        PreparedStatement psjam=null;
        ResultSet rsjam=null;
        try {
            psjam=koneksi.prepareStatement(
                    "select detail_pemberian_obat.kode_brng,detail_pemberian_obat.no_batch,detail_pemberian_obat.no_faktur,"+
                    "hour(detail_pemberian_obat.jam) as jam,sum(detail_pemberian_obat.jml) as jml "+
                    "from detail_pemberian_obat where detail_pemberian_obat.status='Ranap' and "+
                    "detail_pemberian_obat.no_rawat=? and detail_pemberian_obat.tgl_perawatan=? "+
                    "group by detail_pemberian_obat.kode_brng,detail_pemberian_obat.no_batch,detail_pemberian_obat.no_faktur,hour(detail_pemberian_obat.jam)");
            psjam.setString(1,TNoRw.getText());
            psjam.setString(2,Valid.SetTgl(Tanggal.getSelectedItem()+""));
            rsjam=psjam.executeQuery();
            while(rsjam.next()){
                String kunci=rsjam.getString("kode_brng")+"|"+rsjam.getString("no_batch")+"|"+rsjam.getString("no_faktur");
                double[] perJam=hasil.get(kunci);
                if(perJam==null){
                    perJam=new double[24];
                    hasil.put(kunci,perJam);
                }
                int jam=rsjam.getInt("jam");
                if((jam>=0)&&(jam<24)){
                    perJam[jam]+=rsjam.getDouble("jml");
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        } finally {
            try {
                if(rsjam!=null){
                    rsjam.close();
                }
            } catch (Exception e) {
            }
            try {
                if(psjam!=null){
                    psjam.close();
                }
            } catch (Exception e) {
            }
        }
        return hasil;
    }

    // [FIX-KETERANGAN] true bila kolom detail_pemberian_obat.keterangan sudah dibuat di database (SQL: detail_pemberian_obat_keterangan.sql).
    // Hasil "ada" disimpan; kalau belum ada dicek lagi setiap kali, jadi setelah SQL dijalankan tidak perlu restart aplikasi.
    private boolean tabelKetSudahAda=false;
    private boolean tabelKeteranganAda() {
        if(tabelKetSudahAda){
            return true;
        }
        PreparedStatement pscek=null;
        ResultSet rscek=null;
        try {
            pscek=koneksi.prepareStatement("select ket_pemberian_obat_ranap.keterangan from ket_pemberian_obat_ranap where 1=0");
            rscek=pscek.executeQuery();
            tabelKetSudahAda=true;
        } catch (Exception e) {
            tabelKetSudahAda=false;
        } finally {
            try {
                if(rscek!=null){
                    rscek.close();
                }
            } catch (Exception e) {
            }
            try {
                if(pscek!=null){
                    pscek.close();
                }
            } catch (Exception e) {
            }
        }
        return tabelKetSudahAda;
    }
    
    // [FIX-KETERANGAN] Simpan keterangan petugas untuk satu obat (kode+batch+faktur) pada satu tanggal di ket_pemberian_obat_ranap:
    // baris lama dihapus lalu diisi lagi; teks kosong = hanya dihapus. Return false bila ada teks yang harus disimpan tapi gagal
    // (tabel belum dibuat / error database); kalau teks kosong dan tabel belum ada, tidak ada yang perlu dilakukan -> true.
    private boolean simpanKeterangan(String tgl,String kode,String batch,String faktur,String teks) {
        if(!tabelKeteranganAda()){
            return teks.equals("");
        }
        PreparedStatement pshapus=null,psisi=null;
        try {
            pshapus=koneksi.prepareStatement(
                    "delete from ket_pemberian_obat_ranap where ket_pemberian_obat_ranap.tgl_perawatan=? and ket_pemberian_obat_ranap.no_rawat=? and "+
                    "ket_pemberian_obat_ranap.kode_brng=? and ket_pemberian_obat_ranap.no_batch=? and ket_pemberian_obat_ranap.no_faktur=?");
            pshapus.setString(1,tgl);
            pshapus.setString(2,TNoRw.getText());
            pshapus.setString(3,kode);
            pshapus.setString(4,batch);
            pshapus.setString(5,faktur);
            pshapus.executeUpdate();
            if(!teks.equals("")){
                psisi=koneksi.prepareStatement(
                        "insert into ket_pemberian_obat_ranap(tgl_perawatan,no_rawat,kode_brng,no_batch,no_faktur,keterangan) values(?,?,?,?,?,?)");
                psisi.setString(1,tgl);
                psisi.setString(2,TNoRw.getText());
                psisi.setString(3,kode);
                psisi.setString(4,batch);
                psisi.setString(5,faktur);
                psisi.setString(6,teks);
                psisi.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            return false;
        } finally {
            try {
                if(pshapus!=null){
                    pshapus.close();
                }
            } catch (Exception e) {
            }
            try {
                if(psisi!=null){
                    psisi.close();
                }
            } catch (Exception e) {
            }
        }
    }

    // [FIX-KETERANGAN] Keterangan petugas pada tanggal terpilih, per obat, dibaca dari baris jam di detail_pemberian_obat.
    // Kunci map: kode_brng|no_batch|no_faktur (sama dengan baris tabel). Bila jam-jam obat itu berisi keterangan berbeda, semuanya ditampilkan dipisah " | ".
    // Kalau kolomnya belum ada, hasilnya kosong dan form tetap jalan.
    private Map<String,String> ambilKeterangan() {
        Map<String,String> hasil=new HashMap<>();
        if(!tabelKeteranganAda()){
            return hasil;
        }
        PreparedStatement psket=null;
        ResultSet rsket=null;
        try {
            psket=koneksi.prepareStatement(
                    "select ket_pemberian_obat_ranap.kode_brng,ket_pemberian_obat_ranap.no_batch,ket_pemberian_obat_ranap.no_faktur,ket_pemberian_obat_ranap.keterangan "+
                    "from ket_pemberian_obat_ranap where ket_pemberian_obat_ranap.no_rawat=? and ket_pemberian_obat_ranap.tgl_perawatan=? and "+
                    "ket_pemberian_obat_ranap.keterangan<>''");
            psket.setString(1,TNoRw.getText());
            psket.setString(2,Valid.SetTgl(Tanggal.getSelectedItem()+""));
            rsket=psket.executeQuery();
            while(rsket.next()){
                hasil.put(rsket.getString(1)+"|"+rsket.getString(2)+"|"+rsket.getString(3),rsket.getString(4));
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        } finally {
            try {
                if(rsket!=null){
                    rsket.close();
                }
            } catch (Exception e) {
            }
            try {
                if(psket!=null){
                    psket.close();
                }
            } catch (Exception e) {
            }
        }
        return hasil;
    }

    // [FIX-KETERANGAN] Keterangan petugas per tanggal dan obat untuk satu no. rawat, dipakai cetak banyak tanggal.
    // tglAwal/tglAkhir (yyyy-MM-dd) = null -> semua tanggal. Kunci map: tgl_perawatan|kode_brng|no_batch|no_faktur.
    // Keterangan berbeda pada jam-jam obat yang sama dipisah " | ". Kolom belum ada -> hasil kosong.
    private Map<String,String> ambilKeteranganPeriode(String norawat,String tglAwal,String tglAkhir) {
        Map<String,String> hasil=new HashMap<>();
        if(!tabelKeteranganAda()){
            return hasil;
        }
        final boolean dibatasi=(tglAwal!=null)&&(tglAkhir!=null);
        PreparedStatement psket=null;
        ResultSet rsket=null;
        try {
            psket=koneksi.prepareStatement(
                    "select ket_pemberian_obat_ranap.tgl_perawatan,ket_pemberian_obat_ranap.kode_brng,ket_pemberian_obat_ranap.no_batch,ket_pemberian_obat_ranap.no_faktur,"+
                    "ket_pemberian_obat_ranap.keterangan from ket_pemberian_obat_ranap where ket_pemberian_obat_ranap.no_rawat=? "+
                    (dibatasi?"and ket_pemberian_obat_ranap.tgl_perawatan between ? and ? ":"")+
                    "and ket_pemberian_obat_ranap.keterangan<>''");
            psket.setString(1,norawat);
            if(dibatasi){
                psket.setString(2,tglAwal);
                psket.setString(3,tglAkhir);
            }
            rsket=psket.executeQuery();
            while(rsket.next()){
                hasil.put(rsket.getString(1)+"|"+rsket.getString(2)+"|"+rsket.getString(3)+"|"+rsket.getString(4),rsket.getString(5));
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        } finally {
            try {
                if(rsket!=null){
                    rsket.close();
                }
            } catch (Exception e) {
            }
            try {
                if(psket!=null){
                    psket.close();
                }
            } catch (Exception e) {
            }
        }
        return hasil;
    }

    // [FIX-KETERANGAN] teks yang disisipkan di dalam tanda kutip SQL (insert ke tabel temporary): backslash dan kutip digandakan
    private String sqlTeks(String teks) {
        return (teks==null)?"":teks.replace("\\","\\\\").replace("'","''");
    }

    // [FIX-KETERANGAN] teks keterangan pada satu baris tabel: null -> "", spasi di tepi dibuang, dipotong KET_MAKS karakter
    private String teksKeterangan(int baris) {
        Object nilai=tbObat.getValueAt(baris,KOL_KET);
        String teks=(nilai==null)?"":nilai.toString().trim();
        if(teks.length()>KET_MAKS){
            teks=teks.substring(0,KET_MAKS);
        }
        return teks;
    }

    // [FIX-KETERANGAN] batasi panjang teks yang diketik di sel Keterangan (kolom di database varchar(255))
    private static class BatasKeterangan extends javax.swing.text.DocumentFilter {
        @Override
        public void insertString(FilterBypass fb,int offset,String teks,javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            replace(fb,offset,0,teks,attr);
        }
        @Override
        public void replace(FilterBypass fb,int offset,int panjang,String teks,javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            if(teks==null){
                teks="";
            }
            int sisa=KET_MAKS-(fb.getDocument().getLength()-panjang);
            if(teks.length()>sisa){
                teks=teks.substring(0,Math.max(sisa,0));
            }
            super.replace(fb,offset,panjang,teks,attr);
        }
    }

    // [FIX-JAM] 0 -> kosong, 2.0 -> "2", 0.5 -> "0.5" (maks. 3 desimal)
    private String formatJmlPerJam(double jml) {
        double bulat=Math.round(jml*1000.0)/1000.0;
        if(bulat==0){
            return "";
        }
        return java.math.BigDecimal.valueOf(bulat).stripTrailingZeros().toPlainString();
    }

    // [FIX-JAM] angka pada sel tabel; kosong/tidak valid dianggap 0
    private double angkaSel(int baris,int kolom) {
        try {
            Object nilai=tbObat.getValueAt(baris,kolom);
            if(nilai==null){
                return 0;
            }
            if(nilai instanceof Number){
                return ((Number)nilai).doubleValue();
            }
            return Double.parseDouble(nilai.toString().trim().replace(',','.'));
        } catch (Exception e) {
            return 0;
        }
    }

    // [FIX-JAM] jumlah seluruh kolom jam 00-23 pada satu baris
    private double jumlahInputJam(int baris) {
        double total=0;
        for(int k=KOL_JAM_AWAL;k<=KOL_JAM_AKHIR;k++){
            total+=angkaSel(baris,k);
        }
        return total;
    }

    // [FIX-JAM] true bila jam tersebut terjadwal pada stok_obat_pasien (jam00..jam23)
    private boolean jamTerjadwal(int baris,int jam) {
        if((baris<0)||(baris>=jadwalJam.size())||(jam<0)||(jam>23)){
            return false;
        }
        return jadwalJam.get(baris)[jam];
    }

    // [FIX-JAM] Sel jam: angka ringkas di tengah, latar kuning muda bila jam tersebut terjadwal
    private class RendererJam extends WarnaTable {
        private final java.awt.Color warnaJadwal=new java.awt.Color(255,244,179);
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {
            java.awt.Component c=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            if(value instanceof Number){
                setText(formatJmlPerJam(((Number)value).doubleValue()));
            }
            if((!isSelected)&&jamTerjadwal(table.convertRowIndexToModel(row),column-KOL_JAM_AWAL)){
                c.setBackground(warnaJadwal);
            }
            return c;
        }
    }

    private void Kd2KeyPressed(java.awt.event.KeyEvent evt) {                               
        // TODO add your handling code here:
    }                              

    private void formWindowActivated(java.awt.event.WindowEvent evt) {                                     
        tbObat.requestFocus();
    }                                    

    private void formWindowOpened(java.awt.event.WindowEvent evt) {                                  
        runBackground(() ->tampil());
    }                                 

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {                                  
        if(bacaSaja){   // [FIX-BACASAJA] mode lihat & cetak: Delete/Enter tidak mengubah isi tabel
            return;
        }
        if(tbObat.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_DELETE){
                if(tbObat.getSelectedRow()!= -1){
                    if(((tbObat.getSelectedColumn()>=KOL_JAM_AWAL)&&(tbObat.getSelectedColumn()<=KOL_JAM_AKHIR))||(tbObat.getSelectedColumn()==6)||(tbObat.getSelectedColumn()==KOL_KET)){  // [FIX-JAM] [FIX-KETERANGAN]
                        try {
                            tbObat.setValueAt(null,tbObat.getSelectedRow(),tbObat.getSelectedColumn());
                        } catch (Exception e) {
                        }
                    }
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                if(tbObat.getSelectedColumn()==9){
                    try {
                        if(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("0")||tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("")||tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("0.0")||tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().equals("0,0")) {
                            tbObat.setValueAt(embalase,tbObat.getSelectedRow(),9);
                        }
                    } catch (Exception e) {
                        tbObat.setValueAt(0,tbObat.getSelectedRow(),9);
                    }
                }else if(tbObat.getSelectedColumn()==10){
                    try {
                        if(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString().equals("0")||tbObat.getValueAt(tbObat.getSelectedRow(),10).toString().equals("")||tbObat.getValueAt(tbObat.getSelectedRow(),10).toString().equals("0.0")||tbObat.getValueAt(tbObat.getSelectedRow(),10).toString().equals("0,0")) {
                            tbObat.setValueAt(tuslah,tbObat.getSelectedRow(),10);
                        }
                    } catch (Exception e) {
                        tbObat.setValueAt(0,tbObat.getSelectedRow(),10);
                    }
                }
            }
        }
    }                                 

    private void tbObatPropertyChange(java.beans.PropertyChangeEvent evt) {                                      
        if(this.isVisible()==true){
              getData();
        }
    }                                     

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {                                    
        if(tbObat.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                   

    private void KdPjKeyPressed(java.awt.event.KeyEvent evt) {                                
        // TODO add your handling code here:
    }                               

    private void kelasKeyPressed(java.awt.event.KeyEvent evt) {                                 
        // TODO add your handling code here:
    }                                

    private void BtnGudangActionPerformed(java.awt.event.ActionEvent evt) {                                          
        bangsal.isCek();
        bangsal.emptTeks();
        bangsal.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        bangsal.setLocationRelativeTo(internalFrame1);
        bangsal.setAlwaysOnTop(false);
        bangsal.setVisible(true);
    }                                         

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgCariObat3 dialog = new DlgCariObat3(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify                                           
    private widget.Button BtnCari;
    private widget.Button BtnGudang;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkTgl;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Jeniskelas;
    private widget.TextBox Kd2;
    private widget.TextBox KdPj;
    private widget.ScrollPane Scroll;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private widget.Button btnCetak;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel5;
    private javax.swing.JPanel jPanel1;
    private widget.TextBox kdgudang;
    private widget.TextBox kelas;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label21;
    private widget.TextBox nmgudang;
    private widget.panelisi panelisi2;
    private widget.panelisi panelisi3;
    private widget.Table tbObat;
    // End of variables declaration                                      

    
    public void tampil() {         
        try {             
            Valid.tabelKosong(tabMode);
            jadwalJam.clear();
            keluarPerBaris.clear();   // [FIX-TTLKLR]
            // [FIX-JAM] jumlah yang sudah diberikan pada tanggal terpilih, per obat per jam
            Map<String,double[]> pemberianPerJam=ambilPemberianPerJam();
            // [FIX-KETERANGAN] keterangan petugas pada tanggal terpilih, per obat
            Map<String,String> keteranganPerObat=ambilKeterangan();
            // [FIX-FILTER] begin - centang "Semua resep" = tampilkan SEMUA resep (termasuk stok habis) yang dibuat antara Tgl1 s.d. Tgl2
            final boolean semuaResep=ChkTgl.isSelected();
            String tglResepAwal=Valid.SetTgl(Tgl1.getSelectedItem()+"");
            String tglResepAkhir=Valid.SetTgl(Tgl2.getSelectedItem()+"");
            if(tglResepAwal.compareTo(tglResepAkhir)>0){
                String tukar=tglResepAwal;
                tglResepAwal=tglResepAkhir;
                tglResepAkhir=tukar;
            }
            // [FIX-FILTER] end
            pstampilbarang=koneksi.prepareStatement(
                    "select stok_obat_pasien.kode_brng,databarang.nama_brng,sum(stok_obat_pasien.jumlah) as jumlah, "+
                    "stok_obat_pasien.no_batch,stok_obat_pasien.no_faktur,stok_obat_pasien.aturan_pakai,"+
                    "stok_obat_pasien.jam00,stok_obat_pasien.jam01,stok_obat_pasien.jam02,stok_obat_pasien.jam03,stok_obat_pasien.jam04,stok_obat_pasien.jam05,"+
                    "stok_obat_pasien.jam06,stok_obat_pasien.jam07,stok_obat_pasien.jam08,stok_obat_pasien.jam09,stok_obat_pasien.jam10,stok_obat_pasien.jam11,"+
                    "stok_obat_pasien.jam12,stok_obat_pasien.jam13,stok_obat_pasien.jam14,stok_obat_pasien.jam15,stok_obat_pasien.jam16,stok_obat_pasien.jam17,"+
                    "stok_obat_pasien.jam18,stok_obat_pasien.jam19,stok_obat_pasien.jam20,stok_obat_pasien.jam21,stok_obat_pasien.jam22,stok_obat_pasien.jam23 "+
                    "from stok_obat_pasien inner join databarang "+
                    "on databarang.kode_brng=stok_obat_pasien.kode_brng where stok_obat_pasien.no_rawat=? "+
                    "group by stok_obat_pasien.kode_brng,stok_obat_pasien.no_batch,stok_obat_pasien.no_faktur "+
                    // [FIX-FILTER] hanya obat yang punya resep (stok_obat_pasien) pada rentang tanggal; angka total tetap dihitung seluruh periode
                    (semuaResep?"having sum(stok_obat_pasien.tanggal between ? and ?)>0 ":"")+
                    "order by databarang.nama_brng");
            try {
                pstampilbarang.setString(1,TNoRw.getText());
                if(semuaResep){
                    pstampilbarang.setString(2,tglResepAwal);
                    pstampilbarang.setString(3,tglResepAkhir);
                }
                rstampilbarang=pstampilbarang.executeQuery();
                while(rstampilbarang.next()){
                    stokmasuk=0;
                    psstokmasuk=koneksi.prepareStatement(
                        "select sum(stok_obat_pasien.jumlah) as jumlah from stok_obat_pasien where "+
                        "stok_obat_pasien.no_rawat=? and stok_obat_pasien.tanggal=? and stok_obat_pasien.kode_brng=? "+
                        "and stok_obat_pasien.no_batch=? and stok_obat_pasien.no_faktur=?");
                    try {
                        psstokmasuk.setString(1,TNoRw.getText());
                        psstokmasuk.setString(2,Valid.SetTgl(Tanggal.getSelectedItem()+""));
                        psstokmasuk.setString(3,rstampilbarang.getString("kode_brng"));
                        psstokmasuk.setString(4,rstampilbarang.getString("no_batch"));
                        psstokmasuk.setString(5,rstampilbarang.getString("no_faktur"));
                        rsstokmasuk=psstokmasuk.executeQuery();
                        if(rsstokmasuk.next()){
                            stokmasuk=rsstokmasuk.getDouble("jumlah");
                        }
                    } catch (Exception e) {
                        stokmasuk=0;
                        System.out.println("Notofikasi : "+e);
                    } finally{
                        if(rsstokmasuk != null){
                            rsstokmasuk.close();
                        }
                        if(psstokmasuk != null){
                            psstokmasuk.close();
                        }
                    }

                    // [FIX-JAM] isi kolom jam 00-23 dari detail_pemberian_obat (menggantikan Pagi/Siang/Sore/Malam)
                    double[] perJam=pemberianPerJam.get(
                            rstampilbarang.getString("kode_brng")+"|"+rstampilbarang.getString("no_batch")+"|"+rstampilbarang.getString("no_faktur"));

                    keluar=0;
                    pskeluar=koneksi.prepareStatement(
                        "select sum(detail_pemberian_obat.jml) as jml from detail_pemberian_obat where detail_pemberian_obat.status='Ranap' and "+
                        "detail_pemberian_obat.no_rawat=? and detail_pemberian_obat.kode_brng=? and detail_pemberian_obat.no_batch=? and detail_pemberian_obat.no_faktur=?");
                    try {
                        pskeluar.setString(1,TNoRw.getText());
                        pskeluar.setString(2,rstampilbarang.getString("kode_brng"));
                        pskeluar.setString(3,rstampilbarang.getString("no_batch"));
                        pskeluar.setString(4,rstampilbarang.getString("no_faktur"));
                        rskeluar=pskeluar.executeQuery();
                        if(rskeluar.next()){
                            keluar=rskeluar.getDouble("jml");
                        }
                    } catch (Exception e) {
                        keluar=0;
                        System.out.println("Notofikasi : "+e);
                    } finally{
                        if(rskeluar != null){
                            rskeluar.close();
                        }
                        if(pskeluar != null){
                            pskeluar.close();
                        }
                    }
                        

                    retur=0;
                    // [FIX-HILANG] returhariini = retur yang dicatat pada tanggal terpilih (dipakai untuk menentukan baris perlu ditampilkan atau tidak)
                    double returhariini=0;
                    psretur=koneksi.prepareStatement(
                            "select sum(returpasien.jml) as jml,sum(if(returpasien.tanggal=?,returpasien.jml,0)) as jmlhariini from returpasien where returpasien.no_rawat=? and "+
                            "returpasien.kode_brng=? and returpasien.no_batch=? and returpasien.no_faktur=?");
                    try {
                        psretur.setString(1,Valid.SetTgl(Tanggal.getSelectedItem()+""));
                        psretur.setString(2,TNoRw.getText());
                        psretur.setString(3,rstampilbarang.getString("kode_brng"));
                        psretur.setString(4,rstampilbarang.getString("no_batch"));
                        psretur.setString(5,rstampilbarang.getString("no_faktur"));
                        rsretur=psretur.executeQuery();
                        if(rsretur.next()){
                            retur=rsretur.getDouble("jml");
                            returhariini=rsretur.getDouble("jmlhariini");
                        }
                    } catch (Exception e) {
                        System.out.println("Notofikasi : "+e);
                    } finally{
                        if(rsretur != null){
                            rsretur.close();
                        }
                        if(psretur != null){
                            psretur.close();
                        }
                    }
                        

                    returshs=(rstampilbarang.getDouble("jumlah")-keluar);
                    if(returshs<0){
                        returshs=0;
                    }

                    hilang=((rstampilbarang.getDouble(3)-keluar)-retur);
                    if(hilang<0){
                        hilang=0;
                    }
                    
                    // [FIX-ANGKA] SUM() kolom double di MySQL dijumlah apa adanya (0.1+0.1+... bisa 5.999999999999998), jadi semua total
                    // dibulatkan 6 desimal dulu supaya sisa tidak jadi 1.77E-15 dan angka yang tampil/dicetak bulat sesuai data.
                    keluar=bulatkanAngka(keluar);
                    retur=bulatkanAngka(retur);
                    stokmasuk=bulatkanAngka(stokmasuk);
                    double jumlahMasukTotal=bulatkanAngka(rstampilbarang.getDouble("jumlah"));

                    returshs=bulatkanAngka(jumlahMasukTotal-keluar);
                    if(returshs<0.000001){
                        returshs=0;
                    }

                    hilang=bulatkanAngka((jumlahMasukTotal-keluar)-retur);
                    if(hilang<0.000001){
                        hilang=0;
                    }

                    // [FIX-HILANG] begin - sembunyikan obat yang stoknya sudah habis (semua sudah diberikan/diretur),
                    // KECUALI masih ada aktivitas pada tanggal yang dipilih: obat masuk (Msk), ada pemberian, atau ada retur di tanggal itu.
                    // Jadi obat yang habis tetap kelihatan di hari ia diberikan, tapi tidak muncul lagi di tanggal-tanggal berikutnya.
                    // [FIX-FILTER] kalau "Semua resep" dicentang, obat yang habis TIDAK disembunyikan (difilter lewat rentang tanggal resep saja).
                    boolean adaAktivitasHariIni=(stokmasuk>0)||(returhariini>0);
                    if((!adaAktivitasHariIni)&&(perJam!=null)){
                        for(int jam=0;jam<24;jam++){
                            if(perJam[jam]!=0){
                                adaAktivitasHariIni=true;
                                break;
                            }
                        }
                    }
                    if((!semuaResep)&&(hilang<0.000001)&&(!adaAktivitasHariIni)){
                        continue;
                    }
                    // [FIX-HILANG] end

                    // [FIX-JAM] baris tabel: kolom 12..35 = jumlah diberikan jam 00..23 (kosong bila belum ada); jadwal jam disimpan terpisah
                    Object[] barisBaru=new Object[KOL_KET+1];   // [FIX-KETERANGAN]
                    barisBaru[0]=false;
                    barisBaru[1]=rstampilbarang.getString("kode_brng");
                    barisBaru[2]=rstampilbarang.getString("nama_brng");
                    barisBaru[3]=stokmasuk;
                    barisBaru[4]=jumlahMasukTotal;
                    barisBaru[5]=keluar;
                    barisBaru[6]=retur;
                    barisBaru[7]=returshs;
                    barisBaru[8]=hilang;
                    barisBaru[9]=rstampilbarang.getString("no_batch");
                    barisBaru[10]=rstampilbarang.getString("no_faktur");
                    barisBaru[11]=rstampilbarang.getString("aturan_pakai");
                    boolean[] jadwal=new boolean[24];
                    for(int jam=0;jam<24;jam++){
                        jadwal[jam]="true".equals(rstampilbarang.getString("jam"+(jam<10?"0":"")+jam));
                        if((perJam!=null)&&(perJam[jam]!=0)){
                            barisBaru[KOL_JAM_AWAL+jam]=Double.valueOf(Math.round(perJam[jam]*1000000.0)/1000000.0);
                        }
                    }
                    // [FIX-KETERANGAN] kosong bila belum pernah diisi
                    String ket=keteranganPerObat.get(rstampilbarang.getString("kode_brng")+"|"+rstampilbarang.getString("no_batch")+"|"+rstampilbarang.getString("no_faktur"));
                    barisBaru[KOL_KET]=(ket==null)?"":ket;
                    jadwalJam.add(jadwal);
                    double keluarHariIni=0;
                    if(perJam!=null){
                        for(double nilaiJam:perJam){
                            keluarHariIni+=nilaiJam;
                        }
                    }
                    keluarHariIni=bulatkanAngka(keluarHariIni);
                    keluarPerBaris.add(new double[]{Math.max(0,bulatkanAngka(keluar-keluarHariIni)),keluarHariIni});
                    tabMode.addRow(barisBaru);
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally{
                if(rstampilbarang != null){
                    rstampilbarang.close();
                }
                if(pstampilbarang != null){
                    pstampilbarang.close();
                }
            }                
        } catch (Exception e) {
            System.out.println(e);
        }       
    }
    
    public void setNoRm(String norwt,String norm,String pasien,Date tanggal) {        
        TNoRw.setText(norwt);
        TNoRM.setText(norm);
        TPasien.setText(pasien);
        
        Tanggal.setDate(tanggal);
        // [FIX-FILTER] filter "Semua resep" mati saat form dibuka; rentang tanggalnya diisi otomatis (resep pertama s.d. terakhir/tanggal kerja)
        ChkTgl.setSelected(false);
        aturRentangTanggalResep(norwt,tanggal);
        KdPj.setText(Sequel.cariIsi("select reg_periksa.kd_pj from reg_periksa where reg_periksa.no_rawat=?",norwt));
        kdgudang.setText(akses.getkdbangsal());
        nmgudang.setText(bangsal.tampil3(akses.getkdbangsal()));
        kelas.setText(Sequel.cariIsi(
                "select kamar.kelas from kamar inner join kamar_inap on kamar.kd_kamar=kamar_inap.kd_kamar "+
                "where kamar_inap.no_rawat=? and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",norwt));
        if(kelas.getText().equals("Kelas 1")){
            Jeniskelas.setSelectedItem("Kelas 1");
        }else if(kelas.getText().equals("Kelas 2")){
            Jeniskelas.setSelectedItem("Kelas 2");
        }else if(kelas.getText().equals("Kelas 3")){
            Jeniskelas.setSelectedItem("Kelas 3");
        }else if(kelas.getText().equals("Kelas Utama")){
            Jeniskelas.setSelectedItem("Utama");
        }else if(kelas.getText().equals("Kelas VIP")){
            Jeniskelas.setSelectedItem("VIP");
        }else if(kelas.getText().equals("Kelas VVIP")){
            Jeniskelas.setSelectedItem("VVIP");
        } 
        kenaikan=Sequel.cariIsiAngka("select (set_harga_obat_ranap.hargajual/100) from set_harga_obat_ranap where set_harga_obat_ranap.kd_pj='"+KdPj.getText()+"' and set_harga_obat_ranap.kelas='"+kelas.getText()+"'");
        
        BtnGudang.setEnabled(akses.getakses_depo_obat()&&!bacaSaja);
    }  
    
    
    
    // [FIX-FILTER] Isi Tgl1 s.d. Tgl2 otomatis: dari tanggal resep (stok_obat_pasien) paling awal sampai yang paling akhir pasien ini,
    // diperluas sampai tanggal kerja kalau tanggal kerja di luar rentang itu. Jadi begitu "Semua resep" dicentang, langsung tampil semuanya
    // dan tinggal dipersempit kalau mau.
    private void aturRentangTanggalResep(String norwt,Date tanggal){
        Date awal=(tanggal!=null?tanggal:new Date());
        Date akhir=awal;
        try {
            java.text.SimpleDateFormat format=new java.text.SimpleDateFormat("yyyy-MM-dd");
            String tglMin=Sequel.cariIsi("select min(stok_obat_pasien.tanggal) from stok_obat_pasien where stok_obat_pasien.no_rawat=?",norwt);
            String tglMax=Sequel.cariIsi("select max(stok_obat_pasien.tanggal) from stok_obat_pasien where stok_obat_pasien.no_rawat=?",norwt);
            if((tglMin!=null)&&(!tglMin.trim().equals(""))){
                Date d=format.parse(tglMin.trim());
                if(d.before(awal)){
                    awal=d;
                }
            }
            if((tglMax!=null)&&(!tglMax.trim().equals(""))){
                Date d=format.parse(tglMax.trim());
                if(d.after(akhir)){
                    akhir=d;
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        Tgl1.setDate(awal);
        Tgl2.setDate(akhir);
    }

    private void getData(){
        int row=tbObat.getSelectedRow();
        if(TNoRw.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"No. Rawat");
        }else if(row!= -1){ 
            Kd2.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            int kolom=tbObat.getSelectedColumn();  
            if((kolom>=KOL_JAM_AWAL)&&(kolom<=KOL_JAM_AKHIR)){
                // [FIX-JAM] total pemberian = jumlah seluruh kolom jam 00-23 pada baris ini
                double totalJam=jumlahInputJam(row);
                int barisModel=tbObat.convertRowIndexToModel(row);
                double keluarLain=0,keluarSimpan=0;
                if(barisModel<keluarPerBaris.size()){
                    keluarLain=keluarPerBaris.get(barisModel)[0];
                    keluarSimpan=keluarPerBaris.get(barisModel)[1];
                }
                stokmasuk=0;
                try {
                    stokmasuk=Double.parseDouble(tbObat.getValueAt(row,4).toString());
                } catch (Exception e) {
                    stokmasuk=0;
                }
                // batas pemberian di tanggal ini = sisa sebenarnya; kalau data lama sudah terlanjur minus, yang sudah tersimpan tetap boleh ada (tidak boleh ditambah)
                double batasHariIni=Math.max(stokmasuk-keluarLain,keluarSimpan);
                if(tbObat.getValueAt(row,0).toString().equals("false")){
                    // [FIX-TTLKLR] cek terhadap sisa sebenarnya (Ttl.Msk - keluar tanggal lain), bukan lagi hanya Ttl.Msk
                    if(totalJam>(batasHariIni+0.000001)){
                        tbObat.setValueAt(null, row,kolom);  
                        tbObat.setValueAt(bulatkanAngka(keluarLain+jumlahInputJam(row)), row,5);
                        JOptionPane.showMessageDialog(null,"Maaf, Stok tidak cukup....!!!");
                        tbObat.requestFocus();
                    }else{
                        tbObat.setValueAt(bulatkanAngka(keluarLain+totalJam), row,5);  
                    }  
                }else{
                    try {
                        kapasitas=1;
                        pscariharga=koneksi.prepareStatement("select databarang.kelas1,databarang.kelas2,databarang.kelas3,"+
                            "databarang.utama,databarang.vip,databarang.vvip,databarang.beliluar,databarang.karyawan,databarang.h_beli,"+
                            "IFNULL(kapasitas,0) as kapasitas from databarang "+
                            "where databarang.kode_brng=?");
                        try {
                            pscariharga.setString(1,tbObat.getValueAt(row,1).toString());
                            rscariharga=pscariharga.executeQuery();
                            while(rscariharga.next()){
                                if(rscariharga.getDouble("kapasitas")>0){
                                    kapasitas=rscariharga.getDouble("kapasitas");                                
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notofikasi : "+e);
                        } finally{
                            if(rscariharga != null){
                                rscariharga.close();
                            }
                            if(pscariharga != null){
                                pscariharga.close();
                            }
                        }
                            
                        if((totalJam/kapasitas)>(batasHariIni+0.000001)){   // [FIX-TTLKLR]
                            tbObat.setValueAt(null, row,kolom);  
                            tbObat.setValueAt(bulatkanAngka(keluarLain+(jumlahInputJam(row)/kapasitas)), row,5);   // [FIX-TTLKLR]
                            JOptionPane.showMessageDialog(null,"Maaf, Stok tidak cukup....!!!");
                            tbObat.requestFocus();
                        }else{
                            tbObat.setValueAt(bulatkanAngka(keluarLain+(totalJam/kapasitas)), row,5);   // [FIX-TTLKLR]
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
                                               
            }else if(kolom==6){
                retur=0;
                try {
                    retur=Double.parseDouble(tbObat.getValueAt(row,6).toString()); 
                } catch (Exception e) {
                    retur=0;
                }
                stokmasuk=0;
                try {
                    stokmasuk=Double.parseDouble(tbObat.getValueAt(row,7).toString());
                } catch (Exception e) {
                    stokmasuk=0;
                }
                if((stokmasuk-retur)<-0.000001){
                    tbObat.setValueAt(0, row,kolom);  
                    JOptionPane.showMessageDialog(null,"Maaf, Stok tidak cukup....!!!");
                    tbObat.requestFocus();
                }else{
                    tbObat.setValueAt(bulatkanAngka(stokmasuk-retur), row,8);   
                }   
            }
        }
    }

    public JTable getTable(){
        return tbObat;
    }
    
    private void runBackground(Runnable task) {
        if (ceksukses) return;
        ceksukses = true;

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        executor.submit(() -> {
            try {
                task.run();
            } finally {
                ceksukses = false;
                SwingUtilities.invokeLater(() -> {
                    this.setCursor(Cursor.getDefaultCursor());
                });
            }
        });
    }
    
    public void setBacaSaja(boolean nilai) {
        bacaSaja=nilai;
        BtnSimpan.setEnabled(!nilai);
        BtnGudang.setEnabled(!nilai&&akses.getakses_depo_obat());
        if(tbObat.isEditing()){
            tbObat.getCellEditor().cancelCellEditing();
        }
        if(internalFrame1.getBorder() instanceof javax.swing.border.TitledBorder){
            ((javax.swing.border.TitledBorder)internalFrame1.getBorder()).setTitle(nilai?
                    "::[ Data Obat, Alkes & BHP Medis - HANYA LIHAT & CETAK (billing sudah terverifikasi) ]::":
                    "::[ Data Obat, Alkes & BHP Medis ]::");
            internalFrame1.repaint();
        }
    }
}
