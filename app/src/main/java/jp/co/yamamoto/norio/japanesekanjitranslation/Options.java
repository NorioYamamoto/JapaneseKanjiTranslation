package jp.co.yamamoto.norio.japanesekanjitranslation;

import android.app.Application;

public class Options extends Application {
    public boolean isPaste() {
        return paste;
    }

    public void setPaste(boolean paste) {
        this.paste = paste;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    private boolean paste = false;
    private boolean copy = false;
}
