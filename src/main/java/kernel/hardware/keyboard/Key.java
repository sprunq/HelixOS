package kernel.hardware.keyboard;

public class Key {
    public static final int NONE = 0;
    public static final int SPACE = ' ';
    public static final int EXCLAMATION_MARK = '!';
    public static final int QUOTATION_MARK = '"';
    public static final int POUND_KEY = '#';
    public static final int DOLLAR_KEY = '$';
    public static final int PERCENT_KEY = '%';
    public static final int AMPERSAND = '&';
    public static final int SINGLE_QUOTE = '\'';
    public static final int LPAREN = '(';
    public static final int RPAREN = ')';
    public static final int ASTERISK = '*';
    public static final int PLUS = '+';
    public static final int COMMA = ',';
    public static final int MINUS = '-';
    public static final int DOT = '.';
    public static final int SLASH = '/';
    public static final int ZERO = '0';
    public static final int ONE = '1';
    public static final int TWO = '2';
    public static final int THREE = '3';
    public static final int FOUR = '4';
    public static final int FIVE = '5';
    public static final int SIX = '6';
    public static final int SEVEN = '7';
    public static final int EIGHT = '8';
    public static final int NINE = '9';
    public static final int COLON = ':';
    public static final int SEMICOLON = ';';
    public static final int LESS_THAN = '<';
    public static final int EQUALS = '=';
    public static final int GREATER_THAN = '>';
    public static final int QUESTION_MARK = '?';
    public static final int AT_SIGN = '@';
    public static final int A = 'A';
    public static final int B = 'B';
    public static final int C = 'C';
    public static final int D = 'D';
    public static final int E = 'E';
    public static final int F = 'F';
    public static final int G = 'G';
    public static final int H = 'H';
    public static final int I = 'I';
    public static final int J = 'J';
    public static final int K = 'K';
    public static final int L = 'L';
    public static final int M = 'M';
    public static final int N = 'N';
    public static final int O = 'O';
    public static final int P = 'P';
    public static final int Q = 'Q';
    public static final int R = 'R';
    public static final int S = 'S';
    public static final int T = 'T';
    public static final int U = 'U';
    public static final int V = 'V';
    public static final int W = 'W';
    public static final int X = 'X';
    public static final int Y = 'Y';
    public static final int Z = 'Z';
    public static final int LSQUARE = '[';
    public static final int BACKSLASH = '\\';
    public static final int RSQUARE = ']';
    public static final int CARET = '^';
    public static final int UNDERSCORE = '_';
    public static final int GRAVE_ACCENT = '`';
    public static final int a = 'a';
    public static final int b = 'b';
    public static final int c = 'c';
    public static final int d = 'd';
    public static final int e = 'e';
    public static final int f = 'f';
    public static final int g = 'g';
    public static final int h = 'h';
    public static final int i = 'i';
    public static final int j = 'j';
    public static final int k = 'k';
    public static final int l = 'l';
    public static final int m = 'm';
    public static final int n = 'n';
    public static final int o = 'o';
    public static final int p = 'p';
    public static final int q = 'q';
    public static final int r = 'r';
    public static final int s = 's';
    public static final int t = 't';
    public static final int u = 'u';
    public static final int v = 'v';
    public static final int w = 'w';
    public static final int x = 'x';
    public static final int y = 'y';
    public static final int z = 'z';
    public static final int LCURLY = '{';
    public static final int PIPE = '|';
    public static final int RCURLY = '}';
    public static final int TILDE = '~';
    public static final int DELETE = 0x7F;
    public static final int LSHIFT = 0x100;
    public static final int RSHIFT = 0x101;
    public static final int LCTRL = 0x102;
    public static final int RCTRL = 0x103;
    public static final int LALT = 0x104;
    public static final int RALT = 0x105;
    public static final int ENTER = 0x106;
    public static final int BACKSPACE = 0x107;
    public static final int TAB = 0x108;
    public static final int CAPSLOCK = 0x109;
    public static final int ESCAPE = 0x10A;
    public static final int SUPER = 0x10B;
    public static final int WINDOWS = SUPER;
    public static final int ARROW_UP = 0x10D;
    public static final int ARROW_DOWN = 0x10E;
    public static final int ARROW_LEFT = 0x10F;
    public static final int ARROW_RIGHT = 0x110;
    public static final int PAGE_UP = 0x111;
    public static final int PAGE_DOWN = 0x112;
    public static final int INSERT = 0x113;
    public static final int HOME = 0x114;
    public static final int END = 0x115;
    public static final int PRINT_SCREEN = 0x116;
    public static final int SCROLLLOCK = 0x117;
    public static final int PAUSE = 0x118;
    public static final int NUMLOCK = 0x119;
    // public static final int ALT_GR = 0x11A;
    public static final int MENU = 0x11B;
    public static final int F1 = 0x140;
    public static final int F2 = 0x141;
    public static final int F3 = 0x142;
    public static final int F4 = 0x143;
    public static final int F5 = 0x144;
    public static final int F6 = 0x145;
    public static final int F7 = 0x146;
    public static final int F8 = 0x147;
    public static final int F9 = 0x148;
    public static final int F10 = 0x149;
    public static final int F11 = 0x14A;
    public static final int F12 = 0x14B;

    public static final int SECTION = Key.NONE;
    public static final int SHARP_S = Key.S;
    public static final int EURO_SIGN = Key.DOLLAR_KEY;
    public static final int AGUE_ACCENT = Key.GRAVE_ACCENT;

    public static int Ascii(int key) {
        if (key >= 0x20 && key <= 0x7E) {
            return key;
        }
        return 0;
    }

    public static String Name(int key) {
        if (key >= 0x20 && key <= 0x7E) {
            char c = (char) key;
            byte[] chars = new byte[1];
            chars[0] = (byte) c;
            return new String(chars);
        }
        switch (key) {
            case Key.LSHIFT:
                return "LSHIFT";
            case Key.RSHIFT:
                return "RSHIFT";
            case Key.LCTRL:
                return "LCTRL";
            case Key.RCTRL:
                return "RCTRL";
            case Key.LALT:
                return "LALT";
            case Key.RALT:
                return "RALT";
            case Key.ENTER:
                return "ENTER";
            case Key.BACKSPACE:
                return "BACKSPACE";
            case Key.TAB:
                return "TAB";
            case Key.CAPSLOCK:
                return "CAPSLOCK";
            case Key.ESCAPE:
                return "ESCAPE";
            case Key.SUPER:
                return "SUPER";
            case Key.ARROW_UP:
                return "ARROW_UP";
            case Key.ARROW_DOWN:
                return "ARROW_DOWN";
            case Key.ARROW_LEFT:
                return "ARROW_LEFT";
            case Key.ARROW_RIGHT:
                return "ARROW_RIGHT";
            case Key.PAGE_UP:
                return "PAGE_UP";
            case Key.PAGE_DOWN:
                return "PAGE_DOWN";
            case Key.INSERT:
                return "INSERT";
            case Key.HOME:
                return "HOME";
            case Key.END:
                return "END";
            case Key.PRINT_SCREEN:
                return "PRINT_SCREEN";
            case Key.SCROLLLOCK:
                return "SCROLLLOCK";
            case Key.PAUSE:
                return "PAUSE";
            case Key.NUMLOCK:
                return "NUMLOCK";
            // case Key.ALT_GR: return "ALT_GR";
            case Key.MENU:
                return "MENU";
            case Key.DELETE:
                return "DELETE";
            case Key.F1:
                return "F1";
            case Key.F2:
                return "F2";
            case Key.F3:
                return "F3";
            case Key.F4:
                return "F4";
            case Key.F5:
                return "F5";
            case Key.F6:
                return "F6";
            case Key.F7:
                return "F7";
            case Key.F8:
                return "F8";
            case Key.F9:
                return "F9";
            case Key.F10:
                return "F10";
            case Key.F11:
                return "F11";
            case Key.F12:
                return "F12";
            default:
                return "UNKNOWN (".append(Integer.toString(key, 10)).append(")");
        }
    }
}
