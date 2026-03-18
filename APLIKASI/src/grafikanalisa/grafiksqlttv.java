/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grafikanalisa;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import fungsi.koneksiDB;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

/**
 * @author Via
 */
public class grafiksqlttv extends JDialog {

    public static CategoryDataset createDataset1(
            String query, String kolom, String kolom2, String kolom3, String kolom4, String kolom5) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        String series1 = "Suhu";

        try {
            Statement stat = koneksiDB.condb().createStatement();
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String tksbr = rs.getString(1) + " " + rs.getString(2);
                double field1 = rs.getDouble(3);

                result.addValue(field1, series1, tksbr);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        return result;
    }

    public static CategoryDataset createDataset2(
            String query, String kolom, String kolom2, String kolom3, String kolom4, String kolom5) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        String series2 = "Nadi";

        try {
            Statement stat = koneksiDB.condb().createStatement();
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String tksbr = rs.getString(1) + " " + rs.getString(2);
                double field2 = rs.getDouble(4);

                result.addValue(field2, series2, tksbr);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        return result;
    }

    public static CategoryDataset createDataset3(
            String query, String kolom, String kolom2, String kolom3, String kolom4, String kolom5) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        String series3 = "Respirasi";

        try {
            Statement stat = koneksiDB.condb().createStatement();
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String tksbr = rs.getString(1) + " " + rs.getString(2);
                double field3 = rs.getDouble(5);

                result.addValue(field3, series3, tksbr);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        return result;
    }

    public static CategoryDataset createDataset4(
            String query, String kolom, String kolom2, String kolom3, String kolom4, String kolom5) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        String series4 = "SpO2";

        try {
            Statement stat = koneksiDB.condb().createStatement();
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String tksbr = rs.getString(1) + " " + rs.getString(2);
                double field4 = rs.getDouble(6);

                result.addValue(field4, series4, tksbr);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        return result;
    }

    public static CategoryDataset createDataset5(
            String query, String kolom, String kolom2, String kolom3, String kolom4, String kolom5) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        String series5 = "Sistole";
        String series6 = "Diastole";

        try {
            Statement stat = koneksiDB.condb().createStatement();
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String tksbr = rs.getString(1) + " " + rs.getString(2);
                double field5 = rs.getDouble(7);
                double field6 = rs.getDouble(8);

                result.addValue(field5, series5, tksbr);
                result.addValue(field6, series6, tksbr);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        return result;
    }

    private static JFreeChart createChart(
            String query, String kolom, String kolom2, String kolom3, String kolom4, String kolom5) {

        CategoryDataset dataset1 = createDataset1(query, kolom, kolom2, kolom3, kolom4, kolom5);
        CategoryDataset dataset2 = createDataset2(query, kolom, kolom2, kolom3, kolom4, kolom5);
        CategoryDataset dataset3 = createDataset3(query, kolom, kolom2, kolom3, kolom4, kolom5);
        CategoryDataset dataset4 = createDataset4(query, kolom, kolom2, kolom3, kolom4, kolom5);
        CategoryDataset dataset5 = createDataset5(query, kolom, kolom2, kolom3, kolom4, kolom5);

        NumberAxis rangeAxis1 = new NumberAxis("Suhu(ᵒC)");
        rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberAxis rangeAxis2 = new NumberAxis("Nadi(x/menit)");
        rangeAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberAxis rangeAxis3 = new NumberAxis("RR(x/menit)");
        rangeAxis3.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberAxis rangeAxis4 = new NumberAxis("SpO2(%)");
        rangeAxis4.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberAxis rangeAxis5 = new NumberAxis("Tensi(mmhg)");
        rangeAxis5.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        LineAndShapeRenderer renderer1 = new LineAndShapeRenderer();
        renderer1.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator("{0}: {1} = {3}", NumberFormat.getInstance()));
        renderer1.setSeriesPaint(0, new Color(204, 204, 0));

        LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
        renderer2.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator("{0}: {1} = {2}", NumberFormat.getInstance()));
        renderer2.setSeriesPaint(0, Color.RED);

        LineAndShapeRenderer renderer3 = new LineAndShapeRenderer();
        renderer3.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator("{0}: {1} = {2}", NumberFormat.getInstance()));
        renderer3.setSeriesPaint(0, Color.ORANGE);

        LineAndShapeRenderer renderer4 = new LineAndShapeRenderer();
        renderer4.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator("{0}: {1} = {2}", NumberFormat.getInstance()));
        renderer4.setSeriesPaint(0, Color.GREEN);

        LineAndShapeRenderer renderer5 = new LineAndShapeRenderer();
        renderer5.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator("{0}: {1} = {2}", NumberFormat.getInstance()));
        renderer5.setSeriesPaint(0, Color.RED);
        renderer5.setSeriesPaint(1, Color.BLUE);

        CategoryPlot subplot1 = new CategoryPlot(dataset1, null, rangeAxis1, renderer1);
        CategoryPlot subplot2 = new CategoryPlot(dataset2, null, rangeAxis2, renderer2);
        CategoryPlot subplot3 = new CategoryPlot(dataset3, null, rangeAxis3, renderer3);
        CategoryPlot subplot4 = new CategoryPlot(dataset4, null, rangeAxis4, renderer4);
        CategoryPlot subplot5 = new CategoryPlot(dataset5, null, rangeAxis5, renderer5);

        subplot1.setDomainGridlinesVisible(true);
        subplot2.setDomainGridlinesVisible(true);
        subplot3.setDomainGridlinesVisible(true);
        subplot4.setDomainGridlinesVisible(true);
        subplot5.setDomainGridlinesVisible(true);

        addAnnotations(subplot1, dataset1);
        addAnnotations(subplot2, dataset2);
        addAnnotations(subplot3, dataset3);
        addAnnotations(subplot4, dataset4);
        addAnnotations(subplot5, dataset5);

        CategoryAxis domainAxis = new CategoryAxis("TTV");
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(domainAxis);
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.add(subplot4, 1);
        plot.add(subplot5, 1);

        return new JFreeChart("Vital Signs Monitoring", new Font("Tahoma", Font.PLAIN, 12), plot, true);
    }

    public static JPanel createDemoPanel(
            String query, String kolom, String kolom2, String kolom3, String kolom4, String kolom5) {

        JFreeChart chart = createChart(query, kolom, kolom2, kolom3, kolom4, kolom5);
        return new ChartPanel(chart);
    }

    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    public grafiksqlttv(
            String title, String query, String kolom, String kolom2, String kolom3, String kolom4, String kolom5) {

        setTitle(title);
        JPanel chartPanel = createDemoPanel(query, kolom, kolom2, kolom3, kolom4, kolom5);

        // Atur ukuran ChartPanel agar sesuai dengan data yang ditampilkan
        // Misalnya, lebar grafik disesuaikan dengan jumlah data
        int dataCount = getDataCount(query); // Fungsi untuk menghitung jumlah data
        int chartWidth = Math.max(1000, dataCount * 50); // Lebar minimum 1000, atau lebih jika data banyak
        chartPanel.setPreferredSize(new Dimension(chartWidth, screen.height));

        // Buat JScrollPane dan tambahkan ChartPanel ke dalamnya
        JScrollPane scrollPane = new JScrollPane(chartPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Tambahkan JScrollPane ke dalam JDialog
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        setModal(true);
        setIconImage(
                new ImageIcon(super.getClass().getResource("/picture/addressbook-edit24.png")).getImage());
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        chartPanel.setBackground(Color.WHITE);
    }

    // Fungsi untuk menghitung jumlah data dari query
    private int getDataCount(String query) {
        int count = 0;
        try {
            Statement stat = koneksiDB.condb().createStatement();
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                count++;
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        return count;
    }

    private static void addAnnotations(CategoryPlot plot, CategoryDataset dataset) {
        for (int row = 0; row < dataset.getRowCount(); row++) {
            for (int column = 0; column < dataset.getColumnCount(); column++) {
                Number value = dataset.getValue(row, column);
                if (value != null) {
                    String annotationText = value.toString();
                    double adjustedValue = value.doubleValue() - 5;
                    CategoryTextAnnotation annotation
                            = new CategoryTextAnnotation(annotationText, dataset.getColumnKey(column), adjustedValue);
                    annotation.setFont(new Font("Tahoma", Font.PLAIN, 9));
                    annotation.setTextAnchor(org.jfree.ui.TextAnchor.TOP_CENTER);
                    plot.addAnnotation(annotation);
                }
            }
        }
    }

    private static final Logger LOG = Logger.getLogger(grafiksqlttv.class.getName());
}
