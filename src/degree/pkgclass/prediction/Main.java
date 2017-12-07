/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package degree.pkgclass.prediction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Persey
 */
public class Main extends javax.swing.JFrame {

    DefaultTreeModel dtm;
    ParseFile parse = new ParseFile();
    AddUserInfo user = new AddUserInfo();
    Student student = null;
    List<Student> students = new LinkedList<>();
    String studentFile = "";
    CalculateUAv computeAv = new CalculateUAv();
    int currentYear;
    int currentSem;
    
    
    /**
     * Creates new form Main
     */
    public Main() {
        parse.getStudentFiles();
        students = parse.getStudents();
        initComponents();
        ImageIcon windowIcon = new ImageIcon(getClass().getResource("/degree/pkgclass/prediction/cap.png"));
        this.setIconImage(windowIcon.getImage());
        table.setAutoResizeMode(table.AUTO_RESIZE_OFF);
        TableColumn col = table.getColumnModel().getColumn(0);
        col.setPreferredWidth(80);
        col = table.getColumnModel().getColumn(1);
        col.setPreferredWidth(400);
        col = table.getColumnModel().getColumn(2);
        col.setPreferredWidth(80);
        showComponents(false);
        fillDataToTree();
        this.tree.setModel(dtm);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.addTreeSelectionListener((TreeSelectionEvent e) -> {
            this.run.setEnabled(true);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
            if (node == null)
                //Nothing is selected.     
                return;
            
            Object nodeInfo = node.getUserObject();
            if(node.getLevel()==1){
                showComponents(false);
                Student stu = (Student)nodeInfo;
                setSourceFile(stu.getFile());
                studentFile = stu.getFile();
                student = stu;
                enableSemBtn();
                setCurrentUser(stu);
                mainPanel.setVisible(true);
                
            }
            if(node.isLeaf() && node.getLevel() == 3){
                int selYear = Integer.parseInt(node.getParent().toString());
                int selSem;
                if(node.toString().equals("INDUSTRIAL ATTACHMENT")){
                    selSem = 0;
                }else{
                    selSem = Integer.parseInt(node.toString().split(" ")[1]);
                }
                //Student = (Student)
                DefaultMutableTreeNode n = (DefaultMutableTreeNode)node.getParent().getParent();
                Student st = (Student)n.getUserObject();
                getSemCourses(st, selYear, selSem);
                
                setSourceFile(st.getFile());
                studentFile = st.getFile();
                student = st;
                enableSemBtn();
                setCurrentUser(st);
                showComponents(true);
            }
            
        });
        
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) this.tree.getCellRenderer();
        Icon closedIcon = new ImageIcon(getClass().getResource("/degree/pkgclass/prediction/closed.png"));
        Icon openIcon = new ImageIcon(getClass().getResource("/degree/pkgclass/prediction/closed.png"));
        Icon leafIcon = new ImageIcon(getClass().getResource("/degree/pkgclass/prediction/leaf.png"));
        renderer.setClosedIcon(closedIcon);
        renderer.setOpenIcon(openIcon);
        renderer.setLeafIcon(leafIcon); 
    }
    
    private void showComponents(boolean tf){
        if(tf){
            mainPanel.setVisible(true);
            bpan.setVisible(true);
            sep.setVisible(true);
            jPanel1.setVisible(true);
        }else{
            mainPanel.setVisible(false);
            bpan.setVisible(false);
            sep.setVisible(false);
            jPanel1.setVisible(false);
        }
    }
    
    private void setCurrentInfo(Student student){
        CalculateUAv cav = new CalculateUAv();
        cav.processYears(student);
        int semsAv = cav.getNumberOfSemesters();
        
        if(semsAv <= 6){
            List<Integer> years = new LinkedList<>();
            for(Year y: student.getYears()){
                years.add(y.getYear());
            }
            Collections.sort(years);

            youtLoop:
            for(int y: years){
                for(Year yr: student.getYears()){
                    if(yr.getYear() == y){

                        if(yr.getSemesters().isEmpty()){
                            this.currentYear = yr.getYear();
                            this.year.setText(currentYear+"");
                            this.currentSem = 1;
                            this.sem.setText("1");
                            break youtLoop;
                        }
                        else if(yr.getSemesters().size() == 1){
                            this.currentYear = yr.getYear();
                            this.year.setText(currentYear+"");
                            this.currentSem = 2;
                            this.sem.setText("2");
                            break youtLoop;
                        }
                    }
                }
            }
            int yearIndex = student.getFirstYear();
            Years reqYears = parse.getCourseList();
            List<ReqCourse> rq = null;

            if(semsAv == 5){
                rq = reqYears.getYears().get(currentYear-yearIndex+1).getSemesterOne();
            }
            else if(currentSem == 1){
                rq = reqYears.getYears().get(currentYear-yearIndex).getSemesterOne();
            }else if(currentSem == 2){
                rq = reqYears.getYears().get(currentYear-yearIndex).getSemesterTwo();
            }

            addCourseRows(rq);
        }else{
            this.addCourseWindow.dispose();
        }
        
        
    }
    
    private void enableSemBtn(){
        this.addSem.setEnabled(true);
    }
    
    private void getSemCourses(Student s, int yr, int sm){
        for(Year y:s.getYears()){
            
            if(y.getYear() == yr){
                for(Semester smr: y.getSemesters()){
                    if(smr.getSemesterNumber() == sm){
                        addRows(smr);
                    }
                }
            }
        }
    }
    
    private void addRows(Semester s){
        
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
        while(jTable1.getRowCount() != 0){
            for(int i = 0; i < jTable1.getRowCount(); i++){
                model.removeRow(i);
            }
        }
        Object rowData[] = new Object[3];
        
        for(int i = 0; i < s.getCourses().size(); i++){
            rowData[0] = s.getCourses().get(i).getCourseCode();
            rowData[1] = s.getCourses().get(i).getCourseNarration();
            rowData[2] = s.getCourses().get(i).getCourseMark();
            model.addRow(rowData);
        }
    }
    
    public void setCurrentUser(Student stu){
        String[] nam;
        nam = stu.getName().split(" ");
        this.cUserName.setText(nam[0]);
        this.cSurname.setText(nam[1]);
        this.cYear.setText(stu.getCurrentYear()+"");
        this.mainName.setText(stu.getName());
        this.yearRange.setText(stu.getFirstYear()+" - "+stu.getCurrentYear());
    }
    public void setSourceFile(String fname){
        this.srcFileString.setText(fname);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        newStudentDialog = new javax.swing.JDialog();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        syear = new javax.swing.JComboBox<>();
        name = new javax.swing.JTextField();
        lastname = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        addCourseWindow = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        year = new javax.swing.JLabel();
        sem = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        resultDisplay = new javax.swing.JDialog();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        myName = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        finalGrade = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        numOfSems = new javax.swing.JLabel();
        avPreMark = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cUserName = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cSurname = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cYear = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        addSem = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        fgrade = new javax.swing.JLabel();
        pfMark = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel19 = new javax.swing.JPanel();
        mainName = new javax.swing.JLabel();
        yearRange = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        bpan = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        srcFileString = new javax.swing.JLabel();
        sep = new javax.swing.JSeparator();
        jToolBar1 = new javax.swing.JToolBar();
        jButton6 = new javax.swing.JButton();
        run = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        newStudentDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        newStudentDialog.setResizable(false);

        jLabel3.setText("Year Started:");

        jLabel5.setText("Student First Name:");

        jLabel8.setText("Student Last Name:");

        syear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010" }));

        name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(syear, 0, 356, Short.MAX_VALUE)
                    .addComponent(name)
                    .addComponent(lastname))
                .addContainerGap())
            .addComponent(jSeparator1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(syear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(121, 121, 121)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(169, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/degree/pkgclass/prediction/wave.png"))); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel14.setText("1. Add Student...");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 31, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Finish");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout newStudentDialogLayout = new javax.swing.GroupLayout(newStudentDialog.getContentPane());
        newStudentDialog.getContentPane().setLayout(newStudentDialogLayout);
        newStudentDialogLayout.setHorizontalGroup(
            newStudentDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newStudentDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newStudentDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newStudentDialogLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator4)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, newStudentDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap())
        );
        newStudentDialogLayout.setVerticalGroup(
            newStudentDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newStudentDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newStudentDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(newStudentDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        addCourseWindow.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        addCourseWindow.setResizable(false);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/degree/pkgclass/prediction/wave.png"))); // NOI18N
        jLabel15.setText("jLabel3");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel16.setText("1. Add Courses...");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 25, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        table.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Code", "Narration", "Mark"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setGridColor(new java.awt.Color(204, 204, 204));
        table.setIntercellSpacing(new java.awt.Dimension(5, 5));
        table.setRowHeight(30);
        table.setSelectionBackground(new java.awt.Color(204, 204, 204));
        table.setSelectionForeground(new java.awt.Color(0, 102, 0));
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setResizable(false);
            table.getColumnModel().getColumn(2).setResizable(false);
        }

        jLabel17.setText("Year:");

        jLabel18.setText("Semester:");

        year.setText("2000");

        sem.setText("1");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addGap(44, 44, 44)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sem)
                    .addComponent(year))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(year))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(sem))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE))
        );

        jButton4.setText("Finish");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Cancel");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout addCourseWindowLayout = new javax.swing.GroupLayout(addCourseWindow.getContentPane());
        addCourseWindow.getContentPane().setLayout(addCourseWindowLayout);
        addCourseWindowLayout.setHorizontalGroup(
            addCourseWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addCourseWindowLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addCourseWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addCourseWindowLayout.createSequentialGroup()
                        .addComponent(jSeparator5)
                        .addContainerGap())
                    .addGroup(addCourseWindowLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addContainerGap())
                    .addGroup(addCourseWindowLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12))))
        );
        addCourseWindowLayout.setVerticalGroup(
            addCourseWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addCourseWindowLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addCourseWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addCourseWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        resultDisplay.setTitle("Prediction");
        resultDisplay.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        resultDisplay.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        resultDisplay.setResizable(false);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jPanel9.setBackground(new java.awt.Color(91, 192, 235));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/degree/pkgclass/prediction/Bullish_50px_1.png"))); // NOI18N

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 50)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("RESULTS");

        myName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        myName.setForeground(new java.awt.Color(255, 255, 255));
        myName.setText("persey");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(myName)
                    .addComponent(jLabel19))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel19)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(myName)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        finalGrade.setBackground(new java.awt.Color(91, 192, 235));
        finalGrade.setFont(new java.awt.Font("Tahoma", 0, 150)); // NOI18N
        finalGrade.setForeground(new java.awt.Color(91, 192, 235));
        finalGrade.setText("2.1");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel7.setText("Degree Class Predicted:");

        jLabel20.setText("Number of Semesters available:");

        jLabel21.setText("Avarage Predicted Mark:");

        jLabel24.setText("Project By Kushinga Mukabeta (2017).");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(finalGrade))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(numOfSems)
                            .addComponent(avPreMark))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24)
                .addGap(213, 213, 213))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(finalGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(numOfSems))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(avPreMark))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(jLabel24))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(26, 26, 26))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout resultDisplayLayout = new javax.swing.GroupLayout(resultDisplay.getContentPane());
        resultDisplay.getContentPane().setLayout(resultDisplayLayout);
        resultDisplayLayout.setHorizontalGroup(
            resultDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        resultDisplayLayout.setVerticalGroup(
            resultDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocation(new java.awt.Point(200, 50));
        setName("Main Window"); // NOI18N

        tree.setModel(dtm);
        jScrollPane3.setViewportView(tree);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Name:");

        cUserName.setText("Empty");

        jLabel4.setText("Surname:");

        cSurname.setText("Empty");

        jLabel6.setText("Final Year:");

        cYear.setText("Empty");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cSurname, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(cUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cYear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cUserName))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cSurname))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cYear))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        addSem.setText("Add Semester");
        addSem.setEnabled(false);
        addSem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSemActionPerformed(evt);
            }
        });

        jLabel2.setText("Project By Kushinga Mukabeta(2017)");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addSem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(addSem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        mainPanel.setBackground(new java.awt.Color(91, 192, 235));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        fgrade.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        pfMark.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Semester"));

        jTable1.setBackground(new java.awt.Color(240, 240, 240));
        jTable1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTable1.setForeground(new java.awt.Color(4, 98, 138));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Course Code", "Course Narration", "Course Mark"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFocusable(false);
        jTable1.setGridColor(new java.awt.Color(240, 240, 240));
        jTable1.setIntercellSpacing(new java.awt.Dimension(10, 10));
        jTable1.setRowHeight(30);
        jTable1.setSelectionBackground(new java.awt.Color(240, 240, 240));
        jTable1.setSelectionForeground(new java.awt.Color(4, 98, 138));
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                        .addGap(315, 315, 315)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fgrade)
                            .addComponent(pfMark))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(fgrade)
                .addGap(18, 18, 18)
                .addComponent(pfMark)
                .addGap(53, 53, 53)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel19.setBackground(new java.awt.Color(91, 192, 235));

        mainName.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        mainName.setForeground(new java.awt.Color(255, 255, 255));
        mainName.setText("Perseverance Mudzinganyama");

        yearRange.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        yearRange.setForeground(new java.awt.Color(255, 255, 255));
        yearRange.setText("2014 - 2017");

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/degree/pkgclass/prediction/Administrator Male_80px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainName)
                    .addComponent(yearRange))
                .addContainerGap(190, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(mainName)
                        .addGap(20, 20, 20)
                        .addComponent(yearRange))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel12)))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel11.setText("Source File:");

        srcFileString.setText("Null");

        javax.swing.GroupLayout bpanLayout = new javax.swing.GroupLayout(bpan);
        bpan.setLayout(bpanLayout);
        bpanLayout.setHorizontalGroup(
            bpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bpanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(srcFileString, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        bpanLayout.setVerticalGroup(
            bpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bpanLayout.createSequentialGroup()
                .addGroup(bpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(srcFileString))
                .addGap(0, 17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bpan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sep)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(sep, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(bpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jToolBar1.setRollover(true);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/degree/pkgclass/prediction/Add File_30px.png"))); // NOI18N
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton6);

        run.setIcon(new javax.swing.ImageIcon(getClass().getResource("/degree/pkgclass/prediction/run.png"))); // NOI18N
        run.setEnabled(false);
        run.setFocusable(false);
        run.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        run.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runActionPerformed(evt);
            }
        });
        jToolBar1.add(run);

        jMenu1.setText("File");

        jMenuItem1.setText("New Student");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Help");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE))
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(1368, 860));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    
    private void addCourseRows(List<ReqCourse> c){
        
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        while(table.getRowCount()!=0){
            for(int i = 0; i < table.getRowCount(); i++){
                model.removeRow(i);
            }
        }
        Object rowData[] = new Object[3];
        
        for(ReqCourse r: c){
            rowData[0] = r.getCourseCode();
            rowData[1] = r.getCourseName();
            model.addRow(rowData);
        }
        
    }
    
    
    private void addSemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSemActionPerformed
        this.addCourseWindow.pack();
        this.addCourseWindow.setLocationRelativeTo(this);
        setCurrentInfo(student);
        this.addCourseWindow.setVisible(true);
    }//GEN-LAST:event_addSemActionPerformed

    private void nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.newStudentDialog.setVisible(false);
        this.newStudentDialog.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Student newUser = new Student();
        AddUserInfo adInfo = new AddUserInfo();
        List<Integer> years = new LinkedList<>();
        String newName = this.name.getText();
        String newSurname = this.lastname.getText();
        this.name.setText("");
        this.lastname.setText("");
        newUser.setName(newName+" "+newSurname);
        int year1 = Integer.parseInt((String)this.syear.getSelectedItem());
        int year2 = year1+3;

        for(int i = year2; i >= year1; i--){
            years.add(i);
        }
        
        int cy = 0;
        int fy = 0;
        years.stream().map((y) -> {
            Year yr = new Year();
            yr.setYear(y);
            return yr;
        }).forEach((yr) -> {
            newUser.addYear(yr);
        });

        newUser.setCurrentYear(year2);
        newUser.setFirstYear(year1);
        adInfo.addUser(newUser);
        
        DefaultTreeModel model = (DefaultTreeModel)this.tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        
        root.add(new DefaultMutableTreeNode(newUser));
        model.reload();
        
        this.newStudentDialog.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int thisYear = Integer.parseInt(this.year.getText());
        int smr = Integer.parseInt(this.sem.getText());
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        
        DefaultMutableTreeNode yrNode = null;
        DefaultMutableTreeNode semNode = null;
        DefaultMutableTreeNode mainNode = null;
        DefaultMutableTreeNode yearNode = null;
        
        int level = selectedNode.getLevel();
        
        switch(level){
            case 1:
                mainNode = selectedNode;
                break;
            case 2:
                mainNode = (DefaultMutableTreeNode)selectedNode.getParent();
                yearNode = selectedNode;
                break;
            case 3:
                mainNode = (DefaultMutableTreeNode)selectedNode.getParent().getParent();
                yearNode = (DefaultMutableTreeNode)selectedNode.getParent();
                break;
        }
        
        //System.out.println(mainNode.getIndex(new DefaultMutableTreeNode(thisYear)));
        
        for(Year y: this.student.getYears()){
            if(y.getYear() == thisYear){
                yrNode = new DefaultMutableTreeNode(y.getYear());
                Semester sm = new Semester();
                sm.setSemesterNumber(smr);
                if(thisYear - student.getFirstYear() == 2){
                    semNode = new DefaultMutableTreeNode("INDUSTRIAL ATTACHMENT");
                }else{
                    semNode = new DefaultMutableTreeNode("Semester "+sm.getSemesterNumber());
                }
                
                
                
                for(int i = 0; i<this.table.getRowCount(); i++){
                    String cc = (String)this.table.getValueAt(i, 0);
                    String cn = (String)this.table.getValueAt(i, 1);
                    String cm = (String)this.table.getValueAt(i, 2);

                    if(cc != null || cn != null || cm!= null){
                        Course cs = new Course();
                        cs.setCourseCode(cc);
                        cs.setCourseNarration(cn);
                        cs.setCourseMark(Float.parseFloat(cm));
                        sm.addCourse(cs);
                    }
                }
                if(sm.getSemesterNumber() == 2 && yearNode.getChildCount() == 1){
                    y.addSemester(sm);
                }else{
                    y.addSemester(sm);
                    yrNode.add(semNode);
                }
            }
        }
        
        if(mainNode != null){
            if(mainNode.isLeaf()){
                model.insertNodeInto(yrNode, mainNode, mainNode.getChildCount());
            }
            else if(yearNode.getLeafCount() == 1){
                model.insertNodeInto(semNode, yearNode, yearNode.getChildCount());
            }
            else if(yrNode != null){
                model.insertNodeInto(yrNode, mainNode, mainNode.getChildCount());
            }
        }

        new AddUserInfo().addCourses(student);
        this.addCourseWindow.setVisible(false);
        this.addCourseWindow.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        this.addCourseWindow.setVisible(false);
        this.addCourseWindow.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        this.newStudentDialog.pack();
        this.newStudentDialog.setLocationRelativeTo(this);
        this.newStudentDialog.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.newStudentDialog.pack();
        this.newStudentDialog.setLocationRelativeTo(this);
        this.newStudentDialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionPerformed
        this.computeAv.processYears(student);
        this.finalGrade.setText(computeAv.getGrade());
        this.avPreMark.setText(computeAv.getFinalMark()+"");
        this.numOfSems.setText(computeAv.getNumberOfSemesters()+"");
        this.myName.setText(student.getName());
        this.resultDisplay.pack();
        this.resultDisplay.setLocationRelativeTo(this);
        this.resultDisplay.setVisible(true);
    }//GEN-LAST:event_runActionPerformed

    /**
     * @param args the command line arguments
     */
    
    
    private void setDtm(DefaultMutableTreeNode dmtn){
        this.dtm = new DefaultTreeModel(dmtn);
    }
    
    private void fillDataToTree(){
        DefaultMutableTreeNode stu = new DefaultMutableTreeNode("Students");
        for(Student tStudent: students){
            
            DefaultMutableTreeNode proName = new DefaultMutableTreeNode(tStudent);
            

            tStudent.getYears().stream().filter((y) -> (!y.getSemesters().isEmpty())).map((Year y1) -> {
                DefaultMutableTreeNode year = new DefaultMutableTreeNode(y1.getYear());
                y1.getSemesters().stream().map((s) -> new DefaultMutableTreeNode("Semester "+s.getSemesterNumber())).forEach((sem) -> {
                    year.add(sem);
                });
                return year;
            }).forEach((year) -> {
                proName.add(year);
            });
            stu.add(proName);
        }
        setDtm(stu);
        
    }
    
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        //</editor-fold>
        try {
            /* Create and display the form */
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
            new Main().fillDataToTree();
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog addCourseWindow;
    private javax.swing.JButton addSem;
    private javax.swing.JLabel avPreMark;
    private javax.swing.JPanel bpan;
    private javax.swing.JLabel cSurname;
    private javax.swing.JLabel cUserName;
    private javax.swing.JLabel cYear;
    private javax.swing.JLabel fgrade;
    private javax.swing.JLabel finalGrade;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField lastname;
    private javax.swing.JLabel mainName;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel myName;
    private javax.swing.JTextField name;
    private javax.swing.JDialog newStudentDialog;
    private javax.swing.JLabel numOfSems;
    private javax.swing.JLabel pfMark;
    private javax.swing.JDialog resultDisplay;
    private javax.swing.JButton run;
    private javax.swing.JLabel sem;
    private javax.swing.JSeparator sep;
    private javax.swing.JLabel srcFileString;
    private javax.swing.JComboBox<String> syear;
    private javax.swing.JTable table;
    private javax.swing.JTree tree;
    private javax.swing.JLabel year;
    private javax.swing.JLabel yearRange;
    // End of variables declaration//GEN-END:variables
}
