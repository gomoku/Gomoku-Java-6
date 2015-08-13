import java.awt.*;
import java.util.Random;

public class gomoku extends java.applet.Applet implements Runnable{

    Image banmen;
    Graphics dr_ban;
    Choice sente,gote,lan;
    Font f;
    FontMetrics fm;
    Panel pn,pn2,pn3;
    Label siro,kuro,mes;
    Button bt,bt2;
    Checkbox speed;

    int i,i2,i3,j,n,waku_x,waku_y,turn=1,lastX,lastY,r,mesn;

    int koma[][]=new int [16][16];
    int score[][]=new int [16][16];

    int kuhakux[]=new int[6];
    int kuhakuy[]=new int[6];
    int kohox[]=new int [256];
    int kohoy[]=new int [256];

    boolean game=false,cpu=false,flg_paint,cpu_turn=false,waku=false;
    boolean cpuVScpu,draw,stopped;

    String st[]=new String[7];
    Color waku_color;

    Random rnd=new Random();
    Thread runner=null;

    public void start(){

        resize(420,400);

        banmen=createImage(256,256);
        dr_ban=banmen.getGraphics();
        sente=new Choice();
        gote=new Choice();
        lan=new Choice();
        pn=new Panel();
        pn2=new Panel();
        pn3=new Panel();
        siro=new Label();
        kuro=new Label();
        mes=new Label();
        bt=new Button();
        bt2=new Button();
        speed=new Checkbox("cpu wait",null,false);
        speed.setBackground(Color.orange);

        f=new Font("TimesRoman",Font.PLAIN,12);
        sente.setFont(f);
        gote.setFont(f);
        lan.setFont(f);
        siro.setFont(f);
        kuro.setFont(f);
        mes.setFont(f);

        sente.addItem("Human");
        sente.addItem("Computer");
        gote.addItem("Human");
        gote.addItem("Computer");
        gote.select(1);
        lan.addItem("Japanese");
        lan.addItem("English");
        kuro.setText("\u5148\u624b\uff08\u9ed2\uff09");
        kuro.setBackground(Color.orange);
        siro.setText("\u5f8c\u624b\uff08\u767d\uff09");
        siro.setBackground(Color.orange);

        st[0]="\u30b9\u30bf\u30fc\u30c8\u30dc\u30bf\u30f3\u3067\u958b\u59cb\u3\067\u3059";
        st[1]="\u9ed2\u306e\u756a\u3067\u3059";
        st[2]="\u767d\u306e\u756a\u3067\u3059";
        st[3]="\u9ed2\u306e\u52dd\u3061\uff01\uff01";
        st[4]="\u767d\u306e\u52dd\u3061\uff01\uff01";
        st[5]="\u5f15\u304d\u5206\u3051\uff01\uff01";
        st[6]="\u66f4\u65b0\u3057\u3066\u304f\u3060\u3055\u3044";
        mes.setText(st[0]);
        mes.setBackground(Color.orange);
        mesn=0;

        f=new Font("TimesRoman",Font.BOLD,16);
        bt.setFont(f);
        bt2.setFont(f);

        bt.setLabel("Start");
        bt2.setLabel("Stop");

        setBackground(Color.orange);
        sente.setBackground(Color.white);
        gote.setBackground(Color.white);
        lan.setBackground(Color.white);
        bt.setBackground(Color.green);
        bt2.setBackground(Color.green);

        this.setLayout((LayoutManager)null);
        add(pn);
        add(pn2);
        add(pn3);

        pn.reshape(0,8,400,48);
        pn2.reshape(0,328,400,32);
        pn3.reshape(32,368,240,24);

        pn.setLayout(new FlowLayout(FlowLayout.CENTER,4,4));
        pn2.setLayout(new FlowLayout(FlowLayout.CENTER,16,3));
        pn3.setLayout(new FlowLayout());

        pn.add(kuro);
        pn.add(sente);
        pn.add(siro);
        pn.add(gote);

        pn2.add(bt);
        pn2.add(bt2);
        pn2.add(speed);
        pn2.add(lan);

        pn3.add(mes);

        bt2.enable(false);
 
        mesn=0;
        banmenClear();

        stopped=false;

        if (runner==null){

            runner=new Thread(this);
            runner.start();

        }

    }

    public void stop() {

        mes.setText(st[6]);

        if (runner!=null){

            runner.stop();
            runner=null;

        }

    }
	
	    public void banmenClear(){

        int i,j;

        dr_ban.setColor(Color.green);
        dr_ban.fillRect(0,0,256,256);

        dr_ban.setColor(Color.black);

        for (i=0;i<16;i++)
            dr_ban.drawLine(i*16+8,8,i*16+8,248);
        for (i=0;i<16;i++)
            dr_ban.drawLine(8,i*16+8,248,i*16+8);
 
    }

    public void ishi_haiti(int x,int y){

        koma[x][y]=turn;
        lastX=x;
        lastY=y;

        if (turn==1){

            dr_ban.setColor(Color.black);
            dr_ban.fillOval(1+x*16,1+y*16,13,13);

        }

        if (turn==-1) {

            dr_ban.setColor(Color.white);
            dr_ban.fillOval(1+x*16,1+y*16,13,13);
            dr_ban.setColor(Color.gray);
            dr_ban.drawOval(1+x*16,1+y*16,13,13);
        }

    }

    public boolean mouseMove(Event evt,int x,int y){

        int xx,yy;

        if (x<20 || x>275 ||y<68 ||y>319)
            return false;

        xx = (int)((x-20)/16);
        yy = (int)((y-68)/16);

        if (koma[xx][yy]==0 && game==true && !cpuVScpu)
            waku_color=Color.blue;
        else
            waku_color=Color.red;

        waku_x=18+xx*16;
        waku_y=66+yy*16;

        waku=true;
        repaint();

        return true;

    }

    public boolean mouseDown(Event evt,int x,int y){

        int xx,yy;

        if (x<20 || x>276 ||y<68 ||y>320 ||!game)
            return false;

        if (cpu_turn || !flg_paint || cpuVScpu)
            return false;

        xx=(int)((x-20)/16);
        yy=(int)((y-68)/16);

        if (koma[xx][yy]==0){

            ishi_haiti(xx,yy);

            lastX=xx;
            lastY=yy;

            hantei(turn);

            turn=-turn;

            flg_paint=false;
            repaint();

            if (cpu)
                cpu_turn=true;

        }

        return true;

    }

    public void run(){

        while (runner!=null){

            if (cpu_turn && flg_paint && !stopped){

                cpuTurn();

                hantei(turn);

                turn=-turn;

                repaint();
                cpu_turn=false;

            }


            if (cpuVScpu && flg_paint &&!stopped) {

                cpuTurn();
                hantei(turn);

                flg_paint=false;
                repaint();

                turn=-turn;

            }
        }

    }


    public boolean action(Event evt,Object what){

        int saki,ato;

        if (evt.target==bt){

            if(game)
                return false;

            cpu=false;
            cpuVScpu=false;

            for (i=0;i<16;i++)
                for (j=0;j<16;j++)
                    koma[j][i]=0;

            banmenClear();

            turn=1;

            saki=sente.getSelectedIndex();
            ato=gote.getSelectedIndex();

            if (saki==1 && ato==0){
 
                cpu=true;

                j=Math.abs(rnd.nextInt()) % 3+7;
                i=Math.abs(rnd.nextInt()) % 3+7;

                ishi_haiti(j,i);
                turn=-1;

            }

            if (saki==0 && ato==1)
                cpu=true;

            game=true;
            cpu_turn=false;

            bt.enable(false);
            bt2.enable(true);
            sente.enable(false);
            gote.enable(false);

            if (saki==1 && ato==1)
                cpuVScpu=true;

            repaint();

            return true;

        }

        if (evt.target==bt2){

            game=false;
            cpuVScpu=false;
            turn=1;

            bt.enable(true);
            bt2.enable(false);
            sente.enable(true);
            gote.enable(true);

            mesn=0;

            repaint();

            return true;
 
        }

        if (evt.target==lan){

            if (lan.getSelectedIndex()==0){

                st[0]="\u30b9\u30bf\u30fc\u30c8\u30dc\u30bf\u30f3\u3067\u958b\u59cb\u3067\u3059";
                st[1]="\u9ed2\u306e\u756a\u3067\u3059";
                st[2]="\u767d\u306e\u756a\u3067\u3059";
                st[3]="\u9ed2\u306e\u52dd\u3061\uff01\uff01";
                st[4]="\u767d\u306e\u52dd\u3061\uff01\uff01";
                st[5]="\u5f15\u304d\u5206\u3051\uff01\uff01";
                st[6]="\u66f4\u65b0\u3057\u3066\u304f\u3060\u3055\u3044";

                kuro.setText("\u5148\u624b\uff08\u9ed2\uff09");
                siro.setText("\u5f8c\u624b\uff08\u767d\uff09");

            } else if (lan.getSelectedIndex()==1){

                st[0]="Push Start Button!!";
                st[1]="Black's Turn";
                st[2]="White's Turn";
                st[3]="Black Win!!";
                st[4]="White Win!!";
                st[5]="Draw!!";
                st[6]="Please Reload!!";

                kuro.setText("Black");
                siro.setText("White");

            }

            repaint();
            return true;

        }

        return false;

    }
 
    public void sikou(int isi){

        int n0,n1,aite;

        int kati=15000,hissi=1000,sanren=500,mittu=100,futatu=10;
        int kati2=12000,hissi2=500,sanren2=200,mittu2=80,futatu2=10;

        for (i=0;i<16;i++) {

            for(i2=0;i2<12;i2++) {

                n1=0;
                n0=0;
                aite=0;

                for (j=i2;jmax)
                max=score[j][i];

    n=0;

    for (i=0;i<16;i++)
        for (j=0;j<16;j++)
            if (score[j][i]==max) {

                kohox[n]=j;
                kohoy[n]=i;
                n++;

            }

    if (n==0)
        return false;

    n=Math.abs(rnd.nextInt()) % n;

    ishi_haiti(kohox[n],kohoy[n]);
    return true;

}

public void hantei(int v){

    byte n1,n2,n3,n4;

    draw=true;

    for (i=0;i<16;i++)
        for (j=0;j<16;j++)
            if (koma[j][i]==0)
                draw=false;

    if (draw)
        game_end();

    for (i=0;i<16;i++){
        n1=0;
        for (j=0;j<15;j++){

            if (koma[j][i]==v && koma[j+1][i]==v){

                n1++;

                if (n1>=4)
                    game_end();

            } else
                n1=0;

        }

    }
	
    for (i=0;i<16;i++){
        n2=0;
        n3=0;
        n4=0;
        for (j=0;j<15;j++){

            if (koma[i][j]==v && koma[i][j+1]==v){

                n2++;

                if (n2>=4)
                    game_end();

            } else
                n2=0;

            if ((i+j)<15){
                if (koma[i+j][j]==v && koma[i+j+1][j+1]==v){

                    n3++;

                    if (n3>=4)
                        game_end();
 

                } else
                    n3=0;

                if (koma[j][i+j]==v && koma[j+1][i+j+1]==v){

                    n4++;
                    if (n4>=4)
                        game_end();

                } else
                    n4=0;

            }
        }
    }

    for (i=15;i>3;i--) {
        n3=0;
        for (j=0;j<15;j++)

            if ((i-j)>0 && j<15)
                if (koma[i-j][j]==v && koma[i-j-1][j+1]==v) {

                    n3++;
                    if (n3>=4)
                        game_end();

                } else
                    n3=0;
    }

    for (i=0;i<12;i++){
        n4=0;
        for (j=15;j>0;j--)

            if ((15-j+i)<15 && j>0)
                if (koma[j][15-j+i]==v && koma[j-1][15-j+i+1]==v) {

                    n4++;
                    if (n4>=4)
                        game_end();

                } else
                    n4=0;
    }

}

public void game_end(){

    game=false;
    cpuVScpu=false;

    bt.enable(true);
    sente.enable(true);
    gote.enable(true);

    if (turn==1)
        mesn=3;
    else
        mesn=4;

    if (draw)
        mesn=5;

    if (!draw){

        dr_ban.setColor(Color.red);
        dr_ban.fillRect(lastX*16,lastY*16,16,16);

        if (koma[lastX][lastY]==1)
            dr_ban.setColor(Color.black);

        if (koma[lastX][lastY]==-1)
            dr_ban.setColor(Color.white);

        dr_ban.fillOval(1+lastX*16,1+lastY*16,13,13);

    }

    bt2.enable(false);

    repaint();

}

public void paint(Graphics g){

    if (turn==1){

        g.setColor(Color.black);
        if (game==true)
            mesn=1;

    }

    else {
        g.setColor(Color.white);
        if (game==true)
            mesn=2;
    }

    if (!game)
        g.setColor(Color.orange);

    g.fillOval(280,64,24,24);
    mes.setText(st[mesn]);

    g.drawImage(banmen,16,64,this);

    if (waku){

        g.setColor(waku_color);
        g.drawRect(waku_x,waku_y,11,11);

    }

    flg_paint=true;

}

public void update(Graphics g){

    paint(g);

}

}

	