package org.example.views;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.example.helpers.utilities.colorPurple;

public class HeaderPanel extends JPanel {

    private final JLabel label;
    public HeaderPanel( ) {
        super();
        this.setLayout(new FlowLayout(FlowLayout.LEFT,10,5));
        this.setBackground(Color.lightGray);
        this.setBackground(Color.white);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.label = new JLabel(df.format(new Date()).trim());
        this.label.setForeground(colorPurple);
        this.label.setFont(new Font("Sans-serif",Font.BOLD,24));
        this.add(this.label);
    }
}
