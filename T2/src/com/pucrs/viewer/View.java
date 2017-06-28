package com.pucrs.viewer;

import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import com.pucrs.controller.Controller;
import com.pucrs.controller.DangerZone;
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

    private static final Float CROWD_SIZE_SCALER = 1f;

    private Controller controller;

    private List<Person> personList;
    // sistema de coordenadas (comparado ao plano cartesiano normal)
    // x = x
    // y = z
    private Float cordTargetX = 450f, cordTargetY = 0f, cordTargetZ = 450f, rotX_ini, rotY_ini;
    private Float posCameraX = 450f, posCameraY = -400f, posCameraZ = 450f, obsX_ini, obsY_ini, obsZ_ini;
    private Float fAspect = 1f, angle = 105f;

    private float rotateX, rotateY;   // rotation amounts about axes, controlled by keyboard

    int x_ini, y_ini;

    private Camera camera;

    private GL2 GL;
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
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addKeyListener(this);
        canvas.setSize(1900, 1500);

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
        this.GL = gl;

        camera = new Camera();
        camera.apply(gl);

        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL.GL_PROJECTION);  // TODO: Set up a better projection?
        gl.glLoadIdentity();
        gl.glOrtho(-1, 1, -1, 1, -2, 2);
        gl.glMatrixMode(GL.GL_MODELVIEW);

        gl.glLoadIdentity();             // Set up modelview transform.
        gl.glRotatef(rotateY, 0, 1, 0);
        gl.glRotatef(rotateX, 1, 0, 0);

        // TODO: add drawing code!!
        especificaParametrosVisualizacao();

        gl.glColor3f(0.0f, 0.0f, 1.0f);

        gl.glPushMatrix();
        gl.glTranslatef(0, 28, 0);

        //glut.glutWireTeapot(35);

        gl.glPopMatrix();

        desenhaChao();

        desenhaPessoas();

        controller.prepareNextCoords();

        if (controller.hasDanger()) {
            controller.updateDangerZone();
        }

        gl.glFlush();

    }

    private void desenhaPessoas() {
        //GL.glPushMatrix();
        GL.glColor3f(1, 1, 1);
        GL.glLineWidth(2);
        for (int i = 0; i < personList.size() - 1; i++) {
            desenhaPessoa(personList.get(i).getCurrentCoord());
        }
        //GL.glPopMatrix();
    }

    private void desenhaPessoa(Coords coords) {
        GL.glBegin(GL.GL_LINES);
            // top
            GL.glVertex3f(coords.getX()+5f, 0.1f,coords.getY()+5f);
            GL.glVertex3f(coords.getX()+5f, 0.1f, coords.getY()-5f);

            // right
            GL.glVertex3f(coords.getX()-5f, 0.1f, coords.getY()+5f);
            GL.glVertex3f(coords.getX()+5f, 0.1f, coords.getY()+5f);

            // left
            GL.glVertex3f(coords.getX()+5f, 0.1f, coords.getY()-5f);
            GL.glVertex3f(coords.getX()-5f, 0.1f, coords.getY()-5f);

            // bottom
            GL.glVertex3f(coords.getX()-5f, 0.1f, coords.getY()-5f);
            GL.glVertex3f(coords.getX()-5, 0.1f, coords.getY()+5f);
        GL.glEnd();
    }

    private void controlDanger(Float x, Float z) {
        controller.dropDanger(new DangerZone(new Coords(x, z), 10f));
    }

    void posicionaObservador() {
        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glLoadIdentity();
        glu.gluLookAt(posCameraX, posCameraY, posCameraZ, cordTargetX, cordTargetY, cordTargetZ, 0f, 0f, 1f);
    }

    void desenhaChao() {
        GL.glColor3f(1, 0, 1);
        GL.glLineWidth(1);
        GL.glBegin(GL.GL_LINES);
        for (float z = -1000; z <= 1000; z += 10) {
            GL.glVertex3f(-1000, -0.1f, z);
            GL.glVertex3f(1000, -0.1f,z);
        }
        for (float x = -1000; x <= 1000; x += 10) {
            GL.glVertex3f(x, -0.1f, -1000);
            GL.glVertex3f(x, -0.1f, 1000);
        }
        GL.glEnd();
        GL.glLineWidth(1);
    }

    void especificaParametrosVisualizacao() {
        GL.glMatrixMode(GL.GL_PROJECTION);
        GL.glLoadIdentity();
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
            cordTargetX = posCameraX = posCameraX - 25f;
        else if (key == KeyEvent.VK_RIGHT)
            cordTargetX = posCameraX = posCameraX + 25f;
        else if (key == KeyEvent.VK_DOWN)
            cordTargetZ = posCameraZ = posCameraZ - 25f;
        else if (key == KeyEvent.VK_UP)
            cordTargetZ = posCameraZ = posCameraZ + 25f;
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
        controlDanger(new Float(e.getX()),new Float(e.getY()));
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
////        System.out.println("posCameraX: " + posCameraX + ", posCameraY; " + posCameraY + ", posCameraZ: " + posCameraZ);
////
////        System.out.println("rotX_ini: " + rotX_ini + ", rotY_ini; " + rotY_ini);
////        System.out.println("cordTargetX: " + cordTargetX + ", cordTargetY; " + cordTargetY);
//
//        x_ini = 0;
//        y_ini = 0;
//
//        obsX_ini = posCameraX;
//        obsY_ini = posCameraY;
//        obsZ_ini = posCameraZ;
//
//        rotX_ini = cordTargetX;
//        rotY_ini = cordTargetY;
//
//        int x = e.getX();
//        int y = e.getY();
//        //if (e.getButton() == MouseEvent.BUTTON1) {
//            int deltax = x_ini - x;
//            int deltay = y_ini - y;
//
//            cordTargetY = rotY_ini - deltax / SENS_ROT;
//            cordTargetX = rotX_ini - deltay / SENS_ROT;
//        //}
////        if (e.getButton() == MouseEvent.BUTTON2) {
////            int deltaz = y_ini - y;
////            posCameraZ = obsZ_ini + deltaz / SENS_OBS;
////        }
////        if (e.getButton() == MouseEvent.BUTTON3) {
////            int deltax = x_ini - x;
////            int deltay = y_ini - y;
////
////            posCameraX = obsX_ini + deltax / SENS_TRANSL;
////            posCameraY = obsY_ini - deltay / SENS_TRANSL;
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