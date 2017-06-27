package com.pucrs.viewer;

import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import com.pucrs.controller.Controller;
import com.pucrs.parsing.Coords;
import com.pucrs.parsing.Parser;
import com.pucrs.controller.Person;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;

import java.util.List;

public class View extends JPanel implements
        GLEventListener, KeyListener, MouseListener, MouseMotionListener, ActionListener {

    private Controller controller;

    private List<Person> personList;

    private Float rotX = 90f, rotY = 90f, rotX_ini, rotY_ini;
    private Float obsX = 260f, obsY = 905f, obsZ = 280f, obsX_ini, obsY_ini, obsZ_ini;
    private Float fAspect = 1f, angle = 44f;

    private float rotateX, rotateY;   // rotation amounts about axes, controlled by keyboard

    int x_ini, y_ini;

    private Camera camera;

    private static final Float SCALER = 5f;
    private static final Float SENS_ROT = 5f;
    private static final Float SENS_OBS = 10f;
    private static final Float SENS_TRANSL = 30f;

    private GL2 gl;
    private GLU glu = new GLU();

    private Timer animationTimer;

    public View() {
        controller = new Controller();

        GLProfile glProfile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(glProfile);

        final GLCanvas canvas = new GLCanvas(capabilities);
        final Frame frame = new Frame("crowd-emergency");
        final Animator animator = new Animator(canvas);

        canvas.addGLEventListener(this);

        frame.add(canvas);
        frame.setSize(1900, 1500);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                animator.stop();
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setVisible(true);

        animator.start();

        canvas.requestFocus();

        personList = Parser.personList;
    }

    // ---------------  Methods of the GLEventListener interface -----------

    private GLUT glut = new GLUT();  // for drawing the teapot

    /**
     * This method is called when the OpenGL display needs to be redrawn.
     */
    public void display(GLAutoDrawable drawable) {
        // called when the panel needs to be drawn

        GL2 gl = drawable.getGL().getGL2();
        this.gl = gl;

        camera = new Camera();
        camera.apply(gl);

        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(gl.GL_PROJECTION);  // TODO: Set up a better projection?
        gl.glLoadIdentity();
        gl.glOrtho(-1, 1, -1, 1, -2, 2);
        gl.glMatrixMode(gl.GL_MODELVIEW);

        gl.glLoadIdentity();             // Set up modelview transform.
        gl.glRotatef(rotateY, 0, 1, 0);
        gl.glRotatef(rotateX, 1, 0, 0);

        // TODO: add drawing code!!
        especificaParametrosVisualizacao();

        gl.glColor3f(0.0f, 0.0f, 1.0f);

        gl.glPushMatrix();
        gl.glTranslatef(0, 28, 0);

        glut.glutWireTeapot(35);

        gl.glPopMatrix();

        desenhaChao();

        desenhaPessoas();

        controller.prepareNextCoords();

        gl.glFlush();

    }

    private void desenhaPessoas() {
        //gl.glPushMatrix();
        gl.glColor3f(1, 1, 1);
        gl.glLineWidth(3);
        for (int i = 0; i < personList.size() - 1; i++) {
            desenhaPessoa(personList.get(i).getCurrentCoord());
        }
        //gl.glPopMatrix();
    }

    private void desenhaPessoa(Coords coords) {
        gl.glBegin(gl.GL_LINES);
            gl.glVertex3f(coords.getX()+10f, 0.1f,coords.getY()+10f);
            gl.glVertex3f(coords.getX()+10f, 0.1f, coords.getY()-10f);
            gl.glVertex3f(coords.getX()-10f, 0.1f, coords.getY()-10f);
            gl.glVertex3f(coords.getX()-10f, 0.1f, coords.getY()+10f);
        gl.glEnd();
    }

    void posicionaObservador() {
        //System.out.println("obsX: " + obsX + ", obsY; " + obsY + ", obsZ: " + obsZ);
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(-obsX, -obsY, -obsZ);
        gl.glRotatef(rotX, 1, 0, 0);
        gl.glRotatef(rotY, 0, 1, 0);
        camera.lookAt(obsX, obsY, obsZ, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
    }

    void desenhaChao() {
        gl.glColor3f(1, 0, 1);
        gl.glLineWidth(2);
        gl.glBegin(gl.GL_LINES);
        for (float z = -1000; z <= 1000; z += 10) {
            gl.glVertex3f(-1000, -0.1f, z);
            gl.glVertex3f(1000, -0.1f, z);
        }
        for (float x = -1000; x <= 1000; x += 10) {
            gl.glVertex3f(x, -0.1f, -1000);
            gl.glVertex3f(x, -0.1f, 1000);
        }
        gl.glEnd();
        gl.glLineWidth(1);
    }

    void especificaParametrosVisualizacao() {
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(angle, fAspect, 0.5, 500);
        posicionaObservador();
    }


    /**
     * This is called when the GLJPanel is first created.  It can be used to initialize
     * the OpenGL drawing context.
     */
    public void init(GLAutoDrawable drawable) {
        // called when the panel is created
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.8F, 0.8F, 0.8F, 1.0F);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /**
     * Called when the size of the GLJPanel changes.  Note:  glViewport(x,y,width,height)
     * has already been called before this method is called!
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    /**
     * This is called before the GLJPanel is destroyed.  It can be used to release OpenGL resources.
     */
    public void dispose(GLAutoDrawable drawable) {
    }


    // ------------ Support for keyboard handling  ------------

    /**
     * Called when the user presses any key.
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();  // Tells which key was pressed.
        if (key == KeyEvent.VK_LEFT)
            rotateY -= 15;
        else if (key == KeyEvent.VK_RIGHT)
            rotateY += 15;
        else if (key == KeyEvent.VK_DOWN)
            rotateX += 15;
        else if (key == KeyEvent.VK_UP)
            rotateX -= 15;
        else if (key == KeyEvent.VK_HOME)
            rotateX = rotateY = 0;
        repaint();
    }

    /**
     * Called when the user types a character.
     */
    public void keyTyped(KeyEvent e) {
        char ch = e.getKeyChar();  // Which character was typed.
    }

    public void keyReleased(KeyEvent e) {
    }

    // --------------------------- animation support ---------------------------

    private int frameNumber = 0;

    private boolean animating;

    private void updateFrame() {
        // TODO:  add any other updating required for the next frame.
        frameNumber++;
    }

    public void startAnimation() {
        if (!animating) {
            if (animationTimer == null) {
                animationTimer = new Timer(30, this);
            }
            animationTimer.start();
            animating = true;
        }
    }

    public void pauseAnimation() {
        if (animating) {
            animationTimer.stop();
            animating = false;
        }
    }

    public void actionPerformed(ActionEvent evt) {
        updateFrame();
    }


    // ---------------------- support for mouse events ----------------------

    private boolean dragging;  // is a drag operation in progress?

    private int startX, startY;  // starting location of mouse during drag
    private int prevX, prevY;    // previous location of mouse during drag

    /**
     * Called when the user presses a mouse button on the display.
     */
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Called when the user releases a mouse button after pressing it on the display.
     */
    public void mouseReleased(MouseEvent evt) {
        if (!dragging) {
            return;
        }
        dragging = false;
        // TODO:  finish drag (generally nothing to do here)
    }

    /**
     * Called during a drag operation when the user drags the mouse on the display/
     */
    public void mouseDragged(MouseEvent e) {
//        System.out.println("x_ini: " + x_ini + ", y_ini: " + y_ini);
////        System.out.println("getX: " + e.getX() + ", getY: " + e.getY());
////
////        System.out.println("obsX_ini: " + obsX_ini + ", obsY_ini; " + obsY_ini + ", obsZ_ini: " + obsZ_ini);
////        System.out.println("obsX: " + obsX + ", obsY; " + obsY + ", obsZ: " + obsZ);
////
////        System.out.println("rotX_ini: " + rotX_ini + ", rotY_ini; " + rotY_ini);
////        System.out.println("rotX: " + rotX + ", rotY; " + rotY);
//
//        x_ini = 0;
//        y_ini = 0;
//
//        obsX_ini = obsX;
//        obsY_ini = obsY;
//        obsZ_ini = obsZ;
//
//        rotX_ini = rotX;
//        rotY_ini = rotY;
//
//        int x = e.getX();
//        int y = e.getY();
//        //if (e.getButton() == MouseEvent.BUTTON1) {
//            int deltax = x_ini - x;
//            int deltay = y_ini - y;
//
//            rotY = rotY_ini - deltax / SENS_ROT;
//            rotX = rotX_ini - deltay / SENS_ROT;
//        //}
////        if (e.getButton() == MouseEvent.BUTTON2) {
////            int deltaz = y_ini - y;
////            obsZ = obsZ_ini + deltaz / SENS_OBS;
////        }
////        if (e.getButton() == MouseEvent.BUTTON3) {
////            int deltax = x_ini - x;
////            int deltay = y_ini - y;
////
////            obsX = obsX_ini + deltax / SENS_TRANSL;
////            obsY = obsY_ini - deltay / SENS_TRANSL;
////        }
//        posicionaObservador();
//
//        display.repaint();
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent evt) {
    }

    public void mouseExited(MouseEvent evt) {
    }
}