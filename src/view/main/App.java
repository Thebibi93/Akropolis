package view.main;

import javax.swing.JFrame;
import javax.swing.JPanel;

import view.main.states.AppState;
import view.main.states.StartState;

public class App extends JFrame{
    private final int WIDTH = 1500;
    private final int HEIGHT = 700;

    private JPanel screen = new JPanel();
    private static final App INSTANCE = new App();

    public AppState appState;
    
    public App() {
        screen.setLayout(new java.awt.BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Akropolis");
        setResizable(false);
        
        getContentPane().setBackground(java.awt.Color.BLACK);
        screen.setBackground(java.awt.Color.WHITE);
        screen.setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));
        add(screen);
        pack();

        setVisible(true);
    }

    public static App getInstance() {
        return INSTANCE;
    }

    public JPanel getScreen() {
        return screen;
    }

    public void run() {
        appState = AppState.START;
        appState.getState().enter();
    }
    public void exitToMainMenu() {
        // Logique pour revenir au menu principal
        appState.changeState(StartState.getInstance());
    }

    public static void main(String[] args) {
        App app = App.getInstance();
        app.run();
    }
}