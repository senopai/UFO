/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laporan;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author khanzamedia
 */
public class PanelDiagnosa extends widget.panelisi {
    private final DefaultTableModel TabModeDiagnosaPasien,tabModeDiagnosa,tabModeProsedur,TabModeTindakanPasien;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement pspenyakit,psdiagnosapasien,psprosedur,pstindakanpasien;
    private ResultSet rs;
    private int jml=0,i=0,index=0;
    private String[] kode,nama,ciripny,keterangan,kategori,cirium,kode2,panjang,pendek,validcode,accpdx,asterisk,im,urut,multy;
    private boolean[] pilih;
    public String norawat="",status="",norm="",tanggal1="",tanggal2="",keyword="";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    /**
     * Creates new form panelDiagnosa
     */
    public PanelDiagnosa() {
        initComponents();
        TabModeDiagnosaPasien=new DefaultTableModel(null,new Object[]{
                "P","Tgl.Rawat","No.Rawat","No.R.M.","Nama Pasien","Kode","Nama Penyakit",
                "Status","Kasus","Urut"
            }){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class,java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbDiagnosaPasien.setModel(TabModeDiagnosaPasien);
        tbDiagnosaPasien.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbDiagnosaPasien.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 10; i++) {
            TableColumn column = tbDiagnosaPasien.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(110);
            }else if(i==3){
                column.setPreferredWidth(70);
            }else if(i==4){
                column.setPreferredWidth(160);
            }else if(i==5){
                column.setPreferredWidth(50);
            }else if(i==6){
                column.setPreferredWidth(350);
            }else if(i==7){
                column.setPreferredWidth(50);
            }else if(i==8){
                column.setPreferredWidth(50);
            }else if(i==9){
                column.setPreferredWidth(30);
            }
        }
        tbDiagnosaPasien.setDefaultRenderer(Object.class, new WarnaTable());
        
        tabModeDiagnosa=new DefaultTableModel(null,new Object[]{
            "P","Kode","Nama Penyakit","Ciri-ciri Penyakit","Keterangan","Kategori","Ciri-ciri Umum","VC","AP","Ast","IM","Urut"}){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbDiagnosa.setModel(tabModeDiagnosa);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbDiagnosa.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbDiagnosa.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i= 0; i < 12; i++) {
            TableColumn column = tbDiagnosa.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(40);
            }else if(i==2){
                column.setPreferredWidth(215);
            }else if(i==3){
                column.setPreferredWidth(130);
            }else if(i==4){
                column.setPreferredWidth(80);
            }else if(i==5){
                column.setPreferredWidth(80);
            }else if(i==6){
                column.setPreferredWidth(85);
            }else if(i==7){
                column.setPreferredWidth(25);
            }else if(i==8){
                column.setPreferredWidth(25);
            }else if(i==9){
                column.setPreferredWidth(27);
            }else if(i==10){
                column.setPreferredWidth(25);
            }else if(i==11){
                column.setPreferredWidth(30);
            }
        }
        tbDiagnosa.setDefaultRenderer(Object.class, new WarnaTable());
        
        tabModeProsedur=new DefaultTableModel(null,new Object[]{
            "P","Kode","Deskripsi Panjang","Deskripsi Pendek","VC","AP","IM","Urut","Jml"}){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==8)) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbProsedur.setModel(tabModeProsedur);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbProsedur.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbProsedur.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 9; i++) {
            TableColumn column = tbProsedur.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(50);
            }else if(i==2){
                column.setPreferredWidth(372);
            }else if(i==3){
                column.setPreferredWidth(210);
            }else if(i==4){
                column.setPreferredWidth(25);
            }else if(i==5){
                column.setPreferredWidth(25);
            }else if(i==6){
                column.setPreferredWidth(25);
            }else if(i==7){
                column.setPreferredWidth(30);
            }else if(i==8){
                column.setPreferredWidth(25);
            }
        }
        tbProsedur.setDefaultRenderer(Object.class, new WarnaTable());
        
        TabModeTindakanPasien=new DefaultTableModel(null,new Object[]{
                "P","Tgl.Rawat","No.Rawat","No.R.M.","Nama Pasien","Kode","Nama Prosedur","Status","Urut","Jml"
            }){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbTindakanPasien.setModel(TabModeTindakanPasien);
        tbTindakanPasien.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbTindakanPasien.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 10; i++) {
            TableColumn column = tbTindakanPasien.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(110);
            }else if(i==3){
                column.setPreferredWidth(70);
            }else if(i==4){
                column.setPreferredWidth(160);
            }else if(i==5){
                column.setPreferredWidth(50);
            }else if(i==6){
                column.setPreferredWidth(300);
            }else if(i==7){
                column.setPreferredWidth(50);
            }else if(i==8){
                column.setPreferredWidth(30);
            }else if(i==9){
                column.setPreferredWidth(25);
            }
        }
        tbTindakanPasien.setDefaultRenderer(Object.class, new WarnaTable());
           
        Diagnosa.setDocument(new batasInput((byte)100).getKata(Diagnosa));
        Prosedur.setDocument(new batasInput((byte)100).getKata(Prosedur));
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            Diagnosa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(Diagnosa.getText().length()>2){
                        runBackground(() ->tampildiagnosa());
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(Diagnosa.getText().length()>2){
                        runBackground(() ->tampildiagnosa());
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(Diagnosa.getText().length()>2){
                        runBackground(() ->tampildiagnosa());
                    }
                }
            });
        } 
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            Prosedur.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(Prosedur.getText().length()>2){
                        runBackground(() ->tampilprosedure());
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(Prosedur.getText().length()>2){
                        runBackground(() ->tampilprosedure());
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(Prosedur.getText().length()>2){
                        runBackground(() ->tampilprosedure());
                    }
                }
            });
        } 
        siapkanTombolSalin(); // [FIX-SALINDX]
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnStatusBaru = new widget.MenuItem();
        MnStatusLama = new widget.MenuItem();
        TabRawat = new widget.TabPane();
        ScrollInput = new widget.ScrollPane();
        FormData = new widget.PanelBiasa();
        jLabel13 = new widget.Label();
        Diagnosa = new widget.TextBox();
        BtnCariPenyakit = new widget.Button();
        btnTambahPenyakit = new widget.Button();
        Scroll1 = new widget.ScrollPane();
        tbDiagnosa = new widget.Table();
        jLabel15 = new widget.Label();
        Prosedur = new widget.TextBox();
        btnTambahProsedur = new widget.Button();
        BtnCariProsedur = new widget.Button();
        Scroll2 = new widget.ScrollPane();
        tbProsedur = new widget.Table();
        internalFrame2 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbDiagnosaPasien = new widget.Table();
        internalFrame3 = new widget.InternalFrame();
        Scroll3 = new widget.ScrollPane();
        tbTindakanPasien = new widget.Table();

        MnStatusBaru.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnStatusBaru.setForeground(new java.awt.Color(50, 50, 50));
        MnStatusBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnStatusBaru.setText("Status Penyakit Baru");
        MnStatusBaru.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnStatusBaru.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnStatusBaru.setPreferredSize(new java.awt.Dimension(170, 26));
        MnStatusBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnStatusBaruActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnStatusBaru);

        MnStatusLama.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnStatusLama.setForeground(new java.awt.Color(50, 50, 50));
        MnStatusLama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnStatusLama.setText("Status Penyakit Lama");
        MnStatusLama.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnStatusLama.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnStatusLama.setIconTextGap(5);
        MnStatusLama.setPreferredSize(new java.awt.Dimension(170, 26));
        MnStatusLama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnStatusLamaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnStatusLama);

        setLayout(new java.awt.BorderLayout(1, 1));

        TabRawat.setBackground(new java.awt.Color(255, 255, 253));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        ScrollInput.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        ScrollInput.setOpaque(true);

        FormData.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        FormData.setPreferredSize(new java.awt.Dimension(865, 417));
        FormData.setLayout(null);

        jLabel13.setText("Diagnosa :");
        FormData.add(jLabel13);
        jLabel13.setBounds(0, 10, 68, 23);

        Diagnosa.setHighlighter(null);
        Diagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaKeyPressed(evt);
            }
        });
        FormData.add(Diagnosa);
        Diagnosa.setBounds(71, 10, 687, 23);

        BtnCariPenyakit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariPenyakit.setMnemonic('1');
        BtnCariPenyakit.setToolTipText("Alt+1");
        BtnCariPenyakit.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariPenyakit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariPenyakitActionPerformed(evt);
            }
        });
        FormData.add(BtnCariPenyakit);
        BtnCariPenyakit.setBounds(761, 10, 28, 23);

        btnTambahPenyakit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        btnTambahPenyakit.setMnemonic('2');
        btnTambahPenyakit.setToolTipText("Alt+2");
        btnTambahPenyakit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahPenyakitActionPerformed(evt);
            }
        });
        FormData.add(btnTambahPenyakit);
        btnTambahPenyakit.setBounds(792, 10, 28, 23);

        Scroll1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll1.setOpaque(true);

        tbDiagnosa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDiagnosaMouseClicked(evt);
            }
        });
        tbDiagnosa.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbDiagnosaPropertyChange(evt);
            }
        });
        tbDiagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbDiagnosaKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbDiagnosa);

        FormData.add(Scroll1);
        Scroll1.setBounds(11, 36, 810, 165);

        jLabel15.setText("Prosedur :");
        FormData.add(jLabel15);
        jLabel15.setBounds(0, 211, 68, 23);

        Prosedur.setHighlighter(null);
        Prosedur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProsedurKeyPressed(evt);
            }
        });
        FormData.add(Prosedur);
        Prosedur.setBounds(71, 211, 687, 23);

        btnTambahProsedur.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        btnTambahProsedur.setMnemonic('2');
        btnTambahProsedur.setToolTipText("Alt+2");
        btnTambahProsedur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahProsedurActionPerformed(evt);
            }
        });
        FormData.add(btnTambahProsedur);
        btnTambahProsedur.setBounds(792, 211, 28, 23);

        BtnCariProsedur.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariProsedur.setMnemonic('1');
        BtnCariProsedur.setToolTipText("Alt+1");
        BtnCariProsedur.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariProsedur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariProsedurActionPerformed(evt);
            }
        });
        FormData.add(BtnCariProsedur);
        BtnCariProsedur.setBounds(761, 211, 28, 23);

        Scroll2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll2.setOpaque(true);

        tbProsedur.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbProsedur.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProsedurMouseClicked(evt);
            }
        });
        tbProsedur.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbProsedurPropertyChange(evt);
            }
        });
        tbProsedur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbProsedurKeyPressed(evt);
            }
        });
        Scroll2.setViewportView(tbProsedur);

        FormData.add(Scroll2);
        Scroll2.setBounds(11, 237, 810, 165);

        ScrollInput.setViewportView(FormData);

        TabRawat.addTab("Input Data", ScrollInput);

        internalFrame2.setBorder(null);
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll.setOpaque(true);

        tbDiagnosaPasien.setAutoCreateRowSorter(false);
        tbDiagnosaPasien.setComponentPopupMenu(jPopupMenu1);
        Scroll.setViewportView(tbDiagnosaPasien);

        internalFrame2.add(Scroll, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Data Diagnosa", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll3.setOpaque(true);

        tbTindakanPasien.setAutoCreateRowSorter(false);
        tbTindakanPasien.setComponentPopupMenu(jPopupMenu1);
        Scroll3.setViewportView(tbTindakanPasien);

        internalFrame3.add(Scroll3, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Data Prosedur", internalFrame3);

        add(TabRawat, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            runBackground(() ->tampildiagnosa());            
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            if(akses.getpenyakit()==true){
                btnTambahPenyakitActionPerformed(null);
            }
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbDiagnosa.requestFocus();
        }
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void BtnCariPenyakitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariPenyakitActionPerformed
        runBackground(() ->tampildiagnosa());
    }//GEN-LAST:event_BtnCariPenyakitActionPerformed

    private void btnTambahPenyakitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahPenyakitActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgPenyakit tariflab=new DlgPenyakit(null,false);
        tariflab.emptTeks();
        tariflab.isCek();
        tariflab.setSize(this.getWidth(),this.getHeight()+100);
        tariflab.setLocationRelativeTo(this);
        tariflab.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnTambahPenyakitActionPerformed

    private void ProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            runBackground(() ->tampilprosedure());
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            if(akses.geticd9()==true){
                btnTambahProsedurActionPerformed(null);
            }
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbProsedur.requestFocus();
        }
    }//GEN-LAST:event_ProsedurKeyPressed

    private void btnTambahProsedurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahProsedurActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgICD9 tariflab=new DlgICD9(null,false);
        tariflab.emptTeks();
        tariflab.isCek();
        tariflab.setSize(this.getWidth(),this.getHeight()+100);
        tariflab.setLocationRelativeTo(this);
        tariflab.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnTambahProsedurActionPerformed

    private void BtnCariProsedurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariProsedurActionPerformed
        runBackground(() ->tampilprosedure());
    }//GEN-LAST:event_BtnCariProsedurActionPerformed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        pilihTab();
    }//GEN-LAST:event_TabRawatMouseClicked

    private void MnStatusBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnStatusBaruActionPerformed
        if(norawat.equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu pasien...!!!");
        }else{
            Sequel.queryu2("update diagnosa_pasien set status_penyakit='Baru' where no_rawat=? and kd_penyakit=?",2,new String[]{
                tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(),2).toString(),tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(),5).toString()
            });
            runBackground(() ->tampil());
        }
    }//GEN-LAST:event_MnStatusBaruActionPerformed

    private void MnStatusLamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnStatusLamaActionPerformed
        if(norawat.equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu pasien...!!!");
        }else{
            Sequel.queryu2("update diagnosa_pasien set status_penyakit='Lama' where no_rawat=? and kd_penyakit=?",2,new String[]{
                tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(),2).toString(),tbDiagnosaPasien.getValueAt(tbDiagnosaPasien.getSelectedRow(),5).toString()
            });
            runBackground(() ->tampil());
        }
    }//GEN-LAST:event_MnStatusLamaActionPerformed

    private void tbDiagnosaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDiagnosaMouseClicked
        if(tabModeDiagnosa.getRowCount()!=0){
            try {
                if(tbDiagnosa.getSelectedRow()!= -1){
                    if(tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),0).toString().equals("true")&&tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),7).toString().equals("0")){
                        tbDiagnosa.setValueAt(false,tbDiagnosa.getSelectedRow(),0);
                        tbDiagnosa.setValueAt("",tbDiagnosa.getSelectedRow(),11);
                        JOptionPane.showMessageDialog(null,"Maaf, kode diagnosa "+tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),1).toString()+" valid code 0. Hanya berfungsi sebagai header..!!");
                    }else if(tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),0).toString().equals("true")&&tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),7).toString().equals("1")){
                        if(tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),8).toString().equals("N")&&tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),11).toString().equals("1")){
                            tbDiagnosa.setValueAt(false,tbDiagnosa.getSelectedRow(),0);
                            tbDiagnosa.setValueAt("",tbDiagnosa.getSelectedRow(),11);
                            JOptionPane.showMessageDialog(null,"Maaf, kode diagnosa "+tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),1).toString()+" Accpdx N. Tidak bisa menjadi diagnosa utama..!!");
                        }
                    }
                }
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbDiagnosaMouseClicked

    private void tbDiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbDiagnosaKeyPressed
        if(tabModeDiagnosa.getRowCount()!=0){
            try {
                if(tbDiagnosa.getSelectedRow()!= -1){
                    if(tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),0).toString().equals("true")&&tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),7).toString().equals("0")){
                        tbDiagnosa.setValueAt(false,tbDiagnosa.getSelectedRow(),0);
                        tbDiagnosa.setValueAt("",tbDiagnosa.getSelectedRow(),11);
                        JOptionPane.showMessageDialog(null,"Maaf, kode diagnosa "+tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),1).toString()+" valid code 0. Hanya berfungsi sebagai header..!!");
                    }else if(tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),0).toString().equals("true")&&tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),7).toString().equals("1")){
                        if(tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),8).toString().equals("N")&&tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),11).toString().equals("1")){
                            tbDiagnosa.setValueAt(false,tbDiagnosa.getSelectedRow(),0);
                            tbDiagnosa.setValueAt("",tbDiagnosa.getSelectedRow(),11);
                            JOptionPane.showMessageDialog(null,"Maaf, kode diagnosa "+tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(),1).toString()+" Accpdx N. Tidak bisa menjadi diagnosa utama..!!");
                        }
                    }
                }
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbDiagnosaKeyPressed

    private void tbDiagnosaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbDiagnosaPropertyChange
        i=1;
        for(int z=0;z<tbDiagnosa.getRowCount();z++){ 
            if(tbDiagnosa.getValueAt(z,0).toString().equals("true")){
                tbDiagnosa.setValueAt(i,z,11);
                if(tbDiagnosa.getValueAt(z,0).toString().equals("true")&&tbDiagnosa.getValueAt(z,7).toString().equals("0")){
                    tbDiagnosa.setValueAt(false,z,0);
                    tbDiagnosa.setValueAt("",z,11);
                    JOptionPane.showMessageDialog(null,"Maaf, kode diagnosa "+tbDiagnosa.getValueAt(z,1).toString()+" valid code 0. Hanya berfungsi sebagai header..!!");
                }else if(tbDiagnosa.getValueAt(z,0).toString().equals("true")&&tbDiagnosa.getValueAt(z,7).toString().equals("1")){
                    if(tbDiagnosa.getValueAt(z,8).toString().equals("N")&&tbDiagnosa.getValueAt(z,11).toString().equals("1")){
                        tbDiagnosa.setValueAt(false,z,0);
                        tbDiagnosa.setValueAt("",z,11);
                        JOptionPane.showMessageDialog(null,"Maaf, kode diagnosa "+tbDiagnosa.getValueAt(z,1).toString()+" Accpdx N. Tidak bisa menjadi diagnosa utama..!!");
                    }else{
                        i++;
                    }
                }
            }else{
                tbDiagnosa.setValueAt("",z,11);
            }
        }
    }//GEN-LAST:event_tbDiagnosaPropertyChange

    private void tbProsedurMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProsedurMouseClicked
        if(tabModeProsedur.getRowCount()!=0){
            try {
                if(tbProsedur.getSelectedRow()!= -1){
                    if(tbProsedur.getValueAt(tbProsedur.getSelectedRow(),0).toString().equals("true")){
                        if(tbProsedur.getValueAt(tbProsedur.getSelectedRow(),4).toString().equals("0")){
                            tbProsedur.setValueAt(false,tbProsedur.getSelectedRow(),0);
                            tbProsedur.setValueAt("",tbProsedur.getSelectedRow(),7);
                            tbProsedur.setValueAt("",tbProsedur.getSelectedRow(),8);
                            JOptionPane.showMessageDialog(null,"Maaf, kode prosedur "+tbProsedur.getValueAt(tbProsedur.getSelectedRow(),1).toString()+" valid code 0. Hanya berfungsi sebagai header..!!");
                        }else if(tbProsedur.getValueAt(tbProsedur.getSelectedRow(),4).toString().equals("1")){
                            if(tbProsedur.getValueAt(tbProsedur.getSelectedRow(),5).toString().equals("N")&&tbProsedur.getValueAt(tbProsedur.getSelectedRow(),7).toString().equals("1")){
                                tbProsedur.setValueAt(false,tbProsedur.getSelectedRow(),0);
                                tbProsedur.setValueAt("",tbProsedur.getSelectedRow(),7);
                                tbProsedur.setValueAt("",tbProsedur.getSelectedRow(),8);
                                JOptionPane.showMessageDialog(null,"Maaf, kode prosedur "+tbProsedur.getValueAt(tbProsedur.getSelectedRow(),1).toString()+" Accpdx N. Tidak bisa menjadi prosedur utama..!!");
                            }
                        }
                    }
                }
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbProsedurMouseClicked

    private void tbProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbProsedurKeyPressed
        if(tabModeProsedur.getRowCount()!=0){
            try {
                if(tbProsedur.getSelectedRow()!= -1){
                    if(tbProsedur.getValueAt(tbProsedur.getSelectedRow(),0).toString().equals("true")){
                        if(tbProsedur.getValueAt(tbProsedur.getSelectedRow(),4).toString().equals("0")){
                            tbProsedur.setValueAt(false,tbProsedur.getSelectedRow(),0);
                            tbProsedur.setValueAt("",tbProsedur.getSelectedRow(),7);
                            tbProsedur.setValueAt("",tbProsedur.getSelectedRow(),8);
                            JOptionPane.showMessageDialog(null,"Maaf, kode prosedur "+tbProsedur.getValueAt(tbProsedur.getSelectedRow(),1).toString()+" valid code 0. Hanya berfungsi sebagai header..!!");
                        }else if(tbProsedur.getValueAt(tbProsedur.getSelectedRow(),4).toString().equals("1")){
                            if(tbProsedur.getValueAt(tbProsedur.getSelectedRow(),5).toString().equals("N")&&tbProsedur.getValueAt(tbProsedur.getSelectedRow(),7).toString().equals("1")){
                                tbProsedur.setValueAt(false,tbProsedur.getSelectedRow(),0);
                                tbProsedur.setValueAt("",tbProsedur.getSelectedRow(),7);
                                tbProsedur.setValueAt("",tbProsedur.getSelectedRow(),8);
                                JOptionPane.showMessageDialog(null,"Maaf, kode prosedur "+tbProsedur.getValueAt(tbProsedur.getSelectedRow(),1).toString()+" Accpdx N. Tidak bisa menjadi prosedur utama..!!");
                            }
                        }
                    }
                }
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbProsedurKeyPressed

    private void tbProsedurPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbProsedurPropertyChange
        i=1;
        for(int z=0;z<tbProsedur.getRowCount();z++){ 
            if(tbProsedur.getValueAt(z,0).toString().equals("true")){
                tbProsedur.setValueAt(i,z,7);
                if(tbProsedur.getValueAt(z,8).toString().equals("")){
                    tbProsedur.setValueAt("1",z,8);
                }
                if(tbProsedur.getValueAt(z,0).toString().equals("true")&&tbProsedur.getValueAt(z,4).toString().equals("0")){
                    tbProsedur.setValueAt(false,z,0);
                    tbProsedur.setValueAt("",z,7);
                    tbProsedur.setValueAt("",z,8);
                    JOptionPane.showMessageDialog(null,"Maaf, kode prosedur "+tbProsedur.getValueAt(z,1).toString()+" valid code 0. Hanya berfungsi sebagai header..!!");
                }else if(tbProsedur.getValueAt(z,0).toString().equals("true")&&tbProsedur.getValueAt(z,4).toString().equals("1")){
                    if(tbProsedur.getValueAt(z,5).toString().equals("N")&&tbProsedur.getValueAt(z,7).toString().equals("1")){
                        tbProsedur.setValueAt(false,z,0);
                        tbProsedur.setValueAt("",z,7);
                        tbProsedur.setValueAt("",z,8);
                        JOptionPane.showMessageDialog(null,"Maaf, kode prosedur "+tbProsedur.getValueAt(z,1).toString()+" Accpdx N. Tidak bisa menjadi prosedur utama..!!");
                    }else{
                        i++;
                    }
                }
            }else{
                tbProsedur.setValueAt("",z,7);
                tbProsedur.setValueAt("",z,8);
            }
        }
    }//GEN-LAST:event_tbProsedurPropertyChange


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnCariPenyakit;
    private widget.Button BtnCariProsedur;
    public widget.TextBox Diagnosa;
    public widget.PanelBiasa FormData;
    private widget.MenuItem MnStatusBaru;
    private widget.MenuItem MnStatusLama;
    private widget.TextBox Prosedur;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.ScrollPane Scroll3;
    public widget.ScrollPane ScrollInput;
    public widget.TabPane TabRawat;
    public widget.Button btnTambahPenyakit;
    public widget.Button btnTambahProsedur;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel13;
    private widget.Label jLabel15;
    private javax.swing.JPopupMenu jPopupMenu1;
    public widget.Table tbDiagnosa;
    public widget.Table tbDiagnosaPasien;
    public widget.Table tbProsedur;
    public widget.Table tbTindakanPasien;
    // End of variables declaration//GEN-END:variables
    private void tampil() {
        Valid.tabelKosong(TabModeDiagnosaPasien);
        try{            
            psdiagnosapasien=koneksi.prepareStatement(
                "select reg_periksa.tgl_registrasi,diagnosa_pasien.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                "diagnosa_pasien.kd_penyakit,penyakit.nm_penyakit,diagnosa_pasien.status,diagnosa_pasien.status_penyakit,"+
                "diagnosa_pasien.prioritas from diagnosa_pasien inner join reg_periksa on diagnosa_pasien.no_rawat=reg_periksa.no_rawat "+
                "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join penyakit on diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "+
                "where reg_periksa.tgl_registrasi between ? and ? "+(norm.trim().equals("")?"":"and reg_periksa.no_rkm_medis=? ")+
                (keyword.trim().equals("")?"":"and (diagnosa_pasien.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "+
                "pasien.nm_pasien like ? or diagnosa_pasien.kd_penyakit like ? or penyakit.nm_penyakit like ? or "+
                "diagnosa_pasien.status_penyakit like ? or diagnosa_pasien.status like ?)")+
                "order by reg_periksa.tgl_registrasi,diagnosa_pasien.prioritas "
            );
            try {
                i=3;
                psdiagnosapasien.setString(1,tanggal1);
                psdiagnosapasien.setString(2,tanggal2);
                if(!norm.trim().equals("")){
                    psdiagnosapasien.setString(i,norm);
                    i++;
                }
                if(!keyword.trim().equals("")){
                    psdiagnosapasien.setString(i,"%"+keyword+"%");  
                    i++;       
                    psdiagnosapasien.setString(i,"%"+keyword+"%");  
                    i++;       
                    psdiagnosapasien.setString(i,"%"+keyword+"%"); 
                    i++;        
                    psdiagnosapasien.setString(i,"%"+keyword+"%");
                    i++;         
                    psdiagnosapasien.setString(i,"%"+keyword+"%");  
                    i++;       
                    psdiagnosapasien.setString(i,"%"+keyword+"%"); 
                    i++;         
                    psdiagnosapasien.setString(i,"%"+keyword+"%");   
                }
                    
                rs=psdiagnosapasien.executeQuery();
                while(rs.next()){
                    TabModeDiagnosaPasien.addRow(new Object[]{
                        false,rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)
                    });
                }            
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(psdiagnosapasien!=null){
                    psdiagnosapasien.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public int getRecord(){
        switch (TabRawat.getSelectedIndex()) {
            case 0:
                i=0;
                break;
            case 1:
                i=tbDiagnosaPasien.getRowCount();
                break;
            case 2:
                i=tbTindakanPasien.getRowCount();
                break;
            default:
                break;
        }
        return i;
    }
    
    private void tampildiagnosa() {
        try{
            jml=0;
            for(i=0;i<tbDiagnosa.getRowCount();i++){
                if(tbDiagnosa.getValueAt(i,0).toString().equals("true")){
                    jml++;
                }
            }

            pilih=new boolean[jml];
            kode=new String[jml];
            nama=new String[jml];
            ciripny=new String[jml];
            keterangan=new String[jml];
            kategori=new String[jml];
            cirium=new String[jml];
            validcode=new String[jml];
            accpdx=new String[jml];
            asterisk=new String[jml];
            im=new String[jml];
            urut=new String[jml];

            index=0; 
            for(i=0;i<tbDiagnosa.getRowCount();i++){
                if(tbDiagnosa.getValueAt(i,0).toString().equals("true")){
                    pilih[index]=true;
                    kode[index]=tbDiagnosa.getValueAt(i,1).toString();
                    nama[index]=tbDiagnosa.getValueAt(i,2).toString();
                    ciripny[index]=tbDiagnosa.getValueAt(i,3).toString();
                    keterangan[index]=tbDiagnosa.getValueAt(i,4).toString();
                    kategori[index]=tbDiagnosa.getValueAt(i,5).toString();
                    cirium[index]=tbDiagnosa.getValueAt(i,6).toString();
                    validcode[index]=tbDiagnosa.getValueAt(i,7).toString();
                    accpdx[index]=tbDiagnosa.getValueAt(i,8).toString();
                    asterisk[index]=tbDiagnosa.getValueAt(i,9).toString();
                    im[index]=tbDiagnosa.getValueAt(i,10).toString();
                    urut[index]=tbDiagnosa.getValueAt(i,11).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeDiagnosa);
            for(i=0;i<jml;i++){
                tabModeDiagnosa.addRow(new Object[] {pilih[i],kode[i],nama[i],ciripny[i],keterangan[i],kategori[i],cirium[i],validcode[i],accpdx[i],asterisk[i],im[i],urut[i]});
            }  
            
            pilih=null;
            kode=null;
            nama=null;
            ciripny=null;
            keterangan=null;
            kategori=null;
            cirium=null;
            validcode=null;
            accpdx=null;
            asterisk=null;
            im=null;
            urut=null;

            pspenyakit=koneksi.prepareStatement(
                    "select penyakit.kd_penyakit,penyakit.nm_penyakit,penyakit.ciri_ciri,penyakit.keterangan,kategori_penyakit.nm_kategori,"+
                    "kategori_penyakit.ciri_umum,penyakit.validcode,penyakit.accpdx,penyakit.asterisk,penyakit.im from kategori_penyakit "+
                    "inner join penyakit on penyakit.kd_ktg=kategori_penyakit.kd_ktg "+
                    (Diagnosa.getText().trim().equals("")?"":"where penyakit.kd_penyakit like ? or "+
                    "penyakit.nm_penyakit like ? or penyakit.ciri_ciri like ? or penyakit.keterangan like ? or "+
                    "kategori_penyakit.nm_kategori like ? or kategori_penyakit.ciri_umum like ? ")+
                    "order by penyakit.kd_penyakit  LIMIT 1000");
            try {
                if(!Diagnosa.getText().trim().equals("")){
                    pspenyakit.setString(1,"%"+Diagnosa.getText().trim()+"%");
                    pspenyakit.setString(2,"%"+Diagnosa.getText().trim()+"%");
                    pspenyakit.setString(3,"%"+Diagnosa.getText().trim()+"%");
                    pspenyakit.setString(4,"%"+Diagnosa.getText().trim()+"%");
                    pspenyakit.setString(5,"%"+Diagnosa.getText().trim()+"%");
                    pspenyakit.setString(6,"%"+Diagnosa.getText().trim()+"%");  
                }
                rs=pspenyakit.executeQuery();
                while(rs.next()){
                    tabModeDiagnosa.addRow(new Object[]{
                        false,rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
                        rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),""
                    });
                } 
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(pspenyakit!=null){
                    pspenyakit.close();
                }
            }           
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
    
    private void tampilprosedure() {
        try{
            jml=0;
            for(i=0;i<tbProsedur.getRowCount();i++){
                if(tbProsedur.getValueAt(i,0).toString().equals("true")){
                    jml++;
                }
            }

            pilih=new boolean[jml];
            kode2=new String[jml];
            panjang=new String[jml];
            pendek=new String[jml];
            validcode=new String[jml];
            accpdx=new String[jml];
            im=new String[jml];
            urut=new String[jml];
            multy=new String[jml];
            index=0; 
            for(i=0;i<tbProsedur.getRowCount();i++){
                if(tbProsedur.getValueAt(i,0).toString().equals("true")){
                    pilih[index]=true;
                    kode2[index]=tbProsedur.getValueAt(i,1).toString();
                    panjang[index]=tbProsedur.getValueAt(i,2).toString();
                    pendek[index]=tbProsedur.getValueAt(i,3).toString();
                    validcode[index]=tbProsedur.getValueAt(i,4).toString();
                    accpdx[index]=tbProsedur.getValueAt(i,5).toString();
                    im[index]=tbProsedur.getValueAt(i,6).toString();
                    urut[index]=tbProsedur.getValueAt(i,7).toString();
                    multy[index]=tbProsedur.getValueAt(i,8).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeProsedur);
            for(i=0;i<jml;i++){
                tabModeProsedur.addRow(new Object[] {pilih[i],kode2[i],panjang[i],pendek[i],validcode[i],accpdx[i],im[i],urut[i],multy[i]});
            }
            
            pilih=null;
            kode2=null;
            panjang=null;
            pendek=null;
            validcode=null;
            accpdx=null;
            im=null;
            urut=null;
            multy=null;
            
            psprosedur=koneksi.prepareStatement(
                    "select * from icd9 "+(Prosedur.getText().trim().equals("")?"":"where kode like ? or deskripsi_panjang like ? or  deskripsi_pendek like ?")+" order by kode");
            try{
                if(!Prosedur.getText().trim().equals("")){
                    psprosedur.setString(1,"%"+Prosedur.getText().trim()+"%");
                    psprosedur.setString(2,"%"+Prosedur.getText().trim()+"%");
                    psprosedur.setString(3,"%"+Prosedur.getText().trim()+"%");
                }
                    
                rs=psprosedur.executeQuery();
                while(rs.next()){
                    tabModeProsedur.addRow(new Object[]{
                        false,rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),"",""
                    });
                }
            }catch(Exception ex){
                System.out.println(ex);
            }finally{
                if(rs != null){
                    rs.close();
                }
                if(psprosedur != null){
                    psprosedur.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
    
    private void tampil2() {
        Valid.tabelKosong(TabModeTindakanPasien);
        try{            
            pstindakanpasien=koneksi.prepareStatement(
                    "select reg_periksa.tgl_registrasi,prosedur_pasien.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                    "prosedur_pasien.kode,icd9.deskripsi_panjang,prosedur_pasien.status,prosedur_pasien.prioritas,prosedur_pasien.jumlah "+
                    "from prosedur_pasien inner join reg_periksa on prosedur_pasien.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join icd9 on prosedur_pasien.kode=icd9.kode "+
                    "where reg_periksa.tgl_registrasi between ? and ? "+(norm.trim().equals("")?"":"and reg_periksa.no_rkm_medis=? ")+
                    (keyword.trim().equals("")?"":"and (prosedur_pasien.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "+
                    "pasien.nm_pasien like ? or prosedur_pasien.kode like ? or icd9.deskripsi_panjang like ? or "+
                    "prosedur_pasien.status like ?) ")+"order by reg_periksa.tgl_registrasi,prosedur_pasien.prioritas ");
            try {
                i=3;
                pstindakanpasien.setString(1,tanggal1);
                pstindakanpasien.setString(2,tanggal2);
                if(!norm.trim().equals("")){
                    pstindakanpasien.setString(i,norm);
                    i++;
                }
                     
                if(!keyword.trim().equals("")){
                    pstindakanpasien.setString(i,"%"+keyword+"%");  
                    i++;
                    pstindakanpasien.setString(i,"%"+keyword+"%"); 
                    i++;
                    pstindakanpasien.setString(i,"%"+keyword+"%");
                    i++;
                    pstindakanpasien.setString(i,"%"+keyword+"%"); 
                    i++;
                    pstindakanpasien.setString(i,"%"+keyword+"%");  
                    i++;
                    pstindakanpasien.setString(i,"%"+keyword+"%");  
                }
                     
                rs=pstindakanpasien.executeQuery();
                while(rs.next()){
                    TabModeTindakanPasien.addRow(new Object[]{
                        false,rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)
                    });
                }            
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(pstindakanpasien!=null){
                    pstindakanpasien.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void setRM(String norawat,String norm,String tanggal1,String tanggal2,String status,String keyword){
        this.norawat=norawat;
        this.norm=norm;
        this.tanggal1=tanggal1;
        this.tanggal2=tanggal2;
        this.status=status;
        this.keyword=keyword;
    }
    
    public void simpan(){
        try {
            Sequel.AutoComitFalse();
            index=1;
            for(i=0;i<tbDiagnosa.getRowCount();i++){ 
                if(tbDiagnosa.getValueAt(i,0).toString().equals("true")){
                    if(Sequel.cariInteger(
                            "select count(diagnosa_pasien.kd_penyakit) from diagnosa_pasien "+
                            "inner join reg_periksa on diagnosa_pasien.no_rawat=reg_periksa.no_rawat "+
                            "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where "+
                            "pasien.no_rkm_medis='"+norm+"' and diagnosa_pasien.kd_penyakit='"+tbDiagnosa.getValueAt(i,1).toString()+"'")>0){
                        Sequel.menyimpan("diagnosa_pasien","?,?,?,?,?","Penyakit",5,new String[]{
                            norawat,tbDiagnosa.getValueAt(i,1).toString(),status,tbDiagnosa.getValueAt(i,11).toString(),"Lama"
                        });
                    }else{
                        Sequel.menyimpan("diagnosa_pasien","?,?,?,?,?","Penyakit",5,new String[]{
                            norawat,tbDiagnosa.getValueAt(i,1).toString(),status,tbDiagnosa.getValueAt(i,11).toString(),"Baru"
                        });
                    }  
                    
                    if(index==1){
                        if(status.equals("Ralan")){
                            Sequel.mengedit("resume_pasien","no_rawat=?","kd_diagnosa_utama=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }else if(status.equals("Ranap")){
                            Sequel.mengedit("resume_pasien_ranap","no_rawat=?","kd_diagnosa_utama=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }   
                    }else if(index==2){
                        if(status.equals("Ralan")){
                            Sequel.mengedit("resume_pasien","no_rawat=?","kd_diagnosa_sekunder=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }else if(status.equals("Ranap")){
                            Sequel.mengedit("resume_pasien_ranap","no_rawat=?","kd_diagnosa_sekunder=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }
                    }else if(index==3){
                        if(status.equals("Ralan")){
                            Sequel.mengedit("resume_pasien","no_rawat=?","kd_diagnosa_sekunder2=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }else if(status.equals("Ranap")){
                            Sequel.mengedit("resume_pasien_ranap","no_rawat=?","kd_diagnosa_sekunder2=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }
                    }else if(index==4){
                        if(status.equals("Ralan")){
                            Sequel.mengedit("resume_pasien","no_rawat=?","kd_diagnosa_sekunder3=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }else if(status.equals("Ranap")){
                            Sequel.mengedit("resume_pasien_ranap","no_rawat=?","kd_diagnosa_sekunder3=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }   
                    }else if(index==5){
                        if(status.equals("Ralan")){
                            Sequel.mengedit("resume_pasien","no_rawat=?","kd_diagnosa_sekunder4=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }else if(status.equals("Ranap")){
                            Sequel.mengedit("resume_pasien_ranap","no_rawat=?","kd_diagnosa_sekunder4=?",2,new String[]{
                                tbDiagnosa.getValueAt(i,1).toString(),norawat
                            });
                        }
                    }
                        
                    index++;
                }                    
            }
            Sequel.AutoComitTrue();
            for(i=0;i<tbDiagnosa.getRowCount();i++){ 
               tbDiagnosa.setValueAt(false,i,0);
               tbDiagnosa.setValueAt("",i,11);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Kemungkinan ada data diagnosa yang sama dimasukkan sebelumnya...!");
        }

        try {
            Sequel.AutoComitFalse();
            index=1;
            for(i=0;i<tbProsedur.getRowCount();i++){ 
                if(tbProsedur.getValueAt(i,0).toString().equals("true")){
                    Sequel.menyimpan("prosedur_pasien","?,?,?,?,?","ICD 9",5,new String[]{
                        norawat,tbProsedur.getValueAt(i,1).toString(),status,tbProsedur.getValueAt(i,7).toString(),tbProsedur.getValueAt(i,8).toString()
                    });
                    
                    if(index==1){
                        if(status.equals("Ralan")){
                            Sequel.mengedit("resume_pasien","no_rawat=?","kd_prosedur_utama=?",2,new String[]{
                                tbProsedur.getValueAt(i,1).toString(),norawat
                            });
                        }else if(status.equals("Ranap")){
                            Sequel.mengedit("resume_pasien_ranap","no_rawat=?","kd_prosedur_utama=?",2,new String[]{
                                tbProsedur.getValueAt(i,1).toString(),norawat
                            });
                        }
                    }else if(index==2){
                        if(status.equals("Ralan")){
                            Sequel.mengedit("resume_pasien","no_rawat=?","kd_prosedur_sekunder=?",2,new String[]{
                                tbProsedur.getValueAt(i,1).toString(),norawat
                            });
                        }else if(status.equals("Ranap")){
                            Sequel.mengedit("resume_pasien_ranap","no_rawat=?","kd_prosedur_sekunder=?",2,new String[]{
                                tbProsedur.getValueAt(i,1).toString(),norawat
                            });
                        }
                    }else if(index==3){
                        if(status.equals("Ralan")){
                            Sequel.mengedit("resume_pasien","no_rawat=?","kd_prosedur_sekunder2=?",2,new String[]{
                                tbProsedur.getValueAt(i,1).toString(),norawat
                            });
                        }else if(status.equals("Ranap")){
                            Sequel.mengedit("resume_pasien_ranap","no_rawat=?","kd_prosedur_sekunder2=?",2,new String[]{
                                tbProsedur.getValueAt(i,1).toString(),norawat
                            });
                        }
                    }else if(index==4){
                        if(status.equals("Ralan")){
                            Sequel.mengedit("resume_pasien","no_rawat=?","kd_prosedur_sekunder3=?",2,new String[]{
                                tbProsedur.getValueAt(i,1).toString(),norawat
                            });
                        }else if(status.equals("Ranap")){
                            Sequel.mengedit("resume_pasien_ranap","no_rawat=?","kd_prosedur_sekunder3=?",2,new String[]{
                                tbProsedur.getValueAt(i,1).toString(),norawat
                            });
                        }
                    }
                        
                    index++;
                }                    
            }
            Sequel.AutoComitTrue();
            for(i=0;i<tbProsedur.getRowCount();i++){ 
               tbProsedur.setValueAt(false,i,0);
               tbProsedur.setValueAt("",i,7);
               tbProsedur.setValueAt("",i,8);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Kemungkinan ada data prosedur/ICD9 yang sama dimasukkan sebelumnya...!");
        }
        pilihTab();
    }

    public void pilihTab() {
        switch (TabRawat.getSelectedIndex()) {
            case 1:
                runBackground(() ->tampil());
                break;
            case 2:
                runBackground(() ->tampil2());
                break;
            default:
                break;
        }
    }
    
    public void batal(){
        Diagnosa.setText("");
        for(i=0;i<tbDiagnosa.getRowCount();i++){ 
            tbDiagnosa.setValueAt(false,i,0);
            tbDiagnosa.setValueAt("",i,11);
        }
        for(i=0;i<tbProsedur.getRowCount();i++){ 
            tbProsedur.setValueAt(false,i,0);
            tbProsedur.setValueAt("",i,7);
            tbProsedur.setValueAt("",i,8);
        }
        Prosedur.setText("");
    }
    
    public void hapus(){
        if(TabRawat.getSelectedIndex()==1){
            if(TabModeDiagnosaPasien.getRowCount()==0){
                JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            }else{
                for(i=0;i<tbDiagnosaPasien.getRowCount();i++){ 
                    if(tbDiagnosaPasien.getValueAt(i,0).toString().equals("true")){
                        Sequel.queryu2("delete from diagnosa_pasien where no_rawat=? and kd_penyakit=?",2,new String[]{
                            tbDiagnosaPasien.getValueAt(i,2).toString(),tbDiagnosaPasien.getValueAt(i,5).toString()
                        });
                    }
                }
            }                     
        }else if(TabRawat.getSelectedIndex()==2){
            if(TabModeTindakanPasien.getRowCount()==0){
                JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            }else{
                for(i=0;i<tbTindakanPasien.getRowCount();i++){ 
                    if(tbTindakanPasien.getValueAt(i,0).toString().equals("true")){
                        Sequel.queryu2("delete from prosedur_pasien where no_rawat=? and kode=?",2,new String[]{
                            tbTindakanPasien.getValueAt(i,2).toString(),tbTindakanPasien.getValueAt(i,5).toString()
                        });
                    }
                }
            }
        }
        pilihTab();
    }
    
    public void cetak(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(TabRawat.getSelectedIndex()==1){
            if(TabModeDiagnosaPasien.getRowCount()==0){
                JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            }else if(TabModeDiagnosaPasien.getRowCount()!=0){
                Map<String, Object> param = new HashMap<>();
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                Valid.MyReportqry("rptDiagnosa.jasper","report","::[ Data Diagnosa Pasien ]::",
                        "select reg_periksa.tgl_registrasi,diagnosa_pasien.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                        "diagnosa_pasien.kd_penyakit,penyakit.nm_penyakit, diagnosa_pasien.status,diagnosa_pasien.status_penyakit "+
                        "from diagnosa_pasien inner join reg_periksa inner join pasien inner join penyakit "+
                        "on diagnosa_pasien.no_rawat=reg_periksa.no_rawat and reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                        "and diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "+
                        "where reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and reg_periksa.tgl_registrasi like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and diagnosa_pasien.no_rawat like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and reg_periksa.no_rkm_medis like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and pasien.nm_pasien like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and diagnosa_pasien.kd_penyakit like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and penyakit.nm_penyakit like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and diagnosa_pasien.status_penyakit like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and diagnosa_pasien.status like '%"+keyword+"%' "+
                        "order by reg_periksa.tgl_registrasi,diagnosa_pasien.prioritas ",param);
            }
        }else if(TabRawat.getSelectedIndex()==2){
            if(TabModeTindakanPasien.getRowCount()==0){
                JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            }else if(TabModeTindakanPasien.getRowCount()!=0){
                Map<String, Object> param = new HashMap<>();
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                Valid.MyReportqry("rptProsedur.jasper","report","::[ Data Prosedur Tindakan Pasien ]::",
                        "select reg_periksa.tgl_registrasi,prosedur_pasien.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                        "prosedur_pasien.kode,icd9.deskripsi_panjang, prosedur_pasien.status "+
                        "from prosedur_pasien inner join reg_periksa inner join pasien inner join icd9 "+
                        "on prosedur_pasien.no_rawat=reg_periksa.no_rawat and reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                        "and prosedur_pasien.kode=icd9.kode "+
                        "where reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and reg_periksa.tgl_registrasi like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and prosedur_pasien.no_rawat like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and reg_periksa.no_rkm_medis like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and pasien.nm_pasien like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and prosedur_pasien.kode like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and icd9.deskripsi_panjang like '%"+keyword+"%' or "+
                        "reg_periksa.tgl_registrasi between '"+tanggal1+"' and '"+tanggal2+"' and reg_periksa.no_rkm_medis like '%"+norm+"%' and prosedur_pasien.status like '%"+keyword+"%' "+
                        "order by reg_periksa.tgl_registrasi,prosedur_pasien.prioritas ",param);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }
    
    private void runBackground(Runnable task) {
        if (ceksukses) return;
        if (executor.isShutdown() || executor.isTerminated()) return;
        if (!isDisplayable()) return;

        ceksukses = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    ceksukses = false;
                    SwingUtilities.invokeLater(() -> {
                        if (isDisplayable()) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }
    
    // ======================================================================================================
    // [FIX-SALINDX] Salin diagnosa/prosedur dari kunjungan pasien sebelumnya.
    // Hasil salinan hanya DICENTANG di tab "Input Data" (belum tersimpan). Dokter bisa memeriksa/mengubah dulu,
    // lalu menyimpan lewat tombol Simpan seperti biasa. Status penyakit Baru/Lama dihitung ulang oleh simpan().
    // Form induk sebaiknya memanggil setSumberNoRawat(() -> TNoRw.getText()) supaya No.Rawat tujuan selalu
    // yang sedang dipilih di form induk; kalau tidak dipanggil, dipakai norawat dari setRM().
    // ======================================================================================================
    private widget.Button btnSalinDiagnosa;
    private java.util.function.Supplier<String> sumberNoRawat=null;

    private static class HasilSalin {
        int dx=0,pros=0;
        StringBuilder lewati=new StringBuilder();
        void catatLewati(String kode,String alasan){
            lewati.append("- ").append(kode).append(" : ").append(alasan).append("\n");
        }
    }

    public void setSumberNoRawat(java.util.function.Supplier<String> sumber){
        this.sumberNoRawat=sumber;
    }

    private void siapkanTombolSalin(){
        btnSalinDiagnosa=new widget.Button();
        btnSalinDiagnosa.setText("Salin dari Kunjungan Lalu");
        btnSalinDiagnosa.setToolTipText("Salin diagnosa dari kunjungan pasien ini sebelumnya (belum tersimpan, bisa diperiksa dulu)");
        btnSalinDiagnosa.setName("btnSalinDiagnosa");
        btnSalinDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalinDiagnosaActionPerformed();
            }
        });
        // ruang di kanan kolom Diagnosa dipakai untuk tombol baru
        Diagnosa.setBounds(71,10,484,23);
        BtnCariPenyakit.setBounds(558,10,28,23);
        btnTambahPenyakit.setBounds(589,10,28,23);
        FormData.add(btnSalinDiagnosa);
        btnSalinDiagnosa.setBounds(621,10,200,23);
    }

    private String noRawatTujuan(){
        String hasil=null;
        if(sumberNoRawat!=null){
            try {
                hasil=sumberNoRawat.get();
            } catch (Exception e) {
                hasil=null;
            }
        }
        if(hasil==null||hasil.trim().equals("")){
            hasil=norawat;
        }
        return hasil==null?"":hasil.trim();
    }

    private String n(String s){
        return s==null?"":s;
    }

    private String esc(String s){
        return n(s).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    private String cariTeks(String sql,String... param){
        String hasil="";
        try(PreparedStatement ps=koneksi.prepareStatement(sql)){
            for(int k=0;k<param.length;k++){
                ps.setString(k+1,param[k]);
            }
            try(ResultSet r=ps.executeQuery()){
                if(r.next()){
                    hasil=n(r.getString(1));
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        return hasil;
    }

    private int cariAngka(String sql,String... param){
        try{
            return Integer.parseInt(cariTeks(sql,param).trim());
        }catch(Exception e){
            return 0;
        }
    }

    private void btnSalinDiagnosaActionPerformed(){
        if(!akses.getdiagnosa_pasien()){
            JOptionPane.showMessageDialog(null,"Maaf, anda tidak punya hak akses untuk mengisi diagnosa...!!!");
            return;
        }
        String noRawatBaru=noRawatTujuan();
        if(noRawatBaru.equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu pasiennya...!!!");
            return;
        }
        String noRM=cariTeks("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?",noRawatBaru);
        if(noRM.equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, No.Rawat "+noRawatBaru+" tidak ditemukan...!!!");
            return;
        }
        String namaPasien=cariTeks("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?",noRM);
        java.util.Map<String,java.util.List<String[]>> dxMap=new java.util.HashMap<String,java.util.List<String[]>>();
        java.util.Map<String,java.util.List<String[]>> prosMap=new java.util.HashMap<String,java.util.List<String[]>>();
        java.util.List<String[]> kunjungan=ambilKunjunganLalu(noRM,noRawatBaru,dxMap,prosMap);
        if(kunjungan.isEmpty()){
            JOptionPane.showMessageDialog(null,"Pasien "+namaPasien+" belum punya diagnosa/prosedur pada kunjungan sebelumnya...!!!");
            return;
        }

        javax.swing.JDialog dlg=new javax.swing.JDialog(javax.swing.SwingUtilities.getWindowAncestor(this),
                "Salin Diagnosa dari Kunjungan Sebelumnya",java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.getContentPane().add(buatPanelSalin(dlg,noRawatBaru,noRM,namaPasien,kunjungan,dxMap,prosMap));
        dlg.setSize(900,520);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // 30 kunjungan terakhir pasien (selain kunjungan tujuan) yang punya diagnosa/prosedur, terbaru di atas.
    private java.util.List<String[]> ambilKunjunganLalu(String noRM,String noRawatSekarang,
            java.util.Map<String,java.util.List<String[]>> dxMap,java.util.Map<String,java.util.List<String[]>> prosMap){
        java.util.List<String[]> kunjungan=new java.util.ArrayList<String[]>();
        try(PreparedStatement ps=koneksi.prepareStatement(
                "select reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,poliklinik.nm_poli,dokter.nm_dokter,reg_periksa.status_lanjut "+
                "from reg_periksa inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                "where reg_periksa.no_rkm_medis=? and reg_periksa.no_rawat<>? and "+
                "(exists(select 1 from diagnosa_pasien where diagnosa_pasien.no_rawat=reg_periksa.no_rawat) or "+
                "exists(select 1 from prosedur_pasien where prosedur_pasien.no_rawat=reg_periksa.no_rawat)) "+
                "order by reg_periksa.tgl_registrasi desc,reg_periksa.jam_reg desc,reg_periksa.no_rawat desc limit 30")){
            ps.setString(1,noRM);
            ps.setString(2,noRawatSekarang);
            try(ResultSet r=ps.executeQuery()){
                while(r.next()){
                    kunjungan.add(new String[]{n(r.getString(1)),n(r.getString(2)),n(r.getString(3)),n(r.getString(4)),n(r.getString(5)),n(r.getString(6))});
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }

        try(PreparedStatement ps=koneksi.prepareStatement(
                "select diagnosa_pasien.no_rawat,diagnosa_pasien.kd_penyakit,penyakit.nm_penyakit,diagnosa_pasien.prioritas "+
                "from diagnosa_pasien inner join reg_periksa on diagnosa_pasien.no_rawat=reg_periksa.no_rawat "+
                "inner join penyakit on diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "+
                "where reg_periksa.no_rkm_medis=? and reg_periksa.no_rawat<>? "+
                "order by diagnosa_pasien.no_rawat,diagnosa_pasien.prioritas")){
            ps.setString(1,noRM);
            ps.setString(2,noRawatSekarang);
            try(ResultSet r=ps.executeQuery()){
                while(r.next()){
                    String kunci=n(r.getString(1));
                    if(!dxMap.containsKey(kunci)){
                        dxMap.put(kunci,new java.util.ArrayList<String[]>());
                    }
                    dxMap.get(kunci).add(new String[]{kunci,n(r.getString(2)),n(r.getString(3)),n(r.getString(4))});
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }

        try(PreparedStatement ps=koneksi.prepareStatement(
                "select prosedur_pasien.no_rawat,prosedur_pasien.kode,icd9.deskripsi_panjang,prosedur_pasien.jumlah "+
                "from prosedur_pasien inner join reg_periksa on prosedur_pasien.no_rawat=reg_periksa.no_rawat "+
                "inner join icd9 on prosedur_pasien.kode=icd9.kode "+
                "where reg_periksa.no_rkm_medis=? and reg_periksa.no_rawat<>? "+
                "order by prosedur_pasien.no_rawat,prosedur_pasien.prioritas")){
            ps.setString(1,noRM);
            ps.setString(2,noRawatSekarang);
            try(ResultSet r=ps.executeQuery()){
                while(r.next()){
                    String kunci=n(r.getString(1));
                    if(!prosMap.containsKey(kunci)){
                        prosMap.put(kunci,new java.util.ArrayList<String[]>());
                    }
                    prosMap.get(kunci).add(new String[]{kunci,n(r.getString(2)),n(r.getString(3)),n(r.getString(4))});
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        return kunjungan;
    }

    private String ringkasDx(java.util.List<String[]> dx){
        if(dx==null||dx.isEmpty()){
            return "-";
        }
        String utama=dx.get(0)[2];
        if(utama.length()>40){
            utama=utama.substring(0,40)+"...";
        }
        StringBuilder sb=new StringBuilder(dx.get(0)[1]).append(" ").append(utama);
        for(int k=1;k<dx.size()&&k<6;k++){
            sb.append(", ").append(dx.get(k)[1]);
        }
        if(dx.size()>6){
            sb.append(" +").append(dx.size()-6).append(" lagi");
        }
        return sb.toString();
    }

    private javax.swing.JPanel buatPanelSalin(final javax.swing.JDialog dlg,final String noRawatBaru,String noRM,String namaPasien,
            final java.util.List<String[]> kunjungan,final java.util.Map<String,java.util.List<String[]>> dxMap,
            final java.util.Map<String,java.util.List<String[]>> prosMap){
        final javax.swing.JPanel isi=new javax.swing.JPanel(new java.awt.BorderLayout(5,5));
        isi.setBorder(javax.swing.BorderFactory.createEmptyBorder(8,8,8,8));

        javax.swing.JLabel info=new javax.swing.JLabel("<html>Pasien : <b>"+esc(namaPasien)+"</b> ("+esc(noRM)+") &nbsp;&nbsp; Disalin ke No.Rawat : <b>"+esc(noRawatBaru)+"</b><br>"+
                "Klik salah satu kunjungan, centang diagnosa yang mau disalin. Hasilnya masuk ke tab Input Data dan <b>belum tersimpan</b>: periksa dulu, lalu klik Simpan.</html>");
        info.setFont(new java.awt.Font("Tahoma",0,11));
        isi.add(info,java.awt.BorderLayout.NORTH);

        final DefaultTableModel modelKunjungan=new DefaultTableModel(null,new Object[]{"Tgl.Rawat","No.Rawat","Poli/Unit","Dokter","Status","Diagnosa"}){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        for(String[] k:kunjungan){
            modelKunjungan.addRow(new Object[]{k[1],k[0],k[3],k[4],k[5],ringkasDx(dxMap.get(k[0]))});
        }
        final widget.Table tbKunjungan=new widget.Table();
        tbKunjungan.setModel(modelKunjungan);
        tbKunjungan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbKunjungan.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        int[] lebarKunjungan={80,135,140,160,50,315};
        for(int c=0;c<lebarKunjungan.length;c++){
            tbKunjungan.getColumnModel().getColumn(c).setPreferredWidth(lebarKunjungan[c]);
        }
        tbKunjungan.setDefaultRenderer(Object.class,new WarnaTable());
        widget.ScrollPane scrollKunjungan=new widget.ScrollPane();
        scrollKunjungan.setViewportView(tbKunjungan);

        final DefaultTableModel modelItem=new DefaultTableModel(null,new Object[]{"P","Jenis","Kode","Nama","Jml"}){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                return colIndex==0;
            }
            @Override public Class getColumnClass(int columnIndex) {
                return columnIndex==0?java.lang.Boolean.class:java.lang.Object.class;
            }
        };
        final widget.Table tbItem=new widget.Table();
        tbItem.setModel(modelItem);
        tbItem.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] lebarItem={25,70,80,560,40};
        for(int c=0;c<lebarItem.length;c++){
            tbItem.getColumnModel().getColumn(c).setPreferredWidth(lebarItem[c]);
        }
        tbItem.setDefaultRenderer(Object.class,new WarnaTable());
        widget.ScrollPane scrollItem=new widget.ScrollPane();
        scrollItem.setViewportView(tbItem);

        // isi tabel bawah mengikuti kunjungan yang dipilih: diagnosa tercentang, prosedur tidak
        tbKunjungan.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if(e.getValueIsAdjusting()){
                    return;
                }
                modelItem.setRowCount(0);
                int baris=tbKunjungan.getSelectedRow();
                if(baris==-1){
                    return;
                }
                String noRawatSumber=modelKunjungan.getValueAt(baris,1).toString();
                java.util.List<String[]> dx=dxMap.get(noRawatSumber);
                if(dx!=null){
                    for(String[] d:dx){
                        modelItem.addRow(new Object[]{Boolean.TRUE,"Diagnosa",d[1],d[2],""});
                    }
                }
                java.util.List<String[]> pr=prosMap.get(noRawatSumber);
                if(pr!=null){
                    for(String[] p:pr){
                        modelItem.addRow(new Object[]{Boolean.FALSE,"Prosedur",p[1],p[2],p[3]});
                    }
                }
            }
        });

        javax.swing.JSplitPane belah=new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT,scrollKunjungan,scrollItem);
        belah.setDividerLocation(190);
        isi.add(belah,java.awt.BorderLayout.CENTER);

        widget.Button btnSalin=new widget.Button();
        btnSalin.setText("Salin yang Dicentang");
        btnSalin.setPreferredSize(new Dimension(170,30));
        btnSalin.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                java.util.List<String> kodeDx=new java.util.ArrayList<String>();
                java.util.List<String[]> prosedur=new java.util.ArrayList<String[]>();
                for(int r=0;r<modelItem.getRowCount();r++){
                    if(modelItem.getValueAt(r,0).toString().equals("true")){
                        if(modelItem.getValueAt(r,1).toString().equals("Diagnosa")){
                            kodeDx.add(modelItem.getValueAt(r,2).toString());
                        }else{
                            prosedur.add(new String[]{modelItem.getValueAt(r,2).toString(),modelItem.getValueAt(r,4).toString()});
                        }
                    }
                }
                if(kodeDx.isEmpty()&&prosedur.isEmpty()){
                    JOptionPane.showMessageDialog(isi,"Silahkan centang dulu diagnosa/prosedur yang mau disalin..!!");
                    return;
                }
                HasilSalin hasil=terapkanSalinan(noRawatBaru,kodeDx,prosedur);
                StringBuilder pesan=new StringBuilder();
                if(hasil.dx+hasil.pros>0){
                    pesan.append(hasil.dx).append(" diagnosa");
                    if(hasil.pros>0){
                        pesan.append(" dan ").append(hasil.pros).append(" prosedur");
                    }
                    pesan.append(" disalin ke tab Input Data.\nBELUM tersimpan, silahkan periksa urutannya lalu klik Simpan.");
                }else{
                    pesan.append("Tidak ada data yang disalin.");
                }
                if(hasil.lewati.length()>0){
                    pesan.append("\n\nDilewati :\n").append(hasil.lewati);
                }
                JOptionPane.showMessageDialog(isi,pesan.toString());
                if(hasil.dx+hasil.pros>0&&dlg!=null){
                    dlg.dispose();
                }
            }
        });
        widget.Button btnTutup=new widget.Button();
        btnTutup.setText("Tutup");
        btnTutup.setPreferredSize(new Dimension(100,30));
        btnTutup.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(dlg!=null){
                    dlg.dispose();
                }
            }
        });
        javax.swing.JPanel bawah=new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT,5,0));
        bawah.add(btnSalin);
        bawah.add(btnTutup);
        isi.add(bawah,java.awt.BorderLayout.SOUTH);

        if(modelKunjungan.getRowCount()>0){
            tbKunjungan.setRowSelectionInterval(0,0);
        }
        return isi;
    }

    private Object[] ambilPenyakit(String kode){
        try(PreparedStatement ps=koneksi.prepareStatement(
                "select penyakit.kd_penyakit,penyakit.nm_penyakit,penyakit.ciri_ciri,penyakit.keterangan,kategori_penyakit.nm_kategori,"+
                "kategori_penyakit.ciri_umum,penyakit.validcode,penyakit.accpdx,penyakit.asterisk,penyakit.im from kategori_penyakit "+
                "inner join penyakit on penyakit.kd_ktg=kategori_penyakit.kd_ktg where penyakit.kd_penyakit=?")){
            ps.setString(1,kode);
            try(ResultSet r=ps.executeQuery()){
                if(r.next()){
                    return new Object[]{Boolean.TRUE,n(r.getString(1)),n(r.getString(2)),n(r.getString(3)),n(r.getString(4)),n(r.getString(5)),
                        n(r.getString(6)),n(r.getString(7)),n(r.getString(8)),n(r.getString(9)),n(r.getString(10)),""};
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        return null;
    }

    private Object[] ambilProsedur(String kode,String jumlah){
        try(PreparedStatement ps=koneksi.prepareStatement("select * from icd9 where kode=?")){
            ps.setString(1,kode);
            try(ResultSet r=ps.executeQuery()){
                if(r.next()){
                    return new Object[]{Boolean.TRUE,n(r.getString(1)),n(r.getString(2)),n(r.getString(3)),n(r.getString(4)),n(r.getString(5)),
                        n(r.getString(6)),"",(jumlah==null||jumlah.trim().equals(""))?"1":jumlah.trim()};
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        return null;
    }

    // Memasukkan hasil pilihan ke tabel input (tbDiagnosa/tbProsedur) sebagai baris tercentang.
    // Aturan sama dengan tbDiagnosaPropertyChange/tbProsedurPropertyChange: kode header (valid code 0) tidak boleh dicentang
    // dan urutan 1 tidak boleh Accpdx N. Yang sudah dicentang sebelumnya dipertahankan; urutan = posisi baris tercentang.
    private HasilSalin terapkanSalinan(String noRawatBaru,java.util.List<String> kodeDx,java.util.List<String[]> prosedur){
        HasilSalin hasil=new HasilSalin();

        // ---------- Diagnosa ----------
        java.util.List<Object[]> terpilih=new java.util.ArrayList<Object[]>();
        java.util.List<Object[]> sisa=new java.util.ArrayList<Object[]>();
        java.util.Set<String> adaKode=new java.util.HashSet<String>();
        for(int r=0;r<tabModeDiagnosa.getRowCount();r++){
            Object[] baris=new Object[tabModeDiagnosa.getColumnCount()];
            for(int c=0;c<baris.length;c++){
                baris[c]=tabModeDiagnosa.getValueAt(r,c);
            }
            if(String.valueOf(baris[0]).equals("true")){
                terpilih.add(baris);
                adaKode.add(String.valueOf(baris[1]));
            }else{
                sisa.add(baris);
            }
        }
        java.util.List<Object[]> baru=new java.util.ArrayList<Object[]>();
        java.util.Set<String> kodeBaru=new java.util.HashSet<String>();
        for(String kode:kodeDx){
            if(adaKode.contains(kode)){
                hasil.catatLewati(kode,"sudah dicentang di tabel input");
            }else if(cariAngka("select count(*) from diagnosa_pasien where diagnosa_pasien.no_rawat=? and diagnosa_pasien.kd_penyakit=?",noRawatBaru,kode)>0){
                hasil.catatLewati(kode,"sudah tersimpan di kunjungan ini");
            }else{
                Object[] p=ambilPenyakit(kode);
                if(p==null){
                    hasil.catatLewati(kode,"tidak ditemukan di data penyakit");
                }else if(String.valueOf(p[7]).equals("0")){
                    hasil.catatLewati(kode,"kode header (valid code 0)");
                }else{
                    baru.add(p);
                    adaKode.add(kode);
                    kodeBaru.add(kode);
                }
            }
        }
        while(terpilih.isEmpty()&&!baru.isEmpty()&&String.valueOf(baru.get(0)[8]).equals("N")){
            hasil.catatLewati(String.valueOf(baru.get(0)[1]),"Accpdx N, tidak bisa menjadi diagnosa utama");
            kodeBaru.remove(String.valueOf(baru.get(0)[1]));
            baru.remove(0);
        }
        if(!baru.isEmpty()){
            int urut=1;
            tabModeDiagnosa.setRowCount(0);
            for(Object[] b:terpilih){
                b[0]=Boolean.TRUE;
                b[11]=Integer.valueOf(urut++);
                tabModeDiagnosa.addRow(b);
            }
            for(Object[] b:baru){
                b[0]=Boolean.TRUE;
                b[11]=Integer.valueOf(urut++);
                tabModeDiagnosa.addRow(b);
                hasil.dx++;
            }
            for(Object[] b:sisa){
                if(!kodeBaru.contains(String.valueOf(b[1]))){
                    tabModeDiagnosa.addRow(b);
                }
            }
            tbDiagnosa.scrollRectToVisible(tbDiagnosa.getCellRect(0,0,true));
        }

        // ---------- Prosedur ----------
        java.util.List<Object[]> terpilihPros=new java.util.ArrayList<Object[]>();
        java.util.List<Object[]> sisaPros=new java.util.ArrayList<Object[]>();
        java.util.Set<String> adaKodePros=new java.util.HashSet<String>();
        for(int r=0;r<tabModeProsedur.getRowCount();r++){
            Object[] baris=new Object[tabModeProsedur.getColumnCount()];
            for(int c=0;c<baris.length;c++){
                baris[c]=tabModeProsedur.getValueAt(r,c);
            }
            if(String.valueOf(baris[0]).equals("true")){
                terpilihPros.add(baris);
                adaKodePros.add(String.valueOf(baris[1]));
            }else{
                sisaPros.add(baris);
            }
        }
        java.util.List<Object[]> baruPros=new java.util.ArrayList<Object[]>();
        java.util.Set<String> kodeBaruPros=new java.util.HashSet<String>();
        for(String[] pr:prosedur){
            String kode=pr[0];
            if(adaKodePros.contains(kode)){
                hasil.catatLewati(kode,"sudah dicentang di tabel input");
            }else if(cariAngka("select count(*) from prosedur_pasien where prosedur_pasien.no_rawat=? and prosedur_pasien.kode=?",noRawatBaru,kode)>0){
                hasil.catatLewati(kode,"sudah tersimpan di kunjungan ini");
            }else{
                Object[] p=ambilProsedur(kode,pr[1]);
                if(p==null){
                    hasil.catatLewati(kode,"tidak ditemukan di data ICD 9");
                }else if(String.valueOf(p[4]).equals("0")){
                    hasil.catatLewati(kode,"kode header (valid code 0)");
                }else{
                    baruPros.add(p);
                    adaKodePros.add(kode);
                    kodeBaruPros.add(kode);
                }
            }
        }
        while(terpilihPros.isEmpty()&&!baruPros.isEmpty()&&String.valueOf(baruPros.get(0)[5]).equals("N")){
            hasil.catatLewati(String.valueOf(baruPros.get(0)[1]),"Accpdx N, tidak bisa menjadi prosedur utama");
            kodeBaruPros.remove(String.valueOf(baruPros.get(0)[1]));
            baruPros.remove(0);
        }
        if(!baruPros.isEmpty()){
            int urut=1;
            tabModeProsedur.setRowCount(0);
            for(Object[] b:terpilihPros){
                b[0]=Boolean.TRUE;
                b[7]=Integer.valueOf(urut++);
                if(String.valueOf(b[8]).trim().equals("")){
                    b[8]="1";
                }
                tabModeProsedur.addRow(b);
            }
            for(Object[] b:baruPros){
                b[0]=Boolean.TRUE;
                b[7]=Integer.valueOf(urut++);
                tabModeProsedur.addRow(b);
                hasil.pros++;
            }
            for(Object[] b:sisaPros){
                if(!kodeBaruPros.contains(String.valueOf(b[1]))){
                    tabModeProsedur.addRow(b);
                }
            }
            tbProsedur.scrollRectToVisible(tbProsedur.getCellRect(0,0,true));
        }
        return hasil;
    }
}
