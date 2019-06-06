import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JFrame implements ActionListener {

    JTextField text1;
    JTextField text2;
    JLabel label;
    boolean timerbool = false;

    public static void main(String[] args) {
        Main frame = new Main("タイトル");
        frame.setVisible(true);
    }

    Main(String title) {
        setTitle(title);
        setBounds(100, 100, 300, 250);
        //xを押した時にプログラムも停止させる
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //パネルの定義
        JPanel p = new JPanel();

        text1 = new JTextField("3分更新に対しての追加分数を入力してください");
        text2 = new JTextField("更新秒数を入力してください");
        JButton button = new JButton("開始");
        button.addActionListener(this);
        label = new JLabel();

        p.add(text1);
        p.add(text2);
        p.add(button);

        Container contentPane = getContentPane();
        contentPane.add(p, BorderLayout.CENTER);
        contentPane.add(label, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e){
        label.setText("開始されました");
        if (timerbool) {
            return;
        }
        Timer timer = new Timer(false);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("mm");
                SimpleDateFormat sdf2 = new SimpleDateFormat("ss");
                String mm = sdf.format(date);
                String ss = sdf2.format(date);
                int minute = Integer.parseInt(mm);
                int second = Integer.parseInt(ss);
                if (minute % 3 == Integer.parseInt(text1.getText())) {
                    if (Integer.parseInt(text2.getText()) == second) {
                        sound();
                    }
                }
            }
        };
        timer.schedule(task, 0, 1000);
        timerbool = true;
    }

    public static void sound() {
        File path = new File("sound.wav");

        //指定されたURLのオーディオ入力ストリームを取得
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)) {

            //ファイルの形式取得
            AudioFormat af = ais.getFormat();

            //単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
            DataLine.Info dataLine = new DataLine.Info(SourceDataLine.class,af);

            //指定された Line.Info オブジェクトの記述に一致するラインを取得
            SourceDataLine s = (SourceDataLine)AudioSystem.getLine(dataLine);

            //再生準備完了
            s.open();

            //ラインの処理を開始
            s.start();

            //読み込みサイズ
            byte[] data = new byte[s.getBufferSize()];

            //読み込んだサイズ
            int size = -1;

            //再生処理のループ
            while(true) {
                //オーディオデータの読み込み
                size = ais.read(data);
                if ( size == -1 ) {
                    //すべて読み込んだら終了
                    break;
                }
                //ラインにオーディオデータの書き込み
                s.write(data, 0, size);
            }

            //残ったバッファをすべて再生するまで待つ
            s.drain();

            //ライン停止
            s.stop();

            //リソース解放
            s.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
