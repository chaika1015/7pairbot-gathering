
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.stream.*;
import java.io.BufferedReader;
import java.nio.file.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;

public class DrawPattern {
    static int robot = 6;
	public static void main(String[] args) {
        ArrayList<Point[]> initStates = new ArrayList<>();
        try{
            //初期状況を読み込み
            File f = new File("src/initest6.txt");
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            br = new BufferedReader(new FileReader(f));
            line = br.readLine();
            System.out.println(line);
            while(line != null) {
                String[] points = line.split(";");
                Point[] initPoint = new Point[robot];
                for(int i=0; i<robot; i++) {
                    String[] point = points[i].split(",");
                    int x = Integer.parseInt(point[0]);
                    int y = Integer.parseInt(point[1]);
                    initPoint[i] = new Point(x,y);
                }
                initStates.add(initPoint);
                line = br.readLine();
            }
            br.close();

            System.out.println(initStates.size());
                
        }catch(Exception e) {
            System.out.println(e);
        }

        for(int i=0; i<initStates.size(); i++) {
            try {
                BufferedImage img = new BufferedImage(600, 600, BufferedImage.TYPE_3BYTE_BGR);
                Graphics g = img.getGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 600, 600); //背景をまっしろに
                g.setColor(Color.BLACK);
                for(Point p : initStates.get(i)) {
                    double x = (p.getX() + p.getY()/2)*60;
                    double y = (p.getY()*(Math.sqrt(3)/2)*60);
                    g.fillOval((int)x+200, (int)y+200, 40, 40);
                }
                g.fillOval(200, 200, 40, 40);
                g.dispose();
                String number = String.format("%03d", i);
                ImageIO.write(img, "jpeg", new File("figures/sample"+ number +".jpeg"));
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

		
	}
}
