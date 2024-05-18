import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

class Pole extends JComponent implements MouseListener {
    private boolean czyZyje;

    public Pole(){
        this.setLayout(new GridLayout());
        this.czyZyje = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color kolor = new Color(Color.WHITE.getRGB());
        if (this.czyZyje)
            kolor = new Color(Color.BLACK.getRGB());
        g.setColor(kolor);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth(), getHeight());
    }

    public void zmienStan(){
        this.czyZyje = !czyZyje;
    }

    public boolean podajStan(){
        return this.czyZyje;
    }

    public void ustawStan(boolean stan){
        this.czyZyje = stan;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        this.zmienStan();
        this.repaint();
    }

    @Override public void mousePressed(MouseEvent mouseEvent) {}
    @Override public void mouseReleased(MouseEvent mouseEvent) {}
    @Override public void mouseEntered(MouseEvent mouseEvent) {}
    @Override public void mouseExited(MouseEvent mouseEvent) {}
}

class KontenerGry extends JComponent{
    int rozmiar;
    int pelenRozmiar;
    Pole[][] plansza;

    public KontenerGry(int rozmiar){
        this.rozmiar = rozmiar;
        this.pelenRozmiar = rozmiar*rozmiar;
        this.plansza = new Pole[rozmiar][rozmiar];

        this.setLayout(new GridLayout(rozmiar, rozmiar));

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                Pole pole = new Pole();
                plansza[i][j] = pole;
                this.add(plansza[i][j]);
                plansza[i][j].addMouseListener(plansza[i][j]);
            }
        }
    }

    public int czyIstniejeIZyje(int i, int j){
        try{
            return plansza[i][j].podajStan() ? 1 : 0;
        } catch (ArrayIndexOutOfBoundsException e){
            return 0;
        }
    }

    public int iluSasiadow(int i, int j){
        int liczbaSasiadow = 0;

        liczbaSasiadow += czyIstniejeIZyje(i-1, j-1);
        liczbaSasiadow += czyIstniejeIZyje(i, j-1);
        liczbaSasiadow += czyIstniejeIZyje(i+1, j-1);

        liczbaSasiadow += czyIstniejeIZyje(i-1, j);
        liczbaSasiadow += czyIstniejeIZyje(i+1, j);

        liczbaSasiadow += czyIstniejeIZyje(i-1, j+1);
        liczbaSasiadow += czyIstniejeIZyje(i, j+1);
        liczbaSasiadow += czyIstniejeIZyje(i+1, j+1);

        return liczbaSasiadow;
    }

    public void nastepnyKrok(){
        ArrayList<Pole> zyweWNastepnejGen = new ArrayList<>();
        ArrayList<Pole> martweWNastepnejGen = new ArrayList<>();
        for (int i = 0; i < plansza.length; i++) {
            for (int j = 0; j < plansza.length; j++) {
                int liczbaSasiadow = iluSasiadow(i, j);
                if (plansza[i][j].podajStan()){
                    if (liczbaSasiadow < 2) {
                        martweWNastepnejGen.add(plansza[i][j]);
                    } else if (liczbaSasiadow < 4) {
                        zyweWNastepnejGen.add(plansza[i][j]);
                    } else {
                        martweWNastepnejGen.add(plansza[i][j]);
                    }
                } else{
                    if (liczbaSasiadow == 3){
                        zyweWNastepnejGen.add(plansza[i][j]);
                    }
                }
            }
        }

        for (Pole pole : zyweWNastepnejGen) {
            pole.ustawStan(true);
        }
        for (Pole pole : martweWNastepnejGen) {
            pole.ustawStan(false);
        }

        this.repaint();
    }
}

class KontenerBoczny extends JComponent{
    public KontenerBoczny(KontenerGry kontenerGry){
        this.setPreferredSize(new Dimension(200, 600));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Timer timer = new Timer(80, actionEvent -> kontenerGry.nastepnyKrok());

        JButton b1 = new JButton("Nastepny krok");
        JButton b2 = new JButton("Start");
        JButton b3 = new JButton("Stop");
        b3.setEnabled(false);
        b1.addActionListener(actionEvent -> kontenerGry.nastepnyKrok());

        b2.addActionListener(actionEvent -> {
            b1.setEnabled(false);
            b2.setEnabled(false);
            b3.setEnabled(true);
            timer.start();
        });

        b3.addActionListener(actionEvent -> {
            b1.setEnabled(true);
            b2.setEnabled(true);
            b3.setEnabled(false);
            timer.stop();
        });


        this.add(b1);
        this.add(b2);
        this.add(b3);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawLine(0,0,0,getHeight());
    }
}

class KontenerGlowny extends JComponent{
    public KontenerGlowny(){
        this.setLayout(new BorderLayout());

        KontenerGry kontenerGry = new KontenerGry(50);
        KontenerBoczny kontenerBoczny = new KontenerBoczny(kontenerGry);

        this.add(kontenerGry, BorderLayout.CENTER);
        this.add(kontenerBoczny, BorderLayout.EAST);
    }
}

public class Main {
    public static void main(String[] args) {
        JFrame okno = new JFrame("Gra w Zycie");
        okno.add(new KontenerGlowny());
        okno.setSize(800, 600);
        okno.setResizable(false);
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setVisible(true);
    }
}
