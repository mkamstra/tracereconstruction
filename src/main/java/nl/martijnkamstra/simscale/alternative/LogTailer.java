package nl.martijnkamstra.simscale.alternative;

import org.apache.commons.io.input.Tailer;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Martijn Kamstra on 03/04/2017.
 */
public class LogTailer {

    public static void main(String[] args) {
        LogTailer logTailer = new LogTailer();
        try {
            logTailer.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() throws InterruptedException {
        final LogTailerListener tailerListener = new LogTailerListener();
        File file = new File("src/main/resources/log.txt");
        Tailer tailer = new Tailer(file, tailerListener, 1000, false);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                tailer.run();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    tailer.stop();
                }
            }
        });
        executorService.execute(tailer);
        while (true) {
            System.out.println(Arrays.toString(tailerListener.getLines().toArray()));
            if (tailerListener.getLines().size() > 0)
                tailerListener.clear();
            Thread.sleep(1000);
        }
    }
}
