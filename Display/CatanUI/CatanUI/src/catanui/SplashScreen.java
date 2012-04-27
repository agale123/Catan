/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package catanui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author agale
 */
public class SplashScreen extends JPanel{
    private boolean _host;
    private int _AIPlayers;
    private int _boardSize;
    private int _rollTimer;

    private String _hostname;
    private String _port;
    private String _numCon;
    private String _numAI;

    private int _screen;
    /*
     * 1 : First Splash Screen
     * 2 : Hosting Screen
     * 3 : Client Connect Screen
     * 4 : Advanced Settings
     * 5 : Instructions
     * 6 : Waiting for Connections
     */

    public static void main(String[] args) {
        JFrame window = new JFrame("Menu");
        window.setSize(1000, 722);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SplashScreen s = new SplashScreen();
        window.add(s);
        window.setVisible(true);
    }

    public SplashScreen() {
        super();
        
        
        _screen = 1;
        repaint();
    }


    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        Image water = Toolkit.getDefaultToolkit().getImage("title.jpeg");
        g.drawImage(water, 0, 0, 1000, 722, this);

        Color color = new Color(1,1,1,0.75f);
        g.setPaint(color);
        g.fill(new Rectangle(350,0,500,722));

        setLayout(null);
        setBounds(0,0,1000,722);
        JLabel title = new JLabel("The Quest for Catan");
        title.setOpaque(false);
        title.setBounds(450,10,800,40);
        title.setFont(new Font("SansSerif",Font.PLAIN, 30));

        JPanel j = new JPanel();
        j.setBounds(0,0,1000,722);
        j.setOpaque(false);
        j.add(title);

        switch(_screen) {
            case 1 :
                JLabel host = new JLabel("Host Game");
                host.setFont(new Font("SansSerif",Font.PLAIN, 30));
                host.addMouseListener(
                        new MouseAdapter() {
                            public void mouseReleased(MouseEvent e) {
                                SplashScreen.this.beginHost();
                            }
                        }
                );
                host.setOpaque(false);
                host.setBounds(450,310,200,40);
                j.add(host);

                JLabel client = new JLabel("Join Game");
                client.setFont(new Font("SansSerif",Font.PLAIN, 30));
                client.addMouseListener(
                        new MouseAdapter() {
                            public void mouseReleased(MouseEvent e) {
                                SplashScreen.this.beginClient();
                            }
                        }
                );
                client.setOpaque(false);
                client.setBounds(450,360,200,40);
                j.add(client);

                JLabel instructions = new JLabel("Instructions");
                instructions.setFont(new Font("SansSerif",Font.PLAIN, 30));
                instructions.addMouseListener(
                        new MouseAdapter() {
                            public void mouseReleased(MouseEvent e) {
                                SplashScreen.this.beginInstructions();
                            }
                        }
                );
                instructions.setOpaque(false);
                instructions.setBounds(450,470,200,40);
                j.add(instructions);
                add(j);
                break;
            case 2 :
                JLabel numConnections = new JLabel("Number of Connections");
                numConnections.setFont(new Font("SansSerif",Font.PLAIN, 20));
                numConnections.setBounds(450,210,250,40);
                j.add(numConnections);

                JTextField connections = new JTextField();
                connections.setBounds(450,250,200,40);
                connections.addKeyListener(
                        new KeyListener() {
                            public void keyReleased(KeyEvent k) {
                                SplashScreen.this._numCon = ((JTextField) k.getSource()).getText();
                            }
                            public void keyTyped(KeyEvent e) {}
                            public void keyPressed(KeyEvent e) {}
                });
                j.add(connections);


                JLabel numAI = new JLabel("Number of AI Players");
                numAI.setFont(new Font("SansSerif",Font.PLAIN, 20));
                numAI.setBounds(450,310,250,40);
                j.add(numAI);
                

                JTextField AIplayers = new JTextField();
                AIplayers.setBounds(450,350,200,40);
                AIplayers.addKeyListener(
                        new KeyListener() {
                            public void keyReleased(KeyEvent k) {
                                SplashScreen.this._numAI = ((JTextField) k.getSource()).getText();
                            }
                            public void keyTyped(KeyEvent e) {}
                            public void keyPressed(KeyEvent e) {}
                });
                j.add(AIplayers);                

                

                JLabel portText = new JLabel("Port number:");
                portText.setFont(new Font("SansSerif",Font.PLAIN, 20));
                portText.setBounds(450,390,200,40);
                j.add(portText);


                JTextField port = new JTextField();
                port.setBounds(450,430,200,40);
                j.add(port);
                port.addKeyListener(
                        new KeyListener() {
                            public void keyReleased(KeyEvent k) {
                                SplashScreen.this._port = ((JTextField) k.getSource()).getText();
                            }
                            public void keyTyped(KeyEvent e) {}
                            public void keyPressed(KeyEvent e) {}
                });

                JLabel listen = new JLabel("Begin Listening");
                listen.setFont(new Font("SansSerif",Font.PLAIN, 20));
                listen.setBounds(450,470,250,40);
                listen.addMouseListener(
                    new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            if((_port != null)&& (_numCon != null || _numAI != null)) {
                                SplashScreen.this.beginWaiting();
                            }
                        }
                    });
                j.add(listen);

                JLabel back = new JLabel("Back");
                back.setFont(new Font("SansSerif",Font.PLAIN, 30));
                back.addMouseListener(
                        new MouseAdapter() {
                            public void mouseReleased(MouseEvent e) {
                                SplashScreen.this.beginHome();
                            }
                        }
                );
                back.setBounds(450,570,200,40);
                j.add(back);

                add(j);
                break;
            case 3 :
                portText = new JLabel("Port number:");
                portText.setFont(new Font("SansSerif",Font.PLAIN, 20));
                portText.setBounds(450,200,200,40);
                j.add(portText);


                port = new JTextField();
                port.setBounds(450,240,200,40);
                j.add(port);
                port.addKeyListener(
                        new KeyListener() {
                            public void keyReleased(KeyEvent k) {
                                SplashScreen.this._port = ((JTextField) k.getSource()).getText();
                            }
                            public void keyTyped(KeyEvent e) {}
                            public void keyPressed(KeyEvent e) {}
                });

                JLabel hostText = new JLabel("Host name:");
                hostText.setFont(new Font("SansSerif",Font.PLAIN, 20));
                hostText.setBounds(450,280,200,40);
                j.add(hostText);


                JTextField hostVal = new JTextField();
                hostVal.setBounds(450,320,200,40);
                j.add(hostVal);
                hostVal.addKeyListener(
                        new KeyListener() {
                            public void keyReleased(KeyEvent k) {
                                SplashScreen.this._hostname = ((JTextField) k.getSource()).getText();
                            }
                            public void keyTyped(KeyEvent e) {}
                            public void keyPressed(KeyEvent e) {}
                });

                JLabel connect = new JLabel("Connect");
                connect.setFont(new Font("SansSerif",Font.PLAIN, 20));
                connect.setBounds(450,360,250,40);
                connect.addMouseListener(
                    new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            if((_port != null)&& (_hostname != null)) {
                                SplashScreen.this.beginConnect();
                            }
                        }
                    });
                j.add(connect);


                back = new JLabel("Back");
                back.setFont(new Font("SansSerif",Font.PLAIN, 30));
                back.addMouseListener(
                        new MouseAdapter() {
                            public void mouseReleased(MouseEvent e) {
                                SplashScreen.this.beginHome();
                            }
                        }
                );
                back.setBounds(450,570,200,40);
                j.add(back);

                add(j);
                break;
            case 4 :
                JLabel instr = new JLabel("Here is some advanced settings:");
                instr.setFont(new Font("SansSerif",Font.PLAIN, 20));
                instr.setBounds(450,100,300,100);
                j.add(instr);

                back = new JLabel("Back");
                back.setFont(new Font("SansSerif",Font.PLAIN, 30));
                back.addMouseListener(
                        new MouseAdapter() {
                            public void mouseReleased(MouseEvent e) {
                                SplashScreen.this.beginHome();
                            }
                        }
                );
                back.setBounds(450,570,200,40);
                j.add(back);

                add(j);
                break;
            case 5 :
                instr = new JLabel("Here is how to play:");
                instr.setFont(new Font("SansSerif",Font.PLAIN, 20));
                instr.setBounds(450,100,300,100);
                j.add(instr);

                back = new JLabel("Back");
                back.setFont(new Font("SansSerif",Font.PLAIN, 30));
                back.addMouseListener(
                        new MouseAdapter() {
                            public void mouseReleased(MouseEvent e) {
                                SplashScreen.this.beginHome();
                            }
                        }
                );
                back.setBounds(450,570,200,40);
                j.add(back);
                add(j);
                break;
            case 6 :
                instr = new JLabel("Waiting for incoming connections...");
                instr.setFont(new Font("SansSerif",Font.PLAIN, 20));
                instr.setBounds(450,100,400,100);
                j.add(instr);

                back = new JLabel("Back");
                back.setFont(new Font("SansSerif",Font.PLAIN, 30));
                back.addMouseListener(
                        new MouseAdapter() {
                            public void mouseReleased(MouseEvent e) {
                                SplashScreen.this.beginHome();
                            }
                        }
                );
                back.setBounds(450,570,200,40);
                j.add(back);
                add(j);
                break;
            default:
                instr = new JLabel("Trying to connect to server...");
                instr.setFont(new Font("SansSerif",Font.PLAIN, 20));
                instr.setBounds(450,100,300,200);
                j.add(instr);

                back = new JLabel("Back");
                back.setFont(new Font("SansSerif",Font.PLAIN, 30));
                back.addMouseListener(
                        new MouseAdapter() {
                            public void mouseReleased(MouseEvent e) {
                                SplashScreen.this.beginHome();
                            }
                        }
                );
                back.setBounds(450,570,200,40);
                j.add(back);

                add(j);
                break;

        }

        setVisible(true);
    }

    private void beginConnect() {
        _screen = 7;
        this.removeAll();
        repaint();
    }

    /*
     * 1 : First Splash Screen
     * 2 : Hosting Screen
     * 3 : Client Connect Screen
     * 4 : Advanced Settings
     * 5 : Instructions
     * 6 : Waiting for Connections
     * 7 : Waiting to Connect
     */
    private void beginHost() {
       _screen = 2;
       this.removeAll();
       repaint();
    }

    private void beginClient() {
        _screen = 3;
        this.removeAll();
        repaint();
    }

    private void beginInstructions() {
        _screen = 5;
        this.removeAll();
        repaint();
    }

    private void beginSettings() {
        _screen = 4;
        this.removeAll();
        repaint();
    }

    private void beginHome() {
        _port = null;
        _hostname = null;
        _numCon = null;
        _numAI = null;
        _screen = 1;
        this.removeAll();
        repaint();
    }
    private void beginWaiting() {
        _screen = 6;
        this.removeAll();
        repaint();
    }
}