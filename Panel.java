package sss.moveabletest;

import java.awt.*;
import javax.swing.*;

public class Panel extends JPanel{
    @Override
    public void paintComponent(Graphics gr){
        super.paintComponent(gr);
        gr.drawImage(MBrot.img, 0, 0, null);
        gr.drawImage(MBrot.txtimg, 0, 0, null);
    }
}
