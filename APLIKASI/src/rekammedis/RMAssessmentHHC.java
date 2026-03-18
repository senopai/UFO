/*
 * Kontribusi dari Mega Khairunnisa RS Simpangan Depok
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import kepegawaian.DlgCariPetugas;


/**
 *
 * @author perpustakaan
 */
public final class RMAssessmentHHC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;    
    private DlgCariPetugas petugas=new DlgCariPetugas(null,false);
    private String finger="",finger2="";
    private String TANGGALMUNDUR="yes";
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public RMAssessmentHHC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(800,674);
        Psikospiritual.setEditable(false);
        Implementasi.setEditable(false);
        Evaluasi.setEditable(false);

        tabMode = new DefaultTableModel(null, new Object[]{
            "Tanggal", "No.Rawat", "No.R.M.", "Nama Pasien", "Total Skor", "Deskripsi", "Intervensi", "Implementasi", "Evaluasi", 
            "Tgl.Lahir", "JK", "1. Ketaatan", "N. 1", "2. Penerimaan", "N. 2", 
            "3. Perasaan", "N. 3", "4. Masa Depan", "N. 4", "5. Ibadah", "N. 5", 
            "6. Kesembuhan", "N. 6", "7. Makna Agama", "N. 7", "NIP", "Nama Petugas"
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 27; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) { column.setPreferredWidth(115); }       // Tanggal
            else if (i == 1) { column.setPreferredWidth(105); }  // No.Rawat
            else if (i == 2) { column.setPreferredWidth(70); }   // No.R.M.
            else if (i == 3) { column.setPreferredWidth(160); }  // Nama Pasien
            else if (i == 4) { column.setPreferredWidth(70); }   // Total Skor
            else if (i == 5) { column.setPreferredWidth(250); }  // Deskripsi
            else if (i == 6) { column.setPreferredWidth(150); }  // Intervensi
            else if (i == 7) { column.setPreferredWidth(250); }  // Implementasi
            else if (i == 8) { column.setPreferredWidth(250); }  // Evaluasi
            else { 
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setWidth(0);
                column.setPreferredWidth(0);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte)20).getKata(NIP));
        Psikospiritual.setDocument(new batasInput((int)200).getKata(Psikospiritual));
        Implementasi.setDocument(new batasInput((int)250).getKata(Implementasi));
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
        emptTeks();
        
        /*try {
            TANGGALMUNDUR=koneksiDB.TANGGALMUNDUR();
        } catch (Exception e) {
            TANGGALMUNDUR="yes";
        }*/
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
        MnAssessmentHHC = new javax.swing.JMenuItem();
        JK = new widget.TextBox();
        TanggalRegistrasi = new widget.TextBox();
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
        jLabel57 = new widget.Label();
        jLabel217 = new widget.Label();
        cmbSkor1 = new widget.ComboBox();
        jLabel218 = new widget.Label();
        Skor1 = new widget.TextBox();
        jLabel30 = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        Psikospiritual = new widget.TextArea();
        jLabel31 = new widget.Label();
        scrollPane2 = new widget.ScrollPane();
        Implementasi = new widget.TextArea();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel32 = new widget.Label();
        scrollPane3 = new widget.ScrollPane();
        Evaluasi = new widget.TextArea();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel219 = new widget.Label();
        cmbSkor2 = new widget.ComboBox();
        jLabel58 = new widget.Label();
        jLabel221 = new widget.Label();
        cmbSkor3 = new widget.ComboBox();
        jLabel223 = new widget.Label();
        cmbSkor4 = new widget.ComboBox();
        jLabel225 = new widget.Label();
        cmbSkor5 = new widget.ComboBox();
        jLabel59 = new widget.Label();
        jLabel220 = new widget.Label();
        Skor2 = new widget.TextBox();
        jLabel222 = new widget.Label();
        Skor3 = new widget.TextBox();
        jLabel224 = new widget.Label();
        Skor4 = new widget.TextBox();
        jLabel226 = new widget.Label();
        Skor5 = new widget.TextBox();
        jLabel227 = new widget.Label();
        cmbSkor6 = new widget.ComboBox();
        jLabel228 = new widget.Label();
        Skor6 = new widget.TextBox();
        jLabel229 = new widget.Label();
        cmbSkor7 = new widget.ComboBox();
        jLabel230 = new widget.Label();
        Skor7 = new widget.TextBox();
        jLabel231 = new widget.Label();
        TotalSkor = new widget.TextBox();
        jLabel232 = new widget.Label();
        cmbIntervensi = new widget.ComboBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnAssessmentHHC.setBackground(new java.awt.Color(255, 255, 254));
        MnAssessmentHHC.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnAssessmentHHC.setForeground(new java.awt.Color(50, 50, 50));
        MnAssessmentHHC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnAssessmentHHC.setText("Assessment HHC");
        MnAssessmentHHC.setName("MnAssessmentHHC"); // NOI18N
        MnAssessmentHHC.setPreferredSize(new java.awt.Dimension(290, 26));
        MnAssessmentHHC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnAssessmentHHCActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnAssessmentHHC);

        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N

        TanggalRegistrasi.setHighlighter(null);
        TanggalRegistrasi.setName("TanggalRegistrasi"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Skor Aldrette Pasca Anestesi (General Anastesi) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 50));

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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "02-03-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "02-03-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
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
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 476));
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
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 570));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(1275, 525));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 70, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 136, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(326, 10, 285, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "02-03-2026" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(74, 70, 90, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(212, 10, 112, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 70, 70, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam.setName("Jam"); // NOI18N
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(168, 70, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit.setName("Menit"); // NOI18N
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(234, 70, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik.setName("Detik"); // NOI18N
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(300, 70, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(366, 70, 23, 23);

        jLabel18.setText("Petugas :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(0, 40, 70, 23);

        NIP.setEditable(false);
        NIP.setHighlighter(null);
        NIP.setName("NIP"); // NOI18N
        FormInput.add(NIP);
        NIP.setBounds(74, 40, 94, 23);

        NamaPetugas.setEditable(false);
        NamaPetugas.setName("NamaPetugas"); // NOI18N
        FormInput.add(NamaPetugas);
        NamaPetugas.setBounds(170, 40, 175, 23);

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
        btnPetugas.setBounds(348, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel57.setText("Obedient (Ketaatan)");
        jLabel57.setName("jLabel57"); // NOI18N
        FormInput.add(jLabel57);
        jLabel57.setBounds(10, 350, 130, 23);

        jLabel217.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel217.setText("<html><div style='width: 170px;'>Apa yang dipikirkan oleh bapak/ibu/saudara saat ini?</div></html>");
        jLabel217.setName("jLabel217"); // NOI18N
        FormInput.add(jLabel217);
        jLabel217.setBounds(30, 170, 260, 30);

        cmbSkor1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kesembuhan / Ibadah", "Sakit", "Pekerjaan / Keluarga", "Biaya Pengobatan" }));
        cmbSkor1.setName("cmbSkor1"); // NOI18N
        cmbSkor1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSkor1ItemStateChanged(evt);
            }
        });
        cmbSkor1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbSkor1KeyPressed(evt);
            }
        });
        FormInput.add(cmbSkor1);
        cmbSkor1.setBounds(340, 170, 330, 23);

        jLabel218.setText("Nilai :");
        jLabel218.setName("jLabel218"); // NOI18N
        FormInput.add(jLabel218);
        jLabel218.setBounds(670, 170, 50, 23);

        Skor1.setEditable(false);
        Skor1.setFocusTraversalPolicyProvider(true);
        Skor1.setName("Skor1"); // NOI18N
        FormInput.add(Skor1);
        Skor1.setBounds(730, 170, 60, 23);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Keterangan Psikospiritual");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(820, 100, 130, 23);

        scrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane1.setName("scrollPane1"); // NOI18N

        Psikospiritual.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Psikospiritual.setColumns(20);
        Psikospiritual.setRows(5);
        Psikospiritual.setName("Psikospiritual"); // NOI18N
        Psikospiritual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PsikospiritualKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(Psikospiritual);

        FormInput.add(scrollPane1);
        scrollPane1.setBounds(840, 120, 420, 43);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Implementasi (Tindakan)");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(820, 170, 280, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        Implementasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Implementasi.setColumns(20);
        Implementasi.setRows(5);
        Implementasi.setName("Implementasi"); // NOI18N
        Implementasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ImplementasiKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(Implementasi);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(840, 190, 420, 43);

        jSeparator3.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator3.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator3.setName("jSeparator3"); // NOI18N
        FormInput.add(jSeparator3);
        jSeparator3.setBounds(810, 100, 810, 1);

        jSeparator4.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator4.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator4.setName("jSeparator4"); // NOI18N
        FormInput.add(jSeparator4);
        jSeparator4.setBounds(810, 170, 810, 1);

        jSeparator2.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator2.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator2.setName("jSeparator2"); // NOI18N
        FormInput.add(jSeparator2);
        jSeparator2.setBounds(0, 100, 810, 1);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Evaluasi (Hasil)");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(820, 240, 280, 23);

        scrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane3.setName("scrollPane3"); // NOI18N

        Evaluasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Evaluasi.setColumns(20);
        Evaluasi.setRows(5);
        Evaluasi.setName("Evaluasi"); // NOI18N
        Evaluasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EvaluasiKeyPressed(evt);
            }
        });
        scrollPane3.setViewportView(Evaluasi);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(840, 260, 420, 43);

        jSeparator5.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator5.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator5.setName("jSeparator5"); // NOI18N
        FormInput.add(jSeparator5);
        jSeparator5.setBounds(810, 240, 810, 1);

        jLabel219.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel219.setText("<html><div style='width: 170px;'>Bagaimana anggapan bapak/ibu/saudara terhadap sakit yang diderita?</div></html>");
        jLabel219.setName("jLabel219"); // NOI18N
        FormInput.add(jLabel219);
        jLabel219.setBounds(30, 230, 260, 30);

        cmbSkor2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sakit sebagai cobaan, peringatan & rahmat", "Sakit sebagai ujian alami", "Sakit sebagai beban", "Sakit sebagai hukuman / kutukan" }));
        cmbSkor2.setName("cmbSkor2"); // NOI18N
        cmbSkor2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSkor2ItemStateChanged(evt);
            }
        });
        cmbSkor2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbSkor2KeyPressed(evt);
            }
        });
        FormInput.add(cmbSkor2);
        cmbSkor2.setBounds(340, 230, 330, 23);

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel58.setText("Assesment Awal");
        jLabel58.setName("jLabel58"); // NOI18N
        FormInput.add(jLabel58);
        jLabel58.setBounds(10, 150, 80, 23);

        jLabel221.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel221.setText("<html><div style='width: 170px;'>Bagaimana perasaan bapak/ibu/saudara terhadap sakit?</div></html>");
        jLabel221.setName("jLabel221"); // NOI18N
        FormInput.add(jLabel221);
        jLabel221.setBounds(30, 270, 260, 30);

        cmbSkor3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pasrah / Ikhlas", "Sedih", "Takut / Khawatir", "Marah / Tidak terima" }));
        cmbSkor3.setName("cmbSkor3"); // NOI18N
        cmbSkor3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSkor3ItemStateChanged(evt);
            }
        });
        cmbSkor3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbSkor3KeyPressed(evt);
            }
        });
        FormInput.add(cmbSkor3);
        cmbSkor3.setBounds(340, 270, 330, 23);

        jLabel223.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel223.setText("<html><div style='width: 170px;'>Bagaimana pandangan bapak/ibu/saudara terhadap masa depan?</div></html>");
        jLabel223.setName("jLabel223"); // NOI18N
        FormInput.add(jLabel223);
        jLabel223.setBounds(30, 310, 260, 30);

        cmbSkor4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sangat optimis akan sembuh", "Optimis tapi ragu dengan kondisi sakit", "Pasrah tanpa usaha", "Pesimis / Putus asa" }));
        cmbSkor4.setName("cmbSkor4"); // NOI18N
        cmbSkor4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSkor4ItemStateChanged(evt);
            }
        });
        cmbSkor4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbSkor4KeyPressed(evt);
            }
        });
        FormInput.add(cmbSkor4);
        cmbSkor4.setBounds(340, 310, 330, 23);

        jLabel225.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel225.setText("<html><div style='width: 170px;'>Bagaimana ibadah bapak/ibu/saudara selama sehat dan sakit?</div></html>");
        jLabel225.setName("jLabel225"); // NOI18N
        FormInput.add(jLabel225);
        jLabel225.setBounds(30, 370, 260, 30);

        cmbSkor5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tetap sholat tepat waktu (sehat maupun sakit)", "Sholat saat sehat / Tidak saat sakit", "Kadang-kadang beribadah", "Tidak pernah beribadah" }));
        cmbSkor5.setName("cmbSkor5"); // NOI18N
        cmbSkor5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSkor5ItemStateChanged(evt);
            }
        });
        cmbSkor5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbSkor5KeyPressed(evt);
            }
        });
        FormInput.add(cmbSkor5);
        cmbSkor5.setBounds(340, 370, 330, 23);

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel59.setText("Acceptance (Penerimaan)");
        jLabel59.setName("jLabel59"); // NOI18N
        FormInput.add(jLabel59);
        jLabel59.setBounds(10, 210, 130, 23);

        jLabel220.setText("Nilai :");
        jLabel220.setName("jLabel220"); // NOI18N
        FormInput.add(jLabel220);
        jLabel220.setBounds(670, 230, 50, 23);

        Skor2.setEditable(false);
        Skor2.setToolTipText("");
        Skor2.setFocusTraversalPolicyProvider(true);
        Skor2.setName("Skor2"); // NOI18N
        FormInput.add(Skor2);
        Skor2.setBounds(730, 230, 60, 23);

        jLabel222.setText("Nilai :");
        jLabel222.setName("jLabel222"); // NOI18N
        FormInput.add(jLabel222);
        jLabel222.setBounds(670, 270, 50, 23);

        Skor3.setEditable(false);
        Skor3.setFocusTraversalPolicyProvider(true);
        Skor3.setName("Skor3"); // NOI18N
        FormInput.add(Skor3);
        Skor3.setBounds(730, 270, 60, 23);

        jLabel224.setText("Nilai :");
        jLabel224.setName("jLabel224"); // NOI18N
        FormInput.add(jLabel224);
        jLabel224.setBounds(670, 310, 50, 23);

        Skor4.setEditable(false);
        Skor4.setFocusTraversalPolicyProvider(true);
        Skor4.setName("Skor4"); // NOI18N
        FormInput.add(Skor4);
        Skor4.setBounds(730, 310, 60, 23);

        jLabel226.setText("Nilai :");
        jLabel226.setName("jLabel226"); // NOI18N
        FormInput.add(jLabel226);
        jLabel226.setBounds(670, 370, 50, 23);

        Skor5.setEditable(false);
        Skor5.setFocusTraversalPolicyProvider(true);
        Skor5.setName("Skor5"); // NOI18N
        FormInput.add(Skor5);
        Skor5.setBounds(730, 370, 60, 23);

        jLabel227.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel227.setText("<html><div style='width: 170px;'>Hal apa yang bisa membantu mencapai kesembuhan bapak/ibu/saudara?</div></html>");
        jLabel227.setName("jLabel227"); // NOI18N
        FormInput.add(jLabel227);
        jLabel227.setBounds(30, 410, 260, 30);

        cmbSkor6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Allah SWT (melalui doa dan ikhtiar)", "Dokter / Medis", "Obat-obatan saja", "Dukun / Alternatif yang tidak logis" }));
        cmbSkor6.setName("cmbSkor6"); // NOI18N
        cmbSkor6.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSkor6ItemStateChanged(evt);
            }
        });
        cmbSkor6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbSkor6KeyPressed(evt);
            }
        });
        FormInput.add(cmbSkor6);
        cmbSkor6.setBounds(340, 410, 330, 23);

        jLabel228.setText("Nilai :");
        jLabel228.setName("jLabel228"); // NOI18N
        FormInput.add(jLabel228);
        jLabel228.setBounds(670, 410, 50, 23);

        Skor6.setEditable(false);
        Skor6.setFocusTraversalPolicyProvider(true);
        Skor6.setName("Skor6"); // NOI18N
        FormInput.add(Skor6);
        Skor6.setBounds(730, 410, 60, 23);

        jLabel229.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel229.setText("<html><div style='width: 170px;'>Apa makna agama bagi bapak/ibu/saudara?</div></html>");
        jLabel229.setName("jLabel229"); // NOI18N
        FormInput.add(jLabel229);
        jLabel229.setBounds(30, 450, 260, 30);

        cmbSkor7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Melindungi penuh kasih sayang / Sumber kekuatan", "Pedoman hidup", "Kewajiban formalitas", "Tidak memiliki makna khusus" }));
        cmbSkor7.setName("cmbSkor7"); // NOI18N
        cmbSkor7.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSkor7ItemStateChanged(evt);
            }
        });
        cmbSkor7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbSkor7KeyPressed(evt);
            }
        });
        FormInput.add(cmbSkor7);
        cmbSkor7.setBounds(340, 450, 330, 23);

        jLabel230.setText("Nilai :");
        jLabel230.setName("jLabel230"); // NOI18N
        FormInput.add(jLabel230);
        jLabel230.setBounds(670, 450, 50, 23);

        Skor7.setEditable(false);
        Skor7.setFocusTraversalPolicyProvider(true);
        Skor7.setName("Skor7"); // NOI18N
        FormInput.add(Skor7);
        Skor7.setBounds(730, 450, 60, 23);

        jLabel231.setText("Total Skor :");
        jLabel231.setName("jLabel231"); // NOI18N
        FormInput.add(jLabel231);
        jLabel231.setBounds(670, 490, 55, 23);

        TotalSkor.setEditable(false);
        TotalSkor.setFocusTraversalPolicyProvider(true);
        TotalSkor.setName("TotalSkor"); // NOI18N
        FormInput.add(TotalSkor);
        TotalSkor.setBounds(730, 490, 60, 23);

        jLabel232.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel232.setText("Intervensi");
        jLabel232.setName("jLabel232"); // NOI18N
        FormInput.add(jLabel232);
        jLabel232.setBounds(30, 120, 260, 20);

        cmbIntervensi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pengaturan Motivasi", "Relaksasi", "Mengembangkan Kesadaran", "Bimbingan Ibadah", "Motivasi Doa", "Pendalaman Agama" }));
        cmbIntervensi.setName("cmbIntervensi"); // NOI18N
        cmbIntervensi.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbIntervensiItemStateChanged(evt);
            }
        });
        cmbIntervensi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbIntervensiKeyPressed(evt);
            }
        });
        FormInput.add(cmbIntervensi);
        cmbIntervensi.setBounds(340, 120, 330, 23);

        scrollInput.setViewportView(FormInput);

        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        internalFrame1.getAccessibleContext().setAccessibleName("::[ Assessment HHC ]::");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
        }else{            
            Valid.pindah(evt,TCari,Tanggal);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt,TCari,BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"Pasien");
        }else if(NIP.getText().trim().equals("")||NamaPetugas.getText().trim().equals("")){
            Valid.textKosong(NIP,"Petugas");
        }else{
            if(Sequel.cariInteger("select count(no_rawat) from penilaian_psikospiritual_hhc where no_rawat=?", TNoRw.getText()) > 0){
                JOptionPane.showMessageDialog(rootPane,"Maaf, assesment HHC untuk pasien ini sudah pernah diisi sebelumnya");
                tbObat.requestFocus();
            }else{
                if(akses.getkode().equals("Admin Utama")){
                    simpan();
                }else{
                    if(TanggalRegistrasi.getText().equals("")){
                        TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));
                    }
                    if(Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem())==true){
                        simpan();
                    }
                }
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,Implementasi,BtnBatal);
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
                if(NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),21).toString())){
                    if(Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString(),Sequel.ambiltanggalsekarang())==true){
                        hapus();
                    }
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
            Valid.textKosong(TNoRw,"Pasien");
        }else if(NIP.getText().trim().equals("")||NamaPetugas.getText().trim().equals("")){
            Valid.textKosong(NIP,"Petugas");
        }else{
            if(tbObat.getSelectedRow()>-1){
                if(akses.getkode().equals("Admin Utama")){
                    ganti();
                }else{
                    if(NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),25).toString())){
                        if(Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),Sequel.ambiltanggalsekarang())==true){
                            if(TanggalRegistrasi.getText().equals("")){
                                TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));
                            }
                            if(Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem())==true){
                                ganti();
                            }
                        }
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
            Map<String, Object> param = new HashMap<>(); 
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            
            if(TCari.getText().trim().equals("")){
                Valid.MyReportqry("rptAssessmentHHC.jasper","report","::[ Data Assessment Psikospiritual HHC ]::",
                    "select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, p.tanggal, "+
                    "p.skor1, p.nilai1, p.skor2, p.nilai2, p.skor3, p.nilai3, p.skor4, p.nilai4, p.skor5, p.nilai5, p.skor6, p.nilai6, p.skor7, p.nilai7, "+
                    "p.total_skor, p.keterangan_psikospiritual, p.intervensi, p.implementasi, p.evaluasi, p.nip, petugas.nama "+
                    "from penilaian_psikospiritual_hhc p inner join reg_periksa on p.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on p.nip=petugas.nip where "+
                    "p.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59' "+
                    "order by p.tanggal",param);
            }else{
                Valid.MyReportqry("rptAssessmentHHC.jasper","report","::[ Data Assessment Psikospiritual HHC ]::",
                    "select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, p.tanggal, "+
                    "p.skor1, p.nilai1, p.skor2, p.nilai2, p.skor3, p.nilai3, p.skor4, p.nilai4, p.skor5, p.nilai5, p.skor6, p.nilai6, p.skor7, p.nilai7, "+
                    "p.total_skor, p.keterangan_psikospiritual, p.intervensi, p.implementasi, p.evaluasi, p.nip, petugas.nama "+
                    "from penilaian_psikospiritual_hhc p inner join reg_periksa on p.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on p.nip=petugas.nip where "+
                    "p.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59' and "+
                    "(reg_periksa.no_rawat like '%"+TCari.getText().trim()+"%' or pasien.no_rkm_medis like '%"+TCari.getText().trim()+"%' or pasien.nm_pasien like '%"+TCari.getText().trim()+"%' "+
                    "or p.nip like '%"+TCari.getText().trim()+"%' or petugas.nama like '%"+TCari.getText().trim()+"%') "+
                    "order by p.tanggal ",param);
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
        Valid.pindah(evt,Detik,cmbSkor1);
    }//GEN-LAST:event_btnPetugasKeyPressed

    private void MnAssessmentHHCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnAssessmentHHCActionPerformed
        if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(),25).toString());
            param.put("finger", "Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),26).toString()+"\nID "+(finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(),25).toString() : finger)+"\n"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            
            Valid.MyReportqry("rptAssessmentHHC.jasper", "report", "::[ Laporan Assessment HHC ]::",
                    "select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, p.tanggal, "+
                    "p.skor1, p.nilai1, p.skor2, p.nilai2, p.skor3, p.nilai3, p.skor4, p.nilai4, p.skor5, p.nilai5, p.skor6, p.nilai6, p.skor7, p.nilai7, "+
                    "p.total_skor, p.keterangan_psikospiritual, p.intervensi, p.implementasi, p.evaluasi, p.nip, petugas.nama "+
                    "from penilaian_psikospiritual_hhc p inner join reg_periksa on p.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on p.nip=petugas.nip where p.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(),1).toString() + "' " +
                    "and p.tanggal='" + tbObat.getValueAt(tbObat.getSelectedRow(),0).toString() + "'", param);
        }
    }//GEN-LAST:event_MnAssessmentHHCActionPerformed

    private void cmbSkor1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSkor1ItemStateChanged
        isCombo1();
        isjml();
    }//GEN-LAST:event_cmbSkor1ItemStateChanged

    private void cmbSkor1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbSkor1KeyPressed
        Valid.pindah(evt,btnPetugas,cmbSkor2);
    }//GEN-LAST:event_cmbSkor1KeyPressed

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

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void cmbSkor2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSkor2ItemStateChanged
        // TODO add your handling code here:
        isCombo2();
        isjml();
    }//GEN-LAST:event_cmbSkor2ItemStateChanged

    private void cmbSkor2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbSkor2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSkor2KeyPressed

    private void cmbSkor3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSkor3ItemStateChanged
        // TODO add your handling code here:
        isCombo3();
        isjml();
    }//GEN-LAST:event_cmbSkor3ItemStateChanged

    private void cmbSkor3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbSkor3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSkor3KeyPressed

    private void cmbSkor4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSkor4ItemStateChanged
        // TODO add your handling code here:
        isCombo4();
        isjml();
    }//GEN-LAST:event_cmbSkor4ItemStateChanged

    private void cmbSkor4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbSkor4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSkor4KeyPressed

    private void cmbSkor5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSkor5ItemStateChanged
        // TODO add your handling code here:
        isCombo5();
        isjml();
    }//GEN-LAST:event_cmbSkor5ItemStateChanged

    private void cmbSkor5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbSkor5KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSkor5KeyPressed

    private void cmbSkor6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSkor6ItemStateChanged
        // TODO add your handling code here:
        isCombo6();
        isjml();
    }//GEN-LAST:event_cmbSkor6ItemStateChanged

    private void cmbSkor6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbSkor6KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSkor6KeyPressed

    private void cmbSkor7ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSkor7ItemStateChanged
        // TODO add your handling code here:
        isCombo7();
        isjml();
    }//GEN-LAST:event_cmbSkor7ItemStateChanged

    private void cmbSkor7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbSkor7KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSkor7KeyPressed

    private void cmbIntervensiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbIntervensiItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbIntervensiItemStateChanged

    private void cmbIntervensiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbIntervensiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbIntervensiKeyPressed

    private void EvaluasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EvaluasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_EvaluasiKeyPressed

    private void ImplementasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ImplementasiKeyPressed
        Valid.pindah2(evt,Psikospiritual,BtnSimpan);
    }//GEN-LAST:event_ImplementasiKeyPressed

    private void PsikospiritualKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PsikospiritualKeyPressed
        Valid.pindah2(evt,Psikospiritual,Implementasi);
    }//GEN-LAST:event_PsikospiritualKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMAssessmentHHC dialog = new RMAssessmentHHC(new javax.swing.JFrame(), true);
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
    private widget.TextArea Evaluasi;
    private widget.PanelBiasa FormInput;
    private widget.TextArea Implementasi;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.Label LCount;
    private widget.ComboBox Menit;
    private javax.swing.JMenuItem MnAssessmentHHC;
    private widget.TextBox NIP;
    private widget.TextBox NamaPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.TextArea Psikospiritual;
    private widget.ScrollPane Scroll;
    private widget.TextBox Skor1;
    private widget.TextBox Skor2;
    private widget.TextBox Skor3;
    private widget.TextBox Skor4;
    private widget.TextBox Skor5;
    private widget.TextBox Skor6;
    private widget.TextBox Skor7;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TanggalRegistrasi;
    private widget.TextBox TglLahir;
    private widget.TextBox TotalSkor;
    private widget.Button btnPetugas;
    private widget.ComboBox cmbIntervensi;
    private widget.ComboBox cmbSkor1;
    private widget.ComboBox cmbSkor2;
    private widget.ComboBox cmbSkor3;
    private widget.ComboBox cmbSkor4;
    private widget.ComboBox cmbSkor5;
    private widget.ComboBox cmbSkor6;
    private widget.ComboBox cmbSkor7;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel16;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel217;
    private widget.Label jLabel218;
    private widget.Label jLabel219;
    private widget.Label jLabel220;
    private widget.Label jLabel221;
    private widget.Label jLabel222;
    private widget.Label jLabel223;
    private widget.Label jLabel224;
    private widget.Label jLabel225;
    private widget.Label jLabel226;
    private widget.Label jLabel227;
    private widget.Label jLabel228;
    private widget.Label jLabel229;
    private widget.Label jLabel230;
    private widget.Label jLabel231;
    private widget.Label jLabel232;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel4;
    private widget.Label jLabel57;
    private widget.Label jLabel58;
    private widget.Label jLabel59;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
    
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            if(TCari.getText().toString().trim().equals("")){
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, p.tanggal, "+
                    "p.skor1, p.nilai1, p.skor2, p.nilai2, p.skor3, p.nilai3, p.skor4, p.nilai4, p.skor5, p.nilai5, p.skor6, p.nilai6, p.skor7, p.nilai7, "+
                    "p.total_skor, p.keterangan_psikospiritual, p.intervensi, p.implementasi, p.evaluasi, p.nip, petugas.nama "+
                    "from penilaian_psikospiritual_hhc p inner join reg_periksa on p.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on p.nip=petugas.nip where "+
                    "p.tanggal between ? and ? order by p.tanggal");
            }else{
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, p.tanggal, "+
                    "p.skor1, p.nilai1, p.skor2, p.nilai2, p.skor3, p.nilai3, p.skor4, p.nilai4, p.skor5, p.nilai5, p.skor6, p.nilai6, p.skor7, p.nilai7, "+
                    "p.total_skor, p.keterangan_psikospiritual, p.intervensi, p.implementasi, p.evaluasi, p.nip, petugas.nama "+
                    "from penilaian_psikospiritual_hhc p inner join reg_periksa on p.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on p.nip=petugas.nip where "+
                    "p.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? "+
                    "or p.nip like ? or petugas.nama like ?) "+
                    "order by p.tanggal ");
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
                        rs.getString("tanggal"), rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), 
                        rs.getString("total_skor"), rs.getString("keterangan_psikospiritual"), rs.getString("intervensi"), rs.getString("implementasi"), rs.getString("evaluasi"), 
                        rs.getDate("tgl_lahir"), rs.getString("jk"), 
                        rs.getString("skor1"), rs.getString("nilai1"), rs.getString("skor2"), rs.getString("nilai2"), 
                        rs.getString("skor3"), rs.getString("nilai3"), rs.getString("skor4"), rs.getString("nilai4"), 
                        rs.getString("skor5"), rs.getString("nilai5"), rs.getString("skor6"), rs.getString("nilai6"), 
                        rs.getString("skor7"), rs.getString("nilai7"), rs.getString("nip"), rs.getString("nama")
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
    
    private void isCombo1(){
        if(cmbSkor1.getSelectedItem().equals("Kesembuhan / Ibadah")){
            Skor1.setText("4");
        }else if(cmbSkor1.getSelectedItem().equals("Sakit")){
            Skor1.setText("3");
        }else if(cmbSkor1.getSelectedItem().equals("Pekerjaan / Keluarga")){
            Skor1.setText("2");
        }else if(cmbSkor1.getSelectedItem().equals("Biaya Pengobatan")){
            Skor1.setText("1"); 
        }
    }
    
    private void isCombo2(){
        if(cmbSkor2.getSelectedItem().equals("Sakit sebagai cobaan, peringatan & rahmat")){
            Skor2.setText("4");
        }else if(cmbSkor2.getSelectedItem().equals("Sakit sebagai ujian alami")){
            Skor2.setText("3");
        }else if(cmbSkor2.getSelectedItem().equals("Sakit sebagai beban")){
            Skor2.setText("2");
        }else if(cmbSkor2.getSelectedItem().equals("Sakit sebagai hukuman / kutukan")){
            Skor2.setText("1"); 
        }
    }
    
    private void isCombo3(){
        if(cmbSkor3.getSelectedItem().equals("Pasrah / Ikhlas")){
            Skor3.setText("4");
        }else if(cmbSkor3.getSelectedItem().equals("Sedih")){
            Skor3.setText("3");
        }else if(cmbSkor3.getSelectedItem().equals("Takut / Khawatir")){
            Skor3.setText("2");
        }else if(cmbSkor3.getSelectedItem().equals("Marah / Tidak terima")){
            Skor3.setText("1"); 
        }
    }
    
    private void isCombo4(){
        if(cmbSkor4.getSelectedItem().equals("Sangat optimis akan sembuh")){
            Skor4.setText("4");
        }else if(cmbSkor4.getSelectedItem().equals("Optimis tapi ragu dengan kondisi sakit")){
            Skor4.setText("3");
        }else if(cmbSkor4.getSelectedItem().equals("Pasrah tanpa usaha")){
            Skor4.setText("2");
        }else if(cmbSkor4.getSelectedItem().equals("Pesimis / Putus asa")){
            Skor4.setText("1"); 
        }
    }
    
    private void isCombo5(){
        if(cmbSkor5.getSelectedItem().equals("Tetap sholat tepat waktu (sehat maupun sakit)")){
            Skor5.setText("4");
        }else if(cmbSkor5.getSelectedItem().equals("Sholat saat sehat / Tidak saat sakit")){
            Skor5.setText("3");
        }else if(cmbSkor5.getSelectedItem().equals("Kadang-kadang beribadah")){
            Skor5.setText("2");
        }else if(cmbSkor5.getSelectedItem().equals("Tidak pernah beribadah")){
            Skor5.setText("1"); 
        }
    }
    
    private void isCombo6(){
        if(cmbSkor6.getSelectedItem().equals("Allah SWT (melalui doa dan ikhtiar)")){
            Skor6.setText("4");
        }else if(cmbSkor6.getSelectedItem().equals("Dokter / Medis")){
            Skor6.setText("3");
        }else if(cmbSkor6.getSelectedItem().equals("Obat-obatan saja")){
            Skor6.setText("2");
        }else if(cmbSkor6.getSelectedItem().equals("Dukun / Alternatif yang tidak logis")){
            Skor6.setText("1"); 
        }
    }
    
    private void isCombo7(){
        if(cmbSkor7.getSelectedItem().equals("Melindungi penuh kasih sayang / Sumber kekuatan")){
            Skor7.setText("4");
        }else if(cmbSkor7.getSelectedItem().equals("Pedoman hidup")){
            Skor7.setText("3");
        }else if(cmbSkor7.getSelectedItem().equals("Kewajiban formalitas")){
            Skor7.setText("2");
        }else if(cmbSkor7.getSelectedItem().equals("Tidak memiliki makna khusus")){
            Skor7.setText("1"); 
        }
    }
    
    private void isjml(){
        if((!Skor1.getText().equals(""))&&(!Skor2.getText().equals(""))&&(!Skor3.getText().equals(""))&&(!Skor4.getText().equals(""))&&(!Skor5.getText().equals(""))&&(!Skor6.getText().equals(""))&&(!Skor7.getText().equals(""))){
            TotalSkor.setText(Valid.SetAngka2(
                    Double.parseDouble(Skor1.getText().trim())+
                    Double.parseDouble(Skor2.getText().trim())+
                    Double.parseDouble(Skor3.getText().trim())+
                    Double.parseDouble(Skor4.getText().trim())+
                    Double.parseDouble(Skor5.getText().trim())+
                    Double.parseDouble(Skor6.getText().trim())+
                    Double.parseDouble(Skor7.getText().trim())
            ));
        }
    }
    
    private void isHitung(){
        if(TotalSkor.getText().trim().isEmpty()){
            return; 
        }
        try {
            String skorText = TotalSkor.getText().trim().replace(",", ".");
            int total = (int) Math.round(Double.parseDouble(skorText));
            if (total >= 23 && total <= 28) {
                Psikospiritual.setText("Pasien memiliki penerimaan yang ikhlas, optimis, dan tetap menjaga ibadah.");
                Implementasi.setText("Memberikan apresiasi iman (husnuzan), motivasi kesabaran bagi keluarga, serta memastikan fasilitas ibadah dan aturan mahram terpenuhi.");
                Evaluasi.setText("Pasien tenang, rajin berdzikir, kooperatif, sholat terjaga, dan tercipta suasana islami di kamar.");
            } else if (total >= 17 && total <= 22) {
                Psikospiritual.setText("Pasien menerima kondisi sakitnya namun masih merasa sedih atau ragu akan masa depan.");
                Implementasi.setText("Edukasi sakit sebagai penggugur dosa, mengubah sedih menjadi doa, bimbingan sholat sesuai posisi, dan penjagaan privasi (tirai/selimut)");
                Evaluasi.setText("Pasien paham hikmah sakit, cemas berkurang, mulai mencoba sholat, dan komunikasi dengan petugas membaik.");
            } else if (total >= 11 && total <= 16) {
                Psikospiritual.setText("Pasien merasa sakit adalah beban, ibadah terganggu, dan cenderung pesimis.");
                Implementasi.setText("Pendampingan rohani intensif, mengajak istighfar, bimbingan tayamum/sholat untuk penenang hati, dan membatasi kunjungan (ikhtilath)");
                Evaluasi.setText("Pasien kembali ingat Allah, emosi stabil, bersedia sholat dengan bimbingan, dan merasa aman karena privasi terjaga.");
            } else if (total >= 7 && total <= 10) {
                Psikospiritual.setText("Pasien tidak terima dengan keadaan, merasa dihukum, dan kehilangan pegangan agama.");
                Implementasi.setText("Konseling spiritual khusus meluruskan pandangan takdir, mendengarkan keluh kesah, mengajarkan dzikir ringan, dan wajib didampingi mahram.");
                Evaluasi.setText("Pasien tidak lagi menyalahkan keadaan, sikap putus asa berkurang. mulai mau berdzikir, dan keluarga lebih sabar.");
            } else {
                Psikospiritual.setText("-");
                Implementasi.setText("-");
                Evaluasi.setText("-");
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat membaca total skor: " + e.getMessage());
        }
    }
    
    public void emptTeks() {
        Tanggal.setDate(new Date());
        
        cmbSkor1.setSelectedIndex(0);
        Skor1.setBackground(Color.WHITE);
        Skor1.setForeground(new Color(50,50,50));
        
        cmbSkor2.setSelectedIndex(0);
        Skor2.setBackground(Color.WHITE);
        Skor2.setForeground(new Color(50,50,50));
        
        cmbSkor3.setSelectedIndex(0);
        Skor3.setBackground(Color.WHITE);
        Skor3.setForeground(new Color(50,50,50));
        
        cmbSkor4.setSelectedIndex(0);
        Skor4.setBackground(Color.WHITE);
        Skor4.setForeground(new Color(50,50,50));
        
        cmbSkor5.setSelectedIndex(0);
        Skor5.setBackground(Color.WHITE);
        Skor5.setForeground(new Color(50,50,50));
        
        cmbSkor6.setSelectedIndex(0);
        Skor6.setBackground(Color.WHITE);
        Skor6.setForeground(new Color(50,50,50));
        
        cmbSkor7.setSelectedIndex(0);
        Skor7.setBackground(Color.WHITE);
        Skor7.setForeground(new Color(50,50,50));
        
        cmbIntervensi.setSelectedIndex(0);
        
        isCombo1();
        isCombo2();
        isCombo3();
        isCombo4();
        isCombo5();
        isCombo6();
        isCombo7();
        
        isjml();    
        cmbSkor1.requestFocus();
    }

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            Valid.SetTgl(Tanggal,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            Jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString().substring(11,13));
            Menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString().substring(14,16));
            Detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString().substring(17,19));
            
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString()); 
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            TotalSkor.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            Psikospiritual.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            cmbIntervensi.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            Implementasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            Evaluasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());

            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            JK.setText(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            
            cmbSkor1.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            Skor1.setText(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            cmbSkor2.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            Skor2.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            cmbSkor3.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            Skor3.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            cmbSkor4.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            Skor4.setText(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
            cmbSkor5.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),19).toString());
            Skor5.setText(tbObat.getValueAt(tbObat.getSelectedRow(),20).toString());
            cmbSkor6.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),21).toString());
            Skor6.setText(tbObat.getValueAt(tbObat.getSelectedRow(),22).toString());
            cmbSkor7.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),23).toString());
            Skor7.setText(tbObat.getValueAt(tbObat.getSelectedRow(),24).toString());
        }
    }
    
    private void isRawat() {
        try {
            ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi,reg_periksa.jam_reg "+
                    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,TNoRw.getText());
                rs=ps.executeQuery();
                if(rs.next()){
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    JK.setText(rs.getString("jk"));
                    TanggalRegistrasi.setText(rs.getString("tgl_registrasi")+" "+rs.getString("jam_reg"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
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
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }
    }
    
        public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
        ChkInput.setSelected(true);
        isForm();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            if(internalFrame1.getHeight()>648){
                ChkInput.setVisible(false);
                PanelInput.setPreferredSize(new Dimension(WIDTH,476));
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
        BtnSimpan.setEnabled(akses.getkamar_inap());
        BtnHapus.setEnabled(akses.getkamar_inap());
        BtnEdit.setEnabled(akses.getkamar_inap());
        BtnPrint.setEnabled(akses.getkamar_inap()); 
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
        
        if(TANGGALMUNDUR.equals("no")){
            if(!akses.getkode().equals("Admin Utama")){
                Tanggal.setEditable(false);
                Tanggal.setEnabled(false);
                ChkKejadian.setEnabled(false);
                Jam.setEnabled(false);
                Menit.setEnabled(false);
                Detik.setEnabled(false);
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

    private void simpan() {
        isHitung();
        if(Sequel.menyimpantf("penilaian_psikospiritual_hhc","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",22,new String[]{
            TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),
            cmbSkor1.getSelectedItem().toString(), Skor1.getText(), cmbSkor2.getSelectedItem().toString(), Skor2.getText(),
            cmbSkor3.getSelectedItem().toString(), Skor3.getText(), cmbSkor4.getSelectedItem().toString(), Skor4.getText(),
            cmbSkor5.getSelectedItem().toString(), Skor5.getText(), cmbSkor6.getSelectedItem().toString(), Skor6.getText(),
            cmbSkor7.getSelectedItem().toString(), Skor7.getText(), TotalSkor.getText(), Psikospiritual.getText(), 
            cmbIntervensi.getSelectedItem().toString(), Implementasi.getText(), Evaluasi.getText(), NIP.getText()
        })==true){
            tabMode.addRow(new Object[]{
                Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),
                TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TotalSkor.getText(), Psikospiritual.getText(), cmbIntervensi.getSelectedItem().toString(), Implementasi.getText(), Evaluasi.getText(), 
                TglLahir.getText(), JK.getText(), 
                cmbSkor1.getSelectedItem().toString(), Skor1.getText(), cmbSkor2.getSelectedItem().toString(), Skor2.getText(), 
                cmbSkor3.getSelectedItem().toString(), Skor3.getText(), cmbSkor4.getSelectedItem().toString(), Skor4.getText(), 
                cmbSkor5.getSelectedItem().toString(), Skor5.getText(), cmbSkor6.getSelectedItem().toString(), Skor6.getText(), 
                cmbSkor7.getSelectedItem().toString(), Skor7.getText(), NIP.getText(), NamaPetugas.getText()
            }); 
            emptTeks();
            LCount.setText(""+tabMode.getRowCount());
        } 
    }

    private void ganti() {
        if(Sequel.mengedittf("penilaian_psikospiritual_hhc","tanggal=? and no_rawat=?",
            "skor1=?, nilai1=?, skor2=?, nilai2=?, skor3=?, nilai3=?, skor4=?, nilai4=?, skor5=?, nilai5=?, skor6=?, nilai6=?, skor7=?, nilai7=?, total_skor=?, keterangan_psikospiritual=?, intervensi=?, implementasi=?, evaluasi=?, nip=?, tanggal=?, no_rawat=?",
            24, new String[]{
                cmbSkor1.getSelectedItem().toString(), Skor1.getText(), cmbSkor2.getSelectedItem().toString(), Skor2.getText(),
                cmbSkor3.getSelectedItem().toString(), Skor3.getText(), cmbSkor4.getSelectedItem().toString(), Skor4.getText(),
                cmbSkor5.getSelectedItem().toString(), Skor5.getText(), cmbSkor6.getSelectedItem().toString(), Skor6.getText(),
                cmbSkor7.getSelectedItem().toString(), Skor7.getText(), TotalSkor.getText(), Psikospiritual.getText(), 
                cmbIntervensi.getSelectedItem().toString(), Implementasi.getText(), Evaluasi.getText(), NIP.getText(),
                Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),
                TNoRw.getText(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), 
                tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString() 
            })==true){
            tampil();
            emptTeks();
        }
    }

    private void hapus() {
        if(Sequel.queryu2tf("delete from penilaian_psikospiritual_hhc where tanggal=? and no_rawat=?",2,new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), 
            tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()  
        })==true){
            tabMode.removeRow(tbObat.getSelectedRow());
            emptTeks();
            LCount.setText(""+tabMode.getRowCount());
        }else{
            JOptionPane.showMessageDialog(null,"Gagal menghapus..!!");
        }
    }
}
