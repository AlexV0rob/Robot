package gui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer gameVisualizer;

    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        gameVisualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
