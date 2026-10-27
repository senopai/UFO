/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariPetugas;


/**
 *
 * @author perpustakaan
 */
public final class RMPenilaianTambahanBunuhDiriRS extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;    
    private DlgCariPetugas petugas=new DlgCariPetugas(null,false);
    private String finger="";
    private StringBuilder htmlContent;
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public RMPenilaianTambahanBunuhDiriRS(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.R.M.","Nama Pasien","Tgl.Lahir","JK","Tanggal",
            "Komitmen Keselamatan","Rencana Bunuh Diri","Letalitas Rencana","Riwayat Percobaan","Ide Bunuh Diri",
            "Gejala","Pikiran Kematian","Validitas Jawaban","Total Skor","Interval Monitoring","Nama & Paraf",
            "NIP","Petugas"
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 19; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(65);
            }else if(i==2){
                column.setPreferredWidth(160);
            }else if(i==3){
                column.setPreferredWidth(65);
            }else if(i==4){
                column.setPreferredWidth(25);
            }else if(i==5){
                column.setPreferredWidth(115);
            }else if(i==6){
                column.setPreferredWidth(230);
            }else if(i==7){
                column.setPreferredWidth(230);
            }else if(i==8){
                column.setPreferredWidth(230);
            }else if(i==9){
                column.setPreferredWidth(230);
            }else if(i==10){
                column.setPreferredWidth(230);
            }else if(i==11){
                column.setPreferredWidth(230);
            }else if(i==12){
                column.setPreferredWidth(230);
            }else if(i==13){
                column.setPreferredWidth(230);
            }else if(i==14){
                column.setPreferredWidth(65);
            }else if(i==15){
                column.setPreferredWidth(140);
            }else if(i==16){
                column.setPreferredWidth(140);
            }else if(i==17){
                column.setPreferredWidth(75);
            }else if(i==18){
                column.setPreferredWidth(150);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte)20).getKata(NIP));
        ParafPetugas.setDocument(new batasInput((byte)60).getKata(ParafPetugas));
        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        }
        
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){                   
                    NIP.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                    NamaPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                }  
                NIP.requestFocus();
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
        
        ChkInput.setSelected(false);
        isForm();
        jam();
        
        
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnPenilaianTambahanBunuhDiri = new javax.swing.JMenuItem();
        MnRiwayatBunuhDiri = new javax.swing.JMenuItem();
        JK = new widget.TextBox();
        LoadHTML = new widget.editorpane();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        Tanggal = new widget.Tanggal();
        TNoRM = new widget.TextBox();
        jLabel16 = new widget.Label();
        Jam = new widget.ComboBox();
        Menit = new widget.ComboBox();
        Detik = new widget.ComboBox();
        ChkKejadian = new widget.CekBox();
        jLabel18 = new widget.Label();
        NIP = new widget.TextBox();
        NamaPetugas = new widget.TextBox();
        btnPetugas = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        lblJudulSkala = new widget.Label();
        lblRowKomitmen = new widget.Label();
        Komitmen = new widget.ComboBox();
        lblRowRencana = new widget.Label();
        Rencana = new widget.ComboBox();
        lblRowLetalitas = new widget.Label();
        Letalitas = new widget.ComboBox();
        lblRowRiwayat = new widget.Label();
        Riwayat = new widget.ComboBox();
        lblRowIde = new widget.Label();
        Ide = new widget.ComboBox();
        lblRowGejala = new widget.Label();
        Gejala = new widget.ComboBox();
        lblRowPikiran = new widget.Label();
        Pikiran = new widget.ComboBox();
        lblRowValiditas = new widget.Label();
        Validitas = new widget.ComboBox();
        lblTotalSkorRow = new widget.Label();
        TotalSkor = new widget.TextBox();
        lblIntervalRow = new widget.Label();
        LevelSkor = new widget.TextBox();
        lblParafRow = new widget.Label();
        ParafPetugas = new widget.TextBox();
        lblFooter1 = new widget.Label();
        lblFooter2 = new widget.Label();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnPenilaianTambahanBunuhDiri.setBackground(new java.awt.Color(255, 255, 254));
        MnPenilaianTambahanBunuhDiri.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPenilaianTambahanBunuhDiri.setForeground(new java.awt.Color(50, 50, 50));
        MnPenilaianTambahanBunuhDiri.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPenilaianTambahanBunuhDiri.setText("Formulir Pengkajian Tambahan Bunuh Diri");
        MnPenilaianTambahanBunuhDiri.setName("MnPenilaianTambahanBunuhDiri"); // NOI18N
        MnPenilaianTambahanBunuhDiri.setPreferredSize(new java.awt.Dimension(290, 26));
        MnPenilaianTambahanBunuhDiri.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPenilaianTambahanBunuhDiriActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPenilaianTambahanBunuhDiri);

        MnRiwayatBunuhDiri.setBackground(new java.awt.Color(255, 255, 254));
        MnRiwayatBunuhDiri.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnRiwayatBunuhDiri.setForeground(new java.awt.Color(50, 50, 50));
        MnRiwayatBunuhDiri.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnRiwayatBunuhDiri.setText("Cetak Riwayat (Sesuai Filter Tanggal & Kata Kunci)");
        MnRiwayatBunuhDiri.setName("MnRiwayatBunuhDiri"); // NOI18N
        MnRiwayatBunuhDiri.setPreferredSize(new java.awt.Dimension(290, 26));
        MnRiwayatBunuhDiri.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnRiwayatBunuhDiriActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnRiwayatBunuhDiri);

        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengkajian Tambahan Bunuh Diri ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

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
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass8.add(LCount);

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
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-10-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-10-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(310, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(720, 480));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(720, 470));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 80, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(84, 10, 136, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(336, 10, 285, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-10-2026" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(84, 40, 90, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 112, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 80, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam.setName("Jam"); // NOI18N
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(178, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit.setName("Menit"); // NOI18N
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(243, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik.setName("Detik"); // NOI18N
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(308, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(373, 40, 23, 23);

        jLabel18.setText("Petugas :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(400, 40, 70, 23);

        NIP.setEditable(false);
        NIP.setHighlighter(null);
        NIP.setName("NIP"); // NOI18N
        FormInput.add(NIP);
        NIP.setBounds(474, 40, 94, 23);

        NamaPetugas.setEditable(false);
        NamaPetugas.setName("NamaPetugas"); // NOI18N
        FormInput.add(NamaPetugas);
        NamaPetugas.setBounds(570, 40, 187, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setToolTipText("ALt+2");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        btnPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugasKeyPressed(evt);
            }
        });
        FormInput.add(btnPetugas);
        btnPetugas.setBounds(761, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        lblJudulSkala.setText("RIWAYAT PENILAIAN RISIKO BUNUH DIRI & MELUKAI DIRI");
        lblJudulSkala.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblJudulSkala.setName("lblJudulSkala"); // NOI18N
        FormInput.add(lblJudulSkala);
        lblJudulSkala.setBounds(5, 70, 700, 18);

        lblRowKomitmen.setText("Komitmen Keselamatan");
        lblRowKomitmen.setName("lblRowKomitmen"); // NOI18N
        FormInput.add(lblRowKomitmen);
        lblRowKomitmen.setBounds(5, 96, 290, 23);

        Komitmen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mampu membuat komitmen dengan jelas", "Mampu membuat komitmen tapi ragu-ragu", "Menolak/tidak mampu membuat komitmen (tidak mampu menilai: demensia, halusinasi, delusi, delirium, disorientasi)" }));
        Komitmen.setName("Komitmen"); // NOI18N
        Komitmen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                KomitmenItemStateChanged(evt);
            }
        });
        Komitmen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KomitmenKeyPressed(evt);
            }
        });
        FormInput.add(Komitmen);
        Komitmen.setBounds(305, 96, 390, 23);

        lblRowRencana.setText("Rencana Bunuh Diri");
        lblRowRencana.setName("lblRowRencana"); // NOI18N
        FormInput.add(lblRowRencana);
        lblRowRencana.setBounds(5, 124, 290, 23);

        Rencana.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak ada rencana", "Merencanakan ide bunuh diri, belum ada cara", "Merencanakan ide bunuh diri dan sudah ada metode/cara" }));
        Rencana.setName("Rencana"); // NOI18N
        Rencana.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RencanaItemStateChanged(evt);
            }
        });
        Rencana.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RencanaKeyPressed(evt);
            }
        });
        FormInput.add(Rencana);
        Rencana.setBounds(305, 124, 390, 23);

        lblRowLetalitas.setText("Letalitas Rencana");
        lblRowLetalitas.setName("lblRowLetalitas"); // NOI18N
        FormInput.add(lblRowLetalitas);
        lblRowLetalitas.setBounds(5, 152, 290, 23);

        Letalitas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Letalitas rendah (benturkan kepala, mencakar diri, minum deterjen)", "Letalitas sedang (overdosis obat, cairan pembersih, sayat pergelangan tangan)", "Letalitas tinggi (senapan, gantung diri, lompat dari ketinggian, bensin/insektisida)" }));
        Letalitas.setName("Letalitas"); // NOI18N
        Letalitas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LetalitasItemStateChanged(evt);
            }
        });
        Letalitas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LetalitasKeyPressed(evt);
            }
        });
        FormInput.add(Letalitas);
        Letalitas.setBounds(305, 152, 390, 23);

        lblRowRiwayat.setText("Riwayat Percobaan Bunuh Diri");
        lblRowRiwayat.setName("lblRowRiwayat"); // NOI18N
        FormInput.add(lblRowRiwayat);
        lblRowRiwayat.setBounds(5, 180, 290, 23);

        Riwayat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak ada riwayat percobaan", "Riwayat percobaan dengan letalitas sedang", "Riwayat percobaan dengan letalitas tinggi" }));
        Riwayat.setName("Riwayat"); // NOI18N
        Riwayat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RiwayatItemStateChanged(evt);
            }
        });
        Riwayat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RiwayatKeyPressed(evt);
            }
        });
        FormInput.add(Riwayat);
        Riwayat.setBounds(305, 180, 390, 23);

        lblRowIde.setText("Ide Bunuh Diri");
        lblRowIde.setName("lblRowIde"); // NOI18N
        FormInput.add(lblRowIde);
        lblRowIde.setBounds(5, 208, 290, 23);

        Ide.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak ada pikiran bunuh diri", "Pikiran bunuh diri sesekali/singkat", "Pikiran bunuh diri terus menerus" }));
        Ide.setName("Ide"); // NOI18N
        Ide.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                IdeItemStateChanged(evt);
            }
        });
        Ide.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                IdeKeyPressed(evt);
            }
        });
        FormInput.add(Ide);
        Ide.setBounds(305, 208, 390, 23);

        lblRowGejala.setText("Gejala (a.putus asa b.tdk berdaya c.anhedonia d.rasa bersalah/malu e.marah f.impulsif)");
        lblRowGejala.setName("lblRowGejala"); // NOI18N
        FormInput.add(lblRowGejala);
        lblRowGejala.setBounds(5, 236, 290, 23);

        Gejala.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Terdapat 0-2 gejala", "Terdapat 3-4 gejala", "Terdapat 5-6 gejala" }));
        Gejala.setName("Gejala"); // NOI18N
        Gejala.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                GejalaItemStateChanged(evt);
            }
        });
        Gejala.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GejalaKeyPressed(evt);
            }
        });
        FormInput.add(Gejala);
        Gejala.setBounds(305, 236, 390, 23);

        lblRowPikiran.setText("Pikiran Kematian Saat Ini");
        lblRowPikiran.setName("lblRowPikiran"); // NOI18N
        FormInput.add(lblRowPikiran);
        lblRowPikiran.setBounds(5, 264, 290, 23);

        Pikiran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jarang", "Sering", "Terus menerus" }));
        Pikiran.setName("Pikiran"); // NOI18N
        Pikiran.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                PikiranItemStateChanged(evt);
            }
        });
        Pikiran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PikiranKeyPressed(evt);
            }
        });
        FormInput.add(Pikiran);
        Pikiran.setBounds(305, 264, 390, 23);

        lblRowValiditas.setText("Validitas Jawaban Pasien");
        lblRowValiditas.setName("lblRowValiditas"); // NOI18N
        FormInput.add(lblRowValiditas);
        lblRowValiditas.setBounds(5, 292, 290, 23);

        Validitas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jawaban pasien bisa dipercaya", "Bisa dipercaya, sedikit isyarat bunuh diri", "Tidak dapat dipercaya, ada isyarat risiko bunuh diri" }));
        Validitas.setName("Validitas"); // NOI18N
        Validitas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ValiditasItemStateChanged(evt);
            }
        });
        Validitas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ValiditasKeyPressed(evt);
            }
        });
        FormInput.add(Validitas);
        Validitas.setBounds(305, 292, 390, 23);

        lblTotalSkorRow.setText("Total Skor");
        lblTotalSkorRow.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotalSkorRow.setName("lblTotalSkorRow"); // NOI18N
        FormInput.add(lblTotalSkorRow);
        lblTotalSkorRow.setBounds(5, 320, 290, 23);

        TotalSkor.setEditable(false);
        TotalSkor.setName("TotalSkor"); // NOI18N
        TotalSkor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TotalSkorKeyPressed(evt);
            }
        });
        FormInput.add(TotalSkor);
        TotalSkor.setBounds(305, 320, 390, 23);

        lblIntervalRow.setText("Interval Monitoring");
        lblIntervalRow.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblIntervalRow.setName("lblIntervalRow"); // NOI18N
        FormInput.add(lblIntervalRow);
        lblIntervalRow.setBounds(5, 348, 290, 23);

        LevelSkor.setEditable(false);
        LevelSkor.setName("LevelSkor"); // NOI18N
        LevelSkor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LevelSkorKeyPressed(evt);
            }
        });
        FormInput.add(LevelSkor);
        LevelSkor.setBounds(305, 348, 390, 23);

        lblParafRow.setText("Nama dan Paraf");
        lblParafRow.setName("lblParafRow"); // NOI18N
        FormInput.add(lblParafRow);
        lblParafRow.setBounds(5, 376, 290, 23);

        ParafPetugas.setName("ParafPetugas"); // NOI18N
        ParafPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ParafPetugasKeyPressed(evt);
            }
        });
        FormInput.add(ParafPetugas);
        ParafPetugas.setBounds(305, 376, 390, 23);

        lblFooter1.setText("Keterangan Gejala: a.putus asa b.tidak berdaya c.anhedonia d.rasa bersalah/malu e.kemarahan f.impulsivitas");
        lblFooter1.setName("lblFooter1"); // NOI18N
        FormInput.add(lblFooter1);
        lblFooter1.setBounds(5, 408, 700, 16);

        lblFooter2.setText("Skor Total : ≥10 = Risiko Tinggi (monitoring tiap jam)   |   4-9 = Risiko Sedang (tiap shift)   |   0-3 = Risiko Rendah (tiap hari)");
        lblFooter2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFooter2.setName("lblFooter2"); // NOI18N
        FormInput.add(lblFooter2);
        lblFooter2.setBounds(5, 426, 700, 16);

        scrollInput.setViewportView(FormInput);

        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
            isPsien();
        }else{            
            Valid.pindah(evt,TCari,Tanggal);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt,TCari,BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(NIP.getText().trim().equals("")||NamaPetugas.getText().trim().equals("")){
            Valid.textKosong(NIP,"Petugas");
        }else{
            if(Sequel.menyimpantf("penilaian_tambahan_bunuh_diri","?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",14,new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(), NIP.getText(), ""+Komitmen.getSelectedIndex(), ""+Rencana.getSelectedIndex(), ""+Letalitas.getSelectedIndex(), ""+Riwayat.getSelectedIndex(), ""+Ide.getSelectedIndex(), ""+Gejala.getSelectedIndex(), ""+Pikiran.getSelectedIndex(), ""+Validitas.getSelectedIndex(), TotalSkor.getText(), LevelSkor.getText(), ParafPetugas.getText()
            })==true){
                tabMode.addRow(new Object[]{
                    TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), JK.getText(), Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(), ""+Komitmen.getSelectedIndex(), ""+Rencana.getSelectedIndex(), ""+Letalitas.getSelectedIndex(), ""+Riwayat.getSelectedIndex(), ""+Ide.getSelectedIndex(), ""+Gejala.getSelectedIndex(), ""+Pikiran.getSelectedIndex(), ""+Validitas.getSelectedIndex(), TotalSkor.getText(), LevelSkor.getText(), ParafPetugas.getText(), NIP.getText(), NamaPetugas.getText()
                });
                emptTeks();
                LCount.setText(""+tabMode.getRowCount());
            }  
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,ParafPetugas,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        ChkInput.setSelected(true);
        isForm(); 
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbObat.getSelectedRow()>-1){
            if(akses.getkode().equals("Admin Utama")){
                hapus();
            }else{
                if(NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString())){
                    hapus();
                }else{
                    JOptionPane.showMessageDialog(null,"Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
        }   
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(NIP.getText().trim().equals("")||NamaPetugas.getText().trim().equals("")){
            Valid.textKosong(NIP,"Petugas");
        }else{
            if(tbObat.getSelectedRow()>-1){
                if(akses.getkode().equals("Admin Utama")){
                    ganti();
                }else{
                    if(NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString())){
                        ganti();
                    }else{
                        JOptionPane.showMessageDialog(null,"Hanya bisa diganti oleh petugas yang bersangkutan..!!");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        petugas.dispose();
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKeluarActionPerformed(null);
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            try{
                htmlContent = new StringBuilder();
                htmlContent.append(                             
                    "<tr class='isi'>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.Rawat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.R.M.</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Pasien</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tgl.Lahir</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>JK</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tanggal</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Komitmen</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Rencana</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Letalitas</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Riwayat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Ide</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Gejala</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Pikiran</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Validitas</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Total Skor</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Interval Monitoring</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama & Paraf</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>NIP</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Petugas</b></td>"+
                    "</tr>"
                );
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,0).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,3).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,4).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,5).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,6).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,8).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,9).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,10).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,11).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,12).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,13).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,14).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,15).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,16).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,17).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,18).toString()+"</td>"+
                        "</tr>");
                }
                LoadHTML.setText(
                    "<html>"+
                      "<table width='3500px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                       htmlContent.toString()+
                      "</table>"+
                    "</html>"
                );

                File g = new File("file2.css");            
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
                    ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                    ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                    ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                    ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                    ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                    ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
                );
                bg.close();

                File f = new File("DataPenilaianTambahanBunuhDiri.html");            
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));            
                bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                            "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                            "<table width='3500px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                "<tr class='isi2'>"+
                                    "<td valign='top' align='center'>"+
                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                        "<font size='2' face='Tahoma'>DATA PENGKAJIAN TAMBAHAN BUNUH DIRI<br><br></font>"+        
                                    "</td>"+
                               "</tr>"+
                            "</table>")
                );
                bw.close();                         
                Desktop.getDesktop().browse(f.toURI());
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            tampil();
        }else{
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        Valid.pindah(evt,TCari,Jam);
}//GEN-LAST:event_TanggalKeyPressed

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        // Valid.pindah(evt, TNm, BtnSimpan);
}//GEN-LAST:event_TNoRMKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void JamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamKeyPressed
        Valid.pindah(evt,Tanggal,Menit);
    }//GEN-LAST:event_JamKeyPressed

    private void MenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenitKeyPressed
        Valid.pindah(evt,Jam,Detik);
    }//GEN-LAST:event_MenitKeyPressed

    private void DetikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetikKeyPressed
        Valid.pindah(evt,Menit,btnPetugas);
    }//GEN-LAST:event_DetikKeyPressed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void btnPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugasKeyPressed
        Valid.pindah(evt,Detik,Komitmen);
    }//GEN-LAST:event_btnPetugasKeyPressed

    private void MnPenilaianTambahanBunuhDiriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPenilaianTambahanBunuhDiriActionPerformed
        if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),18).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),17).toString():finger)+"\n"+Tanggal.getSelectedItem());
            Valid.MyReportqry("rptFormulirPenilaianTambahanBunuhDiri.jasper","report","::[ Formulir Pengkajian Tambahan Bunuh Diri ]::",
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,penilaian_tambahan_bunuh_diri.tanggal,date_format(reg_periksa.tgl_registrasi,'%d-%m-%Y')as tgl_registrasi,reg_periksa.jam_reg,"+
                    "penilaian_tambahan_bunuh_diri.komitmen,penilaian_tambahan_bunuh_diri.rencana,penilaian_tambahan_bunuh_diri.letalitas,penilaian_tambahan_bunuh_diri.riwayat,"+
                    "penilaian_tambahan_bunuh_diri.ide,penilaian_tambahan_bunuh_diri.gejala,penilaian_tambahan_bunuh_diri.pikiran,penilaian_tambahan_bunuh_diri.validitas,"+
                    "penilaian_tambahan_bunuh_diri.total_skor,penilaian_tambahan_bunuh_diri.level_skor,penilaian_tambahan_bunuh_diri.paraf"+
                    ",penilaian_tambahan_bunuh_diri.nip,petugas.nama,dokter.nm_dokter,poliklinik.nm_poli "+
                    "from penilaian_tambahan_bunuh_diri inner join reg_periksa on penilaian_tambahan_bunuh_diri.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                    "inner join petugas on penilaian_tambahan_bunuh_diri.nip=petugas.nip inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                    "where reg_periksa.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"'",param);
        }
    }//GEN-LAST:event_MnPenilaianTambahanBunuhDiriActionPerformed

    private void MnRiwayatBunuhDiriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnRiwayatBunuhDiriActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Tidak ada data pada rentang tanggal/kata kunci yang dipilih. Silakan cari data terlebih dahulu.");
        }else{
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            param.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            param.put("tglAwal",Valid.SetTgl(DTPCari1.getSelectedItem()+""));
            param.put("tglAkhir",Valid.SetTgl(DTPCari2.getSelectedItem()+""));
            param.put("katakunci",TCari.getText().trim().equals("")?"Semua Data":TCari.getText());
            String qry = "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,penilaian_tambahan_bunuh_diri.tanggal,"+
                    "penilaian_tambahan_bunuh_diri.komitmen,penilaian_tambahan_bunuh_diri.rencana,penilaian_tambahan_bunuh_diri.letalitas,penilaian_tambahan_bunuh_diri.riwayat,"+
                    "penilaian_tambahan_bunuh_diri.ide,penilaian_tambahan_bunuh_diri.gejala,penilaian_tambahan_bunuh_diri.pikiran,penilaian_tambahan_bunuh_diri.validitas,"+
                    "penilaian_tambahan_bunuh_diri.total_skor,penilaian_tambahan_bunuh_diri.level_skor,penilaian_tambahan_bunuh_diri.paraf,"+
                    "penilaian_tambahan_bunuh_diri.nip,petugas.nama "+
                    "from penilaian_tambahan_bunuh_diri inner join reg_periksa on penilaian_tambahan_bunuh_diri.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on penilaian_tambahan_bunuh_diri.nip=petugas.nip "+
                    "where penilaian_tambahan_bunuh_diri.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59'";
            if(!TCari.getText().trim().equals("")){
                qry += " and (reg_periksa.no_rawat like '%"+TCari.getText()+"%' or pasien.no_rkm_medis like '%"+TCari.getText()+"%' or pasien.nm_pasien like '%"+TCari.getText()+"%' or penilaian_tambahan_bunuh_diri.nip like '%"+TCari.getText()+"%' or petugas.nama like '%"+TCari.getText()+"%')";
            }
            qry += " order by pasien.nm_pasien,penilaian_tambahan_bunuh_diri.tanggal";
            Valid.MyReportqry("rptRiwayatPenilaianTambahanBunuhDiri.jasper","report","::[ Riwayat Penilaian Tambahan Bunuh Diri ]::",qry,param);
        }
    }//GEN-LAST:event_MnRiwayatBunuhDiriActionPerformed

    private void KomitmenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KomitmenKeyPressed
        Valid.pindah(evt,btnPetugas,Rencana);
    }//GEN-LAST:event_KomitmenKeyPressed

    private void RencanaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RencanaKeyPressed
        Valid.pindah(evt,Komitmen,Letalitas);
    }//GEN-LAST:event_RencanaKeyPressed

    private void LetalitasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LetalitasKeyPressed
        Valid.pindah(evt,Rencana,Riwayat);
    }//GEN-LAST:event_LetalitasKeyPressed

    private void RiwayatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RiwayatKeyPressed
        Valid.pindah(evt,Letalitas,Ide);
    }//GEN-LAST:event_RiwayatKeyPressed

    private void IdeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IdeKeyPressed
        Valid.pindah(evt,Riwayat,Gejala);
    }//GEN-LAST:event_IdeKeyPressed

    private void GejalaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GejalaKeyPressed
        Valid.pindah(evt,Ide,Pikiran);
    }//GEN-LAST:event_GejalaKeyPressed

    private void PikiranKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PikiranKeyPressed
        Valid.pindah(evt,Gejala,Validitas);
    }//GEN-LAST:event_PikiranKeyPressed

    private void ValiditasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ValiditasKeyPressed
        Valid.pindah(evt,Pikiran,ParafPetugas);
    }//GEN-LAST:event_ValiditasKeyPressed

    private void ParafPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ParafPetugasKeyPressed
        Valid.pindah(evt,Validitas,BtnSimpan);
    }//GEN-LAST:event_ParafPetugasKeyPressed

    private void KomitmenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_KomitmenItemStateChanged
        hitungTotal();
    }//GEN-LAST:event_KomitmenItemStateChanged

    private void RencanaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RencanaItemStateChanged
        hitungTotal();
    }//GEN-LAST:event_RencanaItemStateChanged

    private void LetalitasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LetalitasItemStateChanged
        hitungTotal();
    }//GEN-LAST:event_LetalitasItemStateChanged

    private void RiwayatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RiwayatItemStateChanged
        hitungTotal();
    }//GEN-LAST:event_RiwayatItemStateChanged

    private void IdeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_IdeItemStateChanged
        hitungTotal();
    }//GEN-LAST:event_IdeItemStateChanged

    private void GejalaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_GejalaItemStateChanged
        hitungTotal();
    }//GEN-LAST:event_GejalaItemStateChanged

    private void PikiranItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_PikiranItemStateChanged
        hitungTotal();
    }//GEN-LAST:event_PikiranItemStateChanged

    private void ValiditasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ValiditasItemStateChanged
        hitungTotal();
    }//GEN-LAST:event_ValiditasItemStateChanged

    private void TotalSkorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TotalSkorKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TotalSkorKeyPressed

    private void LevelSkorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LevelSkorKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LevelSkorKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMPenilaianTambahanBunuhDiriRS dialog = new RMPenilaianTambahanBunuhDiriRS(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Gejala;
    private widget.ComboBox Ide;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.ComboBox Komitmen;
    private widget.Label LCount;
    private widget.ComboBox Letalitas;
    private widget.TextBox LevelSkor;
    private widget.editorpane LoadHTML;
    private widget.ComboBox Menit;
    private javax.swing.JMenuItem MnPenilaianTambahanBunuhDiri;
    private javax.swing.JMenuItem MnRiwayatBunuhDiri;
    private widget.TextBox NIP;
    private widget.TextBox NamaPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox ParafPetugas;
    private widget.ComboBox Pikiran;
    private widget.ComboBox Rencana;
    private widget.ComboBox Riwayat;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TglLahir;
    private widget.TextBox TotalSkor;
    private widget.ComboBox Validitas;
    private widget.Button btnPetugas;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel16;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.Label lblFooter1;
    private widget.Label lblFooter2;
    private widget.Label lblIntervalRow;
    private widget.Label lblJudulSkala;
    private widget.Label lblParafRow;
    private widget.Label lblRowGejala;
    private widget.Label lblRowIde;
    private widget.Label lblRowKomitmen;
    private widget.Label lblRowLetalitas;
    private widget.Label lblRowPikiran;
    private widget.Label lblRowRencana;
    private widget.Label lblRowRiwayat;
    private widget.Label lblRowValiditas;
    private widget.Label lblTotalSkorRow;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
    
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            if(TCari.getText().toString().trim().equals("")){
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,penilaian_tambahan_bunuh_diri.tanggal,"+
                    "penilaian_tambahan_bunuh_diri.komitmen,penilaian_tambahan_bunuh_diri.rencana,penilaian_tambahan_bunuh_diri.letalitas,penilaian_tambahan_bunuh_diri.riwayat,penilaian_tambahan_bunuh_diri.ide,penilaian_tambahan_bunuh_diri.gejala,penilaian_tambahan_bunuh_diri.pikiran,penilaian_tambahan_bunuh_diri.validitas,penilaian_tambahan_bunuh_diri.total_skor,penilaian_tambahan_bunuh_diri.level_skor,penilaian_tambahan_bunuh_diri.paraf,penilaian_tambahan_bunuh_diri.nip,petugas.nama "+
                    "from penilaian_tambahan_bunuh_diri inner join reg_periksa on penilaian_tambahan_bunuh_diri.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on penilaian_tambahan_bunuh_diri.nip=petugas.nip where "+
                    "penilaian_tambahan_bunuh_diri.tanggal between ? and ? order by penilaian_tambahan_bunuh_diri.tanggal");
            }else{
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,penilaian_tambahan_bunuh_diri.tanggal,"+
                    "penilaian_tambahan_bunuh_diri.komitmen,penilaian_tambahan_bunuh_diri.rencana,penilaian_tambahan_bunuh_diri.letalitas,penilaian_tambahan_bunuh_diri.riwayat,penilaian_tambahan_bunuh_diri.ide,penilaian_tambahan_bunuh_diri.gejala,penilaian_tambahan_bunuh_diri.pikiran,penilaian_tambahan_bunuh_diri.validitas,penilaian_tambahan_bunuh_diri.total_skor,penilaian_tambahan_bunuh_diri.level_skor,penilaian_tambahan_bunuh_diri.paraf,penilaian_tambahan_bunuh_diri.nip,petugas.nama "+
                    "from penilaian_tambahan_bunuh_diri inner join reg_periksa on penilaian_tambahan_bunuh_diri.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on penilaian_tambahan_bunuh_diri.nip=petugas.nip where "+
                    "penilaian_tambahan_bunuh_diri.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or penilaian_tambahan_bunuh_diri.nip like ? or petugas.nama like ?) "+
                    "order by penilaian_tambahan_bunuh_diri.tanggal ");
            }
                
            try {
                if(TCari.getText().toString().trim().equals("")){
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                }else{
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                }
                    
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getDate("tgl_lahir"),rs.getString("jk"),rs.getString("tanggal"),
                        rs.getString("komitmen"),rs.getString("rencana"),rs.getString("letalitas"),rs.getString("riwayat"),rs.getString("ide"),
                        rs.getString("gejala"),rs.getString("pikiran"),rs.getString("validitas"),rs.getString("total_skor"),rs.getString("level_skor"),
                        rs.getString("paraf"),rs.getString("nip"),rs.getString("nama")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        }catch(SQLException e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }
    
    public void emptTeks() {
        Tanggal.setDate(new Date());
        Komitmen.setSelectedIndex(0);
        Rencana.setSelectedIndex(0);
        Letalitas.setSelectedIndex(0);
        Riwayat.setSelectedIndex(0);
        Ide.setSelectedIndex(0);
        Gejala.setSelectedIndex(0);
        Pikiran.setSelectedIndex(0);
        Validitas.setSelectedIndex(0);
        TotalSkor.setText("0");
        LevelSkor.setText("Rendah (monitor tiap hari)");
        ParafPetugas.setText("");
        Komitmen.requestFocus();
    } 


    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()); 
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            JK.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            Komitmen.setSelectedIndex(Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()));
            Rencana.setSelectedIndex(Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString()));
            Letalitas.setSelectedIndex(Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString()));
            Riwayat.setSelectedIndex(Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString()));
            Ide.setSelectedIndex(Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString()));
            Gejala.setSelectedIndex(Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString()));
            Pikiran.setSelectedIndex(Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString()));
            Validitas.setSelectedIndex(Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString()));
            TotalSkor.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            LevelSkor.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            ParafPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            Jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString().substring(11,13));
            Menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString().substring(14,16));
            Detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString().substring(17,19));
            Valid.SetTgl(Tanggal,tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
        }
    }
    private void isRawat() {
         Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='"+TNoRw.getText()+"' ",TNoRM);
    }

    private void isPsien() {
        try {
            ps=koneksi.prepareStatement("select pasien.nm_pasien,pasien.jk,pasien.tgl_lahir as lahir from pasien where pasien.no_rkm_medis=?");
            try {
                ps.setString(1,TNoRM.getText());
                rs=ps.executeQuery();
                if(rs.next()){
                    TPasien.setText(rs.getString("nm_pasien"));
                    JK.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("lahir"));
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            }finally {
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }
    }
    
    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        Sequel.cariIsi("select reg_periksa.tgl_registrasi from reg_periksa where reg_periksa.no_rawat='"+norwt+"'", DTPCari1);
        DTPCari2.setDate(tgl2);
        isRawat();
        isPsien();
        ChkInput.setSelected(true);
        isForm();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            if(internalFrame1.getHeight()>628){
                ChkInput.setVisible(false);
                PanelInput.setPreferredSize(new Dimension(WIDTH,480));
                FormInput.setVisible(true);      
                ChkInput.setVisible(true);
            }else{
                ChkInput.setVisible(false);
                PanelInput.setPreferredSize(new Dimension(WIDTH,internalFrame1.getHeight()-175));
                FormInput.setVisible(true);      
                ChkInput.setVisible(true);
            }
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getpenilaian_tambahan_bunuh_diri());
        BtnHapus.setEnabled(akses.getpenilaian_tambahan_bunuh_diri());
        BtnEdit.setEnabled(akses.getpenilaian_tambahan_bunuh_diri());
        BtnPrint.setEnabled(akses.getpenilaian_tambahan_bunuh_diri()); 
        if(akses.getjml2()>=1){
            NIP.setEditable(false);
            btnPetugas.setEnabled(false);
            NIP.setText(akses.getkode());
            NamaPetugas.setText(petugas.tampil3(NIP.getText()));
            if(NamaPetugas.getText().equals("")){
                NIP.setText("");
                JOptionPane.showMessageDialog(null,"User login bukan petugas...!!");
            }
        }            
    }

    private void jam(){
        ActionListener taskPerformer = new ActionListener(){
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;
            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";
                
                Date now = Calendar.getInstance().getTime();

                // Mengambil nilaj JAM, MENIT, dan DETIK Sekarang
                if(ChkKejadian.isSelected()==true){
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                }else if(ChkKejadian.isSelected()==false){
                    nilai_jam =Jam.getSelectedIndex();
                    nilai_menit =Menit.getSelectedIndex();
                    nilai_detik =Detik.getSelectedIndex();
                }

                // Jika nilai JAM lebih kecil dari 10 (hanya 1 digit)
                if (nilai_jam <= 9) {
                    // Tambahkan "0" didepannya
                    nol_jam = "0";
                }
                // Jika nilai MENIT lebih kecil dari 10 (hanya 1 digit)
                if (nilai_menit <= 9) {
                    // Tambahkan "0" didepannya
                    nol_menit = "0";
                }
                // Jika nilai DETIK lebih kecil dari 10 (hanya 1 digit)
                if (nilai_detik <= 9) {
                    // Tambahkan "0" didepannya
                    nol_detik = "0";
                }
                // Membuat String JAM, MENIT, DETIK
                String jam = nol_jam + Integer.toString(nilai_jam);
                String menit = nol_menit + Integer.toString(nilai_menit);
                String detik = nol_detik + Integer.toString(nilai_detik);
                // Menampilkan pada Layar
                //tampil_jam.setText("  " + jam + " : " + menit + " : " + detik + "  ");
                Jam.setSelectedItem(jam);
                Menit.setSelectedItem(menit);
                Detik.setSelectedItem(detik);
            }
        };
        // Timer
        new Timer(1000, taskPerformer).start();
    }

    private void ganti() {
        if(Sequel.mengedittf("penilaian_tambahan_bunuh_diri","no_rawat=?","no_rawat=?,tanggal=?,nip=?,komitmen=?,rencana=?,letalitas=?,riwayat=?,ide=?,gejala=?,pikiran=?,validitas=?,total_skor=?,level_skor=?,paraf=?",15,new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(), NIP.getText(), ""+Komitmen.getSelectedIndex(), ""+Rencana.getSelectedIndex(), ""+Letalitas.getSelectedIndex(), ""+Riwayat.getSelectedIndex(), ""+Ide.getSelectedIndex(), ""+Gejala.getSelectedIndex(), ""+Pikiran.getSelectedIndex(), ""+Validitas.getSelectedIndex(), TotalSkor.getText(), LevelSkor.getText(), ParafPetugas.getText(), tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
            })==true){
            tbObat.setValueAt(TNoRw.getText(),tbObat.getSelectedRow(),0);
            tbObat.setValueAt(TNoRM.getText(),tbObat.getSelectedRow(),1);
            tbObat.setValueAt(TPasien.getText(),tbObat.getSelectedRow(),2);
            tbObat.setValueAt(TglLahir.getText(),tbObat.getSelectedRow(),3);
            tbObat.setValueAt(JK.getText(),tbObat.getSelectedRow(),4);
            tbObat.setValueAt(Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),tbObat.getSelectedRow(),5);
            tbObat.setValueAt(""+Komitmen.getSelectedIndex(),tbObat.getSelectedRow(),6);
            tbObat.setValueAt(""+Rencana.getSelectedIndex(),tbObat.getSelectedRow(),7);
            tbObat.setValueAt(""+Letalitas.getSelectedIndex(),tbObat.getSelectedRow(),8);
            tbObat.setValueAt(""+Riwayat.getSelectedIndex(),tbObat.getSelectedRow(),9);
            tbObat.setValueAt(""+Ide.getSelectedIndex(),tbObat.getSelectedRow(),10);
            tbObat.setValueAt(""+Gejala.getSelectedIndex(),tbObat.getSelectedRow(),11);
            tbObat.setValueAt(""+Pikiran.getSelectedIndex(),tbObat.getSelectedRow(),12);
            tbObat.setValueAt(""+Validitas.getSelectedIndex(),tbObat.getSelectedRow(),13);
            tbObat.setValueAt(TotalSkor.getText(),tbObat.getSelectedRow(),14);
            tbObat.setValueAt(LevelSkor.getText(),tbObat.getSelectedRow(),15);
            tbObat.setValueAt(ParafPetugas.getText(),tbObat.getSelectedRow(),16);
            tbObat.setValueAt(NIP.getText(),tbObat.getSelectedRow(),17);
            tbObat.setValueAt(NamaPetugas.getText(),tbObat.getSelectedRow(),18);
            emptTeks();
        }
    }

    private void hapus() {
        if(Sequel.queryu2tf("delete from penilaian_tambahan_bunuh_diri where no_rawat=?",1,new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
        })==true){
            tabMode.removeRow(tbObat.getSelectedRow());
            emptTeks();
            LCount.setText(""+tabMode.getRowCount());
        }else{
            JOptionPane.showMessageDialog(null,"Gagal menghapus..!!");
        }
    }
    
    private int validitasSkor(int idx){
        if(idx==1) return 3;
        if(idx==2) return 4;
        return 0;
    }

    private String levelText(int total){
        if(total>=10){
            return "Tinggi (monitor tiap jam)";
        }else if(total>=4){
            return "Sedang (monitor tiap shift)";
        }else{
            return "Rendah (monitor tiap hari)";
        }
    }

    private void hitungTotal(){
        try {
            int total = Komitmen.getSelectedIndex()+Rencana.getSelectedIndex()+Letalitas.getSelectedIndex()+Riwayat.getSelectedIndex()+Ide.getSelectedIndex()+Gejala.getSelectedIndex()+Pikiran.getSelectedIndex()+validitasSkor(Validitas.getSelectedIndex());
            TotalSkor.setText(""+total);
            LevelSkor.setText(levelText(total));
        } catch (Exception e) {
            TotalSkor.setText("0");
            LevelSkor.setText("Rendah (monitor tiap hari)");
        }
    }
}

