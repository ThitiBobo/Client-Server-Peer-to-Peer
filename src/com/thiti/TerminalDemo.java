import java.io.*;

public class TerminalDemo {
    // Default speed in bits per second for serial line  simulation
    // Set 0 to disable
    static final int SPEED = 1200;

    // ANSI Terminal codes
    static final String ESC = "\033";

    static String clearScreen() { return "\033[2J"; }
    static String cursorHome() { return "\033[H"; }
    static String cursorTo(int row, int column) {
        return String.format("\033[%d;%dH", row, column);
    }
    static String cursorSave() { return "\033[s"; }
    static String cursorRestore() { return "\033[u"; }

    static String scrollScreen() { return "\033[r"; }
    static String scrollSet(int top, int bottom) {
        return String.format("\033[%d;%dr", top, bottom);
    }
    static String scrollUp() { return "\033D"; }
    static String scrollDown() { return "\033D"; }

    static String setAttribute(int attr) {
        return String.format("\033[%dm", attr);
    }

    static final int ATTR_RESET = 0;
    static final int ATTR_BRIGHT = 1;
    static final int ATTR_USCORE = 4;
    static final int ATTR_BLINK = 5;
    static final int ATTR_REVERSE = 7;

    static final int ATTR_FCOL_BLACK = 30;
    static final int ATTR_FCOL_RED = 31;
    static final int ATTR_FCOL_GREEN = 32;
    static final int ATTR_FCOL_YELLOW = 33;

    static final int ATTR__BCOL_BLACK = 40;
    static final int ATTR__BCOL_RED = 41;
    static final int ATTR__BCOL_GREEN = 42;

    public static void main(String[] args) {
        // example string showing some text attributes
        String s = "This \033[31mstring\033[32m should \033[33mchange \033[33m color \033[41m and start \033[5m blinking!\033[0m Isn't that neat?\n";

        // Reset scrolling, clear screen and bring cursor home
        System.out.print(clearScreen());
        System.out.print(scrollScreen());

// Print example string s
        slowPrint(s);

// some text attributes
        slowPrint("This "
                + setAttribute(ATTR_USCORE) + "should be undescored\n"
                + setAttribute(ATTR_RESET)
                + setAttribute(ATTR_FCOL_RED) + "and this red\n"
                + setAttribute(ATTR_RESET)
                + "some "
                + setAttribute(ATTR_BRIGHT)
                + setAttribute(ATTR_FCOL_YELLOW)
                + setAttribute(ATTR_BLINK) + "BRIGHT YELLOW BLINKIN\n"
                + setAttribute(ATTR_RESET)
                + "could be fun.\n\n"
                + "Please press ENTER");

// Wait for ENTER
        try { System.in.read(); } catch(IOException e) {e.printStackTrace();}

// Set scroll area
        slowPrint(""
                + clearScreen()
                + scrollSet(2,20)
                + cursorTo(1,1)
                + "Cleared screen and set scroll rows Top: 2 and Bottom: 20\n"
                + cursorTo(21,1)
                + "Bottom area starts here"
                + cursorTo(2,1)
                + "");

// print some random text
        slowPrint(randomText(60));

// reset text attributes, reset scroll area and set cursor
// below scroll area
        System.out.print(setAttribute(ATTR_RESET));
        System.out.print(scrollScreen());
        System.out.println(cursorTo(22,1));
    }

    // Slow things down to resemble old serial terminals
    private static void slowPrint(String s) {
        slowPrint(s, SPEED);
    }

    private static void slowPrint(String s, int bps) {
        for (int i = 0; i < s.length(); i++) {
            System.out.print(s.charAt(i));

            if(bps == 0) continue;

            try { Thread.sleep((int)(8000.0 / bps)); }
            catch(InterruptedException ex)
            { Thread.currentThread().interrupt(); }
        }
    }

    // Retursn a character representation of sin graph
    private static String randomText(int lines) {
        String r = "";

        for(int i=0; i<lines; i++) {
            int sin = (int)Math.abs((Math.sin(1.0/20 * i)*30));
            r += setAttribute((sin / 4) + 30);
            for(int j=0; j<80; j++) {
                if(j > 40 + sin)
                    break;
                r += (j < (40-sin)) ? " " : "X";
            }
            r += setAttribute(ATTR_RESET) + "\n";
        }
        return r;
    }
}