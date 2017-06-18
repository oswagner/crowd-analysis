//package com.pucrs.viewer;
//import com.jogamp.newt.event.*;
//import com.jogamp.newt.event.MouseEvent;
//import com.jogamp.opengl.*;
//import com.jogamp.opengl.awt.GLCanvas;
//import com.jogamp.opengl.glu.GLU;
//import com.jogamp.opengl.util.gl2.GLUT;
//import com.pucrs.parsing.Parser;
//import com.pucrs.parsing.Person;
//
//import java.awt.event.*;
//
//import javax.swing.*;
//import java.util.List;
//
//public class View extends JFrame implements GLEventListener, com.jogamp.newt.event.MouseListener {
//    private List<Person> personList;
//
//    private GL2 gl;
//    private GLUT glut;
//    private GLU glu;
//
//    private Float rotX, rotY, rotX_ini, rotY_ini;
//    private Float obsX, obsY, obsZ =200f, obsX_ini, obsY_ini, obsZ_ini;
//    private Float fAspect = 1f, angle = 44f;
//    int x_ini,y_ini;
//
//    private static final Float SENS_ROT = 5f;
//    private static final Float SENS_OBS = 10f;
//    private static final Float SENS_TRANSL = 30f;
//
//    private int width = 1200;
//    private int height = 825;
//    private Integer maxWidth, maxHeight;
//
//    public View() {
//        super("crowd-analysis");
//
//        personList = Parser.personList;
//        maxWidth = Parser.maxWidth;
//        maxHeight = Parser.maxHeight;
//
//        GLProfile profile = GLProfile.get(GLProfile.GL2);
//        GLCapabilities capabilities = new GLCapabilities(profile);
//        GLCanvas canvas = new GLCanvas(capabilities);
//        canvas.addGLEventListener(this);
//
//        glut = new GLUT();
//        glu = new GLU();
//
//        this.setName("crowd-analysis");
//        this.getContentPane().add(canvas);
//        this.setSize(width, height);
//        this.setLocationRelativeTo(null);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setVisible(true);
//        this.setResizable(false);
//        this.
//
//        canvas.requestFocusInWindow();
//    }
//
//    @Override
//    public void init(GLAutoDrawable glAutoDrawable) {
//        //gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//        //gl.glLineWidth(2f);
//    }
//
//    @Override
//    public void dispose(GLAutoDrawable glAutoDrawable) {
//
//    }
//
//    void PosicionaObservador() {
//        gl.glMatrixMode(gl.GL_MODELVIEW);
//        gl.glLoadIdentity();
//        gl.glTranslatef(-obsX,-obsY,-obsZ);
//        gl.glRotatef(rotX,1,0,0);
//        gl.glRotatef(rotY,0,1,0);
//        glu.gluLookAt(0.0,80.0,200.0, 0.0,0.0,0.0, 0.0,1.0,0.0);
//    }
//    void EspecificaParametrosVisualizacao() {
//        gl.glMatrixMode(gl.GL_PROJECTION);
//        gl.glLoadIdentity();
//        glu.gluPerspective(angle,fAspect,0.5,500);
//        PosicionaObservador();
//    }
//
//    void desenhaChao() {
//        gl.glColor3f(1,0,1);
//        gl.glLineWidth(3);
//        gl.glBegin(gl.GL_LINES);
//        for(float z=-1000; z<=1000; z+=10)
//        {
//            gl.glVertex3f(-1000,-0.1f,z);
//            gl.glVertex3f( 1000,-0.1f,z);
//        }
//        for(float x=-1000; x<=1000; x+=10)
//        {
//            gl.glVertex3f(x,-0.1f,-1000);
//            gl.glVertex3f(x,-0.1f,1000);
//        }
//        gl.glEnd();
//        gl.glLineWidth(1);
//    }
//
//    @Override
//    public void display(GLAutoDrawable drawable) {
//        this.gl = drawable.getGL().getGL2();
//
//        desenhaChao();
//
//        gl.glMatrixMode(gl.GL_PROJECTION);
//        gl.glLoadIdentity();
//        gl.glOrtho(0.0f, maxWidth+75, maxHeight+75, 0.0f, 0.0f, 2.0f);
//        gl.glClearColor(0.392f, 0.584f, 0.929f, 1.0f);
//
//
////        for (int j = 0; j < 500; j++) {
////            gl.glPushMatrix();
////            for (int i = 0; i < personList.size()-1; i++) {
////                gl.glBegin(gl.GL_LINES);
////                    gl.glVertex3f(personList.get(i).getNextCoord().getX(), 0.0f, personList.get(i).getNextCoord().getY());
////                gl.glEnd();
////            }
////            gl.glPopMatrix();
////        }
//    }
//
//    @Override
//    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
//
//    }
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//        // Salva os par�metros atuais
//        x_ini = e.getX();
//        y_ini = e.getY();
//        obsX_ini = obsX;
//        obsY_ini = obsY;
//        obsZ_ini = obsZ;
//        rotX_ini = rotX;
//        rotY_ini = rotY;
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent mouseEvent) {
//
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent mouseEvent) {
//
//    }
//
//    @Override
//    public void mouseDragged(MouseEvent mouseEvent) {
//
//    }
//
//    @Override
//    public void mouseWheelMoved(MouseEvent mouseEvent) {
//
//    }
//
//    @Override
//    public void mouseClicked(MouseEvent e) {
//        int x = e.getX();
//        int y = e.getY();
//        if (e.getButton() == MouseEvent.BUTTON1) {
//            int deltax = x_ini - x;
//            int deltay = y_ini - y;
//
//            rotY = rotY_ini - deltax / SENS_ROT;
//            rotX = rotX_ini - deltay / SENS_ROT;
//        }
//        if (e.getButton() == MouseEvent.BUTTON2) {
//            int deltaz = y_ini - y;
//            obsZ = obsZ_ini + deltaz / SENS_OBS;
//        }
//        if (e.getButton() == MouseEvent.BUTTON3) {
//            int deltax = x_ini - x;
//            int deltay = y_ini - y;
//
//            obsX = obsX_ini + deltax / SENS_TRANSL;
//            obsY = obsY_ini - deltay / SENS_TRANSL;
//        }
//        PosicionaObservador();
//        //glutPostRedisplay();
//    }
//
//    @Override
//    public void mouseEntered(MouseEvent mouseEvent) {
//
//    }
//
//    @Override
//    public void mouseExited(MouseEvent mouseEvent) {
//
//    }
//}
