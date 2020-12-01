package sss.moveabletest;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.lang.Math.*;

public class MBrot {
    //----------------------Starting Variables--------------------
    //These would get changed by the user in a side panel,
    //but here we're just gonna change them in the code

    // Changeable by user:
    /*
    private static double zoom = 1500;
    private static double centera = -0.748;
    private static double centerb =  0.1;
    private static double n = 2;
    */

    private static double zoom = 1;
    private static double centera = 0;
    private static double centerb =  0;
    private static double n = 2;

    private static int iterations = 500;

    // Whether, for each render, it starts at 1 and continues onward, and when to stop
    private static boolean progressiveiteration = false;
    private static int iterationskip = 100;
    private static int maxiteration = 5000;
    private static int miniteration = 1;

    // Color Set (menu for these later):
    private static int colornum = 6;  // Number of starting colors: CAN BE 0
    private static int shadenum = 10;  // Number of shades per color: CAN BE 0
    private static ArrayList<Integer> color1 = new ArrayList<>(Arrays.asList(255, 0, 0));
    private static ArrayList<Integer> color2 = new ArrayList<>(Arrays.asList(255, 150, 0));
    private static ArrayList<Integer> color3 = new ArrayList<>(Arrays.asList(255, 255, 0));
    private static ArrayList<Integer> color4 = new ArrayList<>(Arrays.asList(0, 255, 0));
    private static ArrayList<Integer> color5 = new ArrayList<>(Arrays.asList(0, 0, 255));
    private static ArrayList<Integer> color6 = new ArrayList<>(Arrays.asList(255, 0, 255));
    private static ArrayList<ArrayList<Integer>> colorstartlist =
            new ArrayList<>(Arrays.asList(color1, color2, color3, color4, color5, color6));

    // Not Changeable by user:
    private static double ratio = (3d / 4d);
    private static int width = 1000;
    private static int def_iterations = 500;
    private static double def_arange = 4;
    private static double def_brange = 3;
    private static ArrayList<ArrayList<Integer>> colorlist = new ArrayList<>();

    //----------------------Dependent Variables--------------------
    // These are set here and aren't changed in dependents()
    private static double height_d = ratio * width;
    private static int height = (int)height_d;

    //These are changed in dependents()
    private static double arange = 4d;
    private static double brange = 3d;
    private static double alb = -2.5d;
    private static double aub = 1.5d;
    private static double blb = -1.5d;
    private static double bub = 1.5d;

    //---------------------Visual Objects------------------------
    //These are the components for swing and awt to render
    //The Buffered Image will also get changed

    //Panel
    private static JPanel panel = new Panel();

    //Buffered Image
    static BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    //Iteration Text
    static BufferedImage txtimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    // Starting point called by main.
    // Creates frame and adds panel to it.
    // Calls dependents() and drawLoop().
    private static void start(){
        dependents();

        JFrame frame = new JFrame();
        frame.getContentPane().setPreferredSize(new Dimension(width, height));
        frame.pack();
        frame.setResizable(false);
        frame.setTitle("MBrot Test3");
        frame.setLayout(null);
        //frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setSize(width, height);
        panel.setVisible(true);
        frame.add(panel);

        colorSet();
        drawLoop();
    }

    // Sets the dependent variables based on the user's choice
    private static void dependents(){
        // -----Iterations-----
        // Based on Zoom = 1 and default iterations
        //double iterations_d = def_iterations * zoom;
        //iterations = (int)iterations_d;

        // -----A Range-----
        arange = def_arange * (1 / zoom);

        // -----B Range-----
        brange = def_brange * (1 / zoom);

        // -----ALB and AUB-----
        alb = centera - (arange / 2);
        aub = centera + (arange / 2);

        // -----BLB and BUB-----
        blb = centerb - (brange / 2);
        bub = centerb + (brange / 2);

        // Remake images
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        txtimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    //Creates a list of colors for getRGB()
    private static void colorSet() {
        if (colornum == 1) {
            colorlist.add(color1);
        } else if (colornum == 0) {
            ArrayList<Integer> clr = new ArrayList<>(Arrays.asList(255, 255, 255));
            colorlist.add(clr);
        }else {
            if (shadenum != 0) {
                for (int i = 0; i < colornum - 1; i++) {
                    // Create a list of shades between each color and the next
                    ArrayList<Integer> currentcolor = colorstartlist.get(i);
                    int ccred = currentcolor.get(0);
                    int ccgreen = currentcolor.get(1);
                    int ccblue = currentcolor.get(2);
                    ArrayList<Integer> nextcolor = colorstartlist.get(i + 1);
                    int ncred = nextcolor.get(0);
                    int ncgreen = nextcolor.get(1);
                    int ncblue = nextcolor.get(2);

                    // Difference between each color, which will be divided by number of shades (note that negatives are okay)
                    int difred = ncred - ccred;
                    int difgreen = ncgreen - ccgreen;
                    int difblue = ncblue - ccblue;

                    // How much each color component will increment by to reach the next shade
                    int incred = difred / shadenum;
                    int incgreen = difgreen / shadenum;
                    int incblue = difblue / shadenum;

                    for (int sh = 0; sh < shadenum; sh++) {
                        int thisred = ccred + (incred * sh);
                        int thisgreen = ccgreen + (incgreen * sh);
                        int thisblue = ccblue + (incblue * sh);
                        ArrayList<Integer> thiscolor = new ArrayList<>(Arrays.asList(thisred, thisgreen, thisblue));
                        colorlist.add(thiscolor);
                    }
                }
                // Create a list of shades between each color and the next
                ArrayList<Integer> currentcolor = colorstartlist.get(colorstartlist.size() - 1);
                int ccred = currentcolor.get(0);
                int ccgreen = currentcolor.get(1);
                int ccblue = currentcolor.get(2);
                ArrayList<Integer> nextcolor = colorstartlist.get(0);
                int ncred = nextcolor.get(0);
                int ncgreen = nextcolor.get(1);
                int ncblue = nextcolor.get(2);

                // Difference between each color, which will be divided by number of shades (note that negatives are okay)
                int difred = ncred - ccred;
                int difgreen = ncgreen - ccgreen;
                int difblue = ncblue - ccblue;

                // How much each color component will increment by to reach the next shade
                int incred = difred / shadenum;
                int incgreen = difgreen / shadenum;
                int incblue = difblue / shadenum;

                for (int sh = 0; sh < shadenum; sh++) {
                    int thisred = ccred + (incred * sh);
                    int thisgreen = ccgreen + (incgreen * sh);
                    int thisblue = ccblue + (incblue * sh);
                    ArrayList<Integer> thiscolor = new ArrayList<>(Arrays.asList(thisred, thisgreen, thisblue));
                    colorlist.add(thiscolor);
                }
            } else{
                for (int i = 0; i < colornum; i++){
                    colorlist.add(colorstartlist.get(i));
                }
            }
        }
    }

    // Draws the set to the BufferedImage img.
    private static void drawLoop(){
        Graphics2D gr = (Graphics2D) img.getGraphics();
        if (!progressiveiteration) {
            // Fill background
            Color bgcolor = new Color(175, 175, 175);
            gr.setColor(bgcolor);
            gr.fillRect(0, 0, width, height);
            gr.dispose();

            //Iteration String display: fills image with transparency than adds text
            Graphics2D grtxt = (Graphics2D) txtimg.getGraphics();
            grtxt.clearRect(0, 0, width, height);
            grtxt.setComposite(AlphaComposite.Clear);
            grtxt.fillRect(0, 0, width, height);
            grtxt.setComposite(AlphaComposite.SrcOver);
            String iterstr = Integer.toString(iterations);
            Color txtcolor = new Color(255, 255, 255);
            grtxt.setColor(txtcolor);
            grtxt.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            grtxt.drawString(iterstr, 5, 25);
            panel.repaint();
            grtxt.dispose();

            // Draw points of set
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    List<Double> ab = getAB(x, y);
                    double a = ab.get(0);
                    double b = ab.get(1);
                    int steps = getSteps(a, b);
                    List<Integer> rgb = getRGB(steps);
                    int red = rgb.get(0);
                    int green = rgb.get(1);
                    int blue = rgb.get(2);
                    int color = (red << 16) | (green << 8) | blue;
                    img.setRGB(x, y, color);
                    panel.repaint();
                }
            }
        } else{
            // Draw points of set, starting with iteration = 1 and going onward
            for (int i = miniteration; i <= maxiteration; i += iterationskip) {
                iterations = i;
                // Fill background
                Color bgcolor = new Color(175, 175, 175);
                gr.setColor(bgcolor);
                gr.fillRect(0, 0, width, height);
                gr.dispose();

                //Iteration String display: fills image with transparency than adds text
                Graphics2D grtxt = (Graphics2D) txtimg.getGraphics();
                grtxt.clearRect(0, 0, width, height);
                grtxt.setComposite(AlphaComposite.Clear);
                grtxt.fillRect(0, 0, width, height);
                grtxt.setComposite(AlphaComposite.SrcOver);
                String iterstr = Integer.toString(iterations);
                Color txtcolor = new Color(255, 255, 255);
                grtxt.setColor(txtcolor);
                grtxt.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                grtxt.drawString(iterstr, 5, 25);

                int color = (255 << 16);
                Color col = new Color(color);

                /*
                // Draw center dot
                color = (255 << 16);
                col = new Color(color);
                grtxt.setColor(col);
                grtxt.fillOval(width / 2 - 5, height / 2 - 5, 10, 10);
                panel.repaint();
                 */

                grtxt.dispose();

                //Point Drawing
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        List<Double> ab = getAB(x, y);
                        double a = ab.get(0);
                        double b = ab.get(1);
                        int steps = getSteps(a, b);
                        List<Integer> rgb = getRGB(steps);
                        int red = rgb.get(0);
                        int green = rgb.get(1);
                        int blue = rgb.get(2);
                        color = (red << 16) | (green << 8) | blue;
                        img.setRGB(x, y, color);
                        panel.repaint();
                    }
                }
            }

            if (iterations < maxiteration){
                iterations = maxiteration;
                // Fill background
                Color bgcolor = new Color(175, 175, 175);
                gr.setColor(bgcolor);
                gr.fillRect(0, 0, width, height);
                gr.dispose();

                //Iteration String display: fills image with transparency than adds text
                Graphics2D grtxt = (Graphics2D) txtimg.getGraphics();
                grtxt.clearRect(0, 0, width, height);
                grtxt.setComposite(AlphaComposite.Clear);
                grtxt.fillRect(0, 0, width, height);
                grtxt.setComposite(AlphaComposite.SrcOver);
                String iterstr = Integer.toString(iterations);
                Color txtcolor = new Color(255, 255, 255);
                grtxt.setColor(txtcolor);
                grtxt.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                grtxt.drawString(iterstr, 5, 25);

                int color = (255 << 16);
                Color col = new Color(color);

                /*
                // Draw center dot
                color = (255 << 16);
                col = new Color(color);
                grtxt.setColor(col);
                grtxt.fillOval(width / 2 - 5, height / 2 - 5, 10, 10);
                panel.repaint();
                 */

                grtxt.dispose();

                //Point Drawing
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        List<Double> ab = getAB(x, y);
                        double a = ab.get(0);
                        double b = ab.get(1);
                        int steps = getSteps(a, b);
                        List<Integer> rgb = getRGB(steps);
                        int red = rgb.get(0);
                        int green = rgb.get(1);
                        int blue = rgb.get(2);
                        color = (red << 16) | (green << 8) | blue;
                        img.setRGB(x, y, color);
                        panel.repaint();
                    }
                }
            }
        }

        //--------------------TEST STUFF-----------------------
        gr = (Graphics2D) img.getGraphics();
        int color = (255 << 16) | (1 << 8) | 1;
        Color col = new Color(color);

        /*
        //TEST: draw center of screen based on centera and centerb
        double centerx_d = (width * (centera - alb)) / arange;
        int centerx = (int)centerx_d;
        double centery_d = (height * (centerb - blb)) / brange;
        int centery = (int)centery_d;
        int color = (255 << 16) | (1 << 8) | 1;
        Color col = new Color(color);
        gr.setColor(col);
        gr.fillOval(centerx - 5, centery - 5, 10, 10);
         */

        /*
        //TEST: draw complex plane origin based on a and b bounds
        double originx_d = (width * (0 - alb)) / (aub - alb);
        int originx = (int)originx_d;
        double originy_d = (height * (0 - blb)) / (bub - blb);
        int originy = (int)originy_d;
        color = (1 << 16) | (1 << 8) | 1;
        col = new Color(color);
        gr.setColor(col);
        gr.fillOval(originx - 5, originy - 5, 10, 10);
        panel.repaint();
        */

        /*
        //TEST: screen corners
        color = (255 << 16) | (255 << 8) | 255;
        col = new Color(color);
        gr.setColor(col);
        gr.setStroke(new BasicStroke(5));
        gr.drawOval(-5, -5, 10, 10);
        gr.drawOval(width - 5, height - 5, 10, 10);
        panel.repaint();
        */

        //TEST: draw center of screen raw
        /*
        color = (255 << 16);
        col = new Color(color);
        gr.setColor(col);
        gr.fillOval(width / 2, height / 2, 10, 10);
        panel.repaint();
         */

        gr.dispose();
    }

    // Return a and b based on x, y, width, height, and bounds
    private static List<Double> getAB(int x, int y){
        List<Double> ablist = new ArrayList<>();
        // Formula for getting a:
        double a = ((x * (aub - alb)) / width) + alb;
        // Formula for getting b:
        double b = (((y * (bub - blb)) / height) + blb) * -1;
        ablist.add(a);
        ablist.add(b);
        return ablist;
    }

    // Determine the number of steps before escaping.
    // If it never escapes (in the set), steps = 0.

    //Using complex numbers can be complicated.
    //The original equation is z^2 + c, and then that result gets re-fed back into the equation as z.
    //Z(old) and C are both complex numbers.
    //Therefore we can break them both down into:
    // z = za + zbi
    // c = a + bi
    //Note that like war, c never changes.

    //Our equation becomes:
    // (za + zbi)^2 + a + bi
    // (za + zbi)(za + zbi) + a + bi
    // za^2 + zazbi + zazbi + zbi^2 + a + bi
    // za^2 + 2zazbi - zb^2 + a + bi
    // (za^2 - zb^2 + a) + (2zazb + b)i

    //This result is in complex form, and gives us our newza and newzb
    // newza = za^2 - zb^2 + a
    // newzb = (2 * za * zb) + b

    //We then set the old z's to the new z's
    // za = newza
    // zb = newzb

    //Then we find the absolute distance (d), and if that's <= 2 we rerun with our new za/zb.
    //If it's greater than 2, we stop the iterations.
    // d = |sqrt(|za|^2 + |zb|^2)|
    private static int getSteps(double a, double b){
        boolean go = true;
        int steps = 0;
        double za = 0;
        double zb = 0;
        boolean escape = false;
        if (n == 2) {
            while (go) {
                if (steps >= iterations) {
                    go = false;
                } else {
                    steps++;

                    double newza = Math.pow(za, 2) - Math.pow(zb, 2) + a;
                    double newzb = (2 * za * zb) + b;
                    za = newza;
                    zb = newzb;

                    double distance = Math.abs(Math.sqrt((za * za) + (zb * zb)));

                    if (distance > 2) {
                        escape = true;
                        go = false;
                    }
                }
            }
        } else {
            // THIS IS FOR IF N IS NOT 2, FOR GENERALIZED MANDELBROT
            while (go) {
                if (steps >= iterations) {
                    go = false;
                } else {
                    steps++;

                    if (za == 0 && zb == 0){
                        za = a;
                        zb = b;
                    } else {
                        // Find the magnitude (r) of Z
                        double r = Math.abs(Math.sqrt((za * za) + (zb * zb)));

                        // Find THETA

                        // Adjust THETA based on quadrant
                        double theta = Math.atan(Math.abs(zb / za));
                        double thetadeg = Math.toDegrees(theta);
                            // Quadrant 1
                        if (za > 0 && zb > 0){
                            assert true;
                            // Quadrant 2
                        } else if (za < 0 && zb > 0) {
                            thetadeg = 180 - thetadeg;
                            // Quadrant 3
                        } else if (za < 0 && zb < 0){
                            thetadeg = (180 - thetadeg) * -1;
                            // Quadrant 4
                        } else if (za > 0 && zb < 0){
                            thetadeg = thetadeg * -1;
                            // a axis, right side
                        } else if (zb == 0 && za > 0) {
                            thetadeg = 0;
                            // a axis, left side
                        } else if (zb == 0 && za < 0) {
                            thetadeg = 180;
                            // b axis, up
                        } else if (za == 0 && zb > 0) {
                            thetadeg = 90;
                            // b axis, down
                        } else if (za == 0 && zb < 0) {
                            thetadeg = -90;
                        }

                        theta = Math.toRadians(thetadeg);

                        double nthet = n * theta;

                        /*

                        double nthetdeg = Math.toDegrees(nthet);
                        while (true){
                            if (nthetdeg <= 0){
                                nthetdeg += 360;
                            } else if (nthetdeg > 360) {
                                nthetdeg -= 360;
                            } else {
                                break;
                            }
                        }
                        nthet = Math.toRadians(nthetdeg);

                         */

                        double xa = (Math.pow(r, n) * (Math.cos(nthet)));
                        double xb = (Math.pow(r, n) * (Math.sin(nthet)));
                        za = xa + a;
                        zb = xb + b;
                    }
                    double distance = Math.abs(Math.sqrt((za * za) + (zb * zb)));
                    if (distance > 2) {
                        escape = true;
                        go = false;
                    }
                }
            }
        }

        // If the point never escaped (in the set), we set steps to 0;
        // otherwise, steps remains as the number of iterations before escape;
        if (!escape) {
            steps = 0;
        }
        return steps;
    }

    // Get the color of the coordinate (currently just 2 colors)
    private static List<Integer> getRGB(int steps){
        List<Integer> rgb = new ArrayList<>();
        if (steps == 0){
            rgb.add(0);
            rgb.add(0);
            rgb.add(0);

        //If it's not in the set:
        } else{

            // If the number of steps is less than the length of the color list,
            // the color is just colorlist[steps - 1]
            if (steps <= colorlist.size()){
                rgb = colorlist.get(steps - 1);
            } else {
                // Otherwise, you do steps % length of colorlist, and the color is colorlist[result - 1]
                int result = steps % colorlist.size();
                if (result > colorlist.size() - 1){
                    rgb = colorlist.get(colorlist.size() - 1);
                } else {
                    rgb = colorlist.get(result);
                }
            }
        }
        return rgb;
    }

    // MAIN
    public static void main(String[] args){
        start();
    }
}
