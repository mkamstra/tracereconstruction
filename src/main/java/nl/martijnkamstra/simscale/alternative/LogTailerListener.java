package nl.martijnkamstra.simscale.alternative;

import org.apache.commons.io.input.TailerListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn Kamstra on 03/04/2017.
 */
public class LogTailerListener extends TailerListenerAdapter {

    private final List<String> lines = new ArrayList<>();

    @Override
    public void handle(String line) {
        lines.add(line);
    }

    public List<String> getLines() {
        return lines;
    }

    public void clear() {
        lines.clear();
    }

}
